package com.io.manyWorkerThreadReactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 单Reactor多工作线程模型
 * @author mh
 * @date 2020-04
 */
public class Reactor implements Runnable {

    private final ServerSocketChannel ssc;
    private final Selector selector;

    public Reactor(int port) throws IOException {
        selector = Selector.open();
        ssc = ServerSocketChannel.open();
        InetSocketAddress addr = new InetSocketAddress(port);
        ssc.socket().bind(addr);
        ssc.configureBlocking(false);
        SelectionKey sk = ssc.register(selector, SelectionKey.OP_ACCEPT);
        sk.attach(new Acceptor(selector, ssc));
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            System.out.println("Waiting for new event on port: " + ssc.socket().getLocalPort() + "...");

            try {
                if (selector.select() == 0)
                    continue;
            } catch (IOException e) {
                e.printStackTrace();
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectionKeys.iterator();
            while (it.hasNext()) {
                dispatch((SelectionKey) (it.next()));
                it.remove();
            }
        }
    }

    private void dispatch(SelectionKey key) {
        Runnable r = (Runnable) (key.attachment());
        if (r != null)
            r.run();
    }

}