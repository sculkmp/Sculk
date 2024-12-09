package org.sculk.lang;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class Translatable<V> {
    @Getter
    private String text;
    @Getter
    private Map<String, V> parameters;

    public Translatable(String text, Map<String, V> parameters) {
        this.text = text;
        this.parameters = parameters;
    }

    public Translatable(String text) {
        this.text = text;
        this.parameters = null;
    }

    public Translatable format(String before, String after) {
        return new Translatable(before + "%" + text + after, parameters);
    }

    public Translatable prefix(String prefix) {
        return new Translatable(prefix + "%" + text, parameters);
    }
    public Translatable postfix(String postfix) {
        return new Translatable("%" + text + postfix, parameters);
    }

    public V getParameter(String index) {
        return this.parameters.getOrDefault(index, null);
    }

    public V getParameter(Integer index) {
        return this.getParameter(index.toString());
    }

    public V getParameter(int index) {
        return this.getParameter(String.valueOf(index));
    }
}
