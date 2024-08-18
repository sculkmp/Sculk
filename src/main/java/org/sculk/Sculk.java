package org.sculk;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sculk.network.protocol.ProtocolInfo;
import org.sculk.utils.TextFormat;

import java.io.IOException;

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

    public final static String MINECRAFT_VERSION = ProtocolInfo.MINECRAFT_VERSION;
    public final static String MINECRAFT_VERSION_NETWORK = ProtocolInfo.MINECRAFT_VERSION_NETWORK;
    public final static String CODE_NAME = "Sculk-MP";
    public final static String CODE_VERSION = "v1.0.0";

    public final static String DATA_PATH = System.getProperty("user.dir") + "/";

    public static void main(String[] args) {
        Thread.currentThread().setName("sculkmp-main");
        System.setProperty("log4j.skipJansi", "false");
        System.out.println("Starting SculkMP...");

        Logger logger = LogManager.getLogger(Sculk.class);
        logger.info("{}Starting SculkMP software", TextFormat.WHITE);

        int javaVersion = getJavaVersion();
        if(javaVersion < 21) {
            logger.error("{}Using unsupported Java version! Minimum supported version is Java 21, found java {}", TextFormat.RED, javaVersion);
            LogManager.shutdown();
            return;
        }

        try {
            new Server(logger, DATA_PATH);
        } catch(Exception e) {
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
