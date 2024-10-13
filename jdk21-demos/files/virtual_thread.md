# Virtual Thread

虚拟线程是轻量级线程，可以显著减少编写、维护和观察高吞吐量并发应用程序的工作量。

虚拟线程支持Thread-Local variables

可以直接使用Thread.Build API来创建虚拟线程，也可以通过Executors.newVirtualThreadPerTaskExecutor()方法来创建虚拟线程



## Virtual Thread的目标

使以简单的每个请求一个线程风格编写的服务器应用程序能够扩展到接近最佳的硬件利用率。

允许使用java.lang.Thread API的现有代码以最小的更改采用虚拟线程。

使用现有JDK工具对虚拟线程进行简单的故障排除、调试和分析。



virtual thread的目标不是要删除线程的传统实现，或者无声地将现有应用程序迁移到使用虚拟线程。

virtual thread的目标不是改变Java的基本并发模型。

在Java语言或Java库中提供新的数据并行结构并不是virtual thread的目标，stream API仍然是并行处理大型数据集的首选方式。



虚拟线程是java.lang.Thread的一个实例，它没有绑定到特定的操作系统线程。相比之下，平台线程是以传统方式实现的java.lang.Thread实例，作为操作系统线程的薄包装。



## Virtual Thread的含义

虚拟线程成本低且数量多，因此不应该使用池:应该为每个应用程序任务创建一个新的虚拟线程。虚拟线程不应该被池化。大多数虚拟线程的寿命都很短，调用栈也很浅，只执行一个HTTP客户机调用或一个JDBC查询。相比之下，平台线程是重量级且昂贵的，因此经常必须池化。它们往往是长寿命的，具有较深的调用堆栈，并且在许多任务之间共享。



现在，JDK中的每个java.lang.Thread实例都是一个平台线程。平台线程在底层操作系统线程上运行Java代码，并在代码的整个生命周期内捕获操作系统线程。平台线程的数量受限于操作系统线程的数量。



虚拟线程是Java .lang. thread的一个实例，它在底层操作系统线程上运行Java代码，但在代码的整个生命周期内不会捕获操作系统线程。这意味着许多虚拟线程可以在同一个操作系统线程上运行它们的Java代码，从而有效地共享它。当平台线程独占宝贵的操作系统线程时，虚拟线程不会。虚拟线程的数量可能比操作系统线程的数量大得多。



虚拟线程是由JDK而不是操作系统提供的线程的轻量级实现。它们是用户模式线程的一种形式，在其他多线程语言中已经取得了成功(例如，Go中的gooutine和Erlang中的processes)。在早期的Java版本中，用户模式线程甚至被称为“绿色线程”，当时操作系统线程还没有成熟和普及。然而，Java的绿色线程都共享一个操作系统线程(M:1调度)，并且最终被平台线程超越，实现为操作系统线程的包装器(1:1调度)。虚拟线程采用M:N调度，即大量的虚拟线程被调度到较少的操作系统线程上运行。



开发人员通常会将应用程序代码从基于传统线程池的ExecutorService迁移到每任务一个虚拟线程的ExecutorService。与任何资源池一样，线程池旨在共享昂贵的资源，但虚拟线程并不昂贵，因此永远不需要将它们合用。



开发人员有时使用线程池来限制对有限资源的并发访问。例如，如果一个服务不能处理超过20个并发请求，那么通过提交到大小为20的线程池的任务向该服务发出所有请求将确保这一点。由于平台线程的高成本使得线程池无处不在，所以这种习惯用法变得普遍，但是不要为了限制并发性而试图将虚拟线程池化。而是使用专门为此目的设计的结构，例如信号量。



结合线程池，开发人员有时使用线程局部变量在共享同一线程的多个任务之间共享昂贵的资源。例如，如果创建数据库连接的成本很高，那么您可以打开它一次，并将其存储在线程本地变量中，以供同一线程中的其他任务稍后使用。如果将代码从使用线程池迁移到每个任务使用一个虚拟线程，那么要小心使用这种习惯用法，因为为每个虚拟线程创建昂贵的资源可能会显著降低性能。更改这些代码以使用替代缓存策略，以便在大量虚拟线程之间有效地共享昂贵的资源。



