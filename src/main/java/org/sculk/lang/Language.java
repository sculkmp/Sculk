package org.sculk.lang;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.sculk.player.text.IJsonText;
import org.sculk.player.text.RawTextBuilder;
import org.sculk.player.text.TextBuilder;
import org.sculk.player.text.TranslaterBuilder;

import javax.annotation.Nullable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static final Pattern PATTERN_STRING = Pattern.compile("%%(s)");
    private static final Pattern PATTERN_INDEX = Pattern.compile("%%(\\d+)");

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

    public final String translate(TranslaterBuilder<?> c){
        Object with;
        Pattern pattern = Pattern.compile("%%|%((\\d+)\\$)?s");
        List<String> data = new ArrayList<>();
        String baseText;
        String text;
        int     i;
        int     index;

        baseText = this.internalGet(c.getTranslate());
        if (baseText == null)
            baseText = c.getTranslate();
        Matcher matcher = pattern.matcher(baseText);
        with = c.getWith();
        if (with instanceof RawTextBuilder _withRaw){
            for (IJsonText raw: _withRaw.getBuild())
            {
                if (data instanceof TranslaterBuilder<?> _data)
                    data.add(translate(_data));
                else if(data instanceof RawTextBuilder _data)
                    data.add(translate(_data));
                else if(data instanceof TextBuilder _data)
                {
                    text = this.internalGet(_data.getText());
                    if (text == null)
                        text = _data.getText();
                    data.add(text);
                }
            }
        } else if (with instanceof List<?> _withList) {
            for (Object item : _withList) {
                if (item instanceof String str) {
                    data.add(str);
                }
            }
        }

        StringBuffer result = new StringBuffer();
        i = 0;
        while (matcher.find()) {
            if ("%%".equals(matcher.group())) {
                // Remplacer "%%" par "%"
                matcher.appendReplacement(result, "%");
            } else {
                // Gérer "%s" ou "%n$s"
                String indexGroup = matcher.group(2); // Capture du numéro dans "%n$s"
                if(indexGroup != null)
                {
                    index = Integer.parseInt(indexGroup) - 1;
                }else {
                    index = i;
                    ++i;
                }

                // Vérification des limites
                if (index < 0 || index >= data.size()) {
                    throw new IllegalArgumentException("Argument manquant pour le translater : " + matcher.group());
                }

                // Remplacer par l'argument correspondant
                matcher.appendReplacement(result, Matcher.quoteReplacement(data.get(index)));
            }
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public final String translate(RawTextBuilder c){
        StringBuilder stringBuilder;
        String          text;
        List<IJsonText> list;

        list = c.getBuild();
        stringBuilder = new StringBuilder();
        for (IJsonText data: list)
        {
            if (data instanceof TranslaterBuilder<?> _data)
                stringBuilder.append(translate(_data));
            else if(data instanceof RawTextBuilder _data)
                stringBuilder.append(translate(_data));
            else if(data instanceof TextBuilder _data)
            {
                text = this.internalGet(_data.getText());
                if (text == null)
                    text = _data.getText();
                stringBuilder.append(text);
            }
        }
        return stringBuilder.toString();
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
