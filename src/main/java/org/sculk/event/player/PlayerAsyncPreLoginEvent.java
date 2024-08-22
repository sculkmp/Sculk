package org.sculk.event.player;


import org.sculk.Player;
import org.sculk.network.session.SculkServerSession;
import org.sculk.player.client.LoginChainData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
public final class PlayerAsyncPreLoginEvent extends PlayerEvent {

    private final LoginChainData loginChainData;

    public enum LoginResult {
        SUCCESS,
        KICK
    }
    private LoginResult loginResult = LoginResult.SUCCESS;
    private String kickMessage = "Sculk server";

    private final List<Consumer<SculkServerSession>> scheduledActions = new ArrayList<>();

    public PlayerAsyncPreLoginEvent(LoginChainData loginChainData) {
        super(null);
        this.loginChainData = loginChainData;
    }

    public LoginChainData getLoginChainData() {
        return loginChainData;
    }

    public LoginResult getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(LoginResult loginResult) {
        this.loginResult = loginResult;
    }

    public String getKickMessage() {
        return kickMessage;
    }

    public void setKickMessage(String kickMessage) {
        this.kickMessage = kickMessage;
    }

    public List<Consumer<SculkServerSession>> getScheduledActions() {
        return new ArrayList<>(scheduledActions);
    }

    @Override
    public Player getPlayer() {
        throw new UnsupportedOperationException("No player instance provided in async event");
    }

    public void disAllow(String message) {
        this.loginResult = LoginResult.KICK;
        this.kickMessage = message;
    }

    public void allow() {
        this.loginResult = LoginResult.SUCCESS;
    }

    public void scheduleSyncAction(Consumer<SculkServerSession> action) {
        this.scheduledActions.add(action);
    }

}
