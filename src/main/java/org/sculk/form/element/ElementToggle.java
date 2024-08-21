package org.sculk.form.element;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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
