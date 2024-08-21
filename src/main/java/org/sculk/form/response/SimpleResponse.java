package org.sculk.form.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.sculk.form.element.button.ElementButton;

@Getter
@Setter
@Accessors(chain = true)
public class SimpleResponse implements Response {
    protected boolean closed = false;
    protected int buttonId = -1;
    protected ElementButton button = null;
}
