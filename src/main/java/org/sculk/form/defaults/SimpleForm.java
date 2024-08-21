package org.sculk.form.defaults;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.*;
import lombok.experimental.Accessors;
import org.cloudburstmc.protocol.bedrock.packet.ModalFormResponsePacket;
import org.sculk.form.Form;
import org.sculk.form.element.button.ElementButton;
import org.sculk.form.element.button.Image;
import org.sculk.form.response.SimpleResponse;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
public class SimpleForm implements Form {
    protected String title;
    protected String content;
    protected ObjectArrayList<ElementButton> elements;

    public SimpleForm() {
        this("");
    }

    public SimpleForm(String title) {
        this(title, "");
    }

    public SimpleForm(String title, String content) {
        this(title, content, new ObjectArrayList<>());
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

    /**
     *
     * Forms need an identifier so that the Minecraft client knows what type of form to open. ('type' => 'form')
     * The json data of a simple form contains a title, content and an array of buttons (text + optional image).
     *
     * @return A json object containing data used by the Minecraft client to construct a simple form
     */
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

    /**
     *
     * The client sends us a buttonId corresponding to the button's position within the form, which we use to determine the button clicked in our elements array
     * If the response does not contain an integer ('null'), the form has been closed.
     *
     * @param packet The packet sent to the server by the client
     * @return A response object
     */
    @Override
    public SimpleResponse processResponse(ModalFormResponsePacket packet) {
        SimpleResponse response = new SimpleResponse();

        String data = packet.getFormData().trim();
        int buttonId;
        try {
            buttonId = Integer.parseInt(data);
        } catch (Exception e) {
            return response.setClosed(true);
        }

        ElementButton button = buttonId < this.elements.size() ? this.elements.get(buttonId) : null;
        return response.setButtonId(buttonId)
                .setButton(button);
    }
}
