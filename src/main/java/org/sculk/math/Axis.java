package org.sculk.math;

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
public final class Axis {
    public static final int Y = 0;
    public static final int Z = 1;
    public static final int X = 2;
    /**
     * Returns a human-readable string representation of the given axis.
     *
     * @throws IllegalArgumentException if the axis is invalid
     */
    public static String toString(int axis) {
        return switch (axis) {
            case Y -> "y";
            case Z -> "z";
            case X -> "x";
            default -> throw new IllegalArgumentException("Invalid axis " + axis);
        };
    }
}
