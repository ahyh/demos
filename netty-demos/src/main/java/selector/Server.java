package selector;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

@Slf4j
public class Server {

    private static void split(ByteBuffer buffer) {
        buffer.flip();
        for (int i = 0; i < buffer.limit(); i++) {
            // 读取到一条完整的消息
            if (buffer.get(i) == '\n') {
                // 把完整的消息存入新的ByteBuffer
                int len = i + 1 - buffer.position();
                ByteBuffer target = ByteBuffer.allocate(len);
                for (int j = 0; j < len; j++) {
                    target.put(buffer.get());
                }
                target.flip();
                log.debug(new String(target.array(), 0, target.remaining()));
            }
        }
        // 未读的部分向前移动，等待和下次读取的内容合并
        buffer.compact();
    }

    public static void main(String[] args) throws Exception {
        // 1- 创建Selector
        Selector selector = Selector.open();
        // 2- 获取通道
        ServerSocketChannel ssc = ServerSocketChannel.open();
        // 3- 设置为非阻塞模式
        ssc.configureBlocking(false);
        // 4- bind port
        ssc.bind(new InetSocketAddress(8080));
        // 5- 将ssc注册到selector, 并且指定监听accept事件
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        // 6- 轮询Selector上准备就绪的事件
        while (selector.select() > 0) {
            System.out.println("select");
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // 事件需要移除
                iterator.remove();
                log.debug("key: {}", key);
                if (key.isAcceptable()) {
                    // 如果是accept事件，获取socketChannel
                    SocketChannel socketChannel = ssc.accept();
                    // 设置为非阻塞
                    socketChannel.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    // 将socketChannel注册到selector上, 带上attachment, 将bytebuffer作为附件关联到selectionKey
                    socketChannel.register(selector, SelectionKey.OP_READ, buffer);
                } else if (key.isReadable()) {
                    try {
                        // 可读事件，获取SocketChannel
                        SocketChannel channel = (SocketChannel) key.channel();
                        // 定义缓冲区
                        ByteBuffer attachmentBuffer = (ByteBuffer) key.attachment();
                        int read = channel.read(attachmentBuffer);
                        if (read == -1) {
                            key.cancel();
                        } else {
                            split(attachmentBuffer);
                            if (attachmentBuffer.position() == attachmentBuffer.limit()) {
                                // 需要扩容
                                ByteBuffer newBuffer = ByteBuffer.allocate(attachmentBuffer.capacity() * 2);
                                // copy old buffer data to new buffer
                                attachmentBuffer.flip();
                                newBuffer.put(attachmentBuffer);
                                key.attach(newBuffer);
                            }
                        }
                    } catch (Exception e) {
                        log.error("error : {}", e);
                        key.cancel();
                    }
                }
            }
        }
    }
}
