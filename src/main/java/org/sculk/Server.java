package org.sculk;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import org.apache.logging.log4j.Logger;
import org.sculk.config.Config;
import org.sculk.console.TerminalConsole;
import org.sculk.network.BedrockInterface;
import org.sculk.network.Network;
import org.sculk.network.SourceInterface;
import org.sculk.utils.TextFormat;

import java.net.InetSocketAddress;
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
@Log4j2
public class Server {

    private static Server instance = null;

    private final Logger logger;
    private final TerminalConsole console;

    private Network network;

    private final Path dataPath;
    private final Path pluginDataPath;

    private final Config properties;
    private final Config config;
    private final Config operators;
    private final Config whitelist;
    private final Config banByName;
    private final Config banByIp;

    private final Map<UUID, Player> playerList = new HashMap<>();

    private String motd;
    private int maxPlayers;
    private String defaultGamemode;
    private UUID serverId;

    public volatile boolean shutdown = false;

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
            this.properties.set("gamemode", "Survival");
            this.properties.set("pvp", true);
            this.properties.set("difficulty", 1);
            this.properties.set("level-name", "world");
            this.properties.set("level-seed", "");
            this.properties.set("level-type", "DEFAULT");
            this.properties.set("auto-save", true);
            this.properties.set("xbox-auth", true);
            this.properties.save();
        }
        this.motd = this.properties.getString("motd");

        this.operators = new Config(this.dataPath.resolve("op.txt").toString(), Config.ENUM);
        this.whitelist = new Config(this.dataPath.resolve("whitelist.txt").toString(), Config.ENUM);
        this.banByName = new Config(this.dataPath.resolve("banned-players.txt").toString(), Config.ENUM);
        this.banByIp = new Config(this.dataPath.resolve("banned-ip.txt").toString(), Config.ENUM);

        logger.info("Selected {} as the base language", this.properties.getString("language"));
        logger.info("Starting Minecraft: Bedrock Edition server version {}", TextFormat.AQUA + Sculk.MINECRAFT_VERSION + TextFormat.WHITE);

        this.console = new TerminalConsole(this);
        this.start();
    }

    public void start() {
        this.console.getConsoleThread().start();

        InetSocketAddress bindAddress = new InetSocketAddress("0.0.0.0", 19132);
        this.serverId = UUID.randomUUID();
        this.network = new Network(this);
        this.network.setName(this.motd);
        try {
            this.network.registerInterface(new BedrockInterface(this));
            getLogger().info("Minecraft network interface running on {}", bindAddress);
        } catch(Exception e) {
            logger.fatal("**** FAILED TO BIND TO " + bindAddress);
            logger.fatal("Peahaps a server s already running on that port?");
            shutdown();
        }

        if(this.properties.getBoolean("xbox-auth")) {
            logger.info("Online mode is enable. The server will verify that players are authenticated to XboxLive.");
        } else {
            logger.info("{}Online mode is not enabled. The server no longer checks if players are authenticated to XboxLive.", TextFormat.RED);
        }
        logger.info("This server is running on version {}",TextFormat.AQUA + Sculk.CODE_VERSION);
        logger.info("Sculk-MP is distributed undex the {}",TextFormat.AQUA + "GNU GENERAL PUBLIC LICENSE");

        
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        getLogger().info("Done ("+ (double) (System.currentTimeMillis() - Sculk.START_TIME) / 1000 +"s)! For help, type \"help\" or \"?");
    }

    public void shutdown() {
        if(this.shutdown) {
            return;
        }
        this.logger.info("Stopping the server");
        this.shutdown = true;

        this.logger.info("Stopping network interfaces");
        for(SourceInterface sourceInterface : this.network.getInterfaces()) {
            sourceInterface.shutdown();
            this.network.unregisterInterface(sourceInterface);
        }

        this.logger.info("Closing console");
        this.console.getConsoleThread().interrupt();

        this.logger.info("Stopping other threads");
    }

    public boolean isRunning() {
        return !this.shutdown;
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

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getDefaultGamemode() {
        return defaultGamemode;
    }

    public String getMotd() {
        return motd;
    }
}
