package org.sculk.network;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.*;
import io.netty.channel.kqueue.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.ThreadFactory;
import java.util.function.BiFunction;

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
public class EventLoops {
    private static final ChannelType CHANNEL_TYPE;

    static {
        boolean disableNative = System.getProperties().contains("disableNativeEventLoop");

        if (!disableNative && Epoll.isAvailable()) {
            CHANNEL_TYPE = ChannelType.EPOLL;
        } else if (!disableNative && KQueue.isAvailable()) {
            CHANNEL_TYPE = ChannelType.KQUEUE;
        } else {
            CHANNEL_TYPE = ChannelType.NIO;
        }
    }

    public static ChannelType getChannelType() {
        return CHANNEL_TYPE;
    }

    @Getter
    @RequiredArgsConstructor
    public enum ChannelType {
        EPOLL(EpollDatagramChannel.class, EpollSocketChannel.class, EpollServerSocketChannel.class,
                EpollEventLoopGroup::new, Epoll.isAvailable()),
        KQUEUE(KQueueDatagramChannel.class, KQueueSocketChannel.class, KQueueServerSocketChannel.class,
                KQueueEventLoopGroup::new, KQueue.isAvailable()),
        NIO(NioDatagramChannel.class, NioSocketChannel.class, NioServerSocketChannel.class,
                NioEventLoopGroup::new, true);

        private final Class<? extends DatagramChannel> datagramChannel;
        private final Class<? extends SocketChannel> socketChannel;
        private final Class<? extends ServerSocketChannel> serverSocketChannel;
        private final BiFunction<Integer, ThreadFactory, EventLoopGroup> eventLoopGroupFactory;
        private final boolean available;

        public EventLoopGroup newEventLoopGroup(int threads, ThreadFactory factory) {
            return this.eventLoopGroupFactory.apply(threads, factory);
        }
    }
}
