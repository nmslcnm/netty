package com.io.reactor.MultipleReactor;
      
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

public interface MultipleHandlerState {

    void changeState(MultipleWorkerHandler h);

    void handle(MultipleWorkerHandler h, SelectionKey sk, SocketChannel sc,
                ThreadPoolExecutor pool) throws IOException ;
}