线程转储是另一种流行的工具，用于对以每个请求一个线程的方式编写的应用程序进行故障排除。不幸的是，JDK的传统线程转储(通过jstack或jcmd获得)提供了一个线程的扁平列表。这适用于数十或数百个平台线程，但不适用于数千或数百万个虚拟线程。因此，我们不会扩展传统的线程转储来包含虚拟线程;相反，我们将在JCMD中引入一种新的线程转储，将虚拟线程与平台线程一起呈现，并以一种有意义的方式进行分组。当程序使用结构化并发时，可以显示线程之间更丰富的关系。

```
$ jcmd <pid> Thread.dump_to_file -format=json <file>
```



## Virtual Thread的调度

JDK的虚拟线程调度程序是一个窃取工作的ForkJoinPool，它以FIFO模式运行。调度程序的并行性是用于调度虚拟线程的平台线程的数量。默认情况下，它等于可用处理器的数量，但可以使用系统属性jdk.virtualThreadScheduler.parallelism进行调优。这个ForkJoinPool不同于普通的池，例如，在并行流的实现中，它以后进先出模式运行。



调度器为其分配虚拟线程的平台线程称为虚拟线程的载体。一个虚拟线程可以在其生命周期内被安排在不同的载体上;换句话说，调度器不维护虚拟线程和任何特定平台线程之间的关联。从Java代码的角度来看，一个正在运行的虚拟线程在逻辑上独立于它当前的载体:



- 虚拟线程无法获取平台线程（Virtual Thread Carrier）的标识。thread . currentthread()返回的值始终是虚拟线程本身。

- Carrier和Virtual Thread的堆栈轨迹是分开的。在Virtual Thread中抛出的异常将不包括Carrier的堆栈帧。线程转储不会在虚拟线程的堆栈中显示运营商的堆栈帧，反之亦然。

- Carrier的线程局部变量对Virtual Thread不可用，反之亦然。




此外，从Java代码的角度来看，虚拟线程和它的载体临时共享一个操作系统线程的事实是不可见的。相比之下，从本机代码的角度来看，虚拟线程和它的载体都运行在同一个本机线程上。因此，在同一虚拟线程上多次调用的本机代码可能在每次调用时观察到不同的操作系统线程标识符。



调度程序目前没有为虚拟线程实现时间共享。时间共享是对已经消耗了分配的CPU时间的线程的强制抢占。虽然当平台线程数量相对较少且CPU利用率为100%时，时间共享可以有效地减少某些任务的延迟，但对于一百万个虚拟线程，时间共享是否同样有效尚不清楚。



## Virtual Thread的执行

要利用虚拟线程，不需要重写程序。虚拟线程不要求或期望应用程序代码显式地将控制权交还给调度器;换句话说，虚拟线程是不合作的。用户代码不能假设虚拟线程如何或何时分配给平台线程，也不能假设平台线程如何或何时分配给处理器内核。



为了在虚拟线程中运行代码，JDK的虚拟线程调度器通过将虚拟线程挂载到平台线程上来分配虚拟线程在平台线程上执行。这使得平台线程成为虚拟线程的载体。稍后，在运行一些代码之后，虚拟线程可以从它的载体卸载。此时，平台线程是空闲的，因此调度器可以在其上挂载不同的虚拟线程，从而使其再次成为载体。



通常，当虚拟线程阻塞I/O或JDK中的其他阻塞操作(如BlockingQueue.take())时，它将卸载。当阻塞操作准备完成时(例如，在套接字上已经接收到字节)，它将虚拟线程提交给调度器，调度器将虚拟线程挂载到载体上以恢复执行。



JDK中的绝大多数阻塞操作都将卸载虚拟线程，从而释放其载体和底层操作系统线程来承担新的工作。但是，JDK中的一些阻塞操作不会卸载虚拟线程，从而阻塞了它的载体和底层操作系统线程。这是因为操作系统级别(例如，许多文件系统操作)或JDK级别(例如，Object.wait())的限制。这些阻塞操作的实现将通过临时扩展调度器的并行性来补偿OS线程的捕获。因此，调度器的ForkJoinPool中的平台线程数量可能会暂时超过可用处理器的数量。调度器可用的最大平台线程数可以通过系统属性jdk.virtualThreadScheduler.maxPoolSize进行调优。



