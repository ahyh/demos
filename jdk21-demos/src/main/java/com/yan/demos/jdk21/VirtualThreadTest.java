package com.yan.demos.jdk21;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * 设置编译环境为JDK-21
 */
public class VirtualThreadTest {

    public static void main(String[] args) {
        try {
            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
            IntStream.range(0, 10_000).forEach(i -> {
                executor.submit(() -> {
                    Thread.sleep(Duration.ofSeconds(1));
                    return i;
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
