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

    String getUsername();
    UUID getClientUUID();
    String getIdentityPublicKey();
    long getClientId();
    String getServerAddress();
    String getDeviceModel();
    int getDeviceOS();
    String getDeviceId();
    String getGameVersion();
    int getGuiScale();
    String getLanguageCode();
    String getXUID();
    boolean isXboxAuthed();
    int getCurrentInputMode();
    int getDefaultInputMode();
    Skin getSkin();
    void setSkin(Skin skin);
    int getUIProfile();

}
