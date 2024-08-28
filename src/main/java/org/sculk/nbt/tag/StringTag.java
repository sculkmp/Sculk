package org.sculk.nbt.tag;


import org.cloudburstmc.nbt.NBTOutputStream;

import java.io.IOException;

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
public class StringTag extends Tag {

    public String data;

    public StringTag(String name) {
        super(name);
    }

    public StringTag(String name, String data) {
        super(name);
        this.data = data;
        if(data == null) {
            throw new IllegalArgumentException("Empty string not allowed");
        }
    }

}
