# Thread state switch

线程状态转换

## 1- New -> Runnable

调用Thread.start()



## 2- Runnable <--> Waiting

2.1 - Thread获取对象锁（lock）之后

调用lock.wait(), Thread状态从Runnable -> Waiting

调用lock.notify(), lock.notifyAll(), Thread.interrupt()时

竞争锁（lock）成功, Thread状态: Waiting -> Runnable
竞争锁（lock）失败, Thread状态: Waiting -> Blocked



2.2 - 当前线程调用Thread.join()时, 当前线程状态: Runnable -> Waiting

如果当前Thread运行结束了，当前线程状态:  Waiting -> Runnable



2.3 - 当前线程调用LockSupport.park()时, 当前线程状态: Runnable -> Waiting

如果当前Thread调用LockSupport.unpark()，当前线程状态:  Waiting -> Runnable



## 3- Runnable <--> Timed_Waiting

3.1- Thread获取对象锁（lock）之后

调用lock.wait(long timeout), Thread状态从Runnable -> Timed_Waiting

Thread等待时间超过了timeout, 或者调用lock.notify(), lock.notifyAll(), Thread.interrupt()时

竞争锁（lock）成功, Thread状态: Timed_Waiting -> Runnable
竞争锁（lock）失败, Thread状态: Timed_Waiting -> Blocked



3.2 - 当前线程调用Thread.join()时, 当前线程状态: Runnable -> Timed_Waiting

如果当前线程等待时间超过timeout，或者Thread运行结束了，或者调用了当前线程的interrupt(), 当前线程状态:  Timed_Waiting-> Runnable



3.3 - 当前线程调用了Thead.sleep(long n), 当前线程状态: Runnable -> Timed_Waiting

如果当前线程等待时间超过n, 当前线程状态:  Timed_Waiting-> Runnable



3.4 - 当前线程调用LockSupport.parkNanos(n) 或者LockSupport.parkUntil(mills)时, 当前线程状态: Runnable -> Timed_Waiting

如果当前Thread调用LockSupport.unpark()或者调用了当前线程的interrupt(), 或者等待超时，当前线程状态:  Timed_Waiting -> Runnable



## 4 - Runnable <--> Blocked

4.1- thread用synchronized(lock)获取锁对象时如果竞争失败，从 Runnable -> Blocked

持有Lock锁对象的同步代码块执行完毕后，会唤醒对象上所有的Blocked的线程重新竞争锁对象，如果线程t竞争成功，则线程t的状态从Blocked -> Runnable, 其他竞争失败的线程仍然是Blocked



## 5-  Runnable -> Terminate

当前线程的所有代码执行完毕，Runnable -> Terminate