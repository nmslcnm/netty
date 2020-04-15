package com.io.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 聊天室服务端
 * @author mh
 * @date 2020-04-14
 */
public class ChatService {

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private long timeout=2000;

    public ChatService(){
        try {
            serverSocketChannel=ServerSocketChannel.open();
            selector=Selector.open();
            serverSocketChannel.bind(new InetSocketAddress(8989));
            serverSocketChannel.configureBlocking(false);
            SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        new ChatService().start();
    }

    public void start() throws Exception{
        int count=0;
        long start=System.nanoTime();
        while (true){

            //空轮询解决
            selector.select(timeout);
            long end=System.nanoTime();
            if(end-start>= TimeUnit.MILLISECONDS.toNanos(timeout)){
                count=1;
            }else{
                count++;
            }
            if(count>=10){
                System.out.println("有可能发生空轮询"+count+"次");
                rebuildSelector();
                count=0;
                selector.selectNow();
                continue;
            }

            //得到SelectionKey对象，判断是事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey selectionKey=iterator.next();
                if(selectionKey.isAcceptable()){     //连接事件
                    SocketChannel accept = serverSocketChannel.accept();
                    accept.configureBlocking(false);
                    accept.register(selector,SelectionKey.OP_READ);
                    System.out.println(accept.getRemoteAddress().toString()+"上线了");
                }
                if(selectionKey.isReadable()){     //读取数据事件
                    readClientData(selectionKey);
                }
                //本次删除运行完的对象
                iterator.remove();
            }
        }
    }

    private void rebuildSelector() throws IOException {
        Selector newSelector=Selector.open();
        Selector oldSelect=selector;
        for (SelectionKey selectionKey : oldSelect.keys()) {
            int i = selectionKey.interestOps();
            selectionKey.cancel();
            selectionKey.channel().register(newSelector,i);
        }
        selector=newSelector;
        oldSelect.close();
    }

    //读取客户端发来的数据
    private void readClientData(SelectionKey selectionKey) throws IOException {
        System.out.println("来啦，老弟......");
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int read = socketChannel.read(byteBuffer);
        byteBuffer.flip();
        if(read>0){
            byte[] bytes=new byte[read];
            byteBuffer.get(bytes,0,read);
            //读取了数据  广播
            String s = new String(bytes,"utf-8");
            writeClientData(socketChannel,s);
        }
    }

    //广播
    private void writeClientData(SocketChannel socketChannel,String s) throws IOException {
        Set<SelectionKey> keys = selector.keys();
        for (SelectionKey key : keys) {
            if(key.isValid()){
                SelectableChannel channel = key.channel();
                if(channel instanceof  SocketChannel){
                    SocketChannel socketChannel1= (SocketChannel) channel;
                    if(channel!=socketChannel){
                        ByteBuffer wrap = ByteBuffer.wrap(s.getBytes());
                        socketChannel1.write(wrap);
                    }
                }
            }
        }
    }

}
