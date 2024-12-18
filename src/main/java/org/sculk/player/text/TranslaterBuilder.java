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
public class TranslaterBuilder<V> implements IJsonText, Cloneable {

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
        map.put(this.getName(), Language.reformatConfigToMinecraft(this.translate));
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
            baseText = Language.reformatConfigToMinecraft(this.translate);
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
    public TranslaterBuilder<V> clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (TranslaterBuilder) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}