package com.io.netty.rpc.client;

import com.io.netty.rpc.server.TestService;

public class ClientRPC {
    public static void main(String[] args) throws InterruptedException {
        TestService testService = (TestService) RpcProxy.create(TestService.class);
        System.out.println(testService.listByIndex(0));
    }
}
