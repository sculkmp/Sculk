package org.sculk;

import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.data.*;
import org.cloudburstmc.protocol.bedrock.data.skin.SerializedSkin;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.StartGamePacket;
import org.cloudburstmc.protocol.common.util.OptionalBoolean;
import org.sculk.player.PlayerInterface;
import org.sculk.player.client.ClientChainData;
import org.sculk.player.client.LoginChainData;

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
public class Player implements PlayerInterface {

    private final BedrockServerSession serverSession;
    private LoginChainData loginChainData;

    public Player(BedrockServerSession session, ClientChainData data) {
        this.serverSession = session;
        this.loginChainData = data;

    }

    public void processLogin() {

    }

    public void completeLogin() {
        StartGamePacket startGamePacket = new StartGamePacket();
        startGamePacket.setUniqueEntityId(this.getUniqueId());
        startGamePacket.setRuntimeEntityId(this.getRuntimeId());
        //startGamePacket.setPlayerGameType(GameType.CREATIVE);
        //startGamePacket.setPlayerPosition(pos);
        //startGamePacket.setRotation(Vector2f.from(this.getYaw(), this.getPitch()));
        startGamePacket.setSeed(-1L);
        startGamePacket.setDimensionId(0);
        startGamePacket.setTrustingPlayers(false);
        startGamePacket.setLevelGameType(GameType.CREATIVE);
        startGamePacket.setDifficulty(0);
        //startGamePacket.setDefaultSpawn(this.getSpawn().getPosition().toInt());
        startGamePacket.setAchievementsDisabled(true);
        startGamePacket.setDayCycleStopTime(0);
        startGamePacket.setRainLevel(0);
        startGamePacket.setLightningLevel(0);
        startGamePacket.setCommandsEnabled(true);
        startGamePacket.setMultiplayerGame(true);
        startGamePacket.setBroadcastingToLan(true);
       // NetworkUtils.gameRulesToNetwork(this.getLevel().getGameRules(), startGamePacket.getGamerules());
        startGamePacket.setLevelId(""); // This is irrelevant since we have multiple levels
        startGamePacket.setLevelName("World"); // We might as well use the MOTD instead of the default level name
        startGamePacket.setGeneratorId(1); // 0 old, 1 infinite, 2 flat - Has no effect to my knowledge
        //startGamePacket.setItemDefinitions(CloudItemRegistry.get().getItemEntries());
        //startGamePacket.setXblBroadcastMode(GamePublishSetting.PUBLIC);
        startGamePacket.setPlatformBroadcastMode(GamePublishSetting.PUBLIC);
        startGamePacket.setDefaultPlayerPermission(PlayerPermission.MEMBER);
        startGamePacket.setServerChunkTickRange(4);
        startGamePacket.setVanillaVersion("1.17.40"); // Temporary hack that allows player to join by disabling the new chunk columns introduced in update 1.18
        startGamePacket.setPremiumWorldTemplateId("");
        startGamePacket.setMultiplayerCorrelationId("");
        startGamePacket.setInventoriesServerAuthoritative(true);
        startGamePacket.setRewindHistorySize(0);
        startGamePacket.setServerAuthoritativeBlockBreaking(false);
        startGamePacket.setAuthoritativeMovementMode(AuthoritativeMovementMode.CLIENT);
        startGamePacket.setServerEngine("");
        startGamePacket.setPlayerPropertyData(NbtMap.EMPTY);
        startGamePacket.setWorldTemplateId(new UUID(0, 0));
        startGamePacket.setWorldEditor(false);
        startGamePacket.setChatRestrictionLevel(ChatRestrictionLevel.NONE);
        startGamePacket.setSpawnBiomeType(SpawnBiomeType.DEFAULT);
        startGamePacket.setCustomBiomeName("");
        startGamePacket.setEducationProductionId("");
        startGamePacket.setForceExperimentalGameplay(OptionalBoolean.empty());

        sendDataPacket(startGamePacket);
        this.getServer().addOnlinePlayer(this);
        getServer().onPlayerCompleteLogin(this);
    }

    public long getUniqueId() {
        return UUID.randomUUID().getMostSignificantBits();
    }

    public long getRuntimeId() {
        return UUID.randomUUID().getMostSignificantBits();
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public UUID getServerId() {
        return null;
    }

    @Override
    public Server getServer() {
        return Server.getInstance();
    }

    public boolean sendDataPacket(BedrockPacket packet) {
        sendPacketInternal(packet);
        return true;
    }

    public void sendPacketInternal(BedrockPacket packet) {
        this.serverSession.sendPacket(packet);
    }

    public SerializedSkin getSerializedSkin() {
        return ((ClientChainData) this.loginChainData).getSerializedSkin();
    }

    public long getPing() {
        return 1;
    }

}
