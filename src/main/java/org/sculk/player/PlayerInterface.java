package org.sculk.player;


import org.sculk.Server;

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
public interface PlayerInterface {

    String getName();
    UUID getServerId();
    Server getServer();
    boolean isOnline();
    UUID getUniqueId();
    boolean isBanned();
    void setBanned(boolean value);
    boolean isWhitelisted();
    void setWhitelisted(boolean value);
}
