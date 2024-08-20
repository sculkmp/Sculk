package org.sculk;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sculk.network.protocol.ProtocolInfo;
import org.sculk.utils.TextFormat;

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
public class Sculk {

    public static final long START_TIME = System.currentTimeMillis();
    public static final String MINECRAFT_VERSION = ProtocolInfo.MINECRAFT_VERSION;
    public static final String MINECRAFT_VERSION_NETWORK = ProtocolInfo.MINECRAFT_VERSION_NETWORK;
    public static final String CODE_NAME = "Sculk";
    public static final String CODE_VERSION = "v1.0.0";
    public static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();
    public static final String DATA_PATH = System.getProperty("user.dir") + "/";

    public static void main(String[] args) {
        Thread.currentThread().setName("sculkmp-main");
        System.setProperty("log4j.skipJansi", "false");

        Logger log = LogManager.getLogger(Sculk.class);
        log.info("{}Starting {} software", TextFormat.WHITE, CODE_NAME);

        int javaVersion = getJavaVersion();
        if (javaVersion < 21) {
            log.error("{}Using unsupported Java version! Minimum supported version is Java 21, found java {}", TextFormat.RED, javaVersion);
            LogManager.shutdown();
            return;
        }

        try {
            new Server(log, DATA_PATH);
        } catch (Exception e) {
            log.throwing(e);
            shutdown();
        }
    }

    protected static void shutdown() {
        LogManager.shutdown();
        Runtime.getRuntime().halt(0);
    }

    private static int getJavaVersion() {
        return Runtime.version().feature();
    }
}