package org.sculk.network.broadcaster;

import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataMap;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetEntityDataPacket;
import org.sculk.entity.Attribute;
import org.sculk.entity.Entity;
import org.sculk.entity.HumanEntity;
import org.sculk.entity.Living;
import org.sculk.network.session.SculkServerSession;

import java.util.List;
import java.util.Map;

public class StandardEntityEventBroadcaster implements EntityEventBroadcaster{
    private PacketBroadcaster broadcaster;
    public StandardEntityEventBroadcaster(
            PacketBroadcaster broadcaster
    ){
        this.broadcaster = broadcaster;
    }

    private void sendDataPacket(List<SculkServerSession> recipients, BedrockPacket packet){
        this.broadcaster.broadcastPackets(recipients, List.of(packet));
    }
    
    public void syncAttributes(List<SculkServerSession> recipients, Living entity, Map<String, Attribute> attributes)
    {
        //TODO
    }

    public void syncActorData(List<SculkServerSession> recipients, Entity entity, EntityDataMap properties)
    {
        SetEntityDataPacket packet = new SetEntityDataPacket();
        packet.setRuntimeEntityId(entity.getEntityId());
        packet.setMetadata(properties);
        this.sendDataPacket(recipients, packet);
    }

    public void onEntityRemoved(List<SculkServerSession> recipients, Entity entity)
    {
        //TODO
    }


    public void onMobMainHandItemChange(List<SculkServerSession> recipients, HumanEntity mob)
    {
        //TODO
    }

    public void onMobOffHandItemChange(List<SculkServerSession> recipients, HumanEntity mob)
    {
        //TODO
    }


    public void onMobArmorChange(List<SculkServerSession> recipients, Living mob)
    {
        //TODO
    }


    public void onPickUpItem(List<SculkServerSession> recipients, Entity collector, Entity pickedUp)
    {
        //TODO
    }


    public void onEmote(List<SculkServerSession> recipients, HumanEntity from, String emoteId)
    {
        //TODO
    }
}
