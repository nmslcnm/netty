package com.io.netty.rpc.client;

import com.io.netty.rpc.entity.ClassInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcProxy {

    public static Object create(Class clazz){
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                ClassInfo classInfo=new ClassInfo();
                classInfo.setClassName(clazz.getName());
                classInfo.setMethodName(method.getName());
                classInfo.setArgs(args);
                classInfo.setClazzType(method.getParameterTypes());
                EventLoopGroup eventExecutors=new NioEventLoopGroup();
                Bootstrap bootstrap=new Bootstrap();
                //创建业务处理类
                ClientRPCHandler nettyClientHendler = new ClientRPCHandler();
                try {
                    bootstrap.group(eventExecutors)    //设置线程组
                            .channel(NioSocketChannel.class) //设置使用SocketChannel为管道通信的底层实现
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    ChannelPipeline pipeline = socketChannel.pipeline();
                                    //添加编码器
                                    pipeline.addLast(new ObjectEncoder());
                                    pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));//
                                    pipeline.addLast(nettyClientHendler);
                                }
                            });
                    ChannelFuture future = bootstrap.connect("127.0.0.1", 8980).sync();  //connect方法是异步的    sync方法是同步的
                    future.channel().writeAndFlush(classInfo).sync();
                    future.channel().closeFuture().sync();
                }catch (Exception e){
                    e.printStackTrace();
                }
                return nettyClientHendler.getResponse();
            }
        });
    }

}
