/*
 * Copyright 2022 WaterdogTEAM
 * Licensed under the GNU General Public License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sculk.network.query;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.jodah.expiringmap.ExpiringMap;
import org.sculk.Server;
import org.sculk.event.server.QueryHandlerEvent;
import org.sculk.player.Player;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class QueryHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    public static final String NAME = "query-handler";

    public static final ByteBuf QUERY_SIGNATURE = Unpooled.wrappedBuffer(new byte[]{(byte) 0xFE, (byte) 0xFD});
    public static final byte[] LONG_RESPONSE_PADDING_TOP = new byte[]{115, 112, 108, 105, 116, 110, 117, 109, 0, -128, 0};
    public static final byte[] LONG_RESPONSE_PADDING_BOTTOM = new byte[]{1, 112, 108, 97, 121, 101, 114, 95, 0, 0};

    public static final short PACKET_HANDSHAKE = 0x09;
    public static final short PACKET_STATISTICS = 0x00;
    private static final String GAME_ID = "MINECRAFTPE";

    private final Server server;

    private final ExpiringMap<InetAddress, QuerySession> querySessions = ExpiringMap.builder()
            .expirationListener(this::onQueryExpired)
            .expiration(60, TimeUnit.SECONDS)
            .build();

    public QueryHandler(Server proxy) {
        this.server = proxy;
    }

    public void onQueryExpired(InetAddress address, QuerySession session) {
        this.server.getLogger().warn("Pending query from " + address + " has expired: token=" + session.token);
    }

    private void writeInt(ByteBuf buf, int i) {
        this.writeString(buf, Integer.toString(i));
    }

    private void writeString(ByteBuf buf, String string) {
        for (char c : string.toCharArray()) {
            buf.writeByte(c);
        }
        buf.writeByte(0);
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        if (!super.acceptInboundMessage(msg)) {
            return false;
        }

        DatagramPacket packet = (DatagramPacket) msg;
        if (!packet.content().isReadable(7)) {
            return false;
        }

        int startIndex = packet.content().readerIndex();
        try {
            ByteBuf magic = packet.content().readSlice(2);
            return ByteBufUtil.equals(magic, QUERY_SIGNATURE);
        } finally {
            packet.content().readerIndex(startIndex);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) {
        try {
            ByteBuf buf = packet.content();
            this.onQuery(packet.sender(), buf.skipBytes(2), ctx, (InetSocketAddress) ctx.channel().localAddress());
        } catch (Exception e) {
            this.server.getLogger().error("Can not handle query packet!", e);
        }
    }

    public void onQuery(InetSocketAddress address, ByteBuf packet, ChannelHandlerContext ctx, InetSocketAddress bindAddress) {
        if (address.getAddress() == null) {
            // We got unresolved address
            return;
        }
        short packetId = packet.readUnsignedByte();
        int sessionId = packet.readInt();

        if (packetId == PACKET_HANDSHAKE) {
            ByteBuf reply = ctx.alloc().ioBuffer(10);
            reply.writeByte(PACKET_HANDSHAKE);
            reply.writeInt(sessionId);

            int token = ThreadLocalRandom.current().nextInt();
            this.querySessions.put(address.getAddress(), new QuerySession(token, System.currentTimeMillis()));
            this.writeInt(reply, token);
            ctx.writeAndFlush(new DatagramPacket(reply, address));
            return;
        }

        if (packetId == PACKET_STATISTICS && packet.isReadable(4)) {
            QuerySession session = this.querySessions.remove(address.getAddress());
            int token = packet.readInt();
            if (session == null || session.token != token) {
                return;
            }

            ByteBuf reply = ctx.alloc().ioBuffer(64);
            reply.writeByte(PACKET_STATISTICS);
            reply.writeInt(sessionId);

            this.writeData(address, packet.readableBytes() == 8, reply, bindAddress);
            ctx.writeAndFlush(new DatagramPacket(reply, address));
        }
    }

    private void writeData(InetSocketAddress address, boolean simple, ByteBuf buf, InetSocketAddress bindAddress) {
        QueryHandlerEvent event = new QueryHandlerEvent(
                this.server.getMotd(),
                "SMP",
                "MCPE",
                "",
                this.server.getOnlinePlayers().values(),
                this.server.getMaxPlayers(),
                "WaterdogPE",
                address
        );
        event.call();

        if (simple) {
            this.writeString(buf, event.getMotd());
            this.writeString(buf, event.getSmp());
            this.writeString(buf, event.getS());
            this.writeString(buf, Integer.toString(event.getValues().size()));
            this.writeString(buf, Integer.toString(event.getMaxPlayers()));
            buf.writeShortLE(bindAddress.getPort());
            this.writeString(buf, bindAddress.getHostName());
            return;
        }

        Map<String, String> map = new Object2ObjectArrayMap<>();
        map.put("hostname", event.getMotd());
        map.put("gametype", event.getSmp());
        map.put("map", event.getS());
        map.put("numplayers", Integer.toString(event.getValues().size()));
        map.put("maxplayers", Integer.toString(event.getMaxPlayers()));
        map.put("hostport", Integer.toString(bindAddress.getPort()));
        map.put("hostip", bindAddress.getHostName());
        map.put("game_id", GAME_ID);
        map.put("version", event.getMcpe());
        map.put("plugins", ""); // Do not list plugins
        map.put("whitelist",  "off");

        buf.writeBytes(LONG_RESPONSE_PADDING_TOP);
        map.forEach((key, value) -> {
            this.writeString(buf, key);
            this.writeString(buf, value);
        });
        buf.writeByte(0);
        buf.writeBytes(LONG_RESPONSE_PADDING_BOTTOM);

        if (!event.getValues().isEmpty()) {
            for (Player player : event.getValues()) {
                this.writeString(buf, player.getName());
            }
        }
        buf.writeByte(0);
    }

    private record QuerySession(int token, long time) {

    }
}
