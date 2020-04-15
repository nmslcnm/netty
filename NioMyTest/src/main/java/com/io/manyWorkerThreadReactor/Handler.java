package com.io.manyWorkerThreadReactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 单Reactor多工作线程模型
 *  HandlerState状态处理初始化是ReadState，读状态。
 *  在Reactor线程循环内，检测到状态改变。则调用Handler的run方法
 * @author mh
 * @date 2020-04
 */
public class Handler implements Runnable {

    private final SelectionKey sk;
    private final SocketChannel sc;
    private static final int THREAD_COUNTING = 10;
    private static ThreadPoolExecutor pool = new ThreadPoolExecutor(
            THREAD_COUNTING, THREAD_COUNTING, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    HandlerState state; //以状态模式实现Handler

    public Handler(SelectionKey sk, SocketChannel sc) {
        this.sk = sk;
        this.sc = sc;
        state = new ReadState(); // 首先设置读状态
        pool.setMaximumPoolSize(32);
    }

    @Override
    public void run() {
        try {
            state.handle(this, sk, sc, pool);
        } catch (IOException e) {
            System.out.println("client close");
            closeChannel();
        }
    }

    public void closeChannel() {
        try {
            sk.cancel();
            sc.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void setState(HandlerState state) {
        this.state = state;
    }
}