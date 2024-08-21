package org.sculk.form.element;

import com.google.common.base.Preconditions;
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
public class ElementSlider implements Element {
    protected String text;
    protected float min;
    protected float max;
    protected int step;
    protected float defaultValue;

    public ElementSlider() {
        this("");
    }

    public ElementSlider(String text) {
        this(text, 1);
    }

    public ElementSlider(String text, float min) {
        this(text, min, Math.max(min, 100));
    }

    public ElementSlider(String text, float min, float max) {
        this(text, min, max, 1);
    }

    public ElementSlider(String text, float min, float max, int step) {
        this(text, min, max, step, 1);
    }

    @Override
    public Type getType() {
        return Type.SLIDER;
    }

    @Override
    public JsonObject toJson() {
        Preconditions.checkArgument(this.min < this.max, "Maximum slider value must exceed the minimum value");
        Preconditions.checkArgument(this.defaultValue >= this.min && this.defaultValue <= this.max, "Default value out of range");

        JsonObject object = new JsonObject();
        object.addProperty("type", "slider");
        object.addProperty("text", this.text);
        object.addProperty("min", this.min);
        object.addProperty("max", this.max);
        object.addProperty("step", this.step);
        object.addProperty("default", this.defaultValue);
        return object;
    }
}
