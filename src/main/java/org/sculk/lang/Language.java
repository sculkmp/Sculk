package org.sculk.lang;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.annotation.Nullable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;

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
public class Language {
    @Getter
    private String name;
    @Getter
    private Locale locale;

    @Getter
    private @NonNull Map<String, String> translate;

    public Language(String name, Locale locale, Map<String, String> translate) {
        this.name = name;
        this.locale = locale;
        this.translate = translate;
    }

    public final @Nullable String internalGet(String key){
        String value = translate.getOrDefault(key, null);
        if (value != null)
            return value.replace("\\n", "\n");
        return null;
    }

    public String translate(String str, @Nullable List<Object> params, String onlyPrefix) {
        String replacement;
        String baseText;
        int size;

        baseText = (onlyPrefix == null || str.startsWith(onlyPrefix)) ? internalGet(str) : null;
        if (baseText == null)
            baseText = parseTranslation(str, onlyPrefix);
        if (params != null) {
            size = params.size();
            for (int i = 0; i < size; i++) {
                Object param = params.get(i);
                replacement = switch (param) {
                    case Translatable value -> translate(value);
                    case String value -> value;
                    case Double value -> value.toString();
                    case Float value -> value.toString();
                    case Integer value -> value.toString();
                    case Long value -> value.toString();
                    case Boolean value -> value.toString();
                    case Date value -> value.toString();
                    case InetSocketAddress value -> value.toString();
                    case null, default -> throw new IllegalArgumentException("Illegal parameter: " + param);
                };
                baseText = baseText.replace("{" + "%" + i + "}", replacement);
            }
        }
        return baseText;
    }

    public String translate(LanguageKeys str, @Nullable List<Object> params, String onlyPrefix) {
        return this.translate(str.toString(), params, onlyPrefix);
    }

    public String translate(String str, @Nullable List<Object> params) {
        return this.translate(str, params, null);
    }

    public String translate(LanguageKeys str, @Nullable List<Object> params) {
        return this.translate(str.toString(), params);
    }

    public String translate(String str, String onlyPrefix) {
        return this.translate(str, null, onlyPrefix);
    }


    public String translate(LanguageKeys str, String onlyPrefix) {
        return this.translate(str, null, onlyPrefix);
    }

    public String translate(String str) {
        return this.translate(str, null, null);
    }

    public String translate(LanguageKeys str) {
        return this.translate(str, null, null);
    }

    public final String translate(Translatable<?> c){
        String replacement;
        String baseText;

        baseText = this.internalGet(c.getText());
        if (baseText == null)
            baseText = this.parseTranslation(c.getText());
        for (Map.Entry<String, ?> entry : c.getParameters().entrySet()) {
            String key = entry.getKey();
            if (entry.getValue() instanceof Translatable value) {
                replacement = translate(value);
            } else if (entry.getValue() instanceof String value) {
                replacement = value;
            }else {
                throw new IllegalArgumentException("Illegal parameter: " + entry.getValue());
            }
            baseText = baseText.replace("{" + "%" + key + "}", replacement);
        }
        return baseText;
    }



    /**
     * Replaces translation keys embedded inside a string with their raw values.
     * Embedded translation keys must be prefixed by a "%" character.
     *
     * This is used to allow the "text" field of a Translatable to contain formatting (e.g., color codes) and
     * multiple embedded translation keys.
     *
     * Normal translations whose "text" is just a single translation key don't need to use this method, and can be
     * processed directly via a get() method.
     *
     * @param text The input text containing embedded translation keys.
     * @param onlyPrefix If non-null, only translation keys with this prefix will be replaced.
     *                   This is used to allow a client to do its own translating of specific strings.
     * @return The processed string with translation keys replaced.
     */
    protected String parseTranslation(@NonNull String text, @Nullable String onlyPrefix) {
        StringBuilder newString = new StringBuilder();
        StringBuilder replaceString = null;

        int len = text.length();
        for (int i = 0; i < len; ++i) {
            char c = text.charAt(i);
            if (replaceString != null) {
                int ord = (int) c;
                if (
                        (ord >= 0x30 && ord <= 0x39) || // 0-9
                                (ord >= 0x41 && ord <= 0x5A) || // A-Z
                                (ord >= 0x61 && ord <= 0x7A) || // a-z
                                c == '.' || c == '-'
                ) {
                    replaceString.append(c);
                } else {
                    String key = replaceString.substring(1);
                    String translation = internalGet(key);
                    if (onlyPrefix == null || key.startsWith(onlyPrefix)) {
                        newString.append(translation);
                    } else {
                        newString.append(replaceString);
                    }
                    replaceString = null;

                    if (c == '%') {
                        replaceString = new StringBuilder(String.valueOf(c));
                    } else {
                        newString.append(c);
                    }
                }
            } else if (c == '%') {
                replaceString = new StringBuilder(String.valueOf(c));
            } else {
                newString.append(c);
            }
        }

        if (replaceString != null) {
            String key = replaceString.substring(1);
            String translation = internalGet(key);
            if (onlyPrefix == null || key.startsWith(onlyPrefix)) {
                newString.append(translation);
            } else {
                newString.append(replaceString);
            }
        }

        return newString.toString();
    }

    protected String parseTranslation(String text) {
        return this.parseTranslation(text, null);
    }
}
