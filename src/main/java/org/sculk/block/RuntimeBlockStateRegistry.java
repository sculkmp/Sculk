package org.sculk.block;

import lombok.Getter;
import lombok.SneakyThrows;
import org.sculk.world.light.LightUpdate;

import java.util.*;

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
public class RuntimeBlockStateRegistry {
    private static final RuntimeBlockStateRegistry instance;

    static {
        instance = new RuntimeBlockStateRegistry();
    }

    private final Map<Long, Block> typeIndex = new HashMap<>();
    private final Map<Long, Block> fullList = new HashMap<>();
    public final Map<Long, Integer> light = new HashMap<>();
    public final Map<Long, Integer> lightFilter = new HashMap<>();
    public final Map<Long, Boolean> blocksDirectSkyLight = new HashMap<>();
    public final Map<Long, Float> blastResistance = new HashMap<>();
    RuntimeBlockStateRegistry() {
        for (VanillaBlocks block: VanillaBlocks.values()){
            register(block.getBlock());
        }
    }


    /**
     * Maps a block type's state permutations to its corresponding state IDs. This is necessary for the block to be
     * recognized when fetching it by its state ID from chunks at runtime.
     *
     */
    @SneakyThrows
    public void register(Block block) {
        long typeId = block.getTypeId();

        if (typeIndex.containsKey(typeId)) {
            throw new IllegalArgumentException(STR."Block ID \{typeId} is already used by another block");
        }

        typeIndex.put(typeId, block.clone());

        block.generateStatePermutations().forEach(block1 -> {
            if (block1 == null)
                return;
            this.fillStaticArrays(block1.getStateId(), block1);
        });
    }

    private void fillStaticArrays(long index, Block block) {
        long fullId = block.getStateId();
        if (index != fullId) {
            throw new IllegalStateException("Cannot fill static arrays for an invalid block state");
        } else {
            fullList.put(index, block);
            blastResistance.put(index, block.getBreakInfo().getBlastResistance());
            light.put(index, block.getLightLevel());
            lightFilter.put(index, Math.min(15, block.getLightFilter() + LightUpdate.BASE_LIGHT_FILTER));
            if (block.blocksDirectSkyLight()) {
                blocksDirectSkyLight.put(index, true);
            }
        }
    }
    public Block fromStateId(long stateId){
        if(stateId < 0){
            throw new IllegalArgumentException("Block state ID cannot be negative");
        }
        Block block;
        if(this.fullList.containsKey(stateId)) { //hot
            block = this.fullList.get(stateId);
        }else{
            long typeId = stateId >> Block.INTERNAL_STATE_DATA_BITS;
            long stateData = (stateId ^ typeId) & Block.INTERNAL_STATE_DATA_MASK;
            block = new UnknownBlock(new BlockIdentifier(typeId), new BlockTypeInfo(BlockBreakInfo.instant()), stateData);
        }

        return block;
    }

    public Map<Long, Block> getAllKnownStates() {
        return fullList;
    }

    public static RuntimeBlockStateRegistry getInstance() {
        return instance;
    }
}
