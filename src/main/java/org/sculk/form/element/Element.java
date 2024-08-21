package org.sculk.form.element;

import org.sculk.utils.json.Serializable;

public interface Element extends Serializable {

    Type getType();

    enum Type {
        DROPDOWN,
        INPUT,
        LABEL,
        SLIDER,
        STEP_SLIDER,
        TOGGLE
    }
}
