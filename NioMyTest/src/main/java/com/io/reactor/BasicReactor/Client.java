package com.io.reactor.BasicReactor;
      
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *  单Reactor单线程模式
 *  客户端
 * @auto mh
 * @date 2020-04
 */
public class Client {

    public static void main(String[] args) {
        String hostname="127.0.0.1";
        int port = 8980;
        try {
            Socket client = new Socket(hostname, port);
            System.out.println("连接到的ip:"+ hostname);
            PrintWriter out = new PrintWriter(client.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String input;

            while((input=stdIn.readLine()) != null) {
                out.println(input);
                out.flush();
                if(input.equals("exit")) {
                    break;
                }
                System.out.println("server: "+in.readLine());
            }
            client.close();
            System.out.println("client stop");
        } catch (UnknownHostException e) {
        } catch (IOException e) {
        }

    }

}
