package org.sculk.event;


import java.lang.reflect.Method;
import java.util.List;

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
public interface EventCallHandler {

    interface ListenerMethod extends Comparable<ListenerMethod> {
        Object getListener();
        Method getMethods();
        void run(Event event) throws Exception;
    }

    void call(Event event);
    List<ListenerMethod> getMethods();

}