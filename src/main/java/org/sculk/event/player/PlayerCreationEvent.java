package org.sculk.event.player;


import lombok.Getter;
import lombok.Setter;
import org.sculk.player.Player;
import org.sculk.event.Event;
import org.sculk.network.session.SculkServerSession;

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
public class PlayerCreationEvent extends Event {

    @Getter
    private final SculkServerSession session;
    @Getter
    @Setter
    private Class<? extends Player> baseClass;
    @Getter
    @Setter
    private Class<? extends Player> playerClass;

    public PlayerCreationEvent(SculkServerSession session) {
        this.session = session;
        this.baseClass = Player.class;
        this.playerClass = Player.class;
    }

}
