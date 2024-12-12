package org.sculk.player;


import org.sculk.Server;

import java.util.UUID;

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
public interface PlayerInterface {

    /**
     * Retrieves the name of the player.
     *
     * @return the name of the player as a String.
     */
    String getName();

    /**
     * Retrieves the server instance associated with the player.
     *
     * @return the server instance on which the player is connected or actively associated with.
     */
    Server getServer();

    /**
     * Checks if the player is currently online.
     *
     * @return true if the player is online, false otherwise.
     */
    boolean isOnline();

    /**
     * Retrieves the unique identifier for the player.
     *
     * @return a UUID representing the unique ID of the player
     */
    UUID getUniqueId();

    /**
     * Checks if the player is banned from the server.
     *
     * @return true if the player is banned, false otherwise.
     */
    boolean isBanned();

    /**
     * Sets the banned status of the player.
     *
     * @param value true to ban the player, false to unban the player.
     */
    void setBanned(boolean value);

    /**
     * Checks if the player is whitelisted.
     *
     * @return true if the player is whitelisted, false otherwise.
     */
    boolean isWhitelisted();

    /**
     * Sets the whitelisted status of a player.
     *
     * @param value true if the player should be whitelisted, false otherwise.
     */
    void setWhitelisted(boolean value);
}
