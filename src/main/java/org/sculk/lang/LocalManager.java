package org.sculk.lang;

import lombok.Getter;
import org.sculk.Server;
import org.sculk.config.Config;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

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
public class LocalManager {
    @Getter
    public Map<String, Language> languageMap;
    public LocalManager(ClassLoader loader, String resourcePath) {
        languageMap = new HashMap<>();
        InputStream languagesStream = loader.getResourceAsStream(resourcePath + "/languages.json");
        if (languagesStream == null) {
            throw new AssertionError("Unable to find language.json");
        }
        Config languages = new Config(Config.JSON);
        languages.load(languagesStream);
        Config config;
        for (Map.Entry<String, Object> entry: languages.getAll().entrySet()){
            String key = entry.getKey();
            String value = (String)entry.getValue();
            Locale locale = Locale.forLanguageTag(key);
            config = new Config(Config.PROPERTIES);
            InputStream data = loader.getResourceAsStream(resourcePath + "/" + key + ".ini");
            if (data == null) {
                throw new AssertionError("Unable to find " + resourcePath + "/" +key + ".ini");
            }
            config.load(data);
            String[] languagePart = key.split("_");
            languageMap.put(key, new Language(value, Locale.of(languagePart[0],  languagePart.length > 1 ? languagePart[1] : ""), config.getAll()
                    .entrySet()
                    .stream()
                    .filter(e -> (e.getValue() instanceof String))
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> (String) e.getValue()))));
        }
    }

    public Language getLanguage(Locale locale){
        return languageMap.get(locale.getLanguage() + (locale.getCountry().isEmpty() ? "" : "_" + locale.getCountry()));
    }

    public Language getLanguage(String language){
        return languageMap.get(language);
    }

    private String getNameLanguage(Config languagesName, String local)
    {
        for (Object entry: languagesName.getAll().values()){
            if (entry instanceof ArrayList<?> _info){
                if (_info.size() == 2 && _info.getFirst().toString().equals(local))
                        return (String) _info.get(1);
            }
        }
        return null;
    }
}
