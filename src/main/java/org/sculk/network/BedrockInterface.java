package org.sculk.network;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.protocol.bedrock.BedrockPong;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockServerInitializer;
import org.sculk.Server;
import org.sculk.config.ServerPropertiesKeys;
import org.sculk.network.packets.LoginPacketHandler;
import org.sculk.network.protocol.ProtocolInfo;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
public class BedrockInterface implements AdvancedSourceInterface {

    private final Server server;
    private final List<Channel> channels = new ArrayList<>();
    private final BedrockPong bedrockPong = new BedrockPong();

    public BedrockInterface(Server server) throws Exception {
        this.server = server;
        ServerBootstrap bootstrap = new ServerBootstrap()
                .channelFactory(RakChannelFactory.server(NioDatagramChannel.class))
                .group(new NioEventLoopGroup())
                .childHandler(new BedrockServerInitializer() {
                    @Override
                    protected void initSession(BedrockServerSession bedrockServerSession) {
                        bedrockServerSession.setCodec(ProtocolInfo.CODEC);
                        bedrockServerSession.setLogging(false);
                        bedrockServerSession.setPacketHandler(new LoginPacketHandler(bedrockServerSession, server, BedrockInterface.this));
                    }
                })
                .localAddress(this.server.getProperties().get(ServerPropertiesKeys.SERVER_IP, "0.0.0.0"), this.server.getProperties().get(ServerPropertiesKeys.SERVER_PORT, 19132));
        this.channels.add(bootstrap.bind().awaitUninterruptibly().channel());
    }

    @Override
    public void blockAddress(InetAddress address) {

    }

    @Override
    public void blockAddress(InetAddress address, long timeout, TimeUnit unit) {

    }

    @Override
    public void unblockAddress(InetAddress address) {

    }

    @Override
    public void setNetwork(Network network) {

    }

    @Override
    public void sendRawPacket(InetSocketAddress socketAddress, ByteBuf payload) {

    }

    @Override
    public void setName(String name) {
        this.bedrockPong.edition("MCPE")
                .motd(this.server.getMotd())
                .subMotd(this.server.getMotd())
                .playerCount(this.server.getOnlinePlayers().size())
                .serverId(1)
                .maximumPlayerCount(20)
                .version("1")
                .protocolVersion(ProtocolInfo.CURRENT_PROTOCOL)
                .gameType("Survival")
                .nintendoLimited(false)
                .ipv4Port(19132)
                .ipv6Port(19132);

        for (Channel channel : this.channels) {
            channel.config().setOption(RakChannelOption.RAK_ADVERTISEMENT, this.bedrockPong.toByteBuf());
        }
    }

    @Override
    public boolean process() {
        return true;
    }

    @Override
    public void shutdown() {
        for(Channel channel : this.channels) {
            channel.closeFuture().awaitUninterruptibly();
        }
    }

    @Override
    public void emergencyShutdown() {
        this.shutdown();
    }
}
