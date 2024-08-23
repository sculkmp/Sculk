package org.sculk.data.runtime;

import lombok.Getter;
import org.sculk.math.Axis;
import org.sculk.math.Facing;
import org.sculk.utils.SculkMath;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
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
public class RuntimeDataWriter implements RuntimeDataDescriber {
    private final long maxBits;
    @Getter
    private long value = 0;
    @Getter
    private long offset = 0;

    public RuntimeDataWriter(long maxBits) {
        this.maxBits = maxBits;
    }

    public void writeInt(long bits, AtomicLong value) {
        if (this.offset + bits > this.maxBits)
            throw new IllegalArgumentException("Bit buffer cannot be larger than " + this.maxBits + " bits (already have " + this.offset + " bits)");
        if ((value.get() & (~0L << bits)) != 0)
            throw new IllegalArgumentException("Value " + value + " does not fit into " + bits + " bits");
        this.value |= (value.get() << this.offset);
        this.offset += bits;
    }

    public void _int(long bits, AtomicLong value) {
        this.writeInt(bits, value);
    }

    @Override
    public void boundedInt(long bits, long min, long max, @Nullable AtomicLong value) {
        assert value != null;
        long offset = this.offset;
        this.writeBoundedIntAuto(min, max, value);
        long actualBits = this.offset - offset;
        if (actualBits != bits) {
            throw new IllegalArgumentException("Bits should be " + actualBits + " for the given bounds, but received " + bits + ". Use boundedIntAuto() for automatic bits calculation.");
        }
    }

    private void writeBoundedIntAuto(long min, long max, AtomicLong value) {
        long _value = value.get();
        if (_value < min || _value > max) {
            throw new IllegalArgumentException("Value " + _value + " is outside the range " + min + " - " + max);
        }
        long bits = SculkMath.log2(max - min) + 1;
        this.writeInt(bits, new AtomicLong(_value - min));
    }

    @Override
    public void boundedIntAuto(long min, long max, @Nullable AtomicLong value) {
        assert value != null;
        this.writeBoundedIntAuto(min, max, value);
    }

    public void writeBool(AtomicBoolean value) {
        this.writeInt(1, new AtomicLong(value.get() ? 1L : 0L));
    }

    @Override
    public void bool(AtomicBoolean value) {
        this.writeBool(value);
    }

    @Override
    public void horizontalFacing(@Nullable AtomicInteger facing) {
        assert facing != null;
        this.writeInt(2, new AtomicLong(switch (facing.get()) {
            case Facing.NORTH -> 0;
            case Facing.EAST -> 1;
            case Facing.SOUTH -> 2;
            case Facing.WEST -> 3;
            default -> throw new IllegalArgumentException("Invalid horizontal facing " + facing.get());
        }));
    }

    @Override
    public void facingFlags(@Nullable List<Integer> faces) {
        assert faces != null;
        for (int facings : Facing.ALL) {
            this.writeBool(new AtomicBoolean(faces.contains(facings)));
        }
    }

    @Override
    public void horizontalFacingFlags(@Nullable List<Integer> faces) {
        assert faces != null;
        for (int facings : Facing.HORIZONTAL) {
            this.writeBool(new AtomicBoolean(faces.contains(facings)));
        }
    }

    @Override
    public void facing(@Nullable AtomicInteger facing) {
        assert facing != null;
        this.writeInt(3, new AtomicLong(switch (facing.get()) {
            case 0 -> Facing.DOWN;
            case 1 -> Facing.UP;
            case 2 -> Facing.NORTH;
            case 3 -> Facing.SOUTH;
            case 4 -> Facing.WEST;
            case 5 -> Facing.EAST;
            default -> throw new IllegalArgumentException("Invalid horizontal facing " + facing.get());
        }));
    }

    @Override
    public void facingExcept(@Nullable AtomicInteger facing, int except) {
        this.facing(facing);
    }

    @Override
    public void axis(@Nullable AtomicInteger axis) {
        assert axis != null;
        this.writeInt(2, new AtomicLong(switch (axis.get()) {
            case Axis.X -> 0;
            case Axis.Y -> 1;
            case Axis.Z -> 2;
            default -> throw new IllegalArgumentException("Invalid axis value " + axis.get());
        }));
    }

    @Override
    public void horizontalAxis(@Nullable AtomicInteger axis) {
        assert axis != null;
        this.writeInt(1, new AtomicLong(switch (axis.get()) {
            case Axis.X -> 0;
            case Axis.Z -> 1;
            default -> throw new IllegalArgumentException("Invalid horizontal axis value " + axis.get());
        }));
    }

    @Override
    public void railShape(@Nullable AtomicInteger railShape) {
        assert railShape != null;
        this._int(4, new AtomicLong(railShape.get()));
    }

    @Override
    public void straightOnlyRailShape(@Nullable AtomicInteger railShape) {
        assert railShape != null;
        this._int(3, new AtomicLong(railShape.get()));
    }
}
