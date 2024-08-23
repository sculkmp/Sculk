package org.sculk.block;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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
public final class BlockTypeInfo {
    @Getter
    private BlockBreakInfo breakInfo;
    @Getter
    private List<String> typeTags;
    public BlockTypeInfo(final BlockBreakInfo breakInfo) {
        this.breakInfo = breakInfo;
        this.typeTags = new ArrayList<>();
    }

    public boolean hasTypeTag(String tag) {
        return typeTags.contains(tag);
    }
}
