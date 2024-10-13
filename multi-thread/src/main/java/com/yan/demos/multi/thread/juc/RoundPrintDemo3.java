package com.yan.demos.multi.thread.juc;

import java.util.concurrent.locks.LockSupport;

public class RoundPrintDemo3 {

    private static Thread t1;
    private static Thread t2;
    private static Thread t3;

    public static void main(String[] args) {
        ParkStatus parkStatus = new ParkStatus(5);

        t1 = new Thread(() -> {
            parkStatus.print("a", t2);
        }, "a");

        t2 = new Thread(() -> {
            parkStatus.print("b", t3);
        }, "b");

        t3 = new Thread(() -> {
            parkStatus.print("c", t1);
        }, "c");

        t1.start();
        t2.start();
        t3.start();
        LockSupport.unpark(t1);

    }
}

class ParkStatus {

    private int loopNumber;

    public ParkStatus(int loopNumber) {
        this.loopNumber = loopNumber;
    }

    /**
     * @param str  要打印的内容
     * @param next 需要唤醒的下一个线程
     */
    public void print(String str, Thread next) {
        for (int i = 0; i < loopNumber; i++) {
            // 先阻塞住停下来
            LockSupport.park();
            // 打印
            System.out.print(str);
            // 唤醒下一个线程
            LockSupport.unpark(next);
        }
    }
}
