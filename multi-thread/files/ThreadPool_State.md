# ThreaPool 

## ThreadPool State

*   RUNNING:  Accept new tasks and process queued tasks
*   SHUTDOWN: Don't accept new tasks, but process queued tasks
*   STOP:     Don't accept new tasks, don't process queued tasks,  and interrupt in-progress tasks
*   TIDYING:  All tasks have terminated, workerCount is zero, the thread transitioning to state TIDYING will run the terminated() hook method
*   TERMINATED: terminated() has completed



ThreadPoolExecutor Constructor Method

/**

* corePoolSize:  the number of threads to keep in the pool, even

* maximumPoolSize: the maximum number of threads to allow in the pool

* keepAliveTime: when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating.

* Timeunit : the time unit for the {@code keepAliveTime} argument

* workQueue: the queue to use for holding tasks before they are executed.  This queue will hold only the {@code Runnable} tasks submitted by the {@code execute} method.

* threadFactory: the factory to use when the executor creates a new thread

* com.yh.netty.demo.handler : the com.yh.netty.demo.handler to use when execution is blocked because the thread bounds and queue capacities are reached

  */
  public ThreadPoolExecutor(int corePoolSize,
  int maximumPoolSize,
  long keepAliveTime,
  TimeUnit unit,
  BlockingQueue<Runnable> workQueue,
  ThreadFactory threadFactory,
  RejectedExecutionHandler com.yh.netty.demo.handler)



