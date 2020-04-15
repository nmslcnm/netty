package com.io.reactor.BasicReactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 单Reactor单线程模式示例代码
 * @author mh
 * @date 2020-04
 */
public class BasicReactor implements Runnable {

    private final ServerSocketChannel ssc;
    private final Selector selector;

    public BasicReactor(int port) throws IOException {
        selector = Selector.open();
        ssc = ServerSocketChannel.open();
        InetSocketAddress addr = new InetSocketAddress(port);
        ssc.socket().bind(addr);
        ssc.configureBlocking(false);
        SelectionKey sk = ssc.register(selector, SelectionKey.OP_ACCEPT);
        // 添加附加对象
        sk.attach(new Acceptor(selector, ssc));
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            System.out.println("Waiting: " + ssc.socket().getLocalPort() + "...");
            try {
                // 如果沒有事件就不执行
                if (selector.select() == 0)
                    continue;
            } catch (IOException e) {
                e.printStackTrace();
            }
            Set<SelectionKey> selected = selector.selectedKeys();
            Iterator<SelectionKey> it = selected.iterator();
            while (it.hasNext()) {
                dispatch((it.next()));
                it.remove();
            }
        }
    }

    /**
     * 分发执行不同的事件，当创建对象后只有一个OP_ACCEPT可连接事件，
     * 1、当有客户端连接，则会执行Acceptor连接附加对象的run方法
     * 2、如果检测到读写事件，则会执行Handlers连接附加对象线程方法
     * @param key
     */
    private void dispatch(SelectionKey key) {
        Runnable r = (Runnable) (key.attachment()); // 根據事件之key綁定的對象開新線程
        if (r != null)
            r.run();
    }

}