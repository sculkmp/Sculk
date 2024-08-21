package org.sculk.form.defaults;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.*;
import lombok.experimental.Accessors;
import org.sculk.form.IForm;
import org.sculk.form.element.button.ElementButton;
import org.sculk.form.element.button.Image;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class SimpleForm implements IForm {
    @NonNull
    protected String title;
    @NonNull
    protected String content;
    protected ObjectArrayList<ElementButton> elements;

    public SimpleForm() {
        this("");
    }

    public SimpleForm(String title) {
        this(title, "");
    }

    public SimpleForm addButton(String text) {
        return this.addButton(text, null);
    }

    public SimpleForm addButton(String text, Image image) {
        return this.addButton(new ElementButton(text, image));
    }

    public SimpleForm addButton(ElementButton button) {
        this.elements.add(button);
        return this;
    }

    @Override
    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", "form"); // DO NOT CHANGE: Required to find out which form should be created client-side
        object.addProperty("title", this.getTitle());
        object.addProperty("content", this.getContent());

        JsonArray buttons = new JsonArray();
        this.getElements().stream()
                .map(ElementButton::toJson)
                .forEach(buttons::add);
        object.add("buttons", buttons);

        return object;
    }
}
