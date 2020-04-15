package com.io.reactor.BasicReactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 *
 *  单Reactor单线程模式
 *
 * 从SocketChannel客户端通道中读写数据
 *  和具体业务处理(包括decode、compute、encode)
 * @author  mh
 * @date 2020-04
 */
public class Handlers implements Runnable {

    private final SelectionKey sk;
    private final SocketChannel sc;
    int state;

    public Handlers(SelectionKey sk, SocketChannel sc) {
        this.sk = sk;
        this.sc = sc;
        state = 0;
    }

    @Override
    public void run() {
        try {
            if (state == 0)
                read();
            else
                send();
        } catch (IOException e) {
            System.out.println(" client close");
            closeChannel();
        }
    }

    private void closeChannel() {
        try {
            sk.cancel();
            sc.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private synchronized void read() throws IOException {
        byte[] arr = new byte[1024];
        ByteBuffer buf = ByteBuffer.wrap(arr);

        int numBytes = sc.read(buf);
        if(numBytes == -1) {
            System.out.println(" client close");
            closeChannel();
            return;
        }
        String str = new String(arr);
        if ((str != null) && !str.equals(" ")) {
            process(str);
            System.out.println(sc.socket().getRemoteSocketAddress().toString()+": "+ str);
            state = 1;
            sk.interestOps(SelectionKey.OP_WRITE);
            sk.selector().wakeup(); // 使阻塞住的selector操作立即返回
        }
    }

    private void send() throws IOException  {
        String str = "Your message has sent to " + sc.socket().getLocalSocketAddress().toString() + "\r\n";
        // wrap方法自动把buf的position设0，所以不需要再flip方法
        ByteBuffer buf = ByteBuffer.wrap(str.getBytes());

        while (buf.hasRemaining()) {
            sc.write(buf);
        }

        state = 0;
        sk.interestOps(SelectionKey.OP_READ);
        sk.selector().wakeup();
    }

    void process(String str) {
        // do process(decode, compute  encode)
    }
}