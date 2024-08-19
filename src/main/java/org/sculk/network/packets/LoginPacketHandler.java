package org.sculk.network.packets;


import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.bedrock.packet.NetworkSettingsPacket;
import org.cloudburstmc.protocol.bedrock.packet.RequestNetworkSettingsPacket;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.sculk.Server;
import org.sculk.network.BedrockInterface;
import org.sculk.player.PlayerLoginData;

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
public class LoginPacketHandler implements BedrockPacketHandler {

    private final BedrockServerSession session;
    private final Server server;
    private final PlayerLoginData loginData;


    public LoginPacketHandler(BedrockServerSession session, Server server, BedrockInterface bedrockInterface) {
        this.session = session;
        this.server = server;
        this.loginData = new PlayerLoginData(session, server, bedrockInterface);
    }

    @Override
    public PacketSignal handle(RequestNetworkSettingsPacket packet) {
        return PacketSignal.HANDLED;
    }

}
