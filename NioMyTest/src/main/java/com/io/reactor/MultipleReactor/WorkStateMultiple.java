package com.io.reactor.MultipleReactor;
      
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * 多Reactor多线程模型
 *
 *  Work事件
 *  @auto mh
 *  @date 2020-04
 */
public class WorkStateMultiple implements MultipleHandlerState {

    public WorkStateMultiple() {
    }

    @Override
    public void changeState(MultipleWorkerHandler h) {
        // TODO Auto-generated method stub
        h.setState(new WriteStateMultiple());
    }

    @Override
    public void handle(MultipleWorkerHandler h, SelectionKey sk, SocketChannel sc,
                       ThreadPoolExecutor pool) throws IOException {
        // TODO Auto-generated method stub

    }

}