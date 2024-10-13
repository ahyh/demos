package com.yan.demos.multi.thread.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 可重入锁
 */
@Slf4j
public class ReentrantLockDemo {

    /**
     * 可重入锁
     */
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition condition1 = lock.newCondition();
    private static boolean t1Ready = false;
    private static final Condition condition2 = lock.newCondition();
    private static boolean t2Ready = false;

    public static void main(String[] args) {
        // 测试锁重入
        // method1();

        // 测试可打断
        // testInterrupt();

        // 测试尝试获取锁
        // testTryLock();

        // 测试带超时限制的等待
        // testTimeout2();

        // 测试condition
        testCondition();
    }

    private static void method1() {
        lock.lock();
        try {
            log.info("invoke method1");
            method2();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private static void method2() {
        lock.lock();
        try {
            log.info("invoke method2");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }


    private static void testInterrupt() {
        Thread thread = new Thread(() -> {
            try {
                // 如果没有竞争可以获取锁，如果有竞争就阻塞等待，但是可以被其他线程调用interrupt方法打断
                log.info("尝试获取锁");
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error("没有获取到锁");
                return;
            }
            try {
                log.info("获取到锁");
            } finally {
                lock.unlock();
            }
        }, "t1");

        // 主线程先获取锁，thread线程进入阻塞队列
        lock.lock();

        thread.start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("打断thread");
        thread.interrupt();
    }

    /**
     * 如果获取不到锁，直接返回false
     */
    private static void testTryLock() {
        Thread t1 = new Thread(() -> {
            // 尝试获取锁
            log.info("尝试获取锁");
            if (!lock.tryLock()) {
                log.info("没有获取到锁");
                return;
            }
            try {
                log.info("获取到锁");
            } finally {
                lock.unlock();
            }

        }, "t1");

        // 主线程先获取锁，t1线程获取不到锁，t1获取不到锁直接返回
        lock.lock();
        t1.start();
    }

    /**
     * 带超时时间的等待
     */
    private static void testTimeout2() {
        Thread t1 = new Thread(() -> {
            // 尝试获取锁
            log.info("尝试获取锁");
            try {
                if (!lock.tryLock(2, TimeUnit.SECONDS)) {
                    log.info("没有获取到锁");
                    return;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error("获取不到锁");
                return;
            }
            try {
                log.info("获取到锁");
            } finally {
                lock.unlock();
            }

        }, "t1");

        // 主线程先获取锁，t1线程获取不到锁，t1获取不到锁直接返回
        log.info("获取到锁");
        lock.lock();
        t1.start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 主线程释放锁，t1就可以获取锁
        lock.unlock();
    }

    /**
     * 1- condition的await操作需要先获取锁
     * 2- condition的await操作后，会释放锁，进入conditionObject等待
     * 3- await的线程被唤醒（打断获取超时）去重新竞争锁
     * 4- 竞争锁成功后从await之后的code重新执行
     */
    private static void testCondition() {
        Thread t1 = new Thread(() -> {
            lock.lock();
            try {
                log.info("t1 ready? {}", t1Ready);
                while (!t1Ready) {
                    // t1没有ready的话，进入condition1等待
                    log.info("t1 not ready");
                    try {
                        condition1.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.info("t1 ready, start");
            } finally {
                lock.unlock();
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            lock.lock();
            try {
                log.info("t2 ready? {}", t2Ready);
                while (!t2Ready) {
                    // t2没有ready的话，进入condition2等待
                    log.info("t2 not ready");
                    try {
                        condition2.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.info("t2 ready, start");
            } finally {
                lock.unlock();
            }
        }, "t2");

        t1.start();
        t2.start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // t1 ready线程，重新设置t1Ready=true, 然后唤醒t1的await继续往下执行
        new Thread(() -> {
            lock.lock();
            try {
                log.info("t1 ready, signal");
                t1Ready = true;
                condition1.signal();
            } finally {
                lock.unlock();
            }
        }, "t1 ready").start();


        // t2 ready线程，重新设置t2Ready=true, 然后唤醒t2的await继续往下执行
        new Thread(() -> {
            lock.lock();
            try {
                log.info("t2 ready, signal");
                t2Ready = true;
                condition2.signal();
            } finally {
                lock.unlock();
            }
        }, "t2 ready").start();
    }
}
