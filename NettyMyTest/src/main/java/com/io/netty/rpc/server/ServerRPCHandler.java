package com.io.netty.rpc.server;

import com.io.netty.rpc.entity.ClassInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerRPCHandler extends ChannelInboundHandlerAdapter {

    public static ServerRPCHandler serverSocketNettyHendler=new ServerRPCHandler();


    private static ExecutorService executorService= Executors.newFixedThreadPool(1000);


    //读取数据事件
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    ClassInfo classInfo= (ClassInfo) msg;
                    Object o = Class.forName(getImplClassName(classInfo)).newInstance();
                    Method method = o.getClass().getMethod(classInfo.getMethodName(), classInfo.getClazzType());
                    Object invoke = method.invoke(o, classInfo.getArgs());
                    ctx.channel().writeAndFlush(invoke);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    //获取接口实现类
    private String getImplClassName(ClassInfo classInfo) throws Exception {
        String iName="com.io.netty.rpc.server";
        int i = classInfo.getClassName().lastIndexOf(".");
        String className=classInfo.getClassName().substring(i);
        Class aClass = Class.forName(iName + className);
        Reflections reflections=new Reflections(iName);
        Set<Class<?>> classes=reflections.getSubTypesOf(aClass);
        if(classes.size()==1){
            Class[] classes1 = classes.toArray(new Class[0]);
            return classes1[0].getName();
        }else{
            System.out.println("找到多个或没有实现类");
            return null;
        }
    }
}
