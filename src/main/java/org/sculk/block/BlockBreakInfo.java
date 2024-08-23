package org.sculk.block;

import jline.internal.Nullable;
import lombok.Getter;

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
public class BlockBreakInfo {
    /**
     * If the tool is the correct type and high enough harvest level (tool tier), base break time is hardness multiplied
     * by this value.
     */
    public final static float COMPATIBLE_TOOL_MULTIPLIER = 1.5F;
    /**
     * If the tool is an incorrect type or too low harvest level (tool tier), base break time is hardness multiplied by
     * this value.
     */
    public final static float  INCOMPATIBLE_TOOL_MULTIPLIER = 5.0F;
    @Getter
    private float hardness;
    @Getter
    private BlockToolType toolType;
    @Getter
    private int toolHarvestLevel;
    @Getter
    private float blastResistance;

    BlockBreakInfo(float hardness, BlockToolType toolType, int toolHarvestLevel, float blastResistance) {
        this.hardness = hardness;
        this.toolType = toolType;
        this.toolHarvestLevel = toolHarvestLevel;
        this.blastResistance = blastResistance;
    }

    BlockBreakInfo(float hardness, BlockToolType toolType, int toolHarvestLevel) {
        this.hardness = hardness;
        this.toolType = toolType;
        this.toolHarvestLevel = toolHarvestLevel;
        this.blastResistance = hardness * 5F;
    }
    BlockBreakInfo(float hardness, BlockToolType toolType) {
        this.hardness = hardness;
        this.toolType = toolType;
        this.toolHarvestLevel = 0;
        this.blastResistance = hardness * 5F;
    }

    public static BlockBreakInfo instant() {
        return instant(BlockToolType.NONE);
    }
    public static BlockBreakInfo instant(BlockToolType toolType) {
        return instant(toolType, 0);
    }

    public static BlockBreakInfo instant(BlockToolType toolType, int toolHarvestLevel) {
        return new BlockBreakInfo(0, toolType, toolHarvestLevel);
    }

    public static BlockBreakInfo indestructible(float blastResistance) {
        return new BlockBreakInfo(-1.0F, BlockToolType.NONE, 0, blastResistance);
    }
    public static BlockBreakInfo indestructible() {
        return new BlockBreakInfo(-1.0F, BlockToolType.NONE, 0, 18000000.0F);
    }

}
