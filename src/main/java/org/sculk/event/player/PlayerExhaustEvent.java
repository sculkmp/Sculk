package org.sculk.event.player;


import lombok.Getter;
import org.sculk.entity.Entity;
import org.sculk.entity.HumanEntity;
import org.sculk.event.Cancellable;
import org.sculk.event.EntityEvent;
import org.sculk.event.Event;
import org.sculk.player.client.ClientChainData;

import java.net.SocketAddress;

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
public class PlayerExhaustEvent extends EntityEvent implements Cancellable {

    protected HumanEntity humanEntity;
    protected float amount;
    protected int cause;

    public static final int CAUSE_ATTACK = 1;
    public static final int CAUSE_DAMAGE = 2;
    public static final int CAUSE_MINING = 3;
    public static final int CAUSE_HEALTH_REGEN = 4;
    public static final int CAUSE_POTION = 5;
    public static final int CAUSE_WALKING = 6;
    public static final int CAUSE_SPRINTING = 7;
    public static final int CAUSE_SWIMMING = 8;
    public static final int CAUSE_JUMPING = 9;
    public static final int CAUSE_SPRINT_JUMPING = 10;
    public static final int CAUSE_CUSTOM = 11;

    public PlayerExhaustEvent(HumanEntity humanEntity, float amount, int cause) {
        this.humanEntity = humanEntity;
        this.amount = amount;
        this.cause = cause;
    }

    public Entity getPlayer() {
        return this.humanEntity;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getCause() {
        return cause;
    }

}
