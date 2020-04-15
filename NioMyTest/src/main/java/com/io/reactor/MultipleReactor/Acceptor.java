
package com.io.reactor.MultipleReactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 多Reactor多线程模型
 *
 * 接受连接请求，并创建cores是电脑CPU核数的选择器数组selectors，
 * 负责连接到服务器的客户端注册到对应的selectors。
 * 并创建工作子Reactor的WorkerSubReactor，并直接启动工作子Reactor，
 * 工作Reactor将会与主Reactor在不同线程遍历检查selector状态是否有更新。
 *  所谓的状态更新就是是否有新连接事件，或更新为读写状态 selector.select() == 0
 *
 * 当第一个客户端连接时，便会注册到下标是0的selectors，以后一次类推
 *
 * @author mh
 * @date 2020-04-14
 */
public class Acceptor implements Runnable {

    private final ServerSocketChannel ssc;
    private final int cores = Runtime.getRuntime().availableProcessors();
    private final Selector[] selectors = new Selector[cores];
    // 当前subReactor索引值
    private int selIdx = 0;
    private WorkerSubReactor[] r = new WorkerSubReactor[cores];
    private Thread[] t = new Thread[cores];

    public Acceptor(ServerSocketChannel ssc) throws IOException {
        this.ssc = ssc;
        for (int i = 0; i < cores; i++) {
            selectors[i] = Selector.open();
            r[i] = new WorkerSubReactor(selectors[i], ssc, i);
            t[i] = new Thread(r[i]);
            t[i].start();
        }
    }

    @Override
    public synchronized void run() {
        try {
            SocketChannel sc = ssc.accept();
            System.out.println(sc.socket().getRemoteSocketAddress().toString() + " is connected.");

            if (sc != null) {
                sc.configureBlocking(false);
                r[selIdx].setRestart(true);
                selectors[selIdx].wakeup();
                SelectionKey sk = sc.register(selectors[selIdx],
                        SelectionKey.OP_READ);
                selectors[selIdx].wakeup();
                r[selIdx].setRestart(false);
                sk.attach(new MultipleWorkerHandler(sk, sc));
                if (++selIdx == selectors.length)
                    selIdx = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}