package org.sculk.network.server;

import io.netty.channel.Channel;
import org.cloudburstmc.protocol.bedrock.BedrockPeer;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockServerInitializer;
import org.sculk.Server;
import org.sculk.network.BedrockInterface;
import org.sculk.network.broadcaster.StandardEntityEventBroadcaster;
import org.sculk.network.broadcaster.StandardPacketBroadcaster;
import org.sculk.network.protocol.ProtocolInfo;
import org.sculk.network.session.SculkPeer;
import org.sculk.network.session.SculkServerSession;

import java.lang.ref.WeakReference;

public class SculkBedrockServerInitializer extends BedrockServerInitializer {

    private final BedrockInterface bedrockInterface;
    private final Server server;
    private final StandardEntityEventBroadcaster entityEventBroadcaster;
    private final StandardPacketBroadcaster packetBroadcaster;

    public SculkBedrockServerInitializer(BedrockInterface bedrockInterface, Server server){
        this.bedrockInterface = bedrockInterface;
        this.server = server;
        this.packetBroadcaster = new StandardPacketBroadcaster();
        this.entityEventBroadcaster = new StandardEntityEventBroadcaster(packetBroadcaster);
    }
    @Override
    protected void initSession(BedrockServerSession bedrockServerSession) {
        bedrockServerSession.setCodec(ProtocolInfo.CODEC);
        bedrockServerSession.setLogging(false);
    }

    @Override
    protected BedrockPeer createPeer(Channel channel) {
        return new SculkPeer(channel, this::createSession);
    }

    @Override
    public BedrockServerSession createSession0(BedrockPeer peer, int subClientId) {
        return new SculkServerSession(this.bedrockInterface, this.packetBroadcaster, this.entityEventBroadcaster ,server, (SculkPeer) peer, subClientId);
    }
}
