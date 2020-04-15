package com.io.manyWorkerThreadReactor;
      
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *  单Reactor多工作线程模型
 *  WriteState 写状态执行
 *  在ReadState修改状态OP_WRITE 可写状态
 * @auto mh
 * @date 2020-04
 */
public class WriteState implements HandlerState{

    public WriteState() {
    }

    @Override
    public void changeState(Handler h) {
        h.setState(new ReadState());
    }

    @Override
    public void handle(Handler h, SelectionKey sk, SocketChannel sc,
                       ThreadPoolExecutor pool) throws IOException { // send()

        String str = "Your message has sent to " + sc.socket().getLocalSocketAddress().toString() + "\r\n";
        ByteBuffer buf = ByteBuffer.wrap(str.getBytes());

        while (buf.hasRemaining()) {
            sc.write(buf);
        }

        h.setState(new ReadState());
        sk.interestOps(SelectionKey.OP_READ);
        sk.selector().wakeup();
    }
}