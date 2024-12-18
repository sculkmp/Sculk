package org.sculk.lang;

import lombok.Getter;
import lombok.NonNull;
import org.sculk.player.text.IJsonText;
import org.sculk.player.text.RawTextBuilder;
import org.sculk.player.text.TextBuilder;
import org.sculk.player.text.TranslaterBuilder;

import javax.annotation.Nullable;
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
    private static final Pattern PATTERN_STRING_EXTERNAL = Pattern.compile("%%[sd]");
    private static final Pattern PATTERN_INDEX_EXTERNAL = Pattern.compile("%%(\\d+)(\\$[sd])|%%");
    private static final Pattern PATTERN_INTERNAL = Pattern.compile("(?<!%)%[sd]|(?<!%)%(\\d+)(\\$[sd])");

    public Language(String name, Locale locale, Map<String, String> translate) {
        this.name = name;
        this.locale = locale;
        this.translate = new HashMap<>();
        translate.forEach((key, value) -> {
            this.translate.put(key, reformatConfigToMinecraft(value));
        });
        translate.clear();
    }

    public static String reformatConfigToMinecraft(String value){
        StringBuilder result = new StringBuilder();
        Matcher matcher = PATTERN_INTERNAL.matcher(value);
        while (matcher.find()) {
            matcher.appendReplacement(result, "%" + Matcher.quoteReplacement(matcher.group()));
        }
        matcher.appendTail(result);
        return result.toString().replace("\\n" , "\n");
    }

    public final @Nullable String internalGet(String key){
        return translate.getOrDefault(key, null);
    }

    public String translate(String str, @Nullable List<Object> params, String onlyPrefix) {
        String replacement;
        String baseText;
        char format;
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
                format = switch (param)
                {
                    case Translatable value -> 's';
                    case String value -> 's';
                    case Double value -> 'd';
                    case Float value -> 'd';
                    case Integer value -> 'd';
                    case Long value -> 'd';
                    case Boolean value -> 's';
                    case Date value -> 's';
                    case InetSocketAddress value -> 's';
                    case null, default -> throw new IllegalArgumentException("Illegal parameter: " + param);
                };
                baseText = baseText.replace("%%" + format, replacement);
            }
        }
        return baseText.replace("%%", "%");
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
            if (isNumber(key))
                baseText = baseText.replace("%%" + key + "$s", replacement);
            else
                baseText = baseText.replace("%%" + key, replacement);
        }
        return baseText;
    }

    public static boolean isNumber(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public final String translate(TranslaterBuilder<?> c){
        Object with;

        List<String> data = new ArrayList<>();
        String baseText;
        String text;
        Matcher matcher;
        int     i;
        int     index;
        String group;
        char format;

        baseText = this.internalGet(c.getTranslate());
        if (baseText == null)
            baseText = Language.reformatConfigToMinecraft(c.getTranslate());
        with = c.getWith();
        if (with instanceof RawTextBuilder _withRaw){
            for (IJsonText raw: _withRaw.getBuild())
            {
                if (raw instanceof TranslaterBuilder<?> _raw)
                    data.add(translate(_raw));
                else if(raw instanceof RawTextBuilder _raw)
                    data.add(translate(_raw));
                else if(raw instanceof TextBuilder _raw)
                    data.add(_raw.getText());
            }
        } else if (with instanceof List<?> _withList) {
            for (Object item : _withList) {
                if (item instanceof String str) {
                    data.add(str);
                }
            }
        }

        StringBuilder result = new StringBuilder();
        i = 0;
        matcher = PATTERN_STRING_EXTERNAL.matcher(baseText);
        int lastEnd = 0;
        while (matcher.find()) {
            group = matcher.group();
            if (i >= data.size()) {
                matcher.appendReplacement(result, "");
                continue;
            }
            format = group.charAt(group.lastIndexOf("%") + 1);
            Object value = data.get(i);
            if (format == 's' && value instanceof String && !isNumber((String) value)) {
                matcher.appendReplacement(result, value.toString());
            } else if (format == 'd'  && (value instanceof Number || (value instanceof String && isNumber((String) value)))) {
                matcher.appendReplacement(result, value.toString());
            }else {
                matcher.appendReplacement(result, Integer.toString(i + 1));
            }
            ++i;
        }
        matcher.appendTail(result);
        baseText = result.toString();
        result.setLength(0);
        matcher = PATTERN_INDEX_EXTERNAL.matcher(baseText);
        while (matcher.find()) {
            group = matcher.group();
            if (group.equals("%%")) {
                matcher.appendReplacement(result, "%");
                continue;
            }
            try {
                index = Integer.parseInt(group.substring(group.lastIndexOf("%") + 1, group.indexOf("$"))) + i - 1;
            } catch (NumberFormatException e) {
                continue;
            }
            if (index < 0 || index >= data.size()) {
                matcher.appendReplacement(result, "");
                continue;
            }
            format = group.charAt(group.lastIndexOf("$") + 1);
            if (format == '\0') {
                matcher.appendReplacement(result, Integer.toString(index - i + 1));
                continue;
            }
            Object value = data.get(index);
            if (format == 's' && value instanceof String && !isNumber((String) value)) {
                matcher.appendReplacement(result, value.toString());
            } else if (format == 'd' && (value instanceof Number || (value instanceof String && isNumber((String) value)))) {
                matcher.appendReplacement(result, value.toString());
            }else {
                matcher.appendReplacement(result, Integer.toString(index - i + 1));
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
            if (data instanceof TranslaterBuilder _data)
                stringBuilder.append(translate(_data));
            else if(data instanceof RawTextBuilder _data)
                stringBuilder.append(translate(_data));
            else if(data instanceof TextBuilder _data)
            {
                stringBuilder.append(_data.getText());
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
