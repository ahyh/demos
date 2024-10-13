package com.yan.demos.jdk21;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 有一半的任务成功或者有一个失败了就关闭
 */
public class StructuredTaskScopeOnFailOrHalfSuccess<T> extends StructuredTaskScope<T>   {

    private final List<T> list = new ArrayList<>();
    private AtomicInteger taskCount = new AtomicInteger(0);

    private AtomicInteger resultTaskCount = new AtomicInteger(0);

    public StructuredTaskScopeOnFailOrHalfSuccess() {
        super("StructuredTaskScopeOnFailOrHalfSuccess", Thread.ofVirtual().factory());
    }


    @Override
    public <U extends T> Subtask<U> fork(Callable<? extends U> task) {
        taskCount.getAndAdd(1);
        return super.fork(task);
    }

    @Override
    protected void handleComplete(Subtask<? extends T> subtask) {
        if (subtask.state() == Subtask.State.SUCCESS) {
            list.add(subtask.get());
            resultTaskCount.getAndAdd(1);
            int resultTasks = resultTaskCount.get();
            int tasks = taskCount.get();
            if (resultTasks >= tasks / 2) {
                super.shutdown();
            }
        }
        if (subtask.state() == Subtask.State.FAILED) {
            super.shutdown();
        }
    }

    public List<T> getResults() {
        return list;
    }
}
