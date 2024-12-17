package org.sculk.api.player;

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

public enum GameMode {
    SURVIVAL("Survival", 0),
    CREATIVE("Creative", 1),
    ADVENTURE("Adventure", 2),
    SPECTATOR("Spectator", 3);

    private final String identifier;
    private final int id;

    GameMode(String identifier, int id) {
        this.identifier = identifier;
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getId() {
        return id;
    }

    public static GameMode fromId(int id) {
        switch (id) {
            case 0:
                return SURVIVAL;
            case 1:
                return CREATIVE;
            case 2:
                return ADVENTURE;
            default:
                return SPECTATOR;
        }
    }
}
