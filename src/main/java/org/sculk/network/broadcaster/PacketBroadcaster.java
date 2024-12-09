package org.sculk.network.broadcaster;

import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.sculk.network.session.SculkServerSession;

import java.util.List;

public interface PacketBroadcaster {

    public void broadcastPackets(List<SculkServerSession> recipients, List<BedrockPacket> packets);
}
