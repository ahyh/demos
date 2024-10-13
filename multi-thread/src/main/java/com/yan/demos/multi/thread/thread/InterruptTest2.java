package com.yan.demos.multi.thread.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j(topic = "c.Test3")
public class InterruptTest2 {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            log.info("park");
            LockSupport.park();
            log.info("unpark");

            // isInterrupted方法不会清除打断标记，被打断的线程不能再park
            // interrupted会清除打断标记
            log.info("interrupt status {}", Thread.interrupted());

            LockSupport.park();
            log.info("unpark");
        });

        thread.start();
        sleep(1000);

        thread.interrupt();
    }

    private static void sleep(long time) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(time);
    }

}
