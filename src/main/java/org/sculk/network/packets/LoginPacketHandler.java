package org.sculk.network.packets;


import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.sculk.Server;
import org.sculk.network.BedrockInterface;
import org.sculk.network.protocol.ProtocolInfo;
import org.sculk.player.PlayerLoginData;
import org.sculk.player.client.ClientChainData;
import org.sculk.utils.TextFormat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final Pattern NAME_PATTERN = Pattern.compile("^[aA-zZ\\s\\d_]{3,16}+$");

    public LoginPacketHandler(BedrockServerSession session, Server server, BedrockInterface bedrockInterface) {
        this.session = session;
        this.server = server;
        this.loginData = new PlayerLoginData(session, server, bedrockInterface);
    }

    @Override
    public PacketSignal handle(LoginPacket packet) {
        this.loginData.setChainData(ClientChainData.read(packet));
        if(this.loginData.getChainData().isXboxAuthed()) {
            session.disconnect("disconnectionScreen.notAuthenticated");
            return PacketSignal.HANDLED;
        }

        String username = this.loginData.getChainData().getUsername();
        Matcher matcher = NAME_PATTERN.matcher(username);
        if(!matcher.matches() || username.equalsIgnoreCase("rcon") || username.equalsIgnoreCase("console")) {
            session.disconnect("disconnectionScreen.invalidName");
            return PacketSignal.HANDLED;
        }

        this.loginData.setName(username);
        if(!this.loginData.getChainData().getSerializedSkin().isValid()) {
            session.disconnect("disconnectionScreen.invalidSkin");
            return PacketSignal.HANDLED;
        }

        PlayStatusPacket statusPacket = new PlayStatusPacket();
        statusPacket.setStatus(PlayStatusPacket.Status.LOGIN_SUCCESS);
        session.sendPacket(statusPacket);

        // TODO: View Login in log
        this.server.getLogger().info("login packet call");
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(RequestNetworkSettingsPacket packet) {
        int protocol = packet.getProtocolVersion();
        BedrockCodec codec = ProtocolInfo.getPacket(protocol);
        if(codec == null) {
            PlayStatusPacket statusPacket = new PlayStatusPacket();
            if(protocol < ProtocolInfo.CODEC.getProtocolVersion()) {
                statusPacket.setStatus(PlayStatusPacket.Status.LOGIN_FAILED_CLIENT_OLD);
            } else {
                statusPacket.setStatus(PlayStatusPacket.Status.LOGIN_FAILED_CLIENT_OLD);
            }
            session.sendPacketImmediately(statusPacket);
            return PacketSignal.HANDLED;
        }
        session.setCodec(codec);
        NetworkSettingsPacket networkSettingsPacket = new NetworkSettingsPacket();
        networkSettingsPacket.setCompressionThreshold(1);
        networkSettingsPacket.setCompressionAlgorithm(PacketCompressionAlgorithm.ZLIB);
        session.sendPacketImmediately(networkSettingsPacket);
        session.setCompression(PacketCompressionAlgorithm.ZLIB);

        // TODO: View RequestNetworkSettings in log
        this.server.getLogger().info("request network packet call");
        return PacketSignal.HANDLED;
    }

}
