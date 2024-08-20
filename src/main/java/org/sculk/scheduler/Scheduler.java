package org.sculk.scheduler;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.sculk.exception.TaskException;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/*
 *   ____             _ _              __  __ ____
 *  / ___|  ___ _   _| | | __         |  \/  |  _ \
 *  \___ \ / __| | | | | |/ /  _____  | |\/| | |_) |
 *   ___) | (__| |_| | |   <  |_____| | |  | |  __/
 *  |____/ \___|\__,_|_|_|\_\         |_|  |_|_|
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

    private volatile int currentTick = -1;

    @Inject
    public Scheduler() {
        this.currentTaskId = new AtomicInteger();
        this.taskHandlerQueue = new ConcurrentLinkedQueue<>();
        this.taskHandlerMap = new ConcurrentHashMap<>();
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

}
