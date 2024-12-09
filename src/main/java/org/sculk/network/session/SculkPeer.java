package org.sculk.network.session;

import io.netty.channel.Channel;
import org.cloudburstmc.protocol.bedrock.BedrockPeer;
import org.cloudburstmc.protocol.bedrock.BedrockSessionFactory;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

import java.util.List;

public class SculkPeer extends BedrockPeer {


    public SculkPeer(Channel channel, BedrockSessionFactory sessionFactory) {
        super(channel, sessionFactory);
    }
    public void sendPacket(int senderClientId, int targetClientId, List<BedrockPacket> packets) {
        packets.forEach(packet -> {
            super.sendPacket(senderClientId, targetClientId, packet);
        });
    }
}
