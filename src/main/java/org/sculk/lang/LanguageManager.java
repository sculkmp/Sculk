package org.sculk.lang;

import org.sculk.Server;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LanguageManager {
    private final Map<Language, Properties> languages = new HashMap<>();
    private Properties currentLanguage;

    public LanguageManager(Language defaultLang) {
        loadAndSetLanguage(defaultLang);
    }

    public void loadAndSetLanguage(Language lang) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("language/" + lang.getFileName())) {
            if (input == null) {
                throw new IOException("Language file not found: " + lang.getFileName());
            }
            Properties properties = new Properties();
            properties.load(input);
            languages.put(lang, properties);
            currentLanguage = properties;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getLanguage() {
        try (InputStream input = LanguageManager.class.getClassLoader().getResourceAsStream("server.properties")) {
            Properties properties = new Properties();
            properties.load(input);
            return properties.getProperty("language", "eng");
        } catch (IOException e) {
            e.printStackTrace();
            return "eng";
        }
    }

    public String tr(LanguageKeys key, Object... params) {
        String pattern = currentLanguage.getProperty(String.valueOf(key), "Translation not found");
        return MessageFormat.format(pattern, params);
    }

    public String tr(LanguageKeys key) {
        return tr(key, new Object[]{});
    }
}