package org.sculk.form.defaults;

import com.google.gson.JsonObject;
import lombok.*;
import lombok.experimental.Accessors;
import org.cloudburstmc.protocol.bedrock.packet.ModalFormResponsePacket;
import org.sculk.form.Form;
import org.sculk.form.response.ModalResponse;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
public class ModalForm implements Form {
    @NonNull
    protected String title;
    @NonNull
    protected String content;

    protected String yes;
    protected String no;

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
    public ModalResponse processResponse(ModalFormResponsePacket packet) {
        ModalResponse response = new ModalResponse();

        String data = packet.getFormData().trim();
        if (data.equals("null")) {
            return response.setClosed(true);
        }

        boolean clickedYes = data.equals("true");
        return clickedYes ? response.setButtonId(0).setText(this.yes)
                : response.setButtonId(1).setText(this.no);
    }
}
