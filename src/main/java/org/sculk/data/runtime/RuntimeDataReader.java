package org.sculk.data.runtime;

import lombok.Getter;
import lombok.SneakyThrows;
import org.sculk.block.InvalidSerializedRuntimeDataException;
import org.sculk.math.Axis;
import org.sculk.math.Facing;
import org.sculk.utils.SculkMath;

import javax.annotation.Nullable;
import java.util.ArrayList;
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
public class RuntimeDataReader implements RuntimeDataDescriber{
    private final long maxBits;
    @Getter
    private long value = 0;
    @Getter
    private long offset = 0;
    public RuntimeDataReader(long maxBits,
                             long value){
        this.maxBits = maxBits;
        this.value = value;
    }


    /**
     * Lit un entier sur un nombre spécifié de bits.
     *
     * @param bits Nombre de bits à lire.
     * @return L'entier lu.
     * @throws IllegalArgumentException Si les bits demandés dépassent le buffer.
     */
    public long readInt(long bits) {
        long bitsLeft = maxBits - offset;
        if (bits > bitsLeft) {
            throw new IllegalArgumentException("No bits left in buffer (need " + bits + ", have " + bitsLeft + ")");
        }

        int mask = (~0 >>> (32 - bits));
        long result = (value >> offset) & mask;
        offset += bits;
        return result;
    }
    /**
     * Lit un entier sur un nombre spécifié de bits.
     *
     * @param bits Nombre de bits à lire.
     * @return L'entier lu.
     * @throws IllegalArgumentException Si les bits demandés dépassent le buffer.
     */
    public int readInt(int bits) {
        long bitsLeft = maxBits - offset;
        if (bits > bitsLeft) {
            throw new IllegalArgumentException("No bits left in buffer (need " + bits + ", have " + bitsLeft + ")");
        }

        int mask = (~0 >>> (32 - bits));
        int result = (int) ((value >> offset) & mask);
        offset += bits;
        return result;
    }

    public void _int(long bits, AtomicLong value) {
        value.set(readInt(bits));
    }

    @Override
    public void boundedInt(long bits, long min, long max, AtomicLong valueRef) {
        long startOffset = this.offset;
        this.boundedIntAuto(min, max, valueRef);
        long actualBits = this.offset - startOffset;
        if (this.offset != startOffset + bits) {
            throw new IllegalArgumentException("Bits should be " + actualBits + " for the given bounds, but received " + bits + ". Use boundedIntAuto() for automatic bits calculation.");
        }
    }

    @SneakyThrows
    private long readBoundedIntAuto(long min, long max) {
        long bits = SculkMath.log2(max - min) + 1;
        long result = this.readInt(bits) + min;
        if (result < min || result > max) {
            throw new InvalidSerializedRuntimeDataException("Value is outside the range " + min + " - " + max);
        }
        return result;
    }
    public void boundedIntAuto(long min, long max, AtomicLong valueRef) {
        valueRef.set(this.readBoundedIntAuto(min, max));
    }
    public boolean readBool() {
        return this.readInt(1) == 1;
    }
    @Override
    public void bool(AtomicBoolean value) {
        value.set(this.readBool());
    }

    @Override
    public void horizontalFacing(@Nullable AtomicInteger facingRef) {
        int facing = switch (this.readInt(2)) {
            case 0 -> Facing.NORTH;
            case 1 -> Facing.EAST;
            case 2 -> Facing.SOUTH;
            case 3 -> Facing.WEST;
            default -> throw new AssertionError("Unreachable");
        };
        facingRef.set(facing);
    }

    @Override
    public void facingFlags(@Nullable List<Integer> facesRef) {
        assert facesRef != null;
        List<Integer> tmp = new ArrayList<>();
        for (int facing : Facing.ALL) {
            if (this.readBool()) {
                tmp.add(facing);
            }
        }
        facesRef.addAll(tmp);
    }

    @Override
    public void horizontalFacingFlags(@Nullable List<Integer> facesRef) {
        assert facesRef != null;
        List<Integer> tmp = new ArrayList<>();
        for (int facing: Facing.HORIZONTAL)
        {
            if (this.readBool()) {
                tmp.add(facing);
            }
        }
        facesRef.addAll(tmp);

    }

    @Override
    @SneakyThrows
    public void facing(@Nullable AtomicInteger facingRef) {
        assert facingRef != null;
        int facing = switch (this.readInt(3)) {
            case 0 -> Facing.DOWN;
            case 1 -> Facing.UP;
            case 2 -> Facing.NORTH;
            case 3 -> Facing.SOUTH;
            case 4 -> Facing.WEST;
            case 5 -> Facing.EAST;
            default -> throw new InvalidSerializedRuntimeDataException("Invalid facing value");
        };
        facingRef.set(facing);
    }

    @Override
    @SneakyThrows
    public void facingExcept(@Nullable AtomicInteger facingRef, int except) {
        assert facingRef != null;
        this.facing(facingRef);
        if (facingRef.get() == except) {
            throw new InvalidSerializedRuntimeDataException("Illegal facing value");
        }
    }

    @Override
    @SneakyThrows
    public void axis(AtomicInteger axisRef) {
        int axis = switch (this.readInt(2)) {
            case 0 -> Axis.X;
            case 1 -> Axis.Z;
            case 2 -> Axis.Y;
            default -> throw new InvalidSerializedRuntimeDataException("Invalid axis value");
        };
        axisRef.set(axis);
    }

    @Override
    public void horizontalAxis(AtomicInteger axisRef) {
        int axis = switch (this.readInt(1)) {
            case 0 -> Axis.X;
            case 1 -> Axis.Z;
            default -> throw new AssertionError("Unreachable");
        };
        axisRef.set(axis);
    }

    @Override
    public void railShape(AtomicInteger railShapeRef) {
        assert railShapeRef != null;
        int result = this.readInt(4);
        /*if (!RailConnectionInfo.CONNECTIONS.containsKey(result) && !RailConnectionInfo.CURVE_CONNECTIONS.containsKey(result)) {
            throw new InvalidSerializedRuntimeDataException("Invalid rail shape " + result);
        }*/
        railShapeRef.set(result);
    }

    @Override
    public void straightOnlyRailShape(AtomicInteger railShapeRef) {
        assert railShapeRef != null;
        int result = this.readInt(3);
        /*if (!RailConnectionInfo.CONNECTIONS.containsKey(result)) {
            throw new InvalidSerializedRuntimeDataException("No rail shape matches meta " + result);
        }*/
        railShapeRef.set(result);
    }
}
