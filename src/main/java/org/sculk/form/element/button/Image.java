package org.sculk.form.element.button;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
 *   ____             _ _
 *  / ___|  ___ _   _| | | __
 *  \___ \ / __| | | | | |/ /
 *   ___) | (__| |_| | |   <
 *  |____/ \___|\__,_|_|_|\_\
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author: SculkTeams
 * @link: http://www.sculkmp.org/
 */

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
