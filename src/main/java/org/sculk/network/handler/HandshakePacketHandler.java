package org.sculk.network.handler;


import org.cloudburstmc.protocol.bedrock.packet.ClientToServerHandshakePacket;
import org.cloudburstmc.protocol.common.PacketSignal;
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
public class HandshakePacketHandler extends SculkPacketHandler {

    private Consumer<Object> onHandshakeCompleted;

    public HandshakePacketHandler(SculkServerSession session, Consumer<Object> onHandshakeCompleted) {
        super(session);
        this.onHandshakeCompleted = onHandshakeCompleted;
    }

    @Override
    public PacketSignal handle(ClientToServerHandshakePacket packet) {
        return PacketSignal.HANDLED;
    }
}
