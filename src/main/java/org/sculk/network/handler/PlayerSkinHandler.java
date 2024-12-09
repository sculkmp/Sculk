package org.sculk.network.handler;

import org.cloudburstmc.protocol.bedrock.packet.EntityPickRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerSkinPacket;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.sculk.network.session.SculkServerSession;
import org.sculk.player.Player;
import org.sculk.utils.SkinUtils;

public class PlayerSkinHandler extends SculkPacketHandler{
    private final Player player;
    public PlayerSkinHandler(SculkServerSession session, Player player) {
        super(session);
        this.player = player;
    }

    @Override
    public PacketSignal handle(PlayerSkinPacket packet) {
        this.player.setSkin(SkinUtils.fromSerialized(packet.getSkin()));
        return PacketSignal.HANDLED;
    }
}
