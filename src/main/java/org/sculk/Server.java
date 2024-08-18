package org.sculk;

import lombok.SneakyThrows;
import org.apache.logging.log4j.Logger;
import org.sculk.config.Config;
import org.sculk.utils.TextFormat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
public class Server {

    private static Server instance = null;

    private final Logger logger;

    private final Path dataPath;
    private final Path pluginDataPath;

    private final Config properties;
    private final Config config;
    private final Config operators;
    private final Config whitelist;
    private final Config banByName;
    private final Config banByIp;


    private final Map<UUID, Player> playerList = new HashMap<>();

    @SneakyThrows
    public Server(Logger logger, String dataPath) {
        instance = this;
        this.logger = logger;
        this.dataPath = Paths.get(dataPath);
        Path pluginsPath = Path.of(dataPath, "plugins");
        this.pluginDataPath = Path.of(dataPath, "plugin_data");
        Path worldsPath = Path.of(dataPath, "worlds");
        Path resourcePath = Path.of(dataPath, "resource_packs");
        Path playerPath = Path.of(dataPath, "players");

        if(!pluginsPath.toFile().exists()) pluginsPath.toFile().mkdirs();
        if(!this.pluginDataPath.toFile().exists()) this.pluginDataPath.toFile().mkdirs();
        if(!worldsPath.toFile().exists()) worldsPath.toFile().mkdirs();
        if(!resourcePath.toFile().exists()) resourcePath.toFile().mkdirs();
        if(!playerPath.toFile().exists()) playerPath.toFile().mkdirs();

        logger.info("Loading {}...", TextFormat.AQUA + "sculk.yml" + TextFormat.WHITE);
        this.config = new Config(this.dataPath + "sculk.yml");

        logger.info("Loading {}...", TextFormat.AQUA + "server.properties" + TextFormat.WHITE);
        this.properties = new Config(this.dataPath.resolve("server.properties").toString(), Config.PROPERTIES);
        if(!this.properties.exists("server-port")) {
            this.properties.set("language", "English");
            this.properties.set("motd", "A Sculk Server Software");
            this.properties.set("server-port", 19132);
            this.properties.set("server-ip", "0.0.0.0");
            this.properties.set("white-list", false);
            this.properties.set("max-players", 20);
            this.properties.set("gamemode", "SURVIVAL");
            this.properties.set("pvp", true);
            this.properties.set("difficulty", 1);
            this.properties.set("level-name", "world");
            this.properties.set("level-seed", "");
            this.properties.set("level-type", "DEFAULT");
            this.properties.set("auto-save", true);
            this.properties.set("xbox-auth", true);
            this.properties.save();
        }

        this.operators = new Config(this.dataPath.resolve("op.txt").toString(), Config.ENUM);
        this.whitelist = new Config(this.dataPath.resolve("whitelist.txt").toString(), Config.ENUM);
        this.banByName = new Config(this.dataPath.resolve("banned-players.txt").toString(), Config.ENUM);
        this.banByIp = new Config(this.dataPath.resolve("banned-ip.txt").toString(), Config.ENUM);

        logger.info("Selected {} as the base language", this.properties.getString("language"));
        logger.info("Starting Minecraft: Bedrock Edition server version {}", TextFormat.AQUA + Sculk.MINECRAFT_VERSION + TextFormat.WHITE);
        if(this.properties.getBoolean("xbox-auth")) {
            logger.info("Online mode is enable. The server will verify that players are authenticated to XboxLive.");
        } else {
            logger.info("{}Online mode is not enabled. The server no longer checks if players are authenticated to XboxLive.", TextFormat.RED);
        }
        logger.info("This server is running on version {}",TextFormat.AQUA + Sculk.CODE_VERSION);
        logger.info("Sculk-MP is distributed undex the {}",TextFormat.AQUA + "GNU GENERAL PUBLIC LICENSE");
        start();
    }

    public void start() {
        getLogger().info("Done ({}s)! For help, type \"help\" or \"?", String.valueOf((double) (System.currentTimeMillis() - Sculk.START_TIME) / 1000));
    }

    public static Server getInstance() {
        return instance;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getDataPath() {
        return dataPath;
    }

    public Path getPluginDataPath() {
        return pluginDataPath;
    }

    public Map<UUID, Player> getOnlinePlayers() {
        return Collections.unmodifiableMap(playerList);
    }

    public Config getProperties() {
        return properties;
    }

    public Config getConfig() {
        return config;
    }

}
