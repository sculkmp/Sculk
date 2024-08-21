package org.sculk.scheduler;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.sculk.exception.TaskException;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

/*
 *   ____             _ _
 *  / ___|  ___ _   _| | | __
 *  \___ \ / __| | | | | |/ /
 *   ___) | (__| |_| | |   <
 *  |____/ \___|\__,_|_|_|\_\
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author: SculkTeams
 * @link: http://www.sculkmp.org/
 */
@Singleton
@Log4j2
public class Scheduler {

    private final AtomicInteger currentTaskId;
    private final Queue<TaskHandler> taskHandlerQueue;
    private final Map<Integer, TaskHandler> taskHandlerMap;
    private final Map<Integer, ArrayDeque<TaskHandler>> integerArrayDequeMap;
    private final ForkJoinPool asyncPool;

    private volatile int currentTick = -1;

    @Inject
    public Scheduler() {
        this.currentTaskId = new AtomicInteger();
        this.taskHandlerQueue = new ConcurrentLinkedQueue<>();
        this.taskHandlerMap = new ConcurrentHashMap<>();
        this.integerArrayDequeMap = new ConcurrentHashMap<>();
        this.asyncPool = ForkJoinPool.commonPool();
    }

    private int nextTaskId() {
        return currentTaskId.incrementAndGet();
    }

    public AtomicInteger getCurrentTaskId() {
        return currentTaskId;
    }

    public TaskHandler scheduleAsyncTask(AsyncTask task) {
        return addTask(task, 0, 0, true);
    }

    public int getQueueSize() {
        int size = taskHandlerQueue.size();
        for(ArrayDeque<TaskHandler> queue : integerArrayDequeMap.values()) {
            size += queue.size();
        }
        return size;
    }

    public ForkJoinPool getAsyncPool() {
        return asyncPool;
    }

    public int getAsyncPoolSize() {
        return getAsyncPool().getPoolSize();
    }

    @SneakyThrows
    private TaskHandler addTask(Runnable task, int delay, int period, boolean async) {
        if(delay < 0 || period < 0) {
            throw new TaskException("Attempted to register a task with negative delay or period");
        }

        TaskHandler taskHandler = new TaskHandler(task, this.nextTaskId(), async);
        taskHandler.setDelay(delay);
        taskHandler.setPeriod(period);
        taskHandler.setNextRunTick(taskHandler.isDelayed() ? currentTick + taskHandler.getDelay() : currentTick);

        if(task instanceof Task) {
            ((Task) task).setTaskHandler(taskHandler);
        }
        taskHandlerQueue.offer(taskHandler);
        taskHandlerMap.put(taskHandler.getTaskId(), taskHandler);
        return taskHandler;
    }

    public void mainThread(int currentTick) {
        TaskHandler taskHandler;
        while((taskHandler = taskHandlerQueue.poll()) != null) {
            int tick = Math.max(currentTick, taskHandler.getNextRunTick());
            this.integerArrayDequeMap.computeIfAbsent(tick, integer -> new ArrayDeque<>()).add(taskHandler);
        }
        if(currentTick - this.currentTick > integerArrayDequeMap.size()) {
            for(Map.Entry<Integer, ArrayDeque<TaskHandler>> entry : integerArrayDequeMap.entrySet()) {
                int tick = entry.getKey();
                if(tick <= currentTick) {
                    runTask(currentTick);
                }
            }
        } else {
            for(int  i = this.currentTick +1; i <= currentTick; i++) {
                runTask(currentTick);
            }
        }
        this.currentTick = currentTick;
        AsyncTask.collectTask();
    }

    private void runTask(int currentTick) {
        ArrayDeque<TaskHandler> queue = integerArrayDequeMap.remove(currentTick);
        if(queue != null) {
            for(TaskHandler taskHandler : queue) {
                if(taskHandler.isCancelled()) {
                    taskHandlerMap.remove(taskHandler.getTaskId());
                } else if(taskHandler.isAsynchronous()) {
                    asyncPool.execute(taskHandler.getTask());
                } else {
                    taskHandler.timing.stopTiming();
                }
                if(taskHandler.isRepeating()) {
                    taskHandler.setNextRunTick(currentTick + taskHandler.getPeriod());
                    taskHandlerQueue.offer(taskHandler);
                } else {
                    try {
                        TaskHandler remove = taskHandlerMap.remove(taskHandler.getTaskId());
                        if(remove != null) {
                            remove.cancel();
                        }
                    } catch(RuntimeException exception) {
                        log.error("Exception while invoking onCancel", exception);
                    }
                }
            }
        }
    }


}
