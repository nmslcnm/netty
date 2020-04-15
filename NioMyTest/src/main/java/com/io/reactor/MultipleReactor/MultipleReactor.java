package com.io.reactor.MultipleReactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 多Reactor多线程模型
 *  主Reactor负责连接
 * @author mh
 * @date 2020-04
 */
public class MultipleReactor implements Runnable {

    private final ServerSocketChannel ssc;
    private final Selector selector; // mainReactor用的selector

    public MultipleReactor(int port) throws IOException {
        selector = Selector.open();
        ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        Acceptor acceptor = new Acceptor(ssc);

        SelectionKey sk = ssc.register(selector,SelectionKey.OP_ACCEPT);
        sk.attach(acceptor);

        InetSocketAddress addr = new InetSocketAddress(port);
        ssc.socket().bind(addr);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            System.out.println("mainReactor waiting for new event on port: "
                    + ssc.socket().getLocalPort() + "...");
            try {

                if (selector.select() == 0)
                    continue;
            } catch (IOException e) {
                e.printStackTrace();
            }
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectedKeys.iterator();
            while (it.hasNext()) {
                dispatch((SelectionKey) (it.next()));
                it.remove();
            }
        }
    }

    private void dispatch(SelectionKey key) {
        Runnable r = (Runnable) (key.attachment()); // 根據事件之key綁定的對象開新線程
        if (r != null)
            r.run();
    }

}