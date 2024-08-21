package org.sculk.form.defaults;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.*;
import lombok.experimental.Accessors;
import org.cloudburstmc.protocol.bedrock.packet.ModalFormResponsePacket;
import org.sculk.Player;
import org.sculk.form.Form;
import org.sculk.form.element.*;
import org.sculk.form.response.CustomResponse;
import org.sculk.form.response.ElementResponse;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
public class CustomForm implements Form {
    private static Type LIST_STRING_TYPE = new TypeToken<List<String>>(){}.getType();

    @NonNull protected String title;
    @NonNull protected ObjectArrayList<Element> elements;

    protected Consumer<Player> closed = player -> {};
    protected BiConsumer<Player, CustomResponse> submitted = (player, response) -> {};

    public CustomForm() {
        this("");
    }

    public CustomForm(String title) {
        this(title, new ObjectArrayList<>());
    }

    public CustomForm addElement(Element element) {
        this.elements.add(element);
        return this;
    }

    /**
     *
     * Forms need an identifier so that the Minecraft client knows what type of form to open. ('type' => 'custom_form')
     * The json data of a custom form contains a title and content (array of elements).
     *
     * @return A json object containing data used by the Minecraft client to construct a custom form
     */
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

    /**
     *
     * The client sends us an array of responses, which are in the same order as the elements within the form
     * The value will be 'null' if the player closes the form
     * We retrieve the corresponding element from our array and set the responses into a new {@link org.sculk.form.response.CustomResponse}.
     *
     * @param packet The packet sent to the server by the client
     * @return A response object
     */
    @Override
    public CustomResponse processResponse(Player player, ModalFormResponsePacket packet) {
        CustomResponse response = new CustomResponse();

        String data = packet.getFormData().trim();
        if (data.equals("null")) {
            this.closed.accept(player);
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

        this.submitted.accept(player, response);
        return response;
    }
}
