# AQS

AbstractQueuedSynchronizer, 是阻塞式锁和相关的同步器工具的框架

特点：

* 用state属性来表示资源的状态（分独占模式和共享模式），子类需要定义如何维护这个状态，控制如何获取和释放锁
  * getState - 获取state属性
  * setStatue - 设置state属性
  * compareAndSetState - 乐观锁机制设置state状态
  * 独占模式只能有一个线程能访问资源，而共享模式可以允许多个线程访问资源
* 提供了基于FIFO的等待队列，类似与Monitor的EntryList
* 条件变量来实现等待、唤醒机制，支持多个条件变量，类似与Monitor的waitSet