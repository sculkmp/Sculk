package org.sculk.player;


import org.apache.logging.log4j.Level;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.sculk.Player;
import org.sculk.Server;
import org.sculk.event.player.PlayerCreationEvent;
import org.sculk.network.BedrockInterface;
import org.sculk.player.client.ClientChainData;
import org.sculk.scheduler.AsyncTask;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Consumer;

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
public class PlayerLoginData {

    private final BedrockServerSession session;
    private final Server server;
    private final BedrockInterface bedrockInterface;

    private boolean shouldLogin;
    private String username;

    private ClientChainData chainData;
    private AsyncTask preLoginEventTask;
    private List<Consumer<Player>> loginTasks;

    public PlayerLoginData(BedrockServerSession serverSession, Server server, BedrockInterface bedrockInterface) {
        this.session = serverSession;
        this.server = server;
        this.bedrockInterface = bedrockInterface;
        this.shouldLogin = false;
    }

    public ClientChainData getChainData() {
        return this.chainData;
    }

    public boolean isShouldLogin() {
        return shouldLogin;
    }

    public BedrockServerSession getSession() {
        return session;
    }

    public void setChainData(ClientChainData data) {
        this.chainData = data;
    }

    public void setName(String username) {
        this.username = username;
    }

    public String getName() {
        return this.username;
    }

    public Player initializePlayer() {
        Player player;

        PlayerCreationEvent event = new PlayerCreationEvent(bedrockInterface, Player.class, Player.class, this.chainData.getClientId(), session.getSocketAddress());
        this.server.getEventManager().fire(event);
        Class<? extends Player> clazz = (Class<? extends Player>) event.getPlayerClass();

        try {
            Constructor<? extends Player> constructor = clazz.getConstructor(BedrockServerSession.class, ClientChainData.class);
            player = constructor.newInstance(session, chainData);
            this.server.addPlayer(session.getSocketAddress(), player);
        } catch(NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            this.server.getLogger().throwing(Level.ERROR, e);
            return null;
        }

        player.completeLogin();
        return player;
    }

    public void setPreLoginEventTask(AsyncTask asyncTask) {
        this.preLoginEventTask = asyncTask;
    }

    public AsyncTask getPreLoginEventTask() {
        return preLoginEventTask;
    }

    public void setLoginTasks(List<Consumer<Player>> loginTasks) {
        this.loginTasks = loginTasks;
    }

    public List<Consumer<Player>> getLoginTasks() {
        return loginTasks;
    }

}
