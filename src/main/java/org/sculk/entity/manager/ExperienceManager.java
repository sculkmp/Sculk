package org.sculk.entity.manager;


import org.sculk.entity.Attribute;
import org.sculk.entity.AttributeFactory;
import org.sculk.entity.Entity;
import org.sculk.entity.HumanEntity;
import org.sculk.event.player.PlayerExperienceChangeEvent;
import org.sculk.utils.ExperienceUtils;

import java.util.Map;

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
public class ExperienceManager {

    private Attribute levelAttribute;
    private Attribute progressAttribute;

    private int totalXp = 0;
    private boolean canAttractXpOrbs = true;
    private int xpCooldown = 0;

    private HumanEntity humanEntity;

    public ExperienceManager(HumanEntity humanEntity) {
        this.humanEntity = humanEntity;
        this.levelAttribute = fetchAttribute(humanEntity, Attribute.EXPERIENCE_LEVEL);
        this.progressAttribute = fetchAttribute(humanEntity, Attribute.EXPERIENCE);
    }

    private static Attribute fetchAttribute(Entity entity, String attributeId) {
        Attribute attribute = AttributeFactory.getINSTANCE().mustGet(attributeId);
        // TODO next step add attribute to entity
        return attribute;
    }

    public boolean setXpAndProgress(Integer level, Float progress) {
        PlayerExperienceChangeEvent playerExperienceChangeEvent = new PlayerExperienceChangeEvent(this.humanEntity, getXpLevel(), getXpProgress(), level, progress);
        playerExperienceChangeEvent.call();

        if(playerExperienceChangeEvent.isCancelled()) {
            return false;
        }
        level = playerExperienceChangeEvent.getNewLevel();
        progress = playerExperienceChangeEvent.getNewProgress();

        if(level != null) {
            this.levelAttribute.setValue(level, true, true);
        }
        if(progress != null) {
            this.progressAttribute.setValue(progress, true, true);
        }
        return true;
    }

    public int getXpLevel() {
        return (int) this.levelAttribute.getCurrentValue();
    }

    public boolean setXpLevel(int level) {
        return this.setXpAndProgress(level, null);
    }

    public boolean addXpLevels(int amount) {
        int oldLevel = this.getXpLevel();
        return this.setXpLevel(oldLevel + amount);
    }

    public boolean substractXpLevels(int amount) {
        return this.addXpLevels(-amount);
    }

    public float getXpProgress() {
        return this.progressAttribute.getCurrentValue();
    }

    public boolean setXpProgress(float progress) {
        return this.setXpAndProgress(null, progress);
    }

    public int getRemainderXp() {
        return (int) (ExperienceUtils.getXpToCompleteLevel(this.getXpLevel()) * this.getXpProgress());
    }

    public int getCurrentTotalXp() {
        return ExperienceUtils.getXpToReachLevel(this.getXpLevel()) + this.getRemainderXp();
    }

    public boolean setCurrentTotalXp(int amount) {
        float newLevel = ExperienceUtils.getLevelFromXp(amount);
        int xpLevel = (int) (newLevel - (int) newLevel);
        float xpProgress = (int) (newLevel - (int) newLevel);
        return setXpAndProgress(xpLevel, xpProgress);
    }

    public boolean addXp(int amount) {
        amount = Math.min(amount, Integer.MAX_VALUE - this.totalXp);
        int oldLevel = this.getXpLevel();
        int oldTotal = this.getCurrentTotalXp();
        if(this.setCurrentTotalXp(oldTotal + amount)) {
            if(amount > 0) {
                this.totalXp += amount;
            }
            return true;
        }
        return false;
    }

    public boolean subtractXp(int amount) {
        return this.addXp(-amount);
    }

    public void setXpAndProgressNoEvent(int level, float progress) {
        this.levelAttribute.setValue(level, true, true);
        this.progressAttribute.setValue(progress, true, true);
    }

    public int getLifetimeTotalXp() {
        return this.totalXp;
    }

    public void setLifetimeTotalXp(int amount) {
        if(amount < 0 || amount > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("XP must be greater than 0 and less than " + Integer.MAX_VALUE);
        }
        this.totalXp = amount;
    }

    public boolean canPickupXp() {
        return this.xpCooldown == 0;
    }

    public void onPickupXp(int xpValue) {
        int mainHandIndex = -1;
        int offHandIndex = -2;

        // TODO: Logic for repair item

        this.addXp(xpValue);
        this.resetXpCooldown();
    }

    public void resetXpCooldown() {
        this.xpCooldown = 2;
    }

    public void tick(int tickDiff) {
        if(this.xpCooldown > 0) {
            this.xpCooldown = Math.max(0, this.xpCooldown - tickDiff);
        }
    }

    public boolean canAttractXpOrbs() {
        return this.canAttractXpOrbs;
    }

    public void setCanAttractXpOrbs(boolean canAttractXpOrbs) {
        this.canAttractXpOrbs = canAttractXpOrbs;
    }

}
