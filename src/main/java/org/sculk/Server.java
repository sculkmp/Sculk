package org.sculk;

import org.apache.logging.log4j.Logger;
import org.sculk.utils.Config;
import org.sculk.utils.ConfigSection;
import org.sculk.utils.TextFormat;

import java.nio.file.Path;
import java.nio.file.Paths;

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

    private Config properties;

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

        logger.info("Loading server configuration");
        this.properties = new Config(this.dataPath + "server.properties", Config.PROPERTIES, new ConfigSection() {
            {
                put("language", "eng");
                put("motd", "A Sculk Server");
                put("server-port", 19132);
                put("server-ip", "0.0.0.0");
                put("white-list", false);
                put("max-players", 20);
                put("gamemode", "SURVIVAL");
                put("pvp", true);
                put("difficulty", 1);
                put("level-name", "world");
                put("level-seed", "");
                put("level-type", "DEFAULT");
                put("auto-save", true);
                put("xbox-auth", true);
            }
        });
        this.properties.save();

        logger.info("Selected English (eng) as the base language");
        logger.info("§aserver start");
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

    public Config getProperties() {
        return properties;
    }
}
