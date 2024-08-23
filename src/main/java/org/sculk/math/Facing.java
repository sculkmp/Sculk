package org.sculk.math;

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
public final class Facing {
    public static final int FLAG_AXIS_POSITIVE = 1;

    /* most significant 2 bits = axis, least significant bit = is positive direction */
    public static final int DOWN = Axis.Y << 1;
    public static final int UP = (Axis.Y << 1) | FLAG_AXIS_POSITIVE;
    public static final int NORTH = Axis.Z << 1;
    public static final int SOUTH = (Axis.Z << 1) | FLAG_AXIS_POSITIVE;
    public static final int WEST = Axis.X << 1;
    public static final int EAST = (Axis.X << 1) | FLAG_AXIS_POSITIVE;

    public static final List<Integer> ALL = List.of(
            DOWN,
            UP,
            NORTH,
            SOUTH,
            WEST,
            EAST
    );

    public static final List<Integer> HORIZONTAL = List.of(
            NORTH,
            SOUTH,
            WEST,
            EAST
    );

    public static final int[][] OFFSET = {
            {0, -1, 0},  // DOWN
            {0, 1, 0},   // UP
            {0, 0, -1},  // NORTH
            {0, 0, 1},   // SOUTH
            {-1, 0, 0},  // WEST
            {1, 0, 0}    // EAST
    };

    private static final int[][][] CLOCKWISE = {
            { // Y axis
                    {EAST, SOUTH, WEST, NORTH}, // Rotations for NORTH
            },
            { // Z axis
                    {EAST, DOWN, WEST, UP},     // Rotations for UP
            },
            { // X axis
                    {NORTH, DOWN, SOUTH, UP},   // Rotations for UP
            }
    };

    /**
     * Returns the axis of the given direction.
     */
    public static int axis(int direction) {
        return direction >> 1; //shift off positive/negative bit
    }

    /**
     * Returns whether the direction is facing the positive of its axis.
     */
    public static boolean isPositive(int direction) {
        return (direction & FLAG_AXIS_POSITIVE) == FLAG_AXIS_POSITIVE;
    }

    /**
     * Returns the opposite Facing of the specified one.
     *
     * @param direction 0-5 one of the Facing::* constants
     */
    public static int opposite(int direction) {
        return direction ^ FLAG_AXIS_POSITIVE;
    }

    /**
     * Rotates the given direction around the axis.
     *
     * @throws IllegalArgumentException if not possible to rotate direction around axis
     */
    public static int rotate(int direction, int axis, boolean clockwise) {
        if (axis >= CLOCKWISE.length || axis < 0) {
            throw new IllegalArgumentException("Invalid axis " + axis);
        }
        if (direction >= CLOCKWISE[axis].length || direction < 0) {
            throw new IllegalArgumentException("Cannot rotate facing \"" + toString(direction) + "\" around axis \"" + Axis.toString(axis) + "\"");
        }

        int rotated = CLOCKWISE[axis][direction][clockwise ? 0 : 2];
        return clockwise ? rotated : opposite(rotated);
    }

    /**
     * @throws IllegalArgumentException
     */
    public static int rotateY(int direction, boolean clockwise) {
        return rotate(direction, Axis.Y, clockwise);
    }

    /**
     * @throws IllegalArgumentException
     */
    public static int rotateZ(int direction, boolean clockwise) {
        return rotate(direction, Axis.Z, clockwise);
    }

    /**
     */
    public static int rotateX(int direction, boolean clockwise) {
        return rotate(direction, Axis.X, clockwise);
    }

    /**
     * Validates the given integer as a Facing direction.
     *
     * @throws IllegalArgumentException if the argument is not a valid Facing constant
     */
    public static void validate(int facing) {
        if (!ALL.contains(facing)) {
            throw new IllegalArgumentException("Invalid direction " + facing);
        }
    }

    /**
     * Returns a human-readable string representation of the given Facing direction.
     */
    public static String toString(int facing) {
        return switch (facing) {
            case DOWN -> "down";
            case UP -> "up";
            case NORTH -> "north";
            case SOUTH -> "south";
            case WEST -> "west";
            case EAST -> "east";
            default -> throw new IllegalArgumentException("Invalid facing " + facing);
        };
    }
}
