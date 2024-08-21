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
public class ElementStepSlider implements Element {
    protected String text;
    protected List<String> steps;
    protected int defaultStep;

    public ElementStepSlider() {
        this("");
    }

    public ElementStepSlider(String text) {
        this(text, new ArrayList<>());
    }

    public ElementStepSlider(String text, List<String> steps) {
        this(text, steps, 0);
    }

    @Override
    public Type getType() {
        return Type.STEP_SLIDER;
    }

    @Override
    public JsonObject toJson() {
        Preconditions.checkArgument(this.defaultStep > -1 && this.defaultStep < this.steps.size(), "Default option not within range");

        JsonObject object = new JsonObject();
        object.addProperty("type", "step_slider");
        object.addProperty("text", this.text);
        object.addProperty("default", this.defaultStep);

        JsonArray optionsArray = new JsonArray();
        this.steps.forEach(optionsArray::add);

        object.add("steps", optionsArray);
        return object;
    }
}
