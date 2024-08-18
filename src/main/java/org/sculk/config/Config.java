package org.sculk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

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
public class Config {

    private Map configMap = new HashMap<>();
    private final String filePath;
    private final boolean isJson;

    public Config(String filePath) throws IOException {
        this.filePath = filePath;
        this.isJson = filePath.endsWith(".json");
        load();
    }

    private void load() throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            save();  // create a new file with an empty config
            return;
        }

        if (isJson) {
            ObjectMapper mapper = new ObjectMapper();
            configMap = mapper.readValue(file, Map.class);
        } else {
            Yaml yaml = new Yaml();  // Using default constructor for YAML
            try (FileInputStream inputStream = new FileInputStream(file)) {
                configMap = yaml.load(inputStream);
            }
        }
    }

    public boolean exists() {
        return new File(filePath).exists();
    }

    public void save() throws IOException {
        File file = new File(filePath);
        if (isJson) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, configMap);
        } else {
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(FlowStyle.BLOCK);
            Yaml yaml = new Yaml(options);

            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file))) {
                yaml.dump(configMap, writer);
            }
        }
    }

    public void set(String key, Object value) throws IOException {
        configMap.put(key, value);
        save();
    }

    public String getString(String key) {
        Object value = configMap.get(key);
        return value != null ? value.toString() : null;
    }

    public Integer getInt(String key) {
        Object value = configMap.get(key);
        return value instanceof Integer ? (Integer) value : null;
    }

    public Boolean getBoolean(String key) {
        Object value = configMap.get(key);
        return value instanceof Boolean ? (Boolean) value : null;
    }

}
