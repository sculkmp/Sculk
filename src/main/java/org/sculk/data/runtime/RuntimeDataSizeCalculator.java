package org.sculk.data.runtime;

import lombok.SneakyThrows;
import org.sculk.math.Facing;
import org.sculk.utils.SculkMath;

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
public class RuntimeDataSizeCalculator implements RuntimeDataDescriber{

    private long bits = 0;

    protected void addBits(long bits) {
        this.bits += bits;
    }
    public void reset() {
        bits = 0;
    }

    public long getBitsUsed() {
        return bits;
    }

    @Override
    public void _int(long bits, AtomicLong value) {
        this.addBits(bits);
    }

    @Override
    @SneakyThrows
    public void boundedInt(long bits, long min, long max, AtomicLong value) {
        long currentBits = this.bits;
        this.boundedIntAuto(min, max, value);
        long actualBits = this.bits - currentBits;
        if (actualBits != bits) {
            throw new IllegalArgumentException("Bits should be " + actualBits + " for the given bounds, but received " + bits + ". Use boundedIntAuto() for automatic bits calculation.");
        }
    }

    @Override
    public void boundedIntAuto(long min, long max, AtomicLong value) {
        this.addBits(SculkMath.log2(max - min) + 1);
    }

    @Override
    public void bool(AtomicBoolean value) {
        this.addBits(1);
    }

    @Override
    public void horizontalFacing(AtomicInteger facing) {
        this.addBits(2);
    }

    @Override
    public void facingFlags(List<Integer> faces) {
        this.addBits(Facing.ALL.size());
    }

    @Override
    public void horizontalFacingFlags(List<Integer> faces) {
        this.addBits(Facing.HORIZONTAL.size());

    }

    @Override
    public void facing(AtomicInteger facing) {
        this.addBits(3);
    }

    @Override
    public void facingExcept(AtomicInteger facing, int except) {
        this.facing(facing);
    }

    @Override
    public void axis(AtomicInteger axis) {
        this.addBits(2);
    }

    @Override
    public void horizontalAxis(AtomicInteger axis) {
        this.addBits(1);
    }

    @Override
    public void railShape(AtomicInteger railShape) {
        this.addBits(4);
    }

    @Override
    public void straightOnlyRailShape(AtomicInteger railShape) {
        this.addBits(3);
    }
}
