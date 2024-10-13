package com.yh.netty.demo.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * 多线程池版的NIO
 */
@Slf4j
public class ThreadPoolServer {

    public static void main(String[] args) throws Exception {
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress("localhost", 8080));
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        Worker worker = new Worker("worker-0");
        // 6- 轮询Selector上准备就绪的事件
        while (selector.select() > 0) {
            System.out.println("select");
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    // 新的客户端连接
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    log.info("before register");
                    worker.register(sc);
                    log.info("after register");
                }
                // 处理完成的事件需要移除
                iterator.remove();
            }
        }
    }
}
