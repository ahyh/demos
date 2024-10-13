package com.yan.demos.multi.thread.pool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 单线程线程池，保证池中始终有一个可以使用的线程
 */
@Slf4j
public class SingleThreadPoolDemo {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            int i = 10;
            // 抛出异常
            int j = i / 0;
            log.info("1");
        });

        executorService.execute(() -> {

            log.info("2");
        });

        executorService.execute(() -> {

            log.info("3");
        });
    }
}
