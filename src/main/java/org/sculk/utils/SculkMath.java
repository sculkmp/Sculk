package org.sculk.utils;

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
public class SculkMath {

    public static long log2(long N)
    {

        // calculate log2 N indirectly
        // using log() method

        return (long)(Math.log(N) / Math.log(2));
    }

    public static int log2(int N)
    {

        // calculate log2 N indirectly
        // using log() method

        return (int)(Math.log(N) / Math.log(2));
    }
}
