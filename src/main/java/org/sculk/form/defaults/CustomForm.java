package org.sculk.form.defaults;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.*;
import lombok.experimental.Accessors;
import org.cloudburstmc.protocol.bedrock.packet.ModalFormResponsePacket;
import org.sculk.form.Form;
import org.sculk.form.element.*;
import org.sculk.form.response.CustomResponse;
import org.sculk.form.response.ElementResponse;
import org.sculk.form.response.Response;

import java.lang.reflect.Type;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class CustomForm implements Form {
    private static Type LIST_STRING_TYPE = new TypeToken<List<String>>(){}.getType();

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
        object.addProperty("type", "custom_form"); // DO NOT CHANGE: Required to find out which form should be created client-side
        object.addProperty("title", this.getTitle());

        JsonArray elementArray = new JsonArray();
        this.getElements().forEach(element -> elementArray.add(element.toJson()));

        object.add("content", elementArray);
        return object;
    }

    @Override
    public Response processResponse(ModalFormResponsePacket packet) {
        CustomResponse response = new CustomResponse();

        String data = packet.getFormData().trim();
        if (data.equals("null")) {
            return response.setClosed(true);
        }

        List<String> parsedResponse = GsonHolder.GSON.fromJson(data, LIST_STRING_TYPE);

        for (int i = 0, responseSize = parsedResponse.size(); i < responseSize; i++) {
            if (i >= this.elements.size()) {
                break;
            }

            String responseData = parsedResponse.get(i);
            Element element = this.elements.get(i);

            switch (element) {
                case ElementDropdown dropdown -> {
                    int index = Integer.parseInt(responseData);
                    String option = dropdown.getOptions().get(index);
                    response.setDropdownResponse(i, new ElementResponse(index, option));
                }
                case ElementInput input -> response.setInputResponse(i, responseData);
                case ElementLabel label -> response.setLabelResponse(i, label.getText());
                case ElementSlider slider -> {
                    float answer = Float.parseFloat(responseData);
                    response.setSliderResponse(i, answer);
                }
                case ElementStepSlider stepSlider -> {
                    int index = Integer.parseInt(responseData);
                    String step = stepSlider.getSteps().get(index);
                    response.setStepSliderResponse(i, new ElementResponse(index, step));
                }
                case ElementToggle toggle -> {
                    boolean value = Boolean.parseBoolean(responseData);
                    response.setToggleResponse(i, value);
                }

                default -> {}
            }
        }
        return response;
    }
}
