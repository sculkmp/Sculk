package org.sculk.event;


import java.util.Collections;
import java.util.Map;

import static org.cloudburstmc.protocol.common.util.Preconditions.checkNotNull;

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
public class EventManager implements EventManagerInterface {

    private volatile Map<Class<? extends Event>, EventCallHandler> eventHandlers = Collections.emptyMap();

    @Override
    public void call(Event event) {
        checkNotNull(event, "event");
        EventCallHandler handler = eventHandlers.get(event.getClass());
        if(handler != null) {
            handler.call(event);
        }
    }

}
