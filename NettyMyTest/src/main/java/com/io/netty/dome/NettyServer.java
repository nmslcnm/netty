package com.io.netty.dome;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Netty服务端，实现聊天室功能 需要启动客户端多次
 * @author mh
 * @date 2020-04
 */
public class NettyServer {
    public static void main(String[] args) {
        EventLoopGroup bossGroup=new NioEventLoopGroup(1);
        EventLoopGroup workGroup=new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap=new ServerBootstrap();
            serverBootstrap.group(bossGroup,workGroup)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new ServerInitializer());
            ChannelFuture channelFuture = serverBootstrap.bind(8980).sync();
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
