package org.sculk.network;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.unix.UnixChannelOption;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.protocol.bedrock.BedrockPeer;
import org.cloudburstmc.protocol.bedrock.BedrockPong;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockServerInitializer;
import org.sculk.Server;
import org.sculk.config.ServerPropertiesKeys;
import org.sculk.network.broadcaster.StandardEntityEventBroadcaster;
import org.sculk.network.broadcaster.StandardPacketBroadcaster;
import org.sculk.network.handler.SessionStartPacketHandler;
import org.sculk.network.protocol.ProtocolInfo;
import org.sculk.network.server.SculkBedrockServerInitializer;
import org.sculk.network.session.SculkServerSession;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
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
    private final List<Channel> serverChannels = new ObjectArrayList<>();
    @Getter
    private final BedrockPong bedrockPong = new BedrockPong();
    @Getter
    private final EventLoopGroup bossEventLoopGroup;
    @Getter
    private final EventLoopGroup workerEventLoopGroup;

    public BedrockInterface(Server server) throws Exception {
        String address = server.getProperties().get(ServerPropertiesKeys.SERVER_IP, "0.0.0.0");
        Integer port = server.getProperties().get(ServerPropertiesKeys.SERVER_PORT, 19132);
        ServerBootstrap bootstrap;
        ThreadFactory workerFactory;
        ThreadFactory bossFactory;
        this.server = server;
        EventLoops.ChannelType channelType = EventLoops.getChannelType();
        server.getLogger().info("Using {} channel implementation as default!", channelType.name());
        for (EventLoops.ChannelType type : EventLoops.ChannelType.values()) {
            server.getLogger().debug("Supported {} channels: {}", type.name(), type.isAvailable());
        }

        workerFactory = new ThreadFactoryBuilder()
                .setNameFormat("Bedrock Listener - #%d")
                .setPriority(5)
                .setDaemon(true)
                .build();
        bossFactory = new ThreadFactoryBuilder()
                .setNameFormat("RakNet Listener - #%d")
                .setPriority(8)
                .setDaemon(true)
                .build();
        this.workerEventLoopGroup = channelType.newEventLoopGroup(0, workerFactory);
        this.bossEventLoopGroup = channelType.newEventLoopGroup(0, bossFactory);
        boolean allowEpoll = Epoll.isAvailable();
        int bindCount = allowEpoll && EventLoops.getChannelType() != EventLoops.ChannelType.NIO
                ? Runtime.getRuntime().availableProcessors() : 1;
        for (int i = 0; i < bindCount; i++) {
            bootstrap = new ServerBootstrap()
                    .channelFactory(RakChannelFactory.server(EventLoops.getChannelType().getDatagramChannel()))
                    .group(this.bossEventLoopGroup, this.workerEventLoopGroup)
                    // .option(CustomChannelOption.IP_DONT_FRAG, 2 /* IP_PMTUDISC_DO */)
                    /*.option(RakChannelOption.RAK_GUID, server.getServerId().getMostSignificantBits())
                    .option(RakChannelOption.RAK_HANDLE_PING, true)
                    .childOption(RakChannelOption.RAK_SESSION_TIMEOUT, 10000L)
                    .childOption(RakChannelOption.RAK_ORDERING_CHANNELS, 1)*/
                    .childHandler(new SculkBedrockServerInitializer(this, this.server));
            if (allowEpoll) {
                bootstrap.option(UnixChannelOption.SO_REUSEPORT, true);
            }
            ChannelFuture future = bootstrap
                    .bind(address, port)
                    .syncUninterruptibly();
            if (future.isSuccess()) {
                this.serverChannels.add(future.awaitUninterruptibly().channel());
            } else {
                throw new IllegalStateException("Can not start server on " + address, future.cause());
            }
        }
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
                .serverId(this.server.getServerId().getMostSignificantBits())
                .maximumPlayerCount(this.server.getMaxPlayers())
                .version("1")
                .protocolVersion(ProtocolInfo.CURRENT_PROTOCOL)
                .gameType("Survival")
                .nintendoLimited(false)
                .ipv4Port(19132)
                .ipv6Port(19132);

        for (Channel channel : this.serverChannels) {
            channel.config().setOption(RakChannelOption.RAK_ADVERTISEMENT, this.bedrockPong.toByteBuf());
        }
    }

    @Override
    public boolean process() {
        return true;
    }

    @Override
    public void shutdown() {
        for(Channel channel : this.serverChannels) {
            channel.closeFuture().awaitUninterruptibly();
        }
    }

    @Override
    public void emergencyShutdown() {
        this.shutdown();
    }
}
