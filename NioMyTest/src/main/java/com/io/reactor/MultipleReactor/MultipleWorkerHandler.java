package com.io.reactor.MultipleReactor;
      
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 多Reactor多线程模型
 *  Handler负责MultipleHandlerState状态是读写和其他的事件
 * @author mh
 * @date 2020-04
 */
public class MultipleWorkerHandler implements Runnable {

    private final SelectionKey sk;
    private final SocketChannel sc;
    private static final int THREAD_COUNTING = 10;
    private static ThreadPoolExecutor pool = new ThreadPoolExecutor(
            THREAD_COUNTING, THREAD_COUNTING, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>()); // 線程池

    MultipleHandlerState state; // 以狀態模式實現Handler

    public MultipleWorkerHandler(SelectionKey sk, SocketChannel sc) {
        this.sk = sk;
        this.sc = sc;
        state = new ReadStateMultiple(); // 初始狀態設定為READING
        pool.setMaximumPoolSize(32); // 設置線程池最大線程數
    }

    @Override
    public void run() {
        try {
            state.handle(this, sk, sc, pool);
        } catch (IOException e) {
            System.out.println("client  close");
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

    public void setState(MultipleHandlerState state) {
        this.state = state;
    }
}