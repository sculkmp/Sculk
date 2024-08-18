package org.sculk.thread;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Builder;

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
@Builder
public class ThreadFactoryBuilder implements ThreadFactory {
    
    private static final ThreadFactory threadFactory = Executors.defaultThreadFactory();
    private final AtomicInteger count = new AtomicInteger(0);
    
    private final boolean daemon;
    private final String format;

    private final int priority = Thread.currentThread().getPriority();
    private final Thread.UncaughtExceptionHandler exceptionHandler;

    private static String format(String format, int count) {
        return String.format(Locale.ROOT, format, count);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = threadFactory.newThread(r);
        if(format != null) {
            thread.setName(format(format, count.getAndIncrement()));
        }
        thread.setDaemon(daemon);
        thread.setPriority(priority);
        if(exceptionHandler != null) {
            thread.setUncaughtExceptionHandler(exceptionHandler);
        }
        return thread;
    }

}
