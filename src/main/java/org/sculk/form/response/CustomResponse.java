package org.sculk.form.response;

import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CustomResponse implements Response {
    protected boolean closed = false;

    protected final Int2ObjectOpenHashMap<Object> responses = new Int2ObjectOpenHashMap<>();

    protected final Int2ObjectOpenHashMap<ElementResponse> dropdownResponses = new Int2ObjectOpenHashMap<>();
    protected final Int2ObjectOpenHashMap<String> inputResponses = new Int2ObjectOpenHashMap<>();
    protected final Int2ObjectOpenHashMap<String> labelResponses = new Int2ObjectOpenHashMap<>();
    protected final Int2FloatOpenHashMap sliderResponses = new Int2FloatOpenHashMap();
    protected final Int2ObjectOpenHashMap<ElementResponse> stepSliderResponses = new Int2ObjectOpenHashMap<>();
    protected final Int2BooleanOpenHashMap toggleResponses = new Int2BooleanOpenHashMap();

    public void setDropdownResponse(int index, ElementResponse response) {
        this.responses.put(index, response.getElementText());
        this.dropdownResponses.put(index, response);
    }

    public void setInputResponse(int index, String response) {
        this.responses.put(index, response);
        this.inputResponses.put(index, response);
    }

    public void setLabelResponse(int index, String response) {
        this.responses.put(index, response);
        this.labelResponses.put(index, response);
    }

    public void setSliderResponse(int index, float response) {
        this.responses.put(index, (Float) response);
        this.sliderResponses.put(index, response);
    }

    public void setStepSliderResponse(int index, ElementResponse response) {
        this.responses.put(index, response.getElementText());
        this.stepSliderResponses.put(index, response);
    }

    public void setToggleResponse(int index, boolean response) {
        this.responses.put(index, (Boolean) response);
        this.toggleResponses.put(index, response);
    }
}
