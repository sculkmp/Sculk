package org.sculk.scheduler;


import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import lombok.extern.log4j.Log4j2;

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
@Log4j2
public abstract class Task implements Runnable {

    private TaskHandler taskHandler = null;

    public TaskHandler getTaskHandler() {
        return taskHandler;
    }

    public final int getTaskId() {
        return getTaskHandler() != null ? getTaskHandler().getTaskId() : -1;
    }

    public void setTaskHandler(TaskHandler taskHandler) {
        this.taskHandler = taskHandler;
    }

    public abstract void onRun(int currentTick);

    @Override
    public final void run() {
        this.onRun(taskHandler.getLastRunTick());
    }

    public void onCancel() {}

    public void cancel() {
        try {
            this.getTaskHandler().cancel();
        } catch (RuntimeException ex) {
            log.error("Exception while invoking onCancel", ex);
        }
    }

}
