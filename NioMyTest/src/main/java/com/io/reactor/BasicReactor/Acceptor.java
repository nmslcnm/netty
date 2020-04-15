
package com.io.reactor.BasicReactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 *  单Reactor单线程模式
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
            SocketChannel socketChannel= ssc.accept();
            System.out.println(socketChannel.socket().getRemoteSocketAddress().toString() + " is connected.");
            if(socketChannel!=null) {
                socketChannel.configureBlocking(false);
                SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
                selector.wakeup();
                selectionKey.attach(new Handlers(selectionKey, socketChannel));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}