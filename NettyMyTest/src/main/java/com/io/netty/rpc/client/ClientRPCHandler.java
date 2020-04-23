package com.io.netty.rpc.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientRPCHandler extends ChannelInboundHandlerAdapter {

    private Object response;

    public Object getResponse(){
        return response;
    }

    //读取数据事件
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.response=msg;
        ctx.close();
    }
}
