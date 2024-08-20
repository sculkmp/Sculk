package org.sculk.exception;


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
public class TaskException extends RuntimeException {

    private final Throwable cause;

    public TaskException(Throwable throwable) {
        cause = throwable;
    }

    public TaskException() {
        cause = null;
    }

    public TaskException(Throwable cause, String message) {
        super(message);
        this.cause = cause;
    }

    public TaskException(String message) {
        super(message);
        cause = null;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

}
