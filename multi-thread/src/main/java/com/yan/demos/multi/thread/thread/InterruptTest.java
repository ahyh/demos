package com.yan.demos.multi.thread.thread;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InterruptTest {

    public static void main(String[] args) throws InterruptedException {
        TwoPhraseTermination twoPhraseTermination = new TwoPhraseTermination();
        twoPhraseTermination.start();
        Thread.sleep(3500);
        twoPhraseTermination.stop();
    }

}

/**
 * 两阶段终止
 */
@Slf4j
class TwoPhraseTermination {

    /**
     * 监控现场
     */
    private Thread monitor;

    private volatile boolean stopFlag = false;

    private boolean started = false;

    public void start() {
        synchronized (this) {
            if (started) {
                return;
            }
            started = true;
        }

        // 启动监控线程的逻辑可以防止synchronized外面
        monitor = new Thread(() -> {
            while (true) {
                Thread thread = Thread.currentThread();
                // 这个地方也可以判断打断标记，设置一个
//                if (stopFlag) {
                if (thread.isInterrupted()) {
                    log.info("after interrupt");
                    break;
                }

                try {
                    Thread.sleep(1000);
                    log.info("execute monitor");
                } catch (InterruptedException e) {
                    // 抛出异常之后，线程的打断标记会被清除
                    log.error("sleep interrupt");
                    // 重新设置打断标记
                    thread.interrupt();
                }
            }
        });
        monitor.start();
    }

    public void stop() {
        // 设置打断标记，因为是volatile修饰的，监控线程可以立即感知到
        stopFlag = true;
        monitor.interrupt();
    }
}