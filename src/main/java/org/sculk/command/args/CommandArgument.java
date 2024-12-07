package org.sculk.command.args;

import org.sculk.Server;
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
public class CommandArgument extends RawStringArgument{
    public CommandArgument(String name) {
        super(name);
    }

    public CommandArgument(String name, boolean optional) {
        super(name, optional);
    }

    @Override
    public boolean canParse(String testString, CommandSender sender) {
        return Server.getInstance().getCommandMap().getCommand(testString) != null;
    }

    @Override
    public Object parse(String argument, CommandSender sender) {
        return Server.getInstance().getCommandMap().getCommand(argument);
    }

    @Override
    public String getTypeName() {
        return "commmand";
    }
}
