
package com.io.manyWorkerThreadReactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 *  单Reactor多工作线程模型
 *  连接器，客户端连接进入
 *  得到的客户端socketChannel,和事件OP_READ可读事件一起注册到Selector选择器上(Handlers)
 * @auto mh
 * @date 2020-04
 */
public class Acceptor implements Runnable {

    private final ServerSocketChannel ssc;
    private final Selector selector;

    public Acceptor(Selector selector, ServerSocketChannel ssc) {
        this.ssc=ssc;
        this.selector=selector;
    }

    @Override
    public void run() {
        try {
            SocketChannel sc= ssc.accept();
            System.out.println(sc.socket().getRemoteSocketAddress().toString() + " is connected.");

            if(sc!=null) {
                sc.configureBlocking(false);
                SelectionKey sk = sc.register(selector, SelectionKey.OP_READ); // SocketChannel向selector註冊一個OP_READ事件，然後返回該通道的key
                selector.wakeup();
                sk.attach(new Handler(sk, sc)); // 给key一个附加对象Handler
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}