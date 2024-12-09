package org.sculk.network.utils;

import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.sculk.network.broadcaster.EntityEventBroadcaster;
import org.sculk.network.broadcaster.PacketBroadcaster;
import org.sculk.network.session.SculkServerSession;
import org.sculk.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class NetworkBroadcastUtils {

    /**
     * Broadcasts an event to entity event broadcasters.
     *
     * @param recipients A list of Player recipients.
     * @param callback A callback that accepts an EntityEventBroadcaster and a list of NetworkSession objects.
     */
    public static void broadcastEntityEvent(List<Player> recipients, BiConsumer<EntityEventBroadcaster, List<SculkServerSession>> callback) {
        // Group broadcasters and their associated sessions
        Map<EntityEventBroadcaster, List<SculkServerSession>> broadcasterTargets = new HashMap<>();

        for (Player recipient : recipients) {
            SculkServerSession session = recipient.getNetworkSession();
            EntityEventBroadcaster broadcaster = session.getEntityEventBroadcaster();

            // Group sessions by broadcaster
            broadcasterTargets
                    .computeIfAbsent(broadcaster, k -> new ArrayList<>())
                    .add(session);
        }

        // Invoke callback for each unique broadcaster
        broadcasterTargets.forEach(callback);
    }


    public static void broadcastPackets(List<Player> recipients, List<BedrockPacket> packets) {
        // Group broadcasters and their associated sessions
        Map<PacketBroadcaster, List<SculkServerSession>> broadcasterTargets = new HashMap<>();

        for (Player recipient : recipients) {
            SculkServerSession session = recipient.getNetworkSession();
            PacketBroadcaster broadcaster = session.getBroadcaster();

            // Group sessions by broadcaster
            broadcasterTargets
                    .computeIfAbsent(broadcaster, k -> new ArrayList<>())
                    .add(session);
        }

        // Invoke callback for each unique broadcaster
        broadcasterTargets.forEach((broadcaster, sculkServerSessions) -> {
            broadcaster.broadcastPackets(sculkServerSessions, packets);
        });
    }
}
