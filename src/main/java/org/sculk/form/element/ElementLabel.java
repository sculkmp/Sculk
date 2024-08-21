package org.sculk.form.element;

import com.google.gson.JsonObject;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
public class ElementLabel implements Element {
    protected String text;

    public ElementLabel() {
        this("");
    }

    @Override
    public Type getType() {
        return Type.LABEL;
    }

    @Override
    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", "label");
        object.addProperty("text", this.text);
        return object;
    }
}
