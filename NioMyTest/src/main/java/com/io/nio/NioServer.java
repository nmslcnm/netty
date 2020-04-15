package com.io.nio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * NIO服务端测试
 * @author mh
 * @date 2020-04
 */
public class NioServer {

    public static void main(String[] args) throws IOException {

        //得到serverSocketChannel服务端Socket通道对象
        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
        //得到Selector选择器对象
        Selector selector=Selector.open();
        //绑定端口
        serverSocketChannel.bind(new InetSocketAddress(8989));
        //设置非阻塞方式
        serverSocketChannel.configureBlocking(false);
        //将ServerSocketChannel注册给Selector，并监听连接
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        int count=0;
        long start=System.nanoTime();
        long timeout=2000;
        while (true){
            selector.select(timeout);
            long end=System.nanoTime();
            if(end-start>= TimeUnit.MILLISECONDS.toNanos(timeout)){
                count=1;
            }else{
                count++;
            }

            if(count>=10){
                System.out.println("有可能发生空轮询"+count+"次");
                count=0;
                selector.selectNow();
                continue;
            }
            try {
                //得到SelectionKey对象，判断是事件
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
//                System.out.println(selectionKeys.size());
//                System.out.println(selector.keys().size());
                for (SelectionKey selectionKey : selectionKeys) {
                    if(selectionKey.isAcceptable()){     //连接事件
                        System.out.println("hello connection");
                        //获取客户端网络通道
                        SocketChannel clientSocket = serverSocketChannel.accept();
                        //设置非阻塞方式
                        clientSocket.configureBlocking(false);
                        //连接上了，注册读取事件
                        clientSocket.register(selector,SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                    }
                    if(selectionKey.isReadable()){     //读取数据事件
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        ByteBuffer buffer=ByteBuffer.allocate(1024);
                        int read = socketChannel.read(buffer);
                        if(read>0){
                            System.out.println(new String(buffer.array()));
                        }
                        selectionKey.interestOps(SelectionKey.OP_WRITE);
                    }
                    if(selectionKey.isWritable()){ //写数据事件
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        Scanner scanner=new Scanner(System.in);
                        String message=scanner.nextLine();
                        ByteBuffer byteBuffer= ByteBuffer.wrap(message.getBytes());
                        socketChannel.write(byteBuffer);
                        selectionKey.interestOps(SelectionKey.OP_READ);
                    }
                    selectionKeys.remove(selectionKey);
                }
            }catch (Exception e){
                System.out.println(e);
            }
         }

    }
}
