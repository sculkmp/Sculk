package org.sculk.data.runtime;

import lombok.SneakyThrows;
import org.sculk.block.InvalidSerializedRuntimeDataException;

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
 * it under the terms of the GNU Lesser General License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author: SculkTeams
 * @link: http://www.sculkmp.org/
 */
public interface RuntimeDataDescriber {
    void _int(long bits, AtomicLong value);

    /**
     * @deprecated Use {@link RuntimeDataDescriber::boundedIntAuto()} instead.
     */
    @SneakyThrows(InvalidSerializedRuntimeDataException.class)
    void boundedInt(long bits, long min, long max, @Nullable AtomicLong value);

    /**
     * Same as boundedInt() but automatically calculates the required number of bits from the range.
     * The range bounds must be constant.
     */
    @SneakyThrows(InvalidSerializedRuntimeDataException.class)
    void boundedIntAuto(long min, long max, @Nullable AtomicLong value);

    void bool(AtomicBoolean value);

    void horizontalFacing(@Nullable AtomicInteger facing);

    void facingFlags(@Nullable List<Integer> faces);

    void horizontalFacingFlags(@Nullable List<Integer> faces);

    @SneakyThrows(InvalidSerializedRuntimeDataException.class)
    void facing(@Nullable AtomicInteger facing);

    @SneakyThrows(InvalidSerializedRuntimeDataException.class)
    void facingExcept(@Nullable AtomicInteger facing, int except);

    @SneakyThrows(InvalidSerializedRuntimeDataException.class)
    void axis(@Nullable AtomicInteger axis);

    void horizontalAxis(@Nullable AtomicInteger axis);

    void railShape(@Nullable AtomicInteger railShape);

    void straightOnlyRailShape(@Nullable AtomicInteger railShape);
}
