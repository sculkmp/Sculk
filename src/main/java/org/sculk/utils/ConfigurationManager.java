package org.sculk.utils;

import org.sculk.Server;
import org.sculk.exception.InvalidConfigurationException;
import org.sculk.utils.config.ServerConfig;

import java.io.File;

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
public class ConfigurationManager {

    private final Server server;
    private ServerConfig serverConfig;

    public ConfigurationManager(Server server) {
        this.server = server;
    }

    public void loadConfig() throws InvalidConfigurationException {
        File serverFile = new File(this.server.getDataPath().toString() + "/server.yml");
        ServerConfig config = new ServerConfig(serverFile);
        config.init();
        this.serverConfig = config;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }
}
