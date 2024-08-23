package org.sculk.network.handler;


import lombok.NonNull;
import org.cloudburstmc.protocol.bedrock.packet.TextPacket;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.sculk.player.Player;
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
public class InGamePacketHandler extends SculkPacketHandler {

    private final @NonNull Player player;

    public InGamePacketHandler(Player player, SculkServerSession session) {
        super(session);
        this.player = player;
    }

    @Override
    public PacketSignal handle(TextPacket packet) {
        switch(packet.getType()) {
            case TextPacket.Type.CHAT -> {
                String chatMessage = packet.getMessage();
                int breakLine = chatMessage.indexOf("\n");
                if(breakLine != -1) {
                    chatMessage = chatMessage.substring(0, breakLine);
                }
                this.player.onChat(chatMessage);
            }
        }
        return PacketSignal.HANDLED;
    }

}
