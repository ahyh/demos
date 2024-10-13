package com.yan.demos.multi.thread.thread;

import com.yan.demos.multi.thread.util.DownloadUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 保护性暂停，一个线程等待另一个线程的结果
 * 使用wait notify来交互两个线程，一个线程等待结果，一个线程计算然后通知等待线程
 */
@Slf4j
public class TestGuardedObject {

    public static void main(String[] args) throws Exception {
        // testGuardedObject();
        testPostMails();
    }

    public static void testPostMails() throws InterruptedException {
        for (int i = 0; i <= 3; i++) {
            // 开启4个线程等待接收信件
            new People().start();
        }
        TimeUnit.SECONDS.sleep(2);
        Set<Integer> ids = MailBox.getIds();
        for (Integer id : ids) {
            new Postman(id, "Hello" + id + " you have a mail").start();
        }
    }

    /**
     * 测试保护性暂停，一个线程等待另一个线程完成计算并唤醒
     */
    public static void testGuardedObject() {
        GuardedObject guardedObject = new GuardedObject();
        new Thread(() -> {
            // 等待结果
            // List<String> data = (List<String>) guardedObject.getData();

            // 最多等200ms
            List<String> data = (List<String>) guardedObject.getData(200);
            log.info("data size {}", data.size());
        }, "t1").start();

        new Thread(() -> {
            // 执行操作
            try {
                log.info("start download");
                List<String> download = DownloadUtil.download();
                guardedObject.complete(download);
                log.info("end download");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "t2").start();
    }
}

// 居民收信
@Slf4j
class People extends Thread {

    @Override
    public void run() {
        GuardedObject guardedObject = MailBox.createGuardedObject();
        log.info("before receive mail");
        Object data = guardedObject.getData(5000);
        log.info("after received mail");
        log.info("data {}", data);
    }
}

// 邮递员投递信件, 一个邮递员投递一个居民的信件
@Slf4j
class Postman extends Thread {

    private int id;
    private String mail;

    public Postman(int id, String mail) {
        this.id = id;
        this.mail = mail;
    }

    @Override
    public void run() {
        GuardedObject guardedObject = MailBox.getGuardedObject(id);
        if (guardedObject != null) {
            // 产生邮件内容
            log.info("before send mail");
            guardedObject.complete(mail);
            log.info("after send mail");
        }
    }
}

class MailBox {

    private static int id = 1;
    private static final Map<Integer, GuardedObject> boxes = new ConcurrentHashMap<>();

    public synchronized static int generateId() {
        return id++;
    }

    public static GuardedObject createGuardedObject() {
        GuardedObject guardedObject = new GuardedObject();
        guardedObject.setId(generateId());
        boxes.put(guardedObject.getId(), guardedObject);
        return guardedObject;
    }

    // 投递之后就需要删除了
    public static GuardedObject getGuardedObject(int id) {
        return boxes.remove(id);
    }

    public static Set<Integer> getIds() {
        return boxes.keySet();
    }
}

/**
 * 保护性暂停，用于一个线程等待另一个线程的结果
 */
class GuardedObject {

    private int id;

    private Object data;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object getData() {
        synchronized (this) {
            while (data == null) {
                try {
                    this.wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    /**
     * 带时限的等待
     *
     * @param timeout 等待的最大时间
     */
    public Object getData(long timeout) {
        synchronized (this) {
            // 记录下等待时间
            long begin = System.currentTimeMillis();
            long passedTime = 0;
            while (data == null) {
                // 经历的时间超过timeout就退出
                long waitTime = timeout - passedTime;
                if (waitTime <= 0) {
                    break;
                }
                try {
                    this.wait(waitTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 经历的时间
                passedTime = System.currentTimeMillis() - begin;
            }
        }
        return data;
    }

    public void complete(Object response) {
        synchronized (this) {
            this.data = response;
            this.notifyAll();
        }
    }
}
