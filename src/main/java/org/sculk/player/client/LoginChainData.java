package org.sculk.player.client;

import org.sculk.player.skin.Skin;

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
public interface LoginChainData {

    /**
     * Retrieves the username associated with the current login session.
     *
     * @return the username of the user.
     */
    String getUsername();

    /**
     * Retrieves the unique identifier of the client.
     *
     * @return the client's UUID
     */
    UUID getClientUUID();

    /**
     * Retrieves the public key associated with the identity of the client.
     *
     * @return the identity public key as a String
     */
    String getIdentityPublicKey();

    /**
     * Retrieves the unique client identifier.
     *
     * @return the client's unique identifier as a long.
     */
    long getClientId();

    /**
     * Retrieves the server address for the current session or context.
     *
     * @return a string representing the server address.
     */
    String getServerAddress();

    /**
     * Retrieves the model of the device being used.
     *
     * @return a String representing the device model.
     */
    String getDeviceModel();

    /**
     * Retrieves the operating system identifier of the device.
     *
     * @return an integer representing the device's operating system.
     */
    int getDeviceOS();

    /**
     * Retrieves the unique identifier of the device.
     *
     * @return the device ID as a String
     */
    String getDeviceId();

    /**
     * Retrieves the version of the game being used by the client.
     *
     * @return a String representing the game version.
     */
    String getGameVersion();

    /**
     * Retrieves the GUI scale setting.
     *
     * @return the current scale factor for the graphical user interface.
     */
    int getGuiScale();

    /**
     * Retrieves the language code associated with the user's settings.
     *
     * @return a string representing the language code.
     */
    String getLanguageCode();

    /**
     * Retrieves the Xbox User ID (XUID) associated with the current login session.
     *
     * @return the XUID as a String, which is a unique identifier for Microsoft accounts.
     */
    String getXUID();

    /**
     * Checks if the user is authenticated with an Xbox account.
     *
     * @return true if the user is authenticated with an Xbox account; false otherwise.
     */
    boolean isXboxAuthed();

    /**
     * Retrieves the current input mode of the client.
     *
     * @return an integer representing the current input mode.
     */
    int getCurrentInputMode();

    /**
     * Retrieves the default input mode for the entity implementing this interface.
     *
     * @return an integer representing the default input mode.
     */
    int getDefaultInputMode();

    /**
     * Retrieves the skin associated with the current object.
     *
     * @return the Skin object representing the current skin configuration.
     */
    Skin getSkin();

    /**
     * Sets the skin data for the client.
     *
     * @param skin the Skin object containing the new skin data for the client
     */
    void setSkin(Skin skin);

    /**
     * Retrieves the UI profile setting for the client.
     *
     * @return an integer representing the UI profile, which determines the layout and
     * interface style used by the client.
     */
    int getUIProfile();

}
