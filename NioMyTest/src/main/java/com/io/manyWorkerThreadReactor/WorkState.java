package com.io.manyWorkerThreadReactor;
      
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

public class WorkState implements HandlerState {

    public WorkState() {
    }

    @Override
    public void changeState(Handler h) {
        h.setState(new WriteState());
    }

    @Override
    public void handle(Handler h, SelectionKey sk, SocketChannel sc,
                       ThreadPoolExecutor pool) throws IOException {
    }

}