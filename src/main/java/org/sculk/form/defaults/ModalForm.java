package org.sculk.form.defaults;

import com.google.gson.JsonObject;
import lombok.*;
import lombok.experimental.Accessors;
import org.cloudburstmc.protocol.bedrock.packet.ModalFormResponsePacket;
import org.sculk.Player;
import org.sculk.form.Form;
import org.sculk.form.response.ModalResponse;

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
public class ModalForm extends Form {
    @NonNull protected String title;
    @NonNull protected String content;

    @NonNull protected String yes;
    @NonNull protected String no;

    protected Consumer<Player> onYes = player -> {};
    protected Consumer<Player> onNo = player -> {};

    public ModalForm() {
        this("");
    }

    public ModalForm(String title) {
        this(title, "");
    }

    public ModalForm(String title, String content) {
        this(title, content, "", "");
    }

    public ModalForm setText(String yes, String no) {
        return this.setYes(yes).setNo(no);
    }

    /**
     *
     * Forms need an identifier so that the Minecraft client knows what type of form to open. ('type' => 'modal')
     * The json data of a modal form contains a title, content and two button texts.
     *
     * @return A json object containing data used by the Minecraft client to construct a modal form
     */
    @Override
    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", "modal"); // DO NOT CHANGE: Required to find out which form should be created client-side
        object.addProperty("title", this.title);
        object.addProperty("content", this.content);
        object.addProperty("button1", this.yes);
        object.addProperty("button2", this.no);
        return object;
    }

    /**
     *
     * The client sends us a boolean value that we can use to determine whether the player clicked 'yes' or 'no'.
     * The value will be 'null' if the player closes the form.
     *
     * @param packet The packet sent to the server by the client
     * @return A response object
     */
    @Override
    public ModalResponse processResponse(Player player, ModalFormResponsePacket packet) {
        ModalResponse response = new ModalResponse();

        String data = packet.getFormData().trim();
        if (data.equals("null")) {
            return response.setClosed(true);
        }

        boolean clickedYes = data.equals("true");
        if (clickedYes) {
            this.onYes.accept(player);
            response.setButtonId(0).setText(this.yes);
        } else {
            this.onNo.accept(player);
            response.setButtonId(1).setText(this.no);
        }
        return response;
    }
}
