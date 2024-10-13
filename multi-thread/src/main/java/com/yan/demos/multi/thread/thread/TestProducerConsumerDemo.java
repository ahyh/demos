package com.yan.demos.multi.thread.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * 测试生产者消费者
 */
@Slf4j
public class TestProducerConsumerDemo {

    public static void main(String[] args) {
        testProduceConsume();
    }

    public static void testProduceConsume() {
        MessageQueue queue = new MessageQueue(2);
        for (int i = 0; i <= 3; i++) {
            int id = i;
            new Thread(() -> {
                try {
                    queue.put(new Message(id, "message" + id));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "producer" + i).start();
        }

        new Thread(() -> {
            try {
                while (true) {
                    TimeUnit.SECONDS.sleep(1);
                    Message message = queue.take();
                    log.info("message id {}, data {}", message.getId(), message.getData());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "consumer").start();
    }
}

/**
 * 线程间通信的Queue
 */
@Slf4j
class MessageQueue {

    private static final LinkedList<Message> list = new LinkedList<>();
    private int capacity;

    public MessageQueue(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 取一个消息
     */
    public Message take() throws InterruptedException {
        synchronized (list) {
            while (list.isEmpty()) {
                log.info("queue empty, take wait");
                list.wait();
            }
            // 退出等待，从队列头部获取消息
            Message message = list.removeFirst();
            // 唤醒其他线程
            list.notifyAll();
            return message;
        }
    }

    public void put(Message message) throws InterruptedException {
        synchronized (list) {
            // 如果队列满了，要等待
            while (list.size() == capacity) {
                log.info("queue full, put wait");
                list.wait();
            }
            // 从尾部加上
            log.info("queue not full, put success");
            list.addLast(message);
            // 唤醒其他线程
            list.notifyAll();
        }
    }

}

class Message {

    private int id;
    private Object data;

    public Message(int id, Object data) {
        this.id = id;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public Object getData() {
        return data;
    }
}