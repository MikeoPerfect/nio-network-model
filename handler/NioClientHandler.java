package handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @author zhongwen Xu
 * @data 2020/9/12 22:53 接收服务器端数据
 **/
public class NioClientHandler implements Runnable {
    private Selector selector;

    public NioClientHandler(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        //循环等待新接入连接
        try {
            while (true) {
                int readyChannel = selector.select();

                if (readyChannel == 0) continue;

                Set<SelectionKey> selectionKeys = selector.selectedKeys();

                Iterator iterator = selectionKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey selectionKey = (SelectionKey) iterator.next();

                    iterator.remove();

                    //如果是 可读事件
                    if (selectionKey.isReadable()) {
                        readHandler(selectionKey, selector);
                    }

                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * 可读事件处理器
     */
    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        ByteBuffer byteBUffer = ByteBuffer.allocate(1024);

        StringBuilder response = new StringBuilder();
        while (socketChannel.read(byteBUffer) > 0){
            
            byteBUffer.flip();

            response.append(Charset.forName("UTF-8").decode(byteBUffer));
        }
        socketChannel.register(selector, SelectionKey.OP_READ);

        if(response.length()>0){
            System.out.println("response::"+response.toString());
        }
    }

}
