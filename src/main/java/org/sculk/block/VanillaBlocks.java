package org.sculk.block;

import lombok.Getter;
import lombok.NonNull;

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
public enum VanillaBlocks {
    AIR(new AirBlock(new BlockIdentifier(BlockTypeIds.AIR), "air", new BlockTypeInfo(BlockBreakInfo.indestructible()))),
    BEDROCK(new Block(new BlockIdentifier(BlockTypeIds.BEDROCK), "bedrock", new BlockTypeInfo(BlockBreakInfo.indestructible())));
    @Getter
    private  @NonNull Block block;
    VanillaBlocks(Block block){
        this.block = block;
    }

}
