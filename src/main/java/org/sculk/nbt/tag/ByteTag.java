package org.sculk.nbt.tag;


import org.cloudburstmc.nbt.NBTInputStream;
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
public class ByteTag extends NumberTag<Integer> {

    public int data;

    @Override
    public Integer getData() {
        return data;
    }

    @Override
    public void setData(Integer data) {
        this.data = data == null ? 0 : data;
    }

    @Override
    void write(NBTOutputStream dos) throws IOException {

    }

    @Override
    public Tag copy() {
        return null;
    }

    public ByteTag(String name) {
        super(name);
    }

    public ByteTag(String name, int data) {
        super(name);
        this.data = data;
    }

}
