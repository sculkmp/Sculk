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
public class ElementInput implements Element {
    protected String text;
    protected String placeholder;
    protected String defaultText;

    public ElementInput() {
        this("");
    }

    public ElementInput(String text) {
        this(text, "");
    }

    public ElementInput(String text, String placeholder) {
        this(text, placeholder, "");
    }

    @Override
    public Type getType() {
        return Type.INPUT;
    }

    @Override
    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", "input");
        object.addProperty("text", this.text);
        object.addProperty("placeholder", this.placeholder);
        object.addProperty("default", this.defaultText);
        return object;
    }
}
