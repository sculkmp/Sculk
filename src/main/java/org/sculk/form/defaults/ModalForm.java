package org.sculk.form.defaults;

import com.google.gson.JsonObject;
import lombok.*;
import lombok.experimental.Accessors;
import org.sculk.form.IForm;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class ModalForm implements IForm {
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
}
