package com.io.netty.rpc.server.impl;

import com.io.netty.rpc.server.TestService;

import java.util.ArrayList;

public class TestServiceImpl implements TestService {

    static ArrayList<String> list = new ArrayList<>();

    static {
        list.add("看到我就对啦");
        list.add("哈哈哈");
    }

    @Override
    public String listByIndex(Integer id) {
        return list.get(id);
    }
}
