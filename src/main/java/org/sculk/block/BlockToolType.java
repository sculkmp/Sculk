package org.sculk.block;
/*
 *   ____             _ _              __  __ ____
 *  / ___|  ___ _   _| | | __         |  \/  |  _ \
 *  \___ \ / __| | | | | |/ /  _____  | |\/| | |_) |
 *   ___) | (__| |_| | |   <  |_____| | |  | |  __/
 *  |____/ \___|\__,_|_|_|\_\         |_|  |_|_|
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author: SculkTeams
 * @link: http://www.sculkmp.org/
 */
public enum BlockToolType {
    NONE(0),
    SWORD(1),
    SHOVEL(1 << 1),
    PICKAXE(1 << 2),
    AXE(1 << 3),
    SHEARS(1 << 4),
    HOE(1 << 5);
    

    private int value;
    BlockToolType(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
