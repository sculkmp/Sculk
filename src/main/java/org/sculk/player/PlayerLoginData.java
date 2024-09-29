package org.sculk.player;


import lombok.Getter;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.common.util.QuadConsumer;
import org.sculk.network.session.SculkServerSession;
import org.sculk.scheduler.AsyncTask;

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
public class PlayerLoginData {

    private final SculkServerSession session;
    private boolean shouldLogin;
    private AsyncTask preLoginEventTask;
    private List<Consumer<SculkServerSession>> loginTasks;
    @Getter
    private final QuadConsumer<Boolean, Boolean, Exception, String> authCallback;

    public PlayerLoginData(SculkServerSession serverSession, QuadConsumer<Boolean, Boolean, Exception, String> authCallback) {
        this.session = serverSession;
        this.shouldLogin = false;
        this.authCallback = authCallback;
    }


    public boolean isShouldLogin() {
        return shouldLogin;
    }

    public void setShouldLogin(boolean shouldLogin) {
        this.shouldLogin = shouldLogin;
    }

    public BedrockServerSession getSession() {
        return session;
    }

    public void setPreLoginEventTask(AsyncTask asyncTask) {
        this.preLoginEventTask = asyncTask;
    }

    public AsyncTask getPreLoginEventTask() {
        return preLoginEventTask;
    }

    public void setLoginTasks(List<Consumer<SculkServerSession>> loginTasks) {
        this.loginTasks = loginTasks;
    }

    public List<Consumer<SculkServerSession>> getLoginTasks() {
        return loginTasks;
    }

}
