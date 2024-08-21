package org.sculk.form.element.button;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Image {
    protected final Type type;
    protected final String path;

    public enum Type {
        PATH,
        URL;

        public Image of(String path) {
            return new Image(this, path);
        }
    }
}
