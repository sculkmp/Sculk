package org.sculk.network.channel;

import org.cloudburstmc.netty.handler.codec.raknet.common.UnconnectedPongEncoder;
import org.cloudburstmc.netty.handler.codec.raknet.server.RakServerOfflineHandler;
import org.sculk.network.BedrockInterface;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import org.sculk.network.channel.server.RakNetPingHandler;
import org.sculk.network.channel.server.ServerDatagramHandler;

public class OfflineServerChannelInitializer  extends ChannelInitializer<Channel> {
    private final BedrockInterface bedrockInterface;

    public OfflineServerChannelInitializer(BedrockInterface proxy) {
        this.bedrockInterface = proxy;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
                .addFirst(ServerDatagramHandler.NAME, new ServerDatagramHandler(this.bedrockInterface))
                .addAfter(RakServerOfflineHandler.NAME, RakNetPingHandler.NAME, new RakNetPingHandler(this.bedrockInterface));
        if (this.bedrockInterface.getBedrockPong() != null) {
            //channel.pipeline().addAfter(UnconnectedPongEncoder.NAME, QueryHandler.NAME, this.bedrockInterface.getQueryHandler());
        }
    }
}
