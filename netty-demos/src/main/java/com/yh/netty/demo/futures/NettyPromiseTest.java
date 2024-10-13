package com.yh.netty.demo.futures;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyPromiseTest {

    public static void main(String[] args) throws Exception {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);
        EventLoop eventLoop = eventLoopGroup.next();

        // 主动创建Promise
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);

        new Thread(() -> {
            log.info("exec start");
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 向Promise中填充结果
            promise.setSuccess(100);
        }, "exec").start();

        log.info("waiting result");
        log.info("result is {}", promise.get());
    }
}
