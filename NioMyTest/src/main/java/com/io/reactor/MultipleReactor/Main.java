package com.io.reactor.MultipleReactor;
      
import java.io.IOException;

/**
 *
 * 多Reactor多线程模型
 *
 *  服务端启动
 *  @auto mh
 *  @date 2020-04
 */
public class Main {

    public static void main(String[] args) {
        try {
            MultipleReactor reactor = new MultipleReactor(8980);
            Thread thread = new Thread(reactor);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}