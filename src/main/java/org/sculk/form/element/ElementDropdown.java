package org.sculk.form.element;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

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
public class ElementDropdown implements Element {
    protected String text;
    protected List<String> options;
    protected int defaultOption;

    public ElementDropdown() {
        this("");
    }

    public ElementDropdown(String text) {
        this(text, new ArrayList<>());
    }

    public ElementDropdown(String text, List<String> options) {
        this(text, options, 0);
    }

    @Override
    public Type getType() {
        return Type.DROPDOWN;
    }

    @Override
    public JsonObject toJson() {
        Preconditions.checkArgument(this.defaultOption > -1 && this.defaultOption < this.options.size(), "Default option not an index");

        JsonObject object = new JsonObject();
        object.addProperty("type", "dropdown");
        object.addProperty("text", this.text);
        object.addProperty("default", this.defaultOption);

        JsonArray optionsArray = new JsonArray();
        this.options.forEach(optionsArray::add);

        object.add("options", optionsArray);
        return object;
    }
}
