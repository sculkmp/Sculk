package org.sculk.network.broadcaster;

import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.sculk.Server;
import org.sculk.network.session.SculkServerSession;

import java.util.List;

public class StandardPacketBroadcaster implements PacketBroadcaster{

    public void broadcastPackets(List<SculkServerSession> recipients, List<BedrockPacket> packets){
        recipients.forEach((sculkServerSession) -> {
            sculkServerSession.sendPacket(packets);
        });

    }
}
