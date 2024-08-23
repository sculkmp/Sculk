package org.sculk.player.text;

import lombok.NonNull;

import java.util.HashMap;

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
public class TextBuilder implements IJsonText{

    private @NonNull String text;
    public TextBuilder() {
        text = "";
    }

    public TextBuilder setText(@NonNull String text) {
        this.text = text;
        return this;
    }

    @Override
    public String getName() {
        return "text";
    }

    @Override
   public Object build() {
        HashMap<String, String> map = new HashMap<>();
        map.put(this.getName(), text);
        return map;
    }

    @Override
    public String toString() {
        return this.text;
    }
}