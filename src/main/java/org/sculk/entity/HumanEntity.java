package org.sculk.entity;


import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.protocol.bedrock.packet.AddPlayerPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerSkinPacket;
import org.sculk.Server;
import org.sculk.data.bedrock.entity.EntityIds;
import org.sculk.entity.manager.ExperienceManager;
import org.sculk.entity.manager.HungerManager;
import org.sculk.event.player.PlayerChangeSkinEvent;
import org.sculk.network.utils.NetworkBroadcastUtils;
import org.sculk.player.Player;
import org.sculk.player.skin.Skin;
import org.sculk.utils.SkinUtils;

import java.util.List;
import java.util.UUID;

import java.util.UUID;

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
public class HumanEntity extends Living {

    @Getter
    protected HungerManager hungerManager;
    @Getter
    protected ExperienceManager experienceManager;
    protected UUID uuid;
    @Setter @Getter
    protected Skin skin;

    public HumanEntity(){
        super();
        this.uuid = UUID.randomUUID();
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.hungerManager = new HungerManager(this);
        this.experienceManager = new ExperienceManager(this);
    }

    public  String getNetworkTypeId()
    {
        return EntityIds.PLAYER;
    }

    protected EntitySizeInfo getInitialSizeInfo(){ return new EntitySizeInfo(1.8F, 0.6F, 1.62F); }

    public void spawn(Player player) {
        if (this != player){
            if (!this.skin.isValid()) {
                throw new IllegalStateException(this.getClass().getSimpleName() + " must have a valid skin set");
            }
        }
    }

    public UUID getUniqueId() {
        return this.uuid;
    }

    public void sendSkin() {
        PlayerSkinPacket skinPacket = new PlayerSkinPacket();
        skinPacket.setUuid(this.getUniqueId());
        skinPacket.setSkin(SkinUtils.toSerialized(skin));
        skinPacket.setNewSkinName(skin.getSkinId());
        skinPacket.setOldSkinName("");
        skinPacket.setTrustedSkin(true);
        NetworkBroadcastUtils.broadcastPackets(this.getViewers(), List.of(skinPacket));
    }

    public void sendSkin(List<Player> target) {
        PlayerSkinPacket skinPacket = new PlayerSkinPacket();
        skinPacket.setUuid(this.getUniqueId());
        skinPacket.setSkin(SkinUtils.toSerialized(skin));
        skinPacket.setNewSkinName(skin.getSkinId());
        skinPacket.setOldSkinName("");
        skinPacket.setTrustedSkin(true);
        NetworkBroadcastUtils.broadcastPackets(target, List.of(skinPacket));
    }

    @Override
    protected void sendSpawnPacket(Player player) {
        //TODO
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
    }

}
