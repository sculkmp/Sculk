package org.sculk.form.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ModalResponse implements Response {
    protected boolean closed = false;
    protected int buttonId = -1;
    protected String text = "";
}
