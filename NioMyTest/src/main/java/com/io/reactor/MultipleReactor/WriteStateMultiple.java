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
 *  写事件
 *  @auto mh
 *  @date 2020-04
 */
public class WriteStateMultiple implements MultipleHandlerState {

    public WriteStateMultiple() {
    }

    @Override
    public void changeState(MultipleWorkerHandler h) {
        h.setState(new ReadStateMultiple());
    }

    @Override
    public void handle(MultipleWorkerHandler h, SelectionKey sk, SocketChannel sc,
                       ThreadPoolExecutor pool) throws IOException { // send()
        String str = "Your message has sent to " + sc.socket().getLocalSocketAddress().toString() + "\r\n";
        ByteBuffer buf = ByteBuffer.wrap(str.getBytes());
        while (buf.hasRemaining()) {
            sc.write(buf);
        }

        h.setState(new ReadStateMultiple());
        sk.interestOps(SelectionKey.OP_READ);
        sk.selector().wakeup();
    }
}