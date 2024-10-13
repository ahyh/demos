package com.yan.demos.multi.thread.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@Slf4j
public class MyLockDemo {

    public static void main(String[] args) {
        MyLock lock = new MyLock();
        new Thread(()-> {
            lock.lock();
            // 不可重入，如果在lock的话，
            // lock.lock();
            try {
                log.info("lock success");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } finally {
                log.info("unlock success");
                lock.unlock();
            }
        },"t1").start();

        new Thread(()-> {
            lock.lock();
            try {
                log.info("lock success");
            } finally {
                log.info("unlock success");
                lock.unlock();
            }
        },"t2").start();
    }
}

/**
 * 自定义锁，不可重入锁
 */
class MyLock implements Lock {

    /**
     * 需要自己看AQS内部实现
     */
    class MySync extends AbstractQueuedSynchronizer {

        /**
         * 尝试获取锁
         */
        @Override
        protected boolean tryAcquire(int arg) {
            // cas加锁
            if (compareAndSetState(0, 1)) {
                // 设置锁的持有者
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            // 防止指令重排序，不能改变顺序
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        /**
         * 是否持有独占锁
         */
        @Override
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        public Condition newCondition() {
            return new ConditionObject();
        }
    }

    private MySync sync = new MySync();

    @Override
    public void lock() {
        sync.acquire(1);
    }

    /**
     * 加锁，可打断
     */
    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    /**
     * 尝试加锁
     */
    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    /**
     * 带超时时间的尝试加锁
     */
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    /**
     * 解锁
     */
    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }

}
