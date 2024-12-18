package org.sculk.network.session;


import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.sculk.Server;
import org.sculk.lang.Translatable;
import org.sculk.network.BedrockInterface;
import org.sculk.network.broadcaster.EntityEventBroadcaster;
import org.sculk.network.broadcaster.PacketBroadcaster;
import org.sculk.network.handler.*;
import org.sculk.player.Player;
import org.sculk.player.client.ClientChainData;
import org.sculk.player.text.RawTextBuilder;
import org.sculk.utils.SkinUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
@Getter
public class SculkServerSession extends BedrockServerSession {

    private final Server server;
    private final BedrockInterface bedrockInterface;

    private final PacketBroadcaster broadcaster;
    private final EntityEventBroadcaster entityEventBroadcaster;
    private @Nullable Player player;
    @Setter
    private @Nullable ClientChainData playerInfo;
    public SculkServerSession(BedrockInterface bedrockInterface, PacketBroadcaster broadcaster, EntityEventBroadcaster entityEventBroadcaster, Server server, SculkPeer peer, int subClientId) {
        super(peer, subClientId);
        this.server = server;
        this.broadcaster = broadcaster;
        this.entityEventBroadcaster = entityEventBroadcaster;
        this.bedrockInterface = bedrockInterface;

        this.setPacketHandler(new SessionStartPacketHandler(this, this::onSessionStartSuccess));
    }

    private void onSessionStartSuccess(Object e) {
        this.setPacketHandler(new LoginPacketHandler(
                this,
                _playerInfo -> this.playerInfo = _playerInfo,
                this::setAuthenticationStatus
        ));
    }


    public void sendPacket(@NonNull List<BedrockPacket> packet) {
        ((SculkPeer)this.peer).sendPacket(this.subClientId, 0, packet);
        this.logOutbound(packet);
    }



    protected void logOutbound(List<BedrockPacket> packets) {
        packets.forEach(this::logOutbound);
    }

    private void setAuthenticationStatus(boolean authenticated, boolean authRequired, Exception error, String clientPubKey) {
        if(error == null){
            if(authenticated && Objects.requireNonNull(playerInfo).getXUID() == null){
                error = new Exception("Expected XUID but none found");
            }else if(clientPubKey == null){
                error = new Exception("Missing client public key"); //failsafe
            }
        }
        if (error != null){
            this.disconnect(error.getMessage());
            return;
        }
        if(!authenticated){
            if(authRequired){
                this.disconnect("Not authenticated");
                return;
            }
        }
        this.onServerLoginSuccess();
    }

    private void onServerLoginSuccess() {
        this.setLogging(true);
        PlayStatusPacket statusPacket = new PlayStatusPacket();
        statusPacket.setStatus(PlayStatusPacket.Status.LOGIN_SUCCESS);
        this.sendPacket(statusPacket);
        this.setPacketHandler(new ResourcePackHandler(this, this::createPlayer));
    }

    private void createPlayer(Object e) {
        this.server.createPlayer(this, this.playerInfo, false).thenAccept(this::onPlayerCreated).exceptionally(ex -> {
            server.getLogger().throwing(ex);
            this.disconnect("Failed to create player.");
            return  null;
        });
    }

    private void onPlayerCreated(Player player) {
        this.player = player;
        this.setPacketHandler(new PreSpawnPacketHandler(this, player));
        this.notifyTerrainReady(null);
    }

    private void notifyTerrainReady(Object e) {

        PlayStatusPacket packet = new PlayStatusPacket();
        packet.setStatus(PlayStatusPacket.Status.PLAYER_SPAWN);
        this.sendPacket(packet);
        this.setPacketHandler(new SpawnResponsePacketHandler(this, this::onClientSpawnResponse));
    }

    private void onClientSpawnResponse(Object e) {
        this.setPacketHandler(new InGamePacketHandler(this.getPlayer(),this));
        assert this.player != null;
        this.player.doFirstSpawn();
    }



    public void syncPlayerList(Map<UUID, Player> players) {
        PlayerListPacket packet = new PlayerListPacket();
        packet.setAction(PlayerListPacket.Action.ADD);
        packet.getEntries().addAll(players.values().stream().map(p -> {
            PlayerListPacket.Entry entry = new PlayerListPacket.Entry(p.getUniqueId());
            String xuid = p.getXuid();
            entry.setEntityId(p.getEntityId());
            entry.setName(p.getName());
            entry.setXuid(xuid != null ? xuid : "");
            entry.setSkin(SkinUtils.toSerialized(p.getSkin()));
            entry.setPlatformChatId("");
            entry.setTrustedSkin(true);
            entry.setBuildPlatform(-1);
            entry.setHost(false);
            entry.setSubClient(false);
            return entry;
        }).toList());
        this.sendPacket(packet);
    }


