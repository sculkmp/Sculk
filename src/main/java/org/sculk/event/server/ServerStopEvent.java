package org.sculk.event.server;


import org.sculk.event.ServerEvent;
import org.sculk.player.Player;

import java.util.Map;
import java.util.UUID;

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
public class ServerStopEvent extends ServerEvent {

    protected Map<UUID, Player> onlinePlayers;

    public ServerStopEvent(Map<UUID, Player> onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public Map<UUID, Player> getOnlinePlayers() {
        return onlinePlayers;
    }

}
