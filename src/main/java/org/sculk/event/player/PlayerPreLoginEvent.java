package org.sculk.event.player;


import lombok.Getter;
import org.sculk.event.Cancellable;
import org.sculk.event.Event;
import org.sculk.player.PlayerLoginData;
import org.sculk.player.client.ClientChainData;

import java.net.SocketAddress;

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
public class PlayerPreLoginEvent extends Event implements Cancellable {

    @Getter
    protected ClientChainData loginData;
    @Getter
    protected SocketAddress address;
    protected String kickMessage;

    public PlayerPreLoginEvent(ClientChainData loginData, SocketAddress address, String kickMessage) {
        this.loginData = loginData;
        this.address = address;
        this.kickMessage = kickMessage;
    }

    public void setKickMessage(String kickMessage) {
        this.kickMessage = kickMessage;
    }

    public String getKickMessage() {
        return kickMessage;
    }

}
