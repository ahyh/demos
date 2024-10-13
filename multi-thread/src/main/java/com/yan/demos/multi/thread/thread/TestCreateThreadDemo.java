package com.yan.demos.multi.thread.thread;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.Test")
public class TestCreateThreadDemo {

    public static void main(String[] args) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                log.info("running");
            }
        });

        thread.start();

        log.info("running");
    }
}
