package org.sculk.entity;


import lombok.Getter;
import org.cloudburstmc.protocol.bedrock.data.AttributeData;

import java.util.HashMap;
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
public final class AttributeFactory {

    @Getter
    private static final AttributeFactory INSTANCE = new AttributeFactory();
    private final Map<String, Attribute> attributeMap = new HashMap<>();

    private AttributeFactory() {
        register(Attribute.ABSORPTION, 0.00f, 340282346638528859811704183484516925440.00f, 0.00f);
        register(Attribute.SATURATION, 0.00f, 20.00f, 20.00f);
        register(Attribute.EXHAUSTION, 0.00f, 5.00f, 0.0f, false);
        register(Attribute.KNOCKBACK_RESISTANCE, 0.00f, 1.00f, 0.00f);
        register(Attribute.HEALTH, 0.00f, 20.00f, 20.00f);
        register(Attribute.MOVEMENT_SPEED, 0.00f, 340282346638528859811704183484516925440.00f, 0.10f);
        register(Attribute.FOLLOW_RANGE, 0.00f, 2048.00f, 16.00f, false);
        register(Attribute.HUNGER, 0.00f, 20.00f, 20.00f);
        register(Attribute.ATTACK_DAMAGE, 0.00f, 340282346638528859811704183484516925440.00f, 1.00f, false);
        register(Attribute.EXPERIENCE_LEVEL, 0.00f, 24791.00f, 0.00f);
        register(Attribute.EXPERIENCE, 0.00f, 1.00f, 0.00f);
        register(Attribute.UNDERWATER_MOVEMENT, 0.0f, 340282346638528859811704183484516925440.00f, 0.02f);
        register(Attribute.LUCK, -1024.0f, 1024.0f, 0.0f);
        register(Attribute.FALL_DAMAGE, 0.0f, 340282346638528859811704183484516925440.00f, 1.0f);
        register(Attribute.HORSE_JUMP_STRENGTH, 0.0f, 2.0f, 0.7f);
        register(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS, 0.0f, 1.0f, 0.0f);
        register(Attribute.LAVA_MOVEMENT, 0.0f, 340282346638528859811704183484516925440.00f, 0.02f);
    }

    public Attribute get(String id) {
        Attribute attribute = attributeMap.get(id);
        return attribute != null ? new Attribute(attribute) : null;
    }

    public Attribute mustGet(String id) {
        Attribute result = get(id);
        if (result == null) {
            throw new IllegalArgumentException("Attribute " + id + " is not registered");
        }
        return result;
    }

    public Attribute register(String id, float minValue, float maxValue, float defaultValue) {
        return register(id, minValue, maxValue, defaultValue, true);
    }

    public Attribute register(String id, float minValue, float maxValue, float defaultValue, boolean shouldSend) {
        Attribute attribute = new Attribute(id, minValue, maxValue, defaultValue, shouldSend);
        attributeMap.put(id, attribute);
        return attribute;
    }

}
