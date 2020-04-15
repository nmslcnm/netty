package com.io.manyWorkerThreadReactor;
      
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *  单Reactor多工作线程模型
 *  ReadState 读状态执行
 *  decode, compute, encode 开启线程处理，与单Reactor单线程模式区别点
 * @auto mh
 * @date 2020-04
 */
public class ReadState implements HandlerState{

    private SelectionKey sk;

    public ReadState() {
    }

    @Override
    public void changeState(Handler h) {
        // TODO Auto-generated method stub
        h.setState(new WorkState());
    }

    @Override
    public void handle(Handler h, SelectionKey sk, SocketChannel sc,
                       ThreadPoolExecutor pool) throws IOException { // read()
        this.sk = sk;
        byte[] arr = new byte[1024];
        ByteBuffer buf = ByteBuffer.wrap(arr);

        int numBytes = sc.read(buf);
        if(numBytes == -1)
        {
            System.out.println("client close");
            h.closeChannel();
            return;
        }
        String str = new String(arr);
        if ((str != null) && !str.equals(" ")) {
            h.setState(new WorkState()); // 改变状态从读到写
            pool.execute(new WorkerThread(h, str)); // 处理工作线程任务 worker thread
            System.out.println(sc.socket().getRemoteSocketAddress().toString() + ": " + str);
        }

    }

    //do process(decode, logically process, encode)
    synchronized void process(Handler h, String str) {

        h.setState(new WriteState()); // 改变状态从读到写
        this.sk.interestOps(SelectionKey.OP_WRITE);
        this.sk.selector().wakeup();
    }

    class WorkerThread implements Runnable {

        Handler h;
        String str;

        public WorkerThread(Handler h, String str) {
            this.h = h;
            this.str=str;
        }

        @Override
        public void run() {
            process(h, str);
        }

    }
}