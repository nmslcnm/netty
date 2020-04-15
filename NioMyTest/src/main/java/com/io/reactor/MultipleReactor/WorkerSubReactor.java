package com.io.reactor.MultipleReactor;
      
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 多Reactor多线程模型
 *  工作Reactor负责读写及其他事件
 * @author mh
 * @date 2020-04
 */
public class WorkerSubReactor implements Runnable {

    private final ServerSocketChannel ssc;
    private final Selector selector;
    private boolean restart = false;
    int num;

    public WorkerSubReactor(Selector selector, ServerSocketChannel ssc, int num) {
        this.ssc = ssc;
        this.selector = selector;
        this.num = num;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {

            System.out.println("waiting for restart");
            while (!Thread.interrupted() && !restart) {
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
    }


    private void dispatch(SelectionKey key) {
        Runnable r = (Runnable) (key.attachment());
        if (r != null)
            r.run();
    }

    public void setRestart(boolean restart) {
        this.restart = restart;
    }
}