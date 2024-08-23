package org.sculk.event.player;


import org.sculk.entity.HumanEntity;
import org.sculk.event.Cancellable;
import org.sculk.event.EntityEvent;

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
public class PlayerExperienceChangeEvent extends EntityEvent implements Cancellable {

    protected HumanEntity humanEntity;
    protected int oldLevel;
    protected float oldProgress;
    protected int newLevel;
    protected float newProgress;

    public PlayerExperienceChangeEvent(HumanEntity humanEntity, int oldLevel, float oldProgress, int newLevel, float newProgress) {
        this.humanEntity = humanEntity;
        this.oldLevel = oldLevel;
        this.oldProgress = oldProgress;
        this.newLevel = newLevel;
        this.newProgress = newProgress;
    }

    public int getOldLevel() {
        return oldLevel;
    }

    public float getOldProgress() {
        return oldProgress;
    }

    public int getNewLevel() {
        return newLevel;
    }

    public float getNewProgress() {
        return newProgress;
    }

    public void setNewLevel(int newLevel) {
        this.newLevel = newLevel;
    }

    public void setNewProgress(float newProgress) {
        if(newProgress < 0.0 || newProgress > 1.0) {
            throw new IllegalArgumentException("XP progress must be range 0-1");
        }
        this.newProgress = newProgress;
    }
}
