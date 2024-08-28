package org.sculk.nbt;


import org.sculk.nbt.tag.ByteTag;
import org.sculk.nbt.tag.NumberTag;
import org.sculk.nbt.tag.Tag;

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
public class CompoundTag extends Tag implements Cloneable {

    private final Map<String, Tag> tags = new HashMap<>();

    public CompoundTag() {
        super("");
    }

    public CompoundTag(String name) {
        super(name);
    }

    public boolean contains(String name) {
        return tags.containsKey(name);
    }

    public Tag get(String name) {
        return tags.get(name);
    }

    public CompoundTag getCompound(String name) {
        if(!tags.containsKey(name)) return new CompoundTag(name);
        return (CompoundTag) tags.get(name);
    }

    public CompoundTag putByte(String name, int value) {
        tags.put(name, new ByteTag(name, value));
        return this;
    }

    public CompoundTag putBoolean(String name, boolean value) {
        putByte(name, value ? 1 : 0);
        return this;
    }

    public String getString(String name) {
        if(!tags.containsKey(name)) return "";
        Tag tag = tags.get(name);
        if(tags instanceof NumberTag) {
            return String.valueOf(((NumberTag<?>) tags).getData());
        }
        return "";
    }

    @Override
    public CompoundTag clone() {
        try {
            return (CompoundTag) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
