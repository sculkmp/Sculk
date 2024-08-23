package org.sculk.utils;


/*
 *   ____             _ _
 *  / ___|  ___ _   _| | | __
 *  \___ \ / __| | | | | |/ /
 *   ___) | (__| |_| | |   <
 *  |____/ \___|\__,_|_|_|\_\
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author: SculkTeams
 * @link: http://www.sculkmp.org/
 */
public abstract class ExperienceUtils {

    public static int getXpToReachLevel(int level) {
        if (level <= 16) {
            return level * level + level * 6;
        } else if (level <= 31) {
            return (int) (level * level * 2.5 - 40.5 * level + 360);
        }
        return (int) (level * level * 4.5 - 162.5 * level + 2220);
    }

    public static int getXpToCompleteLevel(int level) {
        if(level <= 15) {
            return 2 * level + 7;
        }
        else if(level <= 30) {
            return 5 * level - 38;
        } else {
            return 9 * level - 158;
        }
    }

    public static float getLevelFromXp(int xp) {
        if(xp < 0) {
            throw new IllegalArgumentException("XP must be at least 0");
        }
        double a, b, c;

        if (xp <= getXpToReachLevel(16)) {
            a = 1;
            b = 6;
            c = 0;
        } else if (xp <= getXpToReachLevel(31)) {
            a = 2.5;
            b = -40.5;
            c = 360;
        } else {
            a = 4.5;
            b = -162.5;
            c = 2220;
        }

        double[] solutions = solveQuadratic(a, b, c - xp);
        if(solutions.length == 0) {
            throw new RuntimeException("Expected at least 1 solution");
        }
        return (float) Math.max(solutions[0], solutions[1]);
    }

    private static double[] solveQuadratic(double a, double b, double c) {
        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) {
            return new double[0];
        } else if (discriminant == 0) {
            return new double[] { -b / (2 * a) };
        } else {
            double sqrtDiscriminant = Math.sqrt(discriminant);
            return new double[] {
                    (-b + sqrtDiscriminant) / (2 * a),
                    (-b - sqrtDiscriminant) / (2 * a)
            };
        }
    }

}
