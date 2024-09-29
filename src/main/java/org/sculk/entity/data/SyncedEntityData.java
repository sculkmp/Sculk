package org.sculk.entity.data;


import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataMap;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityFlag;
import org.cloudburstmc.protocol.bedrock.packet.SetEntityDataPacket;
import org.sculk.player.Player;

import java.util.EnumSet;

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
public class SyncedEntityData {

    private final EnumSet<EntityFlag> flags = EnumSet.noneOf(EntityFlag.class);
    private final EntityDataMap entityDataMap = new EntityDataMap();

    private final Player player;

    public SyncedEntityData(Player player) {
        this.player = player;
    }

    public void updateFlag() {
        SetEntityDataPacket setEntityDataPacket = new SetEntityDataPacket();
        setEntityDataPacket.getMetadata().putFlags(this.flags);
        this.player.sendDataPacket(setEntityDataPacket);
    }

    public boolean getFlag(EntityFlag entityFlag) {
        return flags.contains(entityFlag);
    }

    public void setFlags(EntityFlag flags, boolean value) {
        if(this.flags.contains(flags) != value) {
            if(value) {
                this.flags.add(flags);
            } else {
                this.flags.remove(flags);
            }
            this.entityDataMap.putFlags(this.flags);
        }
    }

}
