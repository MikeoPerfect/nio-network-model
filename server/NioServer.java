package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @author zhongwen Xu
 * @data 2020/9/12 22:14 NIO服务端
 **/
public class NioServer {
    private static final int port = 8008;

    public void start() throws IOException {

        //创建Selector
        Selector selector = Selector.open();

        //通过ServerSocketChannel创建channel通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //channel通道绑定监听端口
        serverSocketChannel.bind(new InetSocketAddress(port));

        //设置channel为非阻塞模式
        serverSocketChannel.configureBlocking(false);

        //将channel注册到selector上，监听链接事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("server start success");

        //循环等待新接入连接

        while (true){
            int readyChannel = selector.select();
            if(readyChannel == 0) continue;

            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            Iterator iterator = selectionKeys.iterator();

            while (iterator.hasNext()){
                SelectionKey selectionKey = (SelectionKey)iterator.next();

                iterator.remove();

                //如果是 接入事件
                if(selectionKey.isAcceptable()){
                    acceptHandler(serverSocketChannel, selector);
                }


                //如果是 可读事件
                if(selectionKey.isReadable()){
                    readHandler(selectionKey, selector);
                }

            }


        }
    }

    /**
     * 广播给其他客户端
     */
    private void broadCast(Selector selector, SocketChannel sourceChannel, String request){
        //获取所有已接入客户端channel
        Set<SelectionKey> selectionKeySet = selector.keys();
        selectionKeySet.forEach(selectionKey -> {
            Channel targetChannel = selectionKey.channel();
            if(targetChannel instanceof SocketChannel && targetChannel != sourceChannel){
                try {
                    ((SocketChannel) targetChannel).write(Charset.forName("UTF-8").encode(request));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 接入事件处理器
     */
    private void acceptHandler(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {

        SocketChannel socketChannel = serverSocketChannel.accept();

        socketChannel.configureBlocking(false);

        socketChannel.register(selector, SelectionKey.OP_READ);

        socketChannel.write(Charset.forName("UTF-8").encode("你与聊天室非好友关系，请注意个人隐私"));
    }

    /**
     * 可读事件处理器
     */
    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        ByteBuffer byteBUffer = ByteBuffer.allocate(1024);

        StringBuilder request = new StringBuilder();

        while (socketChannel.read(byteBUffer) > 0){
            byteBUffer.flip();
            request.append(Charset.forName("UTF-8").decode(byteBUffer));
        }

        socketChannel.register(selector, SelectionKey.OP_READ);

        if(request.length()>0){
            System.out.println("from request::"+request.toString());
            broadCast(selector, socketChannel, request.toString());
        }
    }

    public static void main(String[]args) throws IOException {
        NioServer nioServer = new NioServer();
        nioServer.start();
    }
}
