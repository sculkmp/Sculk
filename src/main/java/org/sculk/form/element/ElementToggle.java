package org.sculk.form.element;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
public class ElementToggle implements Element {
    protected String text;
    protected boolean defaultValue;

    public ElementToggle() {
        this("");
    }

    public ElementToggle(String text) {
        this(text, false);
    }

    @Override
    public Type getType() {
        return Type.TOGGLE;
    }

    @Override
    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", "toggle");
        object.addProperty("text", this.text);
        object.addProperty("default", this.defaultValue);
        return object;
    }
}
