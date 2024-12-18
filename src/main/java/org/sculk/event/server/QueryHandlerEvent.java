package org.sculk.event.server;


import lombok.Getter;
import lombok.Setter;
import org.sculk.player.Player;

import java.net.InetSocketAddress;
import java.util.Collection;

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
public class QueryHandlerEvent extends ServerEvent {
    @Getter @Setter
    private String motd;
    @Getter @Setter
    private String smp;
    @Getter @Setter
    private String mcpe;
    @Getter @Setter
    private String s;
    @Getter @Setter
    private Collection<Player> values;
    @Getter @Setter
    private int maxPlayers;
    @Getter @Setter
    private String waterdogPE;
    @Getter @Setter
    private InetSocketAddress address;

    public QueryHandlerEvent(String motd, String smp, String mcpe, String s, Collection<Player> values, int maxPlayers, String waterdogPE, InetSocketAddress address) {
        super();
        this.motd = motd;
        this.smp = smp;
        this.mcpe = mcpe;
        this.s = s;
        this.values = values;
        this.maxPlayers = maxPlayers;
        this.waterdogPE = waterdogPE;
        this.address = address;
    }
}
