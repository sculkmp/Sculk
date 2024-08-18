package org.sculk.network;

import lombok.extern.log4j.Log4j2;
import org.sculk.Server;

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
public class Network {

    private final Server server;

    private String name;
    private int maxPlayers;

    public Network(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return server;
    }

    public void setName(String name) {
        if(name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Server name cannot be null or empty");
        }
        this.name = name;
    }

    public void setMaxPlayers(int maxPlayers) {
        if (maxPlayers <= 0) {
            throw new IllegalArgumentException("Max players must be greater than zero");
        }
        this.maxPlayers = maxPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getName() {
        return name;
    }

}
