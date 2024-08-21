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
@RequiredArgsConstructor
public class ModalForm implements Form {
    @NonNull
    protected String title;
    @NonNull
    protected String content;

    protected String yes = "";
    protected String no = "";

    public ModalForm setText(String yes, String no) {
        return this.setYes(yes).setNo(no);
    }

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

    @Override
    public ModalResponse processResponse(ModalFormResponsePacket packet) {
        ModalResponse response = new ModalResponse();

        String data = packet.getFormData().trim();
        if (data.equals("null")) {
            return response.setClosed(true);
        }

        return data.equals("true") ? response.setButtonId(0).setText(this.yes)
                : response.setButtonId(1).setText(this.no);
    }
}
