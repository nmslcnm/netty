package com.io.reactor.BasicReactor;
      
import java.io.IOException;

/**
 *
 * 单Reactor单线程模式
 *
 *  服务端启动
 *  @auto mh
 *  @date 2020-04
 */
public class Main {

    public static void main(String[] args) {
        try {
            BasicReactor reactor = new BasicReactor(8980);
            reactor.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}