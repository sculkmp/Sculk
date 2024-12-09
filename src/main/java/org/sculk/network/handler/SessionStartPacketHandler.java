package org.sculk.network.handler;

import org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm;
import org.cloudburstmc.protocol.bedrock.packet.NetworkSettingsPacket;
import org.cloudburstmc.protocol.bedrock.packet.RequestNetworkSettingsPacket;
import org.cloudburstmc.protocol.bedrock.packet.ServerToClientHandshakePacket;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.sculk.Server;
import org.sculk.network.session.SculkServerSession;

import java.util.function.Consumer;

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
public class SessionStartPacketHandler extends SculkPacketHandler {

    private final Consumer<Object> onSuccess;
    public SessionStartPacketHandler(SculkServerSession session, Consumer<Object> onSuccess) {
        super(session);
        this.onSuccess = onSuccess;
    }

    @Override
    public PacketSignal handle(RequestNetworkSettingsPacket packet) {
        NetworkSettingsPacket networkSettingsPacket = new NetworkSettingsPacket();
        networkSettingsPacket.setCompressionThreshold(1);
        networkSettingsPacket.setCompressionAlgorithm(PacketCompressionAlgorithm.ZLIB);
        session.sendPacketImmediately(networkSettingsPacket);
        session.getPeer().setCompression(PacketCompressionAlgorithm.ZLIB);
        this.onSuccess.accept(null);
        return PacketSignal.HANDLED;
    }

}
