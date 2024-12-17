package org.sculk.config;

import org.sculk.api.player.GameMode;

import java.io.File;
import java.nio.file.Path;

public class ServerProperties {
    private final Config properties;

    public ServerProperties(Path dataPath) {
        File file = new File(dataPath + "/server.properties");
        if (!file.exists()) {
            ConfigSection defaults = getDefaultValues();
            new Config(file.getPath(), Config.PROPERTIES, defaults).save();
        }
        this.properties = new Config(dataPath + "/server.properties", Config.PROPERTIES, getDefaultValues());
    }

    private ConfigSection getDefaultValues() {
        ConfigSection defaults = new ConfigSection();
        defaults.put(ServerPropertiesKeys.LANGUAGE.toString(), "en_GB");
        defaults.put(ServerPropertiesKeys.MOTD.toString(), "A Sculk Server Software");
        defaults.put(ServerPropertiesKeys.SUB_MOTD.toString(), "Powered by Sculk");
        defaults.put(ServerPropertiesKeys.SERVER_IP.toString(), "0.0.0.0");
        defaults.put(ServerPropertiesKeys.SERVER_PORT.toString(), 19132);
        defaults.put(ServerPropertiesKeys.WHITELIST.toString(), "off");
        defaults.put(ServerPropertiesKeys.MAX_PLAYERS.toString(), 20);
        defaults.put(ServerPropertiesKeys.GAMEMODE.toString(), GameMode.SURVIVAL.getId());
        defaults.put(ServerPropertiesKeys.PVP.toString(), "on");
        defaults.put(ServerPropertiesKeys.DIFFICULTY.toString(), 1);
        defaults.put(ServerPropertiesKeys.LEVEL_NAME.toString(), "world");
        defaults.put(ServerPropertiesKeys.LEVEL_SEED.toString(), "");
        defaults.put(ServerPropertiesKeys.LEVEL_TYPE.toString(), "DEFAULT");
        defaults.put(ServerPropertiesKeys.SPAWN_ANIMALS.toString(), "on");
        defaults.put(ServerPropertiesKeys.SPAWN_MONSTERS.toString(), "on");
        defaults.put(ServerPropertiesKeys.AUTO_SAVE.toString(), "on");
        defaults.put(ServerPropertiesKeys.XBOX_AUTH.toString(), "on");
        defaults.put(ServerPropertiesKeys.FORCE_RESOURCE_PACKS.toString(), "off");
        return defaults;
    }

    public ConfigSection getProperties() {
        return this.properties.getRootSection();
    }

    public Integer get(ServerPropertiesKeys key, Integer defaultValue) {
        Object value = this.properties.get(key.toString());
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        } else if (value instanceof Integer) {
            return (Integer) value;
        } else {
            return defaultValue;
        }
    }

    public String get(ServerPropertiesKeys key, String defaultValue) {
        Object value = this.properties.get(key.toString());
        if (value instanceof String) {
            return (String) value;
        } else {
            return defaultValue;
        }
    }

    public Boolean get(ServerPropertiesKeys key, Boolean defaultValue) {
        Object value = this.properties.get(key.toString());
        if (value instanceof String) {
            String stringValue = ((String) value).toLowerCase();
            if (stringValue.equals("on")) {
                return true;
            } else if (stringValue.equals("off")) {
                return false;
            }
        } else if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }

    public Long get(ServerPropertiesKeys key, Long defaultValue) {
        Object value = this.properties.get(key.toString());
        if (value instanceof String stringValue) {
            if (!stringValue.isEmpty()) {
                try {
                    return Long.parseLong(stringValue);
                } catch (NumberFormatException e) {
                    return defaultValue;
                }
            } else {
                return defaultValue;
            }
        } else if (value instanceof Long) {
            return (Long) value;
        } else {
            return defaultValue;
        }
    }

    public void set(String key, Object value) {
        if (value instanceof Boolean) {
            value = (Boolean) value ? "on" : "off";
        }
        this.properties.set(key, value);
    }

    public void remove(String key) {
        this.properties.remove(key);
    }

    public boolean exists(String key) {
        return this.properties.exists(key);
    }

    public void save() {
        this.properties.save();
    }

    public void reload() {
        this.properties.reload();
    }
}