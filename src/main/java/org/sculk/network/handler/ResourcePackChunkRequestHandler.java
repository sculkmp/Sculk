package org.sculk.network.handler;


import io.netty.buffer.Unpooled;
import org.cloudburstmc.protocol.bedrock.packet.RefreshEntitlementsPacket;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackChunkDataPacket;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackChunkRequestPacket;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.sculk.Server;
import org.sculk.network.session.SculkServerSession;
import org.sculk.resourcepack.ResourcePack;

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
public class ResourcePackChunkRequestHandler extends SculkPacketHandler{

    public ResourcePackChunkRequestHandler(SculkServerSession session) {
        super(session);
    }

    @Override
    public PacketSignal handle(ResourcePackChunkRequestPacket packet) {

        ResourcePack resourcePack = Server.getInstance().getResourcePackManager().getResourcePack(packet.getPackId());

        if (resourcePack != null) {
            int size = 1048576;
            ResourcePackChunkDataPacket resourcePackChunkDataPacket = new ResourcePackChunkDataPacket();
            resourcePackChunkDataPacket.setPackId(resourcePack.getUuid());
            resourcePackChunkDataPacket.setChunkIndex(packet.getChunkIndex());
            resourcePackChunkDataPacket.setProgress((long) (size * packet.getChunkIndex()));
            resourcePackChunkDataPacket.setData(Unpooled.wrappedBuffer(resourcePack.getChunk((int) resourcePackChunkDataPacket.getProgress(), size)));

            session.sendPacket(resourcePackChunkDataPacket);
        }

        return super.handle(packet);
    }
}
