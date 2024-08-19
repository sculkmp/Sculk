package org.sculk.player;


import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.sculk.Server;
import org.sculk.network.BedrockInterface;
import org.sculk.player.client.ClientChainData;

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
public class PlayerLoginData {

    private final BedrockServerSession session;
    private final Server server;
    private final BedrockInterface bedrockInterface;

    private boolean shouldLogin;
    private String username;

    private ClientChainData chainData;

    public PlayerLoginData(BedrockServerSession serverSession, Server server, BedrockInterface bedrockInterface) {
        this.session = serverSession;
        this.server = server;
        this.bedrockInterface = bedrockInterface;

        this.shouldLogin = false;
    }

    public ClientChainData getChainData() {
        return this.chainData;
    }

    public void setChainData(ClientChainData data) {
        this.chainData = data;
    }

    public void setName(String username) {
        this.username = username;
    }

    public String getName() {
        return this.username;
    }

}