    public void onPlayerAdded(Player p){
        PlayerListPacket packet = new PlayerListPacket();
        packet.setAction(PlayerListPacket.Action.ADD);
        PlayerListPacket.Entry entry = new PlayerListPacket.Entry(p.getUniqueId());
        String xuid = p.getXuid();
        entry.setEntityId(p.getEntityId());
        entry.setName(p.getName());
        entry.setXuid(xuid != null ? xuid : "");
        entry.setSkin(SkinUtils.toSerialized(p.getSkin()));
        entry.setPlatformChatId("");
        entry.setTrustedSkin(true);
        entry.setBuildPlatform(-1);
        entry.setHost(false);
        entry.setSubClient(false);
        packet.getEntries().add(entry);
    }

    public void onPlayerRemoved(Player p) {
        if(!p.equals(this.player)){
            PlayerListPacket packet = new PlayerListPacket();
            packet.setAction(PlayerListPacket.Action.REMOVE);
            PlayerListPacket.Entry entry = new PlayerListPacket.Entry(p.getUniqueId());
            packet.getEntries().add(entry);
        }
    }

    @Override
    public void setPacketHandler(@NonNull BedrockPacketHandler packetHandler) {
        super.setPacketHandler(packetHandler);
        if (packetHandler instanceof SculkPacketHandler _sculkHandler)
            _sculkHandler.setUp();
    }

    public void onChatMessage(String message) {
        TextPacket packet = new TextPacket();
        packet.setXuid("");
        packet.setType(TextPacket.Type.RAW);
        packet.setMessage(message);
        this.sendPacket(packet);
    }

    public void onChatMessage(RawTextBuilder textBuilder) {
        assert this.player != null;
        TextPacket packet = new TextPacket();
        packet.setXuid("");
        packet.setSourceName("");
        packet.setType(TextPacket.Type.JSON);
        packet.setMessage(new Gson().toJson(textBuilder.build(this.player.getLanguage())));
        this.sendPacket(packet);
    }

    public void onChatMessage(Translatable<?> translatable) {
        TextPacket packet = new TextPacket();
        packet.setXuid("");
        packet.setSourceName("");
        packet.setType(TextPacket.Type.RAW);
        if (this.player != null)
            packet.setMessage(this.player.getLanguage().translate(translatable));
        this.sendPacket(packet);
    }

    public void onJukeboxPopup(String message) {
        TextPacket packet = new TextPacket();
        packet.setType(TextPacket.Type.JUKEBOX_POPUP);
        packet.setMessage(message);
        this.sendPacket(packet);
    }

    public void onPopup(String message) {
        TextPacket packet = new TextPacket();
        packet.setType(TextPacket.Type.POPUP);
        packet.setXuid("");
        packet.setMessage(message);
        this.sendPacket(packet);
    }

    public void onTip(String message) {
        TextPacket packet = new TextPacket();
        packet.setType(TextPacket.Type.TIP);
        packet.setXuid("");
        packet.setMessage(message);
        this.sendPacket(packet);
    }

    public void onAnnouncement(String message) {
        TextPacket packet = new TextPacket();
        packet.setType(TextPacket.Type.ANNOUNCEMENT);
        packet.setXuid("");
        packet.setSourceName("");
        packet.setMessage(message);
        this.sendPacket(packet);
    }

    public void onAnnouncement(RawTextBuilder textBuilder) {
        TextPacket packet = new TextPacket();
        packet.setType(TextPacket.Type.ANNOUNCEMENT_JSON);
        packet.setXuid("");
        packet.setSourceName("");
        packet.setMessage(new Gson().toJson(textBuilder.build()));
        this.sendPacket(packet);
    }

    public void onWhisper(String message) {
        TextPacket packet = new TextPacket();
        packet.setType(TextPacket.Type.WHISPER);
        packet.setXuid("");
        packet.setSourceName("");
        packet.setMessage(message);
        this.sendPacket(packet);
    }

    public void onWhisper(RawTextBuilder textBuilder) {
        TextPacket packet = new TextPacket();
        packet.setType(TextPacket.Type.WHISPER_JSON);
        packet.setXuid("");
        packet.setSourceName("");
        packet.setMessage(new Gson().toJson(textBuilder.build()));
        this.sendPacket(packet);
    }

    public void onMessageTranslation(String translate, List<String> param) {
        TextPacket packet = new TextPacket();
        packet.setType(TextPacket.Type.TRANSLATION);
        packet.setXuid("");
        packet.setSourceName("");
        packet.setMessage(translate);
        packet.setParameters(param);
        this.sendPacket(packet);
    }

    public void onMessageSystem(String message) {
        TextPacket packet = new TextPacket();
        packet.setType(TextPacket.Type.SYSTEM);
        packet.setXuid("");
        packet.setSourceName("");
        packet.setMessage(message);
        this.sendPacket(packet);
    }
}
