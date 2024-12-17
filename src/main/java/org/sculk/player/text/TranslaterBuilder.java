package org.sculk.player.text;

import lombok.Getter;
import org.sculk.lang.Language;

import javax.annotation.Nullable;
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
public class TranslaterBuilder<V> implements IJsonText {

    @Getter
    private String translate;
    @Getter
    private V with;
    private static final Pattern PATTERN_STRING = Pattern.compile("%%(s)");
    private static final Pattern PATTERN_INDEX = Pattern.compile("%%(\\d+)");

    public TranslaterBuilder() {
        this.translate = null;
    }

    public TranslaterBuilder<V> setTranslate(String translate) {
        this.translate = translate;
        return this;
    }

    public TranslaterBuilder<V> setWith(V data) {
        this.with = data;
        return this;
    }

    @Override
    public String getName() {
        return "translate";
    }


    @Override
    public Object build() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(this.getName(), this.translate);
        if (this.with != null) {
            Object with = this.with;

            if (with instanceof RawTextBuilder rawTextBuilder) {
                with = rawTextBuilder.build();
            }
            map.put("with", with);
        }
        return map;
    }


    @Override
    public Object build(Language language) {
        String baseText;
        HashMap<String, Object> map = new HashMap<>();

        baseText = language.internalGet(this.translate);
        if (baseText == null)
            baseText = this.translate;
        map.put(this.getName(), baseText);
        if (this.with != null) {
            Object with = this.with;

            if (with instanceof RawTextBuilder rawTextBuilder) {
                with = rawTextBuilder.build(language);
            }
            map.put("with", with);
        }
        return map;
    }

    @Override
    public String toString() {
        String data = translate;
        if (this.with != null) {
            List<Object> clone = getCloneOfWith();

            if (clone != null) {
                data = replaceStringPatterns(data, clone.iterator());
                data = replaceIndexedPatterns(data, clone);
            }
        }
        return data;
    }

    private List<Object> getCloneOfWith() {
        if (this.with instanceof ArrayList<?>) {
            return new ArrayList<>((ArrayList<?>) this.with);
        } else if (this.with instanceof RawTextBuilder) {
            return new ArrayList<>(((RawTextBuilder) this.with).getBuild());
        }
        return null;
    }

    private String replaceIndexedPatterns(String data, List<Object> clone) {
        Matcher matcher = PATTERN_INDEX.matcher(data);
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            result.append(data, lastEnd, matcher.start());
            String group = matcher.group();
            int index;
            try {
                index = Integer.parseInt(group.substring(2, 3));
            } catch (NumberFormatException e) {
                continue;
            }
            String replacer = safeGetAtIndex(clone, index - 1);
            result.append(replacer);
            lastEnd = matcher.end();
        }
        result.append(data.substring(lastEnd));
        return result.toString();
    }

    private String replaceStringPatterns(String data, Iterator<Object> clone) {
        Matcher matcher = PATTERN_STRING.matcher(data);
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            result.append(data, lastEnd, matcher.start());
            String replacer = clone.hasNext() ? clone.next().toString() : "";
            result.append(replacer);
            lastEnd = matcher.end();
        }
        result.append(data.substring(lastEnd));
        return result.toString();
    }

    private String safeGetAtIndex(List<Object> list, int index) {
        if (!list.isEmpty()) {
            Object removed = list.get(index);
            return removed != null ? removed.toString() : "";
        }
        return "";
    }
}