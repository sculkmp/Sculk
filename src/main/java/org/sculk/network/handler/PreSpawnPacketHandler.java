package org.sculk.network.handler;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.*;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataTypes;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.cloudburstmc.protocol.common.util.OptionalBoolean;
import org.sculk.Sculk;
import org.sculk.player.Player;
import org.sculk.Server;
import org.sculk.network.session.SculkServerSession;

import java.util.List;
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
public class PreSpawnPacketHandler extends SculkPacketHandler {

    private final Player player;
    public PreSpawnPacketHandler(SculkServerSession session, Player player) {
        super(session);
        this.player = player;
    }

    @Override
    public void setUp() {
        StartGamePacket startGamePacket = new StartGamePacket();
        startGamePacket.setUniqueEntityId(session.getPlayer().getEntityId());
        startGamePacket.setRuntimeEntityId(session.getPlayer().getEntityId());
        startGamePacket.setPlayerGameType(GameType.SURVIVAL);
        startGamePacket.setPlayerPosition(Vector3f.from(0, 69, 0));
        startGamePacket.setRotation(Vector2f.from(1, 1));

        startGamePacket.setSeed(-1L);
        startGamePacket.setDimensionId(0);
        startGamePacket.setGeneratorId(1);
        startGamePacket.setLevelGameType(GameType.SURVIVAL);
        startGamePacket.setDifficulty(1);
        startGamePacket.setDefaultSpawn(Vector3i.ZERO);
        startGamePacket.setAchievementsDisabled(true);
        startGamePacket.setCurrentTick(-1);
        startGamePacket.setEduEditionOffers(0);
        startGamePacket.setEduFeaturesEnabled(false);
        startGamePacket.setRainLevel(0);
        startGamePacket.setLightningLevel(0);
        startGamePacket.setMultiplayerGame(true);
        startGamePacket.setBroadcastingToLan(true);
        startGamePacket.setPlatformBroadcastMode(GamePublishSetting.PUBLIC);
        startGamePacket.setXblBroadcastMode(GamePublishSetting.PUBLIC);
        startGamePacket.setCommandsEnabled(true);
        startGamePacket.setTexturePacksRequired(false);
        startGamePacket.setBonusChestEnabled(false);
        startGamePacket.setStartingWithMap(false);
        startGamePacket.setTrustingPlayers(true);
        startGamePacket.setDefaultPlayerPermission(PlayerPermission.MEMBER);
        startGamePacket.setServerChunkTickRange(4);
        startGamePacket.setBehaviorPackLocked(false);
        startGamePacket.setResourcePackLocked(false);
        startGamePacket.setFromLockedWorldTemplate(false);
        startGamePacket.setUsingMsaGamertagsOnly(false);
        startGamePacket.setFromWorldTemplate(false);
        startGamePacket.setWorldTemplateOptionLocked(false);
        startGamePacket.setSpawnBiomeType(SpawnBiomeType.DEFAULT);
        startGamePacket.setCustomBiomeName("");
        startGamePacket.setEducationProductionId("");
        startGamePacket.setForceExperimentalGameplay(OptionalBoolean.empty());

        String serverName = session.getServer().getSubMotd();
        startGamePacket.setLevelId(serverName);
        startGamePacket.setLevelName(serverName);

        startGamePacket.setPremiumWorldTemplateId("00000000-0000-0000-0000-000000000000");
        // startGamePacket.setCurrentTick(0);
        startGamePacket.setEnchantmentSeed(0);
        startGamePacket.setMultiplayerCorrelationId("");

       startGamePacket.getItemDefinitions().addAll(List.of());

        // Needed for custom block mappings and custom skulls system
        startGamePacket.getBlockProperties().addAll(List.of());

/* THANK GEYSER
       // See https://learn.microsoft.com/en-us/minecraft/creator/documents/experimentalfeaturestoggle for info on each experiment
        // data_driven_items (Holiday Creator Features) is needed for blocks and items
        startGamePacket.getExperiments().add(new ExperimentData("data_driven_items", true));
        // Needed for block properties for states
        startGamePacket.getExperiments().add(new ExperimentData("upcoming_creator_features", true));
        // Needed for certain molang queries used in blocks and items
        startGamePacket.getExperiments().add(new ExperimentData("experimental_molang_features", true));
        // Required for experimental 1.21 features
        startGamePacket.getExperiments().add(new ExperimentData("updateAnnouncedLive2023", true));*/

        startGamePacket.setVanillaVersion("*");
        startGamePacket.setInventoriesServerAuthoritative(true);
        startGamePacket.setServerEngine(""); // Do we want to fill this in?

        startGamePacket.setPlayerPropertyData(NbtMap.EMPTY);
        startGamePacket.setWorldTemplateId(UUID.randomUUID());

        startGamePacket.setChatRestrictionLevel(ChatRestrictionLevel.NONE);

        startGamePacket.setAuthoritativeMovementMode(AuthoritativeMovementMode.SERVER);
        startGamePacket.setRewindHistorySize(0);
        startGamePacket.setServerAuthoritativeBlockBreaking(false);

        startGamePacket.setServerId("");
        startGamePacket.setWorldId("");
        startGamePacket.setScenarioId("");
        startGamePacket.setServerEngine(Sculk.CODE_NAME + " " + Sculk.CODE_VERSION);

        session.sendPacket(startGamePacket);
        LevelChunkPacket packet = new LevelChunkPacket();
        packet.setChunkX(0);
        packet.setChunkZ(0);
        packet.setData(Unpooled.EMPTY_BUFFER);
        packet.setCachingEnabled(false);
        packet.setRequestSubChunks(false);
        packet.setDimension(0);
        packet.setSubChunkLimit(0);
        packet.setSubChunkLimit(0);
        session.sendPacket(packet);
        session.sendPacket(new CreativeContentPacket());
        session.sendPacket(new BiomeDefinitionListPacket());
        session.sendPacket(new AvailableEntityIdentifiersPacket());
        session.getPlayer().sendAttributes();
        session.getPlayer().sendCommandsData();
        session.getPlayer().sendData(List.of(session.getPlayer()));

        SetTimePacket setTimePacket = new SetTimePacket();
        setTimePacket.setTime(0);
        session.sendPacket(setTimePacket);
        session.syncPlayerList(session.getServer().getOnlinePlayers());
    }

    @Override
    public PacketSignal handlePacket(BedrockPacket packet) {
        return super.handlePacket(packet);
    }

}
