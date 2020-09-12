package client;

import handler.NioClientHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * @author zhongwen Xu
 * @data 2020/9/12 22:35 Nio客户端
 **/
public class NioClient{
    private static final String hostName = "127.0.0.1";
    private static final int port = 8008;

    public void start(String nickName) throws IOException {
        //连接服务器
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(hostName, port));
        System.out.println("client start success");

        //开线程 接受服务器回包数据
        Selector selector = Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        new Thread(new NioClientHandler(selector)).start();

        //向服务器发送数据
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            String request = scanner.nextLine();
            if(request!=null && request.length()>0){
                socketChannel.write(Charset.forName("UTF-8").encode(nickName+":"+request));
            }
        }
    }



    public static void main(String[]args) throws IOException {
        NioClient client = new NioClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new NioClient().start("a");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new NioClient().start("b");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
