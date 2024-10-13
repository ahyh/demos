package com.yan.demos.multi.thread.juc;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RoundPrintDemo2 {

    public static void main(String[] args) {
        AwaitSignal awaitSignal = new AwaitSignal(5);
        Condition a = awaitSignal.newCondition();
        Condition b = awaitSignal.newCondition();
        Condition c = awaitSignal.newCondition();

        new Thread(() -> {
            awaitSignal.print("a", a, b);
        }, "a").start();

        new Thread(() -> {
            awaitSignal.print("b", b, c);
        }, "b").start();

        new Thread(() -> {
            awaitSignal.print("c", c, a);
        }, "c").start();


        awaitSignal.lock();
        try {
            a.signal();
        } finally {
            awaitSignal.unlock();
        }

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println();
    }
}

class AwaitSignal extends ReentrantLock {

    private int loopNumber;

    public AwaitSignal(int loopNumber) {
        this.loopNumber = loopNumber;
    }

    /**
     * @param str     要打印的内容
     * @param current 在哪一个condition等待
     * @param next    下一个休息室
     */
    public void print(String str, Condition current, Condition next) {
        for (int i = 0; i < loopNumber; i++) {
            lock();
            try {
                current.await();
                System.out.print(str);
                next.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                unlock();
            }
        }
    }
}
