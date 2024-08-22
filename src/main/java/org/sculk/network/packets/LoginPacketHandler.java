package org.sculk.network.packets;


import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jwt.EncryptedJWT;
import lombok.SneakyThrows;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.bedrock.util.EncryptionUtils;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.jose4j.jws.JsonWebSignature;
import org.sculk.Player;
import org.sculk.Server;
import org.sculk.event.player.PlayerAsyncPreLoginEvent;
import org.sculk.event.player.PlayerPreLoginEvent;
import org.sculk.network.BedrockInterface;
import org.sculk.network.protocol.ProtocolInfo;
import org.sculk.player.PlayerLoginData;
import org.sculk.player.client.ClientChainData;
import org.sculk.player.handler.ResourcePackHandler;
import org.sculk.scheduler.AsyncTask;
import org.sculk.utils.TextFormat;
import org.sculk.utils.Utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.List;
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
public class LoginPacketHandler implements BedrockPacketHandler {

    private final BedrockServerSession session;
    private final Server server;
    private final PlayerLoginData loginData;

    private static final Pattern NAME_PATTERN = Pattern.compile("^[aA-zZ\\s\\d_]{3,16}+$");

    public LoginPacketHandler(BedrockServerSession session, Server server, BedrockInterface bedrockInterface) {
        this.session = session;
        this.server = server;
        this.loginData = new PlayerLoginData(session, server, bedrockInterface);
    }

    @SneakyThrows
    @Override
    public PacketSignal handle(LoginPacket packet) {
        this.loginData.setChainData(ClientChainData.read(packet));
        if(this.loginData.getChainData().isXboxAuthed()) {
            session.disconnect("disconnectionScreen.notAuthenticated");
            return PacketSignal.HANDLED;
        }

        String username = this.loginData.getChainData().getUsername();
        Matcher matcher = NAME_PATTERN.matcher(username);
        if(!matcher.matches() || username.equalsIgnoreCase("rcon") || username.equalsIgnoreCase("console")) {
            session.disconnect("disconnectionScreen.invalidName");
            return PacketSignal.HANDLED;
        }

        this.loginData.setName(username);
        if(!this.loginData.getChainData().getSerializedSkin().isValid()) {
            session.disconnect("disconnectionScreen.invalidSkin");
            return PacketSignal.HANDLED;
        }

        PlayerPreLoginEvent playerPreLoginEvent = new PlayerPreLoginEvent(loginData, "Sculk server");
        this.server.getEventManager().call(playerPreLoginEvent);
        if(playerPreLoginEvent.isCancelled()) {
            session.disconnect(playerPreLoginEvent.getKickMessage());
            return PacketSignal.HANDLED;
        }

        //session.setPacketHandler(new ResourcePackHandler(session, server, loginData));

        PlayerLoginData playerLoginData = loginData;
        playerLoginData.setPreLoginEventTask(new AsyncTask() {

            private PlayerAsyncPreLoginEvent playerAsyncPreLoginEvent;

            @Override
            public void onRun() {
                playerAsyncPreLoginEvent = new PlayerAsyncPreLoginEvent(loginData.getChainData());
                server.getEventManager().call(playerAsyncPreLoginEvent);
                server.getLogger().info("call async task");
            }

            @Override
            public void onCompletion(Server server) {
                server.getLogger().info("on complete");
                if(loginData.getSession().getPeer().isConnected()) {
                    loginData.setShouldLogin(true);
                    if(playerAsyncPreLoginEvent.getLoginResult() == PlayerAsyncPreLoginEvent.LoginResult.KICK) {
                        loginData.getSession().disconnect(playerAsyncPreLoginEvent.getKickMessage());
                    } else if(loginData.isShouldLogin()) {
                        server.getLogger().info("should login");
                        try {
                            Player player = loginData.initializePlayer();
                            for(Consumer<Player> action : playerAsyncPreLoginEvent.getScheduledActions()) {
                                action.accept(player);
                            }
                        } catch(Exception e) {
                            server.getLogger().info("Error in player initialization: {}", e.getMessage());
                        }
                    } else {
                        server.getLogger().info("unshould login");
                        loginData.setLoginTasks(playerAsyncPreLoginEvent.getScheduledActions());
                    }
                } else {
                    server.getLogger().info("Already connecter");
                }
            }
        });
        server.getLogger().info("send async task");
        this.server.getScheduler().scheduleAsyncTask(loginData.getPreLoginEventTask());

        PlayStatusPacket statusPacket = new PlayStatusPacket();
        statusPacket.setStatus(PlayStatusPacket.Status.LOGIN_SUCCESS);
        session.sendPacket(statusPacket);

        ServerToClientHandshakePacket serverToClientHandshakePacket = new ServerToClientHandshakePacket();
        serverToClientHandshakePacket.handle(this);

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        keyPairGenerator.initialize(Curve.P_384.toECParameterSpec());

        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        byte[] token = EncryptionUtils.generateRandomToken();

        serverToClientHandshakePacket.setJwt(EncryptionUtils.createHandshakeJwt(keyPair, token));
        System.out.println(session.getCodec().getProtocolVersion());
        session.sendPacket(serverToClientHandshakePacket);


        List<String> chain = packet.getChain();
        try {
            String jwt = chain.get(packet.getChain().size() - 1);
            JsonWebSignature jsonWebSignature = new JsonWebSignature();
            jsonWebSignature.setCompactSerialization(jwt);
        } catch(Exception e) {
            Server.getInstance().getLogger().error("JSON output error " , e.getMessage());
        }

        //packet.setProtocolVersion(712);
        //chain.removeLast();
        //packet.getChain().add();

        // TODO: View Login in log
        this.server.getLogger().info("login packet call");
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(RequestNetworkSettingsPacket packet) {
        int protocol = packet.getProtocolVersion();
        BedrockCodec codec = ProtocolInfo.getPacket(protocol);
        if(codec == null) {
            PlayStatusPacket statusPacket = new PlayStatusPacket();
            if(protocol < ProtocolInfo.CODEC.getProtocolVersion()) {
                statusPacket.setStatus(PlayStatusPacket.Status.LOGIN_FAILED_CLIENT_OLD);
            } else {
                statusPacket.setStatus(PlayStatusPacket.Status.LOGIN_FAILED_CLIENT_OLD);
            }
            session.sendPacketImmediately(statusPacket);
            return PacketSignal.HANDLED;
        }
        session.setCodec(codec);
        NetworkSettingsPacket networkSettingsPacket = new NetworkSettingsPacket();
        networkSettingsPacket.setCompressionThreshold(1);
        networkSettingsPacket.setCompressionAlgorithm(PacketCompressionAlgorithm.ZLIB);

        session.sendPacketImmediately(networkSettingsPacket);
        session.setCompression(PacketCompressionAlgorithm.ZLIB);

        // TODO: View RequestNetworkSettings in log
        this.server.getLogger().info("request network packet call");
        return PacketSignal.HANDLED;
    }

}
