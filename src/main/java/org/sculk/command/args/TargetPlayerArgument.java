package org.sculk.command.args;

import org.cloudburstmc.protocol.bedrock.data.command.CommandParam;
import org.sculk.command.CommandSender;

import java.util.regex.Pattern;

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
public class TargetPlayerArgument extends BaseArgument{

    private static final Pattern pattern = Pattern.compile("^(?!rcon|console)[a-zA-Z0-9_ ]{1,16}$",Pattern.CASE_INSENSITIVE);

    public TargetPlayerArgument(String name) {
        super(name == null ? "player" : name);
    }
    public TargetPlayerArgument(String name, boolean optional) {
        super(name == null ? "player" : name, optional);
    }

    @Override
    public CommandParam getTypeParam() {
        return CommandParam.TARGET;
    }

    @Override
    public boolean canParse(String testString, CommandSender sender) {
        return pattern.matcher(testString).matches();
    }

    @Override
    public Object parse(String argument, CommandSender sender) {
        return argument;
    }

    @Override
    public String getTypeName() {
        return "target";
    }
}
