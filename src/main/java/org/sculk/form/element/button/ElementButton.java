package org.sculk.form.element.button;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.sculk.utils.json.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
public class ElementButton implements Serializable {
    protected String text;
    protected Image image;

    public ElementButton(String text) {
        this(text, null);
    }

    @Override
    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("text", this.text);

        if (this.image != null) {
            JsonObject imageObject = new JsonObject();
            imageObject.addProperty("type", this.image.getType().name().toLowerCase());
            imageObject.addProperty("path", this.image.getPath());
            object.add("image", imageObject);
        }
        return object;
    }
}
