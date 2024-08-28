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
public abstract class NumberTag<T extends Number> extends Tag {

    public NumberTag(String name) {
        super(name);
    }

    public abstract T getData();
    public abstract void setData(T data);

    abstract void write(NBTOutputStream dos) throws IOException;

    public abstract Tag copy();
}
