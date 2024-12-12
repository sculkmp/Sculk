package org.sculk.network.handler;


import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetLocalPlayerAsInitializedPacket;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.sculk.Server;
import org.sculk.event.player.PlayerJoinEvent;
import org.sculk.lang.LanguageKeys;
import org.sculk.network.session.SculkServerSession;
import org.sculk.utils.TextFormat;

import java.util.List;
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
public class SpawnResponsePacketHandler extends SculkPacketHandler {

    private final Consumer<Object> responseCallback;

    public SpawnResponsePacketHandler(SculkServerSession session, Consumer<Object> responseCallback) {
        super(session);
        this.responseCallback = responseCallback;
    }

    @Override
    public PacketSignal handlePacket(BedrockPacket packet) {
        return super.handlePacket(packet);
    }

    @Override
    public PacketSignal handle(SetLocalPlayerAsInitializedPacket packet) {
        this.responseCallback.accept(null);
        Server.getInstance().getLogger().info("§b" + session.getPlayer().getName() + "[/" + session.getPlayerInfo().getServerAddress() + "] logged in with uuid " + session.getPlayer().getUniqueId());

        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(session.getPlayer(), session.getPlayer().getLanguage().translate(LanguageKeys.MINECRAFT_PLAYER_JOIN, List.of(session.getPlayer().getName())));
        playerJoinEvent.call();
        Server.getInstance().broadcastMessage(TextFormat.YELLOW + playerJoinEvent.getJoinMessage() + TextFormat.RESET);
        return PacketSignal.HANDLED;
    }
}
