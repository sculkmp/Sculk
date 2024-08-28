package org.sculk;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sculk.config.ServerProperties;
import org.sculk.config.ServerPropertiesKeys;
import org.sculk.lang.Language;
import org.sculk.lang.LanguageKeys;
import org.sculk.lang.LanguageManager;
import org.sculk.network.protocol.ProtocolInfo;
import org.sculk.utils.TextFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

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

@Log4j2
public class Sculk {

    public static final long START_TIME = System.currentTimeMillis();
    public static final String MINECRAFT_VERSION = ProtocolInfo.MINECRAFT_VERSION;
    public static final String MINECRAFT_VERSION_NETWORK = ProtocolInfo.MINECRAFT_VERSION_NETWORK;
    public static final String CODE_NAME = "Sculk";
    public static final String CODE_VERSION = "1.0.0";
    public static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();
    public static final String DATA_PATH = System.getProperty("user.dir") + "/";

    public static void main(String[] args) {
        Thread.currentThread().setName("sculkmp-main");
        System.setProperty("log4j.skipJansi", "false");
        Logger log = LogManager.getLogger(Sculk.class);

        try {
            Properties properties = loadServerProperties();
            String langCode = properties.getProperty("language", "eng");
            Language language = Language.fromCode(langCode);
            LanguageManager languageManager = new LanguageManager(language);

            log.info(languageManager.tr(LanguageKeys.SCULK_SERVER_STARTING, TextFormat.DARK_AQUA + CODE_NAME + TextFormat.WHITE, TextFormat.AQUA + CODE_VERSION + TextFormat.WHITE));

            new Server(log, DATA_PATH);
        } catch (Exception e) {
            log.throwing(e);
            shutdown();
        }

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

    private static Properties loadServerProperties() throws IOException {
        File file = new File(DATA_PATH + "server.properties");
        Properties properties = new Properties();
        if (!file.exists()) {
            properties.setProperty("language", "eng");
            return properties;
        }
        try (FileInputStream input = new FileInputStream(file)) {
            properties.load(input);
        }
        return properties;
    }

    protected static void shutdown() {
        LogManager.shutdown();
        Runtime.getRuntime().halt(0);
    }

    private static int getJavaVersion() {
        return Runtime.version().feature();
    }
}