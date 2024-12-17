package org.sculk.config;

public enum ServerPropertiesKeys {
    LANGUAGE("language"),
    MOTD("motd"),
    SUB_MOTD("sub-motd"),
    SERVER_PORT("server-port"),
    SERVER_IP("server-ip"),
    WHITELIST("white-list"),
    MAX_PLAYERS("max-players"),
    GAMEMODE("gamemode"),
    PVP("pvp"),
    DIFFICULTY("difficulty"),
    LEVEL_NAME("level-name"),
    LEVEL_SEED("level-seed"),
    LEVEL_TYPE("level-type"),
    SPAWN_ANIMALS("spawn-animals"),
    SPAWN_MONSTERS("spawn-monsters"),
    AUTO_SAVE("auto-save"),
    XBOX_AUTH("xbox-auth"),
    FORCE_RESOURCE_PACKS("force-resource-packs");

    private final String key;

    ServerPropertiesKeys(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
