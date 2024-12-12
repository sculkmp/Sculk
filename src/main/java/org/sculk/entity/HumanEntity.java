package org.sculk.entity;


import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.protocol.bedrock.packet.PlayerSkinPacket;
import org.sculk.data.bedrock.entity.EntityIds;
import org.sculk.entity.manager.ExperienceManager;
import org.sculk.entity.manager.HungerManager;
import org.sculk.network.utils.NetworkBroadcastUtils;
import org.sculk.player.Player;
import org.sculk.player.skin.Skin;
import org.sculk.utils.SkinUtils;

import java.util.List;
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

    /**
     * Retrieves the network type identifier for the entity.
     *
     * @return a string representing the network type ID, specifically "PLAYER".
     */
    public String getNetworkTypeId()
    {
        return EntityIds.PLAYER;
    }

    /**
     * Retrieves the initial size information for an entity.
     *
     * @return an EntitySizeInfo object containing the default dimensions: height of 1.8, width of 0.6 and eye height of 1.62.
     */
    protected EntitySizeInfo getInitialSizeInfo(){ return new EntitySizeInfo(1.8F, 0.6F, 1.62F); }

    /**
     * Handles the spawning process for a player entity. It checks for a valid skin
     * before allowing the entity to spawn if it is not the same as the player parameter.
     *
     * @param player the player entity that is to be spawned. It is used to
     *               differentiate between the player initiating the spawn and other entities.
     * @throws IllegalStateException if the skin of the entity is not valid when
     *                               the spawning is attempted.
     */
    public void spawn(Player player) {
        if (this != player){
            if (!this.skin.isValid()) {
                throw new IllegalStateException(this.getClass().getSimpleName() + " must have a valid skin set");
            }
        }
    }

    /**
     * Retrieves the unique identifier for this entity.
     *
     * @return a UUID representing the unique identifier of the entity.
     */
    public UUID getUniqueId() {
        return this.uuid;
    }

    /**
     * Sends a PlayerSkinPacket to the specified list of target players. The packet
     * contains the skin data associated with this entity, allowing other players
     * to view the updated skin.
     *
     * @param target a list of Player objects representing the recipients of the skin packet.
     *               Each player in this list will receive the updated skin information.
     */
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
