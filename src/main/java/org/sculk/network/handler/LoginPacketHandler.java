package org.sculk.network.handler;

import lombok.SneakyThrows;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.cloudburstmc.protocol.common.util.QuadConsumer;
import org.sculk.Server;
import org.sculk.event.player.PlayerAsyncPreLoginEvent;
import org.sculk.event.player.PlayerPreLoginEvent;
import org.sculk.network.session.SculkServerSession;
import org.sculk.player.PlayerLoginData;
import org.sculk.player.client.ClientChainData;
import org.sculk.scheduler.AsyncTask;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class LoginPacketHandler extends SculkPacketHandler {

    private final Consumer<ClientChainData> playerInfo;
    private final PlayerLoginData loginData;

    private static final Pattern NAME_PATTERN = Pattern.compile("^[aA-zZ\\s\\d_]{3,16}+$");

    public LoginPacketHandler(SculkServerSession session, Consumer<ClientChainData> playerInfo, QuadConsumer<Boolean, Boolean, Exception, String> authCallback) {
        super(session);
        this.playerInfo = playerInfo;
        this.loginData = new PlayerLoginData(session, authCallback);
    }

    @SneakyThrows
    @Override
    public PacketSignal handle(LoginPacket packet) {
        ClientChainData clientChainData = ClientChainData.read(packet);
        if(!clientChainData.isXboxAuthed()) {
            session.disconnect("disconnectionScreen.notAuthenticated");
            return PacketSignal.HANDLED;
        }

        String username = clientChainData.getUsername();
        Matcher matcher = NAME_PATTERN.matcher(username);
        if(!matcher.matches() || username.equalsIgnoreCase("rcon") || username.equalsIgnoreCase("console")) {
            session.disconnect("disconnectionScreen.invalidName");
            return PacketSignal.HANDLED;
        }

        if(!clientChainData.getSerializedSkin().isValid()) {
            session.disconnect("disconnectionScreen.invalidSkin");
            return PacketSignal.HANDLED;
        }

        PlayerPreLoginEvent playerPreLoginEvent = new PlayerPreLoginEvent(clientChainData, session.getSocketAddress(),"Sculk server");
        playerPreLoginEvent.call();
        if(playerPreLoginEvent.isCancelled()) {
            session.disconnect(playerPreLoginEvent.getKickMessage());
            return PacketSignal.HANDLED;
        }

        //session.setPacketHandler(new ResourcePackHandler(session, loginData));

        this.playerInfo.accept(clientChainData);
        loginData.setPreLoginEventTask(new AsyncTask() {

            private PlayerAsyncPreLoginEvent playerAsyncPreLoginEvent;

            @Override
            public void onRun() {
                playerAsyncPreLoginEvent = new PlayerAsyncPreLoginEvent(session.getPlayerInfo());
                playerAsyncPreLoginEvent.call();
            }

            @Override
            public void onCompletion(Server server) {
                if(loginData.getSession().getPeer().isConnected()) {
                    loginData.setShouldLogin(true);
                    if(playerAsyncPreLoginEvent.getLoginResult() == PlayerAsyncPreLoginEvent.LoginResult.KICK) {
                        loginData.getSession().disconnect(playerAsyncPreLoginEvent.getKickMessage());
                    } else if(loginData.isShouldLogin()) {
                        Exception error = null;
                        try {
                            for(Consumer<SculkServerSession> action : playerAsyncPreLoginEvent.getScheduledActions()) {
                                action.accept(LoginPacketHandler.this.session);
                            }
                        } catch(Exception e) {
                            error = e;
                        }
                        loginData.getAuthCallback().accept(clientChainData.isXboxAuthed(), false, error, clientChainData.getIdentityPublicKey());
                    } else {
                        loginData.setLoginTasks(playerAsyncPreLoginEvent.getScheduledActions());
                    }
                } else {
                    session.disconnect("Already connected");
                }
            }
        });
        Server.getInstance().getScheduler().scheduleAsyncTask(loginData.getPreLoginEventTask());
        /*
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        keyPairGenerator.initialize(Curve.P_384.toECParameterSpec());

        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        byte[] token = EncryptionUtils.generateRandomToken();

        ServerToClientndshakePacket serverToClientHandshakePacket = new ServerToClientHandshakePacket();
        serverToClientHandshakePacket.handle(this);
        serverToClientHandshakePacket.setJwt(EncryptionUtils.createHandshakeJwt(keyPair, token));
        System.out.println(session.getCodec().getProtocolVersion());
        session.sendPacket(serverToClientHandshakePacket);*/

        //packet.setProtocolVersion(712);
        //chain.removeLast(Ha);
        //packet.getChain().add();

        // TODO: View Login in log
        return PacketSignal.HANDLED;
    }

}
