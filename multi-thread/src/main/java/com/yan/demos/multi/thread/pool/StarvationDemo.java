package com.yan.demos.multi.thread.pool;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 测试饥饿现象
 */
@Slf4j
public class StarvationDemo {

    private static final List<String> MENU = Arrays.asList("地三鲜", "红烧肉", "小鸡蘑菇", "小炒肉");
    private static final Random random = new Random();

    public static void main(String[] args) {
        // 测试饥饿现象
        // testStarvation();

        // 测试两个线程池解决饥饿问题
        testDiffPools();
    }

    public static void testDiffPools() {
        // 一个线程池负责wait，一个线程负责cook
        ExecutorService waitPool = Executors.newFixedThreadPool(1);
        ExecutorService cookPool = Executors.newFixedThreadPool(1);

        waitPool.execute(() -> {
            log.info("处理点餐1");
            Future<String> cook = cookPool.submit(() -> {
                log.info("做菜1");
                return cook();
            });
            try {
                log.info("上菜1 {}", cook.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        // 提交第二个任务的时候会发生饥饿，都处理点餐了，没法处理做菜了，这种现象不是死锁
        waitPool.execute(() -> {
            log.info("处理点餐2");
            Future<String> cook = cookPool.submit(() -> {
                log.info("做菜2");
                return cook();
            });
            try {
                log.info("上菜2 {}", cook.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    public static void testStarvation() {
        ExecutorService pool = Executors.newFixedThreadPool(2);

        pool.execute(() -> {
            log.info("处理点餐1");
            Future<String> cook = pool.submit(() -> {
                log.info("做菜1");
                return cook();
            });
            try {
                log.info("上菜1", cook.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        // 提交第二个任务的时候会发生饥饿，都处理点餐了，没法处理做菜了，这种现象不是死锁
        pool.execute(() -> {
            log.info("处理点餐2");
            Future<String> cook = pool.submit(() -> {
                log.info("做菜2");
                return cook();
            });
            try {
                log.info("上菜2", cook.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

    }

    public static String cook() {
        return MENU.get(random.nextInt(MENU.size()));
    }
}
