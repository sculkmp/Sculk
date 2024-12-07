package org.sculk.command.args;

import org.sculk.command.CommandSender;

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
public class TextArgument extends RawStringArgument{
    public TextArgument(String name) {
        super(name);
    }

    public TextArgument(String name, boolean optional) {
        super(name, optional);
    }

    @Override
    public boolean canParse(String testString, CommandSender sender) {
        return !testString.isEmpty();
    }

    @Override
    public String getTypeName() {
        return "text";
    }

    @Override
    public int getSpanLength() {
        return Integer.MAX_VALUE;
    }
}
