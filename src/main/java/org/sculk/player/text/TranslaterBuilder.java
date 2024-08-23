package org.sculk.player.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
public class TranslaterBuilder implements IJsonText {

    public String translate;
    private Object with;
    private static final Pattern PATTERN_STRING = Pattern.compile("%%(s)");
    private static final Pattern PATTERN_INDEX = Pattern.compile("%%(\\d+)");

    public TranslaterBuilder() {
        with = null;

    }

    public TranslaterBuilder setTranslate(String translate) {
        this.translate = translate;
        return this;
    }

    public TranslaterBuilder addWith(String data) {
        if (this.with == null)
            this.with = new ArrayList<String>(List.of(data));
        else if (this.with instanceof ArrayList<?>) {
            ArrayList<String> _with = (ArrayList<String>) this.with;
            _with.add(data);
        }
        return this;
    }

    public TranslaterBuilder addWith(ArrayList<String> data) {
        if (this.with == null)
            this.with = data;
        else if (this.with instanceof ArrayList<?>) {
            ArrayList<String> _with = (ArrayList<String>) this.with;
            _with.addAll(data);
        }
        return this;
    }

    public TranslaterBuilder setWith(RawTextBuilder text) {
        if (this.with == null)
            this.with = text;
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
    public String toString() {
        String data = translate;
        if (this.with != null) {
            List<Object> clone = getCloneOfWith();

            if (clone != null) {
                data = replaceIndexedPatterns(data, clone);
                data = replaceStringPatterns(data, clone);
            }
        }
        return data;
    }

    private List<Object> getCloneOfWith() {
        if (this.with instanceof ArrayList<?>) {
            return new ArrayList<>((ArrayList<?>) this.with);
        } else if (this.with instanceof RawTextBuilder) {
            return new ArrayList<>(((RawTextBuilder) this.with).build);
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
            String replacer = safeRemoveAtIndex(clone, index);
            result.append(replacer);
            lastEnd = matcher.end();
        }
        result.append(data.substring(lastEnd));
        return result.toString();
    }

    private String replaceStringPatterns(String data, List<Object> clone) {
        Matcher matcher = PATTERN_STRING.matcher(data);
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            result.append(data, lastEnd, matcher.start());
            String replacer = safeRemoveFirst(clone);
            result.append(replacer);
            lastEnd = matcher.end();
        }
        result.append(data.substring(lastEnd));
        return result.toString();
    }

    private String safeRemoveFirst(List<Object> list) {
        if (!list.isEmpty()) {
            Object removed = list.removeFirst();
            return removed != null ? removed.toString() : "";
        }
        return "";
    }

    private String safeRemoveAtIndex(List<Object> list, int index) {
        if (index >= 0 && index < list.size()) {
            Object removed = list.remove(index);
            return removed != null ? removed.toString() : "";
        }
        return "";
    }
}