package org.sculk.entity;


import lombok.Getter;

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
public class Attribute {

    public static final String MC_PREFIX = "minecraft:";
    public static final String ABSORPTION = MC_PREFIX + "absorption";
    public static final String SATURATION = MC_PREFIX + "player.saturation";
    public static final String EXHAUSTION = MC_PREFIX + "player.exhaustion";
    public static final String KNOCKBACK_RESISTANCE = MC_PREFIX + "knockback_resistance";
    public static final String HEALTH = MC_PREFIX + "health";
    public static final String MOVEMENT_SPEED = MC_PREFIX + "movement";
    public static final String FOLLOW_RANGE = MC_PREFIX + "follow_range";
    public static final String HUNGER = MC_PREFIX + "player.hunger";
    public static final String FOOD = HUNGER;
    public static final String ATTACK_DAMAGE = MC_PREFIX + "attack_damage";
    public static final String EXPERIENCE_LEVEL = MC_PREFIX + "player.level";
    public static final String EXPERIENCE = MC_PREFIX + "player.experience";
    public static final String UNDERWATER_MOVEMENT = MC_PREFIX + "underwater_movement";
    public static final String LUCK = MC_PREFIX + "luck";
    public static final String FALL_DAMAGE = MC_PREFIX + "fall_damage";
    public static final String HORSE_JUMP_STRENGTH = MC_PREFIX + "horse.jump_strength";
    public static final String ZOMBIE_SPAWN_REINFORCEMENTS = MC_PREFIX + "zombie.spawn_reinforcements";
    public static final String LAVA_MOVEMENT = MC_PREFIX + "lava_movement";

    @Getter
    protected String id;
    protected boolean shouldSend;
    protected boolean desynchronized = true;
    @Getter
    protected float minValue;
    @Getter
    protected float maxValue;
    @Getter
    protected float defaultValue;
    @Getter
    protected float currentValue;

    public Attribute(String id, float minValue, float maxValue, float defaultValue, boolean shouldSend) {
        if(minValue > maxValue || defaultValue > maxValue || defaultValue < minValue) {
            throw new IllegalArgumentException("Invalid ranges: min value: " + minValue + ", max value: " + maxValue + ", " + defaultValue + ": " + defaultValue);
        }
        this.id = id;
        this.shouldSend = true;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defaultValue = defaultValue;
        this.currentValue = this.defaultValue;
    }

    public Attribute(Attribute attribute) {
        this.id = attribute.id;
        this.shouldSend = attribute.shouldSend;
        this.minValue = attribute.minValue;
        this.maxValue = attribute.maxValue;
        this.defaultValue = attribute.defaultValue;
        this.currentValue = attribute.currentValue;
    }

    public boolean isSyncable() {
        return this.shouldSend;
    }

    public boolean isDesynchronized() {
        return this.shouldSend && this.desynchronized;
    }

    public void markSynchronized(boolean synced) {
        this.desynchronized = !synced;
    }

    public Attribute setMaxValue(float maxValue) {
        float min = this.getMinValue();
        if(maxValue < min) {
            throw new IllegalArgumentException("Maximum " + maxValue + " is less than the minimum " + min);
        }
        if(this.maxValue != maxValue) {
            this.desynchronized = true;
            this.maxValue = maxValue;
        }
        return this;
    }

    public void resetToDefault() {
        setValue(getDefaultValue(), true, true);
    }

    public Attribute setDefaultValue(float defaultValue) {
        if (defaultValue > getMaxValue() || defaultValue < getMinValue()) {
            throw new IllegalArgumentException("Default " + defaultValue + " is outside the range " + getMinValue() + " - " + getMaxValue());
        }
        if (this.defaultValue != defaultValue) {
            this.desynchronized = true;
            this.defaultValue = defaultValue;
        }
        return this;
    }

    public Attribute setValue(float value, boolean fit, boolean forceSend) {
        if (value > getMaxValue() || value < getMinValue()) {
            if (!fit) {
                throw new IllegalArgumentException("Value " + value + " is outside the range " + getMinValue() + " - " + getMaxValue());
            }
            value = Math.min(Math.max(value, getMinValue()), getMaxValue());
        }
        if (this.currentValue != value) {
            this.desynchronized = true;
            this.currentValue = value;
        } else if (forceSend) {
            this.desynchronized = true;
        }
        return this;
    }

}
