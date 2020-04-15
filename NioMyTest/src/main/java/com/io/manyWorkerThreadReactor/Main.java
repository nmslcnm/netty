package com.io.manyWorkerThreadReactor;
      
import java.io.IOException;

/**
 *
 * 单Reactor多工作线程模型
 *
 *  服务端启动
 *  @auto mh
 *  @date 2020-04
 */
public class Main {

    public static void main(String[] args) {
        try {
            Reactor reactor = new Reactor(8980);
            reactor.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}