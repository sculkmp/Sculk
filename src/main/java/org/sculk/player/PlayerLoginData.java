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


    /**
     * Determines if the player should log in.
     *
     * @return true if the player should proceed to log in, false otherwise.
     */
    public boolean isShouldLogin() {
        return shouldLogin;
    }

    /**
     * Sets the flag indicating whether a player should login.
     *
     * @param shouldLogin A boolean value representing whether the player should login.
     */
    public void setShouldLogin(boolean shouldLogin) {
        this.shouldLogin = shouldLogin;
    }

    /**
     * Retrieves the current Bedrock server session associated with the player login data.
     *
     * @return the current BedrockServerSession
     */
    public BedrockServerSession getSession() {
        return session;
    }

    /**
     * Sets the asynchronous task to be executed before the login event.
     *
     * @param asyncTask the AsyncTask to be assigned to the preLoginEventTask attribute.
     */
    public void setPreLoginEventTask(AsyncTask asyncTask) {
        this.preLoginEventTask = asyncTask;
    }

    /**
     * Retrieves the async task associated with pre-login events.
     *
     * @return the current pre-login event task, which is an instance of AsyncTask.
     */
    public AsyncTask getPreLoginEventTask() {
        return preLoginEventTask;
    }

    /**
     * Sets the login tasks to be executed on a user session during the login process.
     *
     * @param loginTasks a list of Consumer instances that define tasks to be performed on a SculkServerSession.
     */
    public void setLoginTasks(List<Consumer<SculkServerSession>> loginTasks) {
        this.loginTasks = loginTasks;
    }

    /**
     * Retrieves a list of login tasks that need to be executed for a player session.
     *
     * @return a list of Consumers that perform actions with a SculkServerSession during the login process.
     */
    public List<Consumer<SculkServerSession>> getLoginTasks() {
        return loginTasks;
    }

}
