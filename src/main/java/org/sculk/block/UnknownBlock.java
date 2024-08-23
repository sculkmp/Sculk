package org.sculk.block;

import lombok.Getter;
import org.sculk.data.runtime.RuntimeDataDescriber;

import java.util.concurrent.atomic.AtomicLong;

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
public class UnknownBlock extends Block{
    @Getter
    private long stateData;
    public UnknownBlock(BlockIdentifier identifier, BlockTypeInfo blockTypeInfo, long stateData) {
        super(identifier, "Unknown", blockTypeInfo);
        this.stateData = stateData;
    }

    public void describeBlockItemState(RuntimeDataDescriber w){
        AtomicLong data = new AtomicLong(stateData);
        w._int(Block.INTERNAL_STATE_DATA_BITS, data);
        this.stateData = data.get();
    }
}
