package org.sculk.command.error;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

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
public class ErrorParsingArgument {
    public enum Type {
        ERR_INVALID_ARG_VALUE,
        ERR_TOO_MANY_ARGUMENTS,
        ERR_INSUFFICIENT_ARGUMENTS,
        ERR_NO_ARGUMENTS,
        ERR_INVALID_ARGUMENTS;
    }

    @Getter
    private static Map<Type, String> errorMessages;
    static {
        errorMessages = new EnumMap<>(Type.class);

        // Initialize error messages with their corresponding error codes
        errorMessages.put(Type.ERR_INVALID_ARG_VALUE, "Invalid value '{value}' for argument #{position}. Expecting: {expected}.");
        errorMessages.put(Type.ERR_TOO_MANY_ARGUMENTS, "Too many arguments given.");
        errorMessages.put(Type.ERR_INSUFFICIENT_ARGUMENTS, "Insufficient number of arguments given.");
        errorMessages.put(Type.ERR_NO_ARGUMENTS, "No arguments are required for this command.");
        errorMessages.put(Type.ERR_INVALID_ARGUMENTS, "Invalid arguments supplied.");
    }

    @Getter
    private Type type;
    @Getter @Setter
    private @Nullable String value;
    @Getter @Setter
    private @Nullable int position;
    @Getter @Setter
    private @Nullable String expected;
    public ErrorParsingArgument(Type type) {
        this.type = type;
    }


}
