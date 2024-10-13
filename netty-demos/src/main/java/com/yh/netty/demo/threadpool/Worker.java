package com.yh.netty.demo.threadpool;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Worker implements Runnable {

    private Thread thread;
    private Selector selector;
    private String name;
    private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

    private boolean started;

    /**
     * 构造器方法设置worker name
     */
    public Worker(String name) {
        this.name = name;
    }

    public void register(SocketChannel socketChannel) throws IOException {
        if (!started) {
            // start thread
            this.selector = Selector.open();
            this.thread = new Thread(this, this.name);
            this.thread.start();
            started = true;
        }
        // input a Runnable into queue
        queue.add(() -> {
            try {
                socketChannel.register(this.selector, SelectionKey.OP_READ, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // wake up the block com.yh.netty.demo.selector
        this.selector.wakeup();
    }

    @Override
    public void run() {
        while (true) {
            try {
                // block method, if has no IO events happen, will block here when wakeup
                this.selector.select();
                // poll a Runnable and execute
                Runnable task = queue.poll();
                if (task != null) {
                    // register key to com.yh.netty.demo.selector
                    task.run();
                }
                Iterator<SelectionKey> iterator = this.selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isReadable()) {
                        // if readable event happen, read data from channel
                        SocketChannel channel = (SocketChannel) key.channel();
                        // 定义缓冲区
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        // 将通道中的数据读入缓冲区中
                        int len = 0;
                        while ((len = channel.read(buffer)) > 0) {
                            // 切换到读模式
                            buffer.flip();
                            System.out.println(new String(buffer.array(), 0, len));
                            buffer.clear();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