有两种情况下，在阻塞操作期间，虚拟线程无法卸载，因为它被固定在它的载体上:



当它执行同步块或方法中的代码时，或者

当它执行本机方法或外部函数时。

固定不会使应用程序出错，但可能会妨碍其可伸缩性。如果一个虚拟线程在被固定时执行阻塞操作，比如I/O或BlockingQueue.take()，那么它的载体和底层操作系统线程在操作期间被阻塞。长时间的频繁绑定会捕获载波，从而损害应用程序的可伸缩性。



调度器不会通过扩展其并行性来补偿固定。相反，应该通过修改频繁运行的同步块或方法来避免频繁和长时间的固定，并使用java.util.concurrent.locks.ReentrantLock来保护可能长时间的I/O操作。不需要替换不经常使用(例如，仅在启动时执行)或保护内存操作的同步块和方法。一如既往，努力保持锁定策略简单明了。



## Memory use and interaction with garbage collection

虚拟线程的堆栈作为堆栈块对象存储在Java的垃圾收集堆中。堆栈会随着应用程序的运行而增长和收缩，这既是为了提高内存效率，也是为了适应任意深度的堆栈(最多可达JVM配置的平台线程堆栈大小)。这种效率支持大量的虚拟线程，从而保证服务器应用程序中每个请求一个线程的风格的持续可行性。



在上面的第二个例子中，回想一下，假设框架通过创建一个新的虚拟线程并调用handle方法来处理每个请求;即使它在深层调用堆栈的末尾调用handle(在身份验证、事务等之后)，handle本身也会生成多个只执行短期任务的虚拟线程。因此，对于每个具有深调用堆栈的虚拟线程，将有多个具有浅调用堆栈的虚拟线程消耗很少的内存。



通常，虚拟线程所需的堆空间和垃圾收集器活动量很难与异步代码进行比较。一百万个虚拟线程至少需要一百万个对象，但共享一个平台线程池的一百万个任务也是如此。此外，处理请求的应用程序代码通常跨I/O操作维护数据。每个请求线程的代码可以将数据保存在本地变量中，这些变量存储在堆中的虚拟线程堆栈中，而异步代码必须将相同的数据保存在堆对象中，这些堆对象从管道的一个阶段传递到下一个阶段。一方面，虚拟线程所需的堆栈帧布局比紧凑对象更浪费;另一方面，虚拟线程可以在许多情况下(取决于低级GC交互)改变和重用它们的堆栈，而异步管道总是需要分配新对象，因此虚拟线程可能需要更少的分配。总的来说，每个请求一个线程与异步代码的堆消耗和垃圾收集器活动应该大致相似。随着时间的推移，我们期望使虚拟线程堆栈的内部表示更加紧凑。



与平台线程堆栈不同，虚拟线程堆栈不是GC根，因此它们中包含的引用不会在执行并发堆扫描的垃圾收集器(如G1)停止世界暂停时遍历。这也意味着，如果一个虚拟线程被阻塞，例如BlockingQueue.take()，并且没有其他线程可以获得对该虚拟线程或队列的引用，那么该线程可以被垃圾收集——这很好，因为虚拟线程永远不能被中断或解除阻塞。当然，如果虚拟线程正在运行，或者如果它被阻塞并且可以被解除阻塞，那么它将不会被垃圾收集。



当前虚拟线程的一个限制是G1 GC不支持巨大的堆栈块对象。如果一个虚拟线程的堆栈达到区域大小的一半，可能小到512KB，那么可能会抛出StackOverflowError。



## Virtual Thread API

The main API differences between virtual and platforms threads are:

