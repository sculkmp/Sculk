package org.sculk.form.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ElementResponse {
    protected final int elementId;
    protected final String elementText;
}
