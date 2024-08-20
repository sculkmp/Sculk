package org.sculk.scheduler;


import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.sculk.Server;
import org.sculk.thread.ThreadStore;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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
@Log4j2
public abstract class AsyncTask implements Runnable {

    public static final Queue<AsyncTask> LIST = new ConcurrentLinkedQueue<>();

    private Object result;
    private int taskId;
    private boolean finish = false;

    @Override
    public void run() {
        this.result = null;
        this.onRun();
        this.finish = true;
        LIST.offer(this);
    }

    public abstract void onRun();

    public void onCompletion(Server server) {}

    public boolean isFinish() {
        return finish;
    }

    public Object getResult() {
        return result;
    }

    public boolean hasResult() {
        return this.result != null;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getTaskId() {
        return taskId;
    }

    public Object getThreadStore(String identifier) {
        return this.isFinish() ? null : ThreadStore.store.get(identifier);
    }

    public void saveThreadStore(String identifier, Object value) {
        if(this.isFinish()) {
            return;
        }
        ThreadStore.store.put(identifier, value == null ? ThreadStore.store.remove(identifier) : value);
    }

    public static void collectTask() {
        AsyncTask task = LIST.poll();
        try {
            task.onCompletion(Server.getInstance());
        } catch(Exception e) {
            Server.getInstance().getLogger().error("Exception while async task {} invoking onCompletion", e);
        }
    }

    public void clear() {
        this.result = null;
        this.taskId = 0;
        this.finish = false;
    }
}
