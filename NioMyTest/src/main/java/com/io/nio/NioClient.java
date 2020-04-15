package com.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * NIO客户端测试
 * @author mh
 * @date 2020-04
 */
public class NioClient {

    public static void main(String[] args) throws Exception {
        //得到一个网络通道
        SocketChannel socketChannel=SocketChannel.open();
        //设置非阻塞式
        socketChannel.configureBlocking(false);
        //提供服务器ip与端口
        InetSocketAddress inetSocketAddress=new InetSocketAddress("127.0.0.1",8989);
        //连接服务器端
        if(!socketChannel.connect(inetSocketAddress)){
            while(!socketChannel.finishConnect()){
                System.out.println("没连上");
            }
        }
        new Thread(new MyRunble(socketChannel)).start();
        while (true){
            ByteBuffer buffer=ByteBuffer.allocate(1024);
            int read = socketChannel.read(buffer);
            if(read>0){
                System.out.println(new String(buffer.array()));
            }
        }
    }


    static class MyRunble implements  Runnable{
        SocketChannel socketChannel;

        MyRunble(SocketChannel channel){
            this.socketChannel=channel;
        }
        @Override
        public void run() {
            while (true){
                //创建一个buffer对象并存入数据
                Scanner scanner=new Scanner(System.in);
                String message=scanner.nextLine();
                ByteBuffer buffer= ByteBuffer.wrap(message.getBytes());
                //发送数据
                try {
                    socketChannel.write(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
