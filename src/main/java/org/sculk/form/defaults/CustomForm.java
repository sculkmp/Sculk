package org.sculk.form.defaults;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.*;
import lombok.experimental.Accessors;
import org.sculk.form.IForm;
import org.sculk.form.element.Element;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class CustomForm implements IForm {
    @NonNull
    protected String title;
    protected ObjectArrayList<Element> elements = new ObjectArrayList<>();

    public CustomForm addElement(Element element) {
        this.elements.add(element);
        return this;
    }

    @Override
    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", "custom_form");
        object.addProperty("title", this.getTitle());

        JsonArray elementArray = new JsonArray();
        this.getElements().forEach(element -> elementArray.add(element.toJson()));

        object.add("content", elementArray);
        return object;
    }
}
