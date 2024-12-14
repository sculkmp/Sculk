package org.sculk.event.player;


import lombok.NonNull;
import org.sculk.player.Player;

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
public class PlayerJoinEvent extends PlayerEvent {

    @NonNull
    protected String joinMessage;

    public PlayerJoinEvent(Player player, @NonNull String joinMessage) {
        super(player);
        this.joinMessage = joinMessage;
    }

    public @NonNull String getJoinMessage() {
        return joinMessage;
    }

    public void setJoinMessage(@NonNull String joinMessage) {
        this.joinMessage = joinMessage;
    }
}