- The public `Thread` constructors cannot create virtual threads.
- Virtual threads are always daemon threads. The [`Thread.setDaemon(boolean)`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/lang/Thread.html#setDaemon(boolean)) method cannot change a virtual thread to be a non-daemon thread.
- Virtual threads have a fixed priority of [`Thread.NORM_PRIORITY`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/lang/Thread.html#NORM_PRIORITY). The [`Thread.setPriority(int)`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/lang/Thread.html#setPriority(int)) method has no effect on virtual threads. This limitation may be revisited in a future release.
- Virtual threads are not active members of thread groups. When invoked on a virtual thread, [`Thread.getThreadGroup()`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/lang/Thread.html#getThreadGroup()) returns a placeholder thread group with the name `"VirtualThreads"`. The `Thread.Builder` API does not define a method to set the thread group of a virtual thread.
- Virtual threads have no permissions when running with a `SecurityManager` set.
- Virtual threads do not support the [`stop()`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/lang/Thread.html#stop()), [`suspend()`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/lang/Thread.html#suspend()), or [`resume()`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/lang/Thread.html#resume()) methods. These methods throw an exception when invoked on a virtual thread.



Virtual threads support thread-local variables ([`ThreadLocal`](https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/lang/ThreadLocal.html)) and inheritable thread-local variables ([`InheritableThreadLocal`](https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/lang/InheritableThreadLocal.html)), just like platform threads, so they can run existing code that uses thread locals. However, because virtual threads can be very numerous, use thread locals after careful consideration. In particular, do not use thread locals to pool costly resources among multiple tasks sharing the same thread in a thread pool. Virtual threads should never be pooled, since each is intended to run only a single task over its lifetime. We have removed many uses of thread locals from the `java.base` module in preparation for virtual threads, to reduce memory footprint when running with millions of threads.

Additionally:

- The `Thread.Builder` API defines [a method to opt-out of thread locals when creating a thread](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/lang/Thread.Builder.html#allowSetThreadLocals(boolean)). It also defines [a method to opt-out of inheriting the initial value of inheritable thread-locals](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/lang/Thread.Builder.html#inheritInheritableThreadLocals(boolean)). When invoked from a thread that does not support thread locals, [`ThreadLocal.get()`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/lang/ThreadLocal.html#get()) returns the initial value and [`ThreadLocal.set(T)`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/lang/ThreadLocal.html#set(T)) throws an exception.
- The legacy [context class loader](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/lang/Thread.html#getContextClassLoader()) is now specified to work like an inheritable thread local. If [`Thread.setContextClassLoader(ClassLoader)`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/lang/Thread.html#setContextClassLoader(java.lang.ClassLoader)) is invoked on a thread that does not support thread locals then it throws an exception.

[Scope-local variables](https://openjdk.java.net/jeps/8263012) may prove to be a better alternative to thread locals for some use cases.



`java.util.concurrent`

The primitive API to support locking, [`java.util.concurrent.LockSupport`](https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/util/concurrent/locks/LockSupport.html), now supports virtual threads: Parking a virtual thread releases the underlying platform thread to do other work, and unparking a virtual thread schedules it to continue. This change to `LockSupport` enables all APIs that use it (`Lock`s, `Semaphore`s, blocking queues, etc.) to park gracefully when invoked in virtual threads.

Additionally:

- [`Executors.newThreadPerTaskExecutor(ThreadFactory)`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/util/concurrent/Executors.html#newThreadPerTaskExecutor(java.util.concurrent.ThreadFactory)) and [`Executors.newVirtualThreadPerTaskExecutor()`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/util/concurrent/Executors.html#newVirtualThreadPerTaskExecutor()) create an `ExecutorService` that creates a new thread for each task. These methods enable migration and interoperability with existing code that uses thread pools and `ExecutorService`.
- [`ExecutorService`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/util/concurrent/ExecutorService.html) now extends [`AutoCloseable`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/lang/AutoCloseable.html), thus allowing this API to be used with the try-with-resource construct as shown in the examples above.
- [`Future`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/util/concurrent/Future.html) now defines methods to obtain the result or exception of a completed task, and to obtain the task's state. Combined, these additions make it easy to use `Future` objects as elements of streams, filtering a stream of com.yh.netty.demo.futures to find the completed tasks and then mapping that to obtain a stream of results. These methods will also be useful with the API additions proposed for [structured concurrency](https://openjdk.java.net/jeps/8277129).