package com.io.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * 聊天室客户端,可以在IDEA设置多线程parallel运行
 * 方式：Edit Configuration勾选allow parallel run
 * @author mh
 * @date 2020-04
 */
public class ChatClient implements  Runnable{

    private SocketChannel socketChannel;

    private Selector selector;

    public ChatClient(){
        try {
            socketChannel=SocketChannel.open();
            selector=Selector.open();
            socketChannel.configureBlocking(false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void doCon(){
        InetSocketAddress inetSocketAddress=new InetSocketAddress("127.0.0.1",8989);
        try {
            if(socketChannel.connect(inetSocketAddress)){
                socketChannel.register(selector,SelectionKey.OP_READ);
                //写数据
                writeData(socketChannel);
            }else{
                socketChannel.register(selector, SelectionKey.OP_CONNECT);//如果连接不上
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeData(SocketChannel socketChannel) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){
                        Scanner scanner=new Scanner(System.in);
                        String str = scanner.nextLine();
                        if(str.equals("end")){
                            socketChannel.close();
                            return;
                        }
                        ByteBuffer byteBuffer=ByteBuffer.wrap((socketChannel.getLocalAddress().toString()+"说："+str).getBytes());
                        socketChannel.write(byteBuffer);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void readData() throws IOException {
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
        int read = socketChannel.read(byteBuffer);
        if(read>0){
            byte[] array = byteBuffer.array();
            System.out.println(new String(array,"utf-8"));
        }
    }


    public static void main(String[] args) throws IOException {
        new Thread(new ChatClient()).start();
    }

    @Override
    public void run() {
        doCon();
        try {
            while (true){
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    if(selectionKey.isValid()){
                        if(selectionKey.isConnectable()){
                            SocketChannel channel = (SocketChannel) selectionKey.channel();
                            if (channel.finishConnect()){
                                channel.register(selector,SelectionKey.OP_READ);
                                System.out.println("bbbbbbbbbbbbb");
                                //写数据
                                writeData(channel);
                            }else{
                                System.exit(1);
                            }
                        }
                        if(selectionKey.isReadable()){
                            readData();
                        }
                    }
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
