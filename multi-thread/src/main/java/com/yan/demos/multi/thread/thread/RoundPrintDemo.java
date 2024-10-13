package com.yan.demos.multi.thread.thread;

import java.util.concurrent.TimeUnit;

/**
 * 测试交替输出
 */
public class RoundPrintDemo {

    public static void main(String[] args) {
        WaitStatus waitStatus = new WaitStatus(1, 5);
        new Thread(() -> {
            waitStatus.loopPrint("a", 1, 2);
        }, "a").start();

        new Thread(() -> {
            waitStatus.loopPrint("b", 2, 3);
        }, "b").start();

        new Thread(() -> {
            waitStatus.loopPrint("c", 3, 1);
        }, "c").start();

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println();
    }
}

class WaitStatus {
    // 等待标记
    private int flag;
    private int loopNumber;

    public WaitStatus(int flag, int loopNumber) {
        this.flag = flag;
        this.loopNumber = loopNumber;
    }

    public void loopPrint(String str, int waitFlag, int nextFlag) {
        for (int i = 0; i < loopNumber; i++) {
            printNumber(str, waitFlag, nextFlag);
        }
    }

    public void printNumber(String str, int waitFlag, int nextFlag) {
        synchronized (this) {
            while (flag != waitFlag) {
                try {
                    // 条件不一致等待
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.print(str);
            flag = nextFlag;
            this.notifyAll();
        }
    }

}
