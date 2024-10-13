package com.yh.netty.demo.futures;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class JdkFutruesTest {

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Future<Integer> future = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.info("execute callable");
                Thread.sleep(1000);
                return 50;
            }
        });

        log.info("waiting result");
        Integer result = future.get();
        log.info("result is: {}", result);
    }
}
