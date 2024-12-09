package org.sculk.entity;


import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import org.checkerframework.checker.units.qual.t;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.AttributeData;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataMap;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataTypes;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityFlag;
import org.cloudburstmc.protocol.bedrock.packet.AddEntityPacket;
import org.sculk.Server;
import org.sculk.network.broadcaster.EntityEventBroadcaster;
import org.sculk.network.session.SculkServerSession;
import org.sculk.network.utils.NetworkBroadcastUtils;
import org.sculk.player.Player;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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
public abstract class Entity {

    private static long nextEntityId = 1;

    public static long getNextEntityId() {
        return nextEntityId++;
    }


    public abstract String getNetworkTypeId();

    protected List<Player> hasSpawned;
    @Getter
    private long entityId;
    protected Server server;
    @Getter
    protected EntitySizeInfo size;

    @Getter
    private float health = 20.0F;
    @Getter
    private int maxHealth = 20;

    protected float ySize = 0.0F;
    protected float stepHeight = 0.0F;
    public boolean keepMovement = false;
    
    public float fallDistance = 0.0F;
    public int ticksLived = 0;
    public int lastUpdate;
    protected int fireTicks = 0;
    
    protected AttributeMap attributeMap;
    
    protected float gravity;
    protected float drag;
    protected boolean gravityEnabled = true;

    @Getter
    private final EntityDataMap networkProperties;
    protected boolean networkPropertiesDirty = false;
    protected String nameTag = "";
    protected boolean nameTagVisible = true;
    protected boolean alwaysShowName = false;
    protected String scoreTag = "";
    protected float scale = 1.0F;

    protected boolean canClimb = false;
    protected boolean canClimbWalls = false;
    protected boolean noClientPredictions = false;
    protected boolean invisible = false;
    protected boolean silent = false;

    protected @Nullable Integer ownerId;
    protected @Nullable Integer targetId;

    public Entity() {
        this.entityId = getNextEntityId();
        this.networkProperties = new EntityDataMap();
        networkPropertiesDirty = true;
        this.size = this.getInitialSizeInfo();
        this.ownerId = null;
        this.hasSpawned = new ObjectArrayList<Player>();
    }

    abstract protected EntitySizeInfo getInitialSizeInfo();

    public void initEntity() {
    }

    public void onUpdate() {}

    public List<Player> getViewers()
    {
        return this.hasSpawned;
    }

    public void setNameTag(String nameTag) {
        this.nameTag = nameTag;
        this.networkPropertiesDirty = true;
    }

    public void setNameTagVisible(boolean nameTagVisible) {
        this.nameTagVisible = nameTagVisible;
        this.networkPropertiesDirty = true;
    }

    public void setAlwaysShowName(boolean alwaysShowName) {
        this.alwaysShowName = alwaysShowName;
        this.networkPropertiesDirty = true;
    }

    public void setScoreTag(String scoreTag) {
        this.scoreTag = scoreTag;
        this.networkPropertiesDirty = true;
    }

    public void setScale(float scale) {
        this.scale = scale;
        this.networkPropertiesDirty = true;
    }

    public void setSize(EntitySizeInfo size) {
        this.size = size;
        this.networkPropertiesDirty = true;
    }


    public boolean isOnFire(){
        return (this.fireTicks > 0);
    }


    /**
     * @return MetadataProperty[]
     * @phpstan-return array<int, MetadataProperty>
     */
    final protected EntityDataMap getAllNetworkData(){
        if( this.networkPropertiesDirty){
             this.syncNetworkData( this.networkProperties);
             this.networkPropertiesDirty = false;
        }
        return  this.networkProperties;
    }

    protected void syncNetworkData(EntityDataMap properties) {
        properties.put(EntityDataTypes.NAMETAG_ALWAYS_SHOW, (byte) (this.alwaysShowName ? 1 : 0));
        properties.put(EntityDataTypes.HEIGHT, this.size.getHeight() / this.scale);
        properties.put(EntityDataTypes.WIDTH, this.size.getWidth() / this.scale);
        properties.put(EntityDataTypes.SCALE, this.scale);
        properties.put(EntityDataTypes.LEASH_HOLDER, -1L);

        properties.put(EntityDataTypes.NAME, this.nameTag);
        properties.put(EntityDataTypes.SCORE, this.scoreTag);

        properties.setFlag(EntityFlag.HAS_GRAVITY, this.gravityEnabled);
        properties.setFlag(EntityFlag.CAN_CLIMB, this.canClimb);
        properties.setFlag(EntityFlag.CAN_SHOW_NAME, this.nameTagVisible);
        properties.setFlag(EntityFlag.HAS_COLLISION, true);
        properties.setFlag(EntityFlag.NO_AI, this.noClientPredictions);
        properties.setFlag(EntityFlag.INVISIBLE, this.invisible);
        properties.setFlag(EntityFlag.SILENT, this.silent);
        properties.setFlag(EntityFlag.ON_FIRE, this.isOnFire());
        properties.setFlag(EntityFlag.WALL_CLIMBING, this.canClimbWalls);
    }

    /**
     * Called by spawnTo() to send whatever packets needed to spawn the entity to the client.
     */
    protected void sendSpawnPacket(Player player) {
        AddEntityPacket packet = new AddEntityPacket();
        packet.setUniqueEntityId(this.getEntityId());
        packet.setRuntimeEntityId(this.getEntityId());
        packet.setIdentifier(this.getNetworkTypeId());
        packet.setPosition(Vector3f.ZERO);
        packet.setMotion(Vector3f.ZERO);
        packet.setHeadRotation(0);
        packet.setBodyRotation(0);
        packet.setRotation(Vector2f.ZERO);
        packet.setAttributes(this.attributeMap.getAll().values().stream().map(attr -> new AttributeData(attr.getId(), attr.getMinValue(), attr.getMaxValue(), attr.getCurrentValue(), attr.getDefaultValue())).toList());
        packet.setMetadata(this.getAllNetworkData());
        packet.setEntityLinks(List.of());
        player.getNetworkSession().sendPacket(packet);
    }

    public void sendData(List<Player> targets, EntityDataMap data) {
        NetworkBroadcastUtils.broadcastEntityEvent(targets, (EntityEventBroadcaster broadcaster, List<SculkServerSession> recipients) -> broadcaster.syncActorData(recipients, Entity.this, data));
    }

    public void sendData(List<Player> targets) {
        EntityDataMap data = this.getAllNetworkData();

        NetworkBroadcastUtils.broadcastEntityEvent(targets, (EntityEventBroadcaster broadcaster, List<SculkServerSession> recipients) -> broadcaster.syncActorData(recipients, this, data));
    }


    public void sendData(EntityDataMap data) {
        //TODO: add system viewer
        NetworkBroadcastUtils.broadcastEntityEvent(List.of(), (EntityEventBroadcaster broadcaster, List<SculkServerSession> recipients) -> broadcaster.syncActorData(recipients, this, data));
    }


}
