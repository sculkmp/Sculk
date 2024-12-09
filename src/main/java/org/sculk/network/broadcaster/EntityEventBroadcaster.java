package org.sculk.network.broadcaster;

import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataMap;
import org.sculk.entity.*;
import org.sculk.network.session.SculkServerSession;

import java.util.List;
import java.util.Map;

public interface EntityEventBroadcaster {


    public void syncAttributes(List<SculkServerSession> recipients, Living entity, Map<String, Attribute> attributes);

    public void syncActorData(List<SculkServerSession> recipients, Entity entity, EntityDataMap properties);

    //public void onEntityEffectAdded(List<SculkServerSession> recipients, Living entity, EffectInstance effect, bool replacesOldEffect);

    //public void onEntityEffectRemoved(List<SculkServerSession> recipients, Living entity, EffectInstance effect);

    public void onEntityRemoved(List<SculkServerSession> recipients, Entity entity);


    public void onMobMainHandItemChange(List<SculkServerSession> recipients,HumanEntity mob);

    public void onMobOffHandItemChange(List<SculkServerSession> recipients, HumanEntity mob);


    public void onMobArmorChange(List<SculkServerSession> recipients, Living mob);


    public void onPickUpItem(List<SculkServerSession> recipients, Entity collector, Entity pickedUp);


    public void onEmote(List<SculkServerSession> recipients, HumanEntity from, String emoteId);
}
