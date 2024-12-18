package org.sculk.player.text;

import lombok.Getter;
import org.sculk.lang.Language;

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
public class RawTextBuilder implements IJsonText{

    @Getter
    private final List<IJsonText> build;

    public RawTextBuilder() {
        build = new ArrayList<>();
    }

    public static RawTextBuilder create() {
        return new RawTextBuilder();
    }

    public static RawTextBuilder create(List<IJsonText> text) {
        RawTextBuilder builder = new RawTextBuilder();
        for (IJsonText iJsonText : text) {
            builder.add(iJsonText);
        }
        return builder;
    }

    public static RawTextBuilder create(IJsonText... text) {
        RawTextBuilder builder = new RawTextBuilder();
        for (IJsonText iJsonText : text) {
            builder.add(iJsonText);
        }
        return builder;
    }

    @Override
    public String getName() {
        return "rawtext";
    }

    public RawTextBuilder add(IJsonText text) {
        this.build.add(text);
        return this;
    }

    @Override
    public Object build() {
        HashMap<String, List<Object>> map = new HashMap<>();
        map.put(this.getName(), this.build.stream().map(IJsonText::build).toList());
        return map;
    }

    @Override
    public Object build(Language lang) {
        HashMap<String, List<Object>> map = new HashMap<>();
        map.put(this.getName(), this.build.stream().map(iJsonText -> iJsonText.build(lang)).toList());
        return map;
    }
}