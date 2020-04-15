package com.io.reactor.MultipleReactor;
      
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * 多Reactor多线程模型
 *
 *  读事件
 *  @auto mh
 *  @date 2020-04
 */
public class ReadStateMultiple implements MultipleHandlerState {

    private SelectionKey sk;

    public ReadStateMultiple() {
    }

    @Override
    public void changeState(MultipleWorkerHandler h) {
        // TODO Auto-generated method stub
        h.setState(new WorkStateMultiple());
    }

    @Override
    public void handle(MultipleWorkerHandler h, SelectionKey sk, SocketChannel sc,
                       ThreadPoolExecutor pool) throws IOException { // read()
        this.sk = sk;
        byte[] arr = new byte[1024];
        ByteBuffer buf = ByteBuffer.wrap(arr);

        int numBytes = sc.read(buf);
        if(numBytes == -1)
        {
            System.out.println(" client  close");
            h.closeChannel();
            return;
        }
        String str = new String(arr);
        if ((str != null) && !str.equals(" ")) {
            h.setState(new WorkStateMultiple());
            pool.execute(new WorkerThread(h, str)); // do process in worker thread
            System.out.println(sc.socket().getRemoteSocketAddress().toString() + " > " + str);
        }

    }


    synchronized void process(MultipleWorkerHandler h, String str) {
        // do process(decode, logically process, encode)..

        h.setState(new WriteStateMultiple());
        this.sk.interestOps(SelectionKey.OP_WRITE);
        this.sk.selector().wakeup();
    }

    class WorkerThread implements Runnable {

        MultipleWorkerHandler h;
        String str;

        public WorkerThread(MultipleWorkerHandler h, String str) {
            this.h = h;
            this.str=str;
        }

        @Override
        public void run() {
            process(h, str);
        }

    }
}