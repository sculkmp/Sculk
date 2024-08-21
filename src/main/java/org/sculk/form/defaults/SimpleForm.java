package org.sculk.form.defaults;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import lombok.*;
import lombok.experimental.Accessors;
import org.cloudburstmc.protocol.bedrock.packet.ModalFormResponsePacket;
import org.cloudburstmc.protocol.common.util.Preconditions;
import org.sculk.Player;
import org.sculk.form.Form;
import org.sculk.form.element.button.ElementButton;
import org.sculk.form.element.button.Image;
import org.sculk.form.response.SimpleResponse;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
@RequiredArgsConstructor
public class SimpleForm implements Form {
    private static final ElementButton[] EMPTY_ARRAY = new ElementButton[0];

    @NonNull protected String title;
    @NonNull protected String content;
    @NonNull protected Object2ObjectArrayMap<ElementButton, Consumer<Player>> elements; // No OpenHashMap here because it messes with the entry order

    protected Consumer<Player> closed = player -> {};
    protected BiConsumer<Player, SimpleResponse> submitted = (player, response) -> {};

    public SimpleForm() {
        this("");
    }

    public SimpleForm(String title) {
        this(title, "");
    }

    public SimpleForm(String title, String content) {
        this(title, content, new Object2ObjectArrayMap<>());
    }

    public SimpleForm addButton(String text) {
        return this.addButton(text, null);
    }

    public SimpleForm addButton(String text, Image image) {
        return this.addButton(new ElementButton(text, image));
    }

    public SimpleForm addButton(ElementButton button) {
        return this.addButton(button, null);
    }

    public SimpleForm addButton(ElementButton button, Consumer<Player> callback) {
        this.elements.put(button, callback);
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
        this.getElements().keySet()
                .stream()
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
    public SimpleResponse processResponse(Player player, ModalFormResponsePacket packet) {
        SimpleResponse response = new SimpleResponse();

        String data = packet.getFormData().trim();
        int buttonId;
        try {
            buttonId = Integer.parseInt(data);
        } catch (Exception e) {
            this.closed.accept(player);
            return response.setClosed(true); // If the player closes the form, the data will be 'null'
        }

        Preconditions.checkArgument(buttonId < this.elements.size(), "buttonId out of range");

        ElementButton button = this.elements.keySet().toArray(EMPTY_ARRAY)[buttonId];
        if (button != null) {
            Optional.ofNullable(this.elements.get(button)) // Only accept consumer if present
                    .ifPresent(callback -> callback.accept(player));
        }

        response.setButtonId(buttonId)
                .setButton(button);

        this.submitted.accept(player, response);

        return response;
    }
}
