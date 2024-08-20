package org.sculk.event.player;


import org.sculk.event.Cancellable;
import org.sculk.event.Event;
import org.sculk.player.PlayerLoginData;

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

    protected PlayerLoginData loginData;
    protected String kickMessage;

    public PlayerPreLoginEvent(PlayerLoginData loginData, String kickMessage) {
        this.loginData = loginData;
        this.kickMessage = kickMessage;
    }

    public PlayerLoginData getLoginData() {
        return loginData;
    }

    public void setKickMessage(String kickMessage) {
        this.kickMessage = kickMessage;
    }

    public String getKickMessage() {
        return kickMessage;
    }

}
