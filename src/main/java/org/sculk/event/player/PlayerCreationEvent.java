package org.sculk.event.player;


import org.sculk.Player;
import org.sculk.event.Event;
import org.sculk.network.SourceInterface;

import java.net.SocketAddress;

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
public class PlayerCreationEvent extends Event {

    private final SourceInterface sourceInterface;
    private final Long clientId;
    private final SocketAddress socketAddress;

    private Class<? extends Player> baseClass;
    private Class<? extends Player> playerClass;

    public PlayerCreationEvent(SourceInterface sourceInterface, Class<? extends Player> baseClass, Class<? extends Player> playerClass, Long clientId, SocketAddress socketAddress) {
        this.sourceInterface = sourceInterface;
        this.clientId = clientId;
        this.socketAddress = socketAddress;
        this.baseClass = baseClass;
        this.playerClass = playerClass;
    }

    public SourceInterface getSourceInterface() {
        return sourceInterface;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public Long getClientId() {
        return clientId;
    }

    public Class<? extends Player> getBaseClass() {
        return baseClass;
    }

    public Class<? extends Player> getPlayerClass() {
        return playerClass;
    }

    public void setBaseClass(Class<? extends Player> baseClass) {
        this.baseClass = baseClass;
    }

    public void setPlayerClass(Class<? extends Player> playerClass) {
        this.playerClass = playerClass;
    }

}
