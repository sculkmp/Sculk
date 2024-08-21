package org.sculk.player.handler;


import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.sculk.Server;
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
public class ResourcePackHandler implements BedrockPacketHandler {

    private final BedrockServerSession session;
    private final Server server;
    private PlayerLoginData loginData;

    public ResourcePackHandler(BedrockServerSession session, Server server, PlayerLoginData data) {
        this.session = session;
        this.server = server;
        this.loginData = data;
    }

    @Override
    public PacketSignal handle(ResourcePackClientResponsePacket packet) {
        loginData.setShouldLogin(true);
        Server.getInstance().getLogger().info("ResourcePackClientResponsePacket");
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ResourcePackChunkRequestPacket packet) {
        ResourcePackChunkDataPacket dataPacket = new ResourcePackChunkDataPacket();
        dataPacket.setProgress(100L);
        session.sendPacket(dataPacket);

        Server.getInstance().getLogger().info("ResourcePackChunkRequestPacket");
        return PacketSignal.HANDLED;
    }
}