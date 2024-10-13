package com.yan.demos.multi.thread.pool;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 自定义的线程池，方便理解JDK自带的线程池
 */
@Slf4j
public class ThreadPoolDemo {

    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool(3, 1, TimeUnit.SECONDS, 5, ((queue, task) -> {
            // 1- 阻塞，等待队列不满的时候添加task
            // queue.put(task);

            // 2- 带超时阻塞
            // queue.offer(task, 1, TimeUnit.SECONDS);

            // 3- 丢弃任务
            // log.info("discard task");

            // 4- 让调用者抛出异常, 抛出异常后之后的task不会在添加queue了
            // throw new RuntimeException("failure to add task");

            // 5- 让调用者自己执行任务
            ((Runnable) task).run();
        }));
        for (int i = 0; i < 15; i++) {
            int j = i;
            threadPool.execute(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.info("execute for {}", j);
            });
        }
    }
}

/**
 * 拒绝策略
 */
interface RejectPolicy<T> {

    void reject(BlockingQueue<T> queue, T task);


}

@Slf4j
class ThreadPool {

    private BlockingQueue<Runnable> queue;

    private Set<Worker> workers = new HashSet<>();

    /**
     * 核心线程数
     */
    private int coreSize;

    /**
     * 获取任务超时时间
     */
    private long timeout;

    private TimeUnit timeUnit;

    private RejectPolicy rejectPolicy;

    public ThreadPool(int coreSize, long timeout, TimeUnit timeUnit, int capacity, RejectPolicy rejectPolicy) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.rejectPolicy = rejectPolicy;
        this.queue = new BlockingQueue<>(capacity);
    }

    public void execute(Runnable task) {
        // 当任务数没有超过coreSize，直接交给新建worker执行
        // 如果超过了coreSize时，交给任务队列暂存起来
        synchronized (workers) {
            if (workers.size() < coreSize) {
                log.info("new worker");
                Worker worker = new Worker(task);
                workers.add(worker);
                worker.start();
            } else {
                log.info("task add to queue");
                // queue.put(task);
                rejectPolicy.reject(queue, task);
            }
        }
    }

    class Worker extends Thread {

        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        // 执行任务
        @Override
        public void run() {
            // 执行任务，如果创建的时候初始化的任务不为空，执行初始化的任务
            // 从queue中获取任务并执行, 带超时时间
            while (task != null || (task = queue.poll(5, TimeUnit.SECONDS)) != null) {
                try {
                    log.info("task run");
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    task = null;
                }
            }
            // 如果退出循环，就需要将worker从集合中移除
            synchronized (workers) {
                log.info("work remove");
                workers.remove(this);
            }
        }
    }

}

@Slf4j
class BlockingQueue<T> {

    /**
     * 任务队列
     */
    private Deque<T> deque = new ArrayDeque<>();

    /**
     * 锁
     */
    private ReentrantLock lock = new ReentrantLock();

    /**
     * 生产者条件变量，当队列满的时候需要等待
     */
    private Condition fullWaitSet = lock.newCondition();

    /**
     * 消费者条件变量，当队列空的时候需要等待
     */
    private Condition emptyWaitSet = lock.newCondition();

    /**
     * 容量
     */
    private int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 带超时的阻塞获取
     *
     * @return
     */
    public T poll(long timeout, TimeUnit timeUnit) {
        lock.lock();
        try {
            // 转换为纳秒
            long nanos = timeUnit.toNanos(timeout);
            while (deque.isEmpty()) {
                try {
                    if (nanos <= 0) {
                        // 如果超时了，返回null, 无需永久的等待
                        return null;
                    }
                    // 返回剩余的等待时间
                    nanos = emptyWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 元素需要从队列中移除
            T t = deque.removeFirst();
            // 唤醒fullWaitSet
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 阻塞获取
     */
    public T take() {
        lock.lock();
        try {
            // 如果队列为空就等待
            while (deque.isEmpty()) {
                try {
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 元素需要从队列中移除
            T t = deque.removeFirst();
            // 唤醒fullWaitSet
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 阻塞添加
     */
    public void put(T t) {
        lock.lock();
        try {
            // 如果队列已满就等待
            if (deque.size() == capacity) {
                try {
                    log.info("wait for add to queue");
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            deque.addLast(t);
            log.info("add task to queue");
            // 添加完元素之后需要唤醒
            emptyWaitSet.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 带超时时间的添加任务
     */
    public boolean offer(T task, long timeout, TimeUnit timeUnit) {
        lock.lock();
        try {
            long nanos = timeUnit.toNanos(timeout);
            // 如果队列已满就等待
            if (deque.size() == capacity) {
                try {
                    log.info("wait for add to queue");
                    if (nanos <= 0) {
                        // 添加失败
                        return false;
                    }
                    nanos = fullWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            deque.addLast(task);
            log.info("add task to queue");
            // 添加完元素之后需要唤醒
            emptyWaitSet.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    public void tryPut(RejectPolicy<T> rejectPolicy, T task) {
        lock.lock();
        try {
            if (deque.size() == capacity) {
                // 如果队列已满，需要调用rejectPolicy
                rejectPolicy.reject(this, task);
            } else {
                // 如果队列没有满，直接添加进queue即可
                deque.addLast(task);
                log.info("add task to queue");
                // 添加完元素之后需要唤醒
                emptyWaitSet.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取队列中任务的数量
     */
    public int count() {
        lock.lock();
        try {
            return deque.size();
        } finally {
            lock.unlock();
        }
    }
}
