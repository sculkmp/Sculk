package org.sculk.command.utils;


import java.util.ArrayList;
import java.util.regex.Matcher;
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
public final class CommandStringHelper {

    public static String[] parseQuoteAware(String commandLine) {
        ArrayList<String> args = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"((?:\\\\.|[^\\\\\"])*)\"|(\\S+)");
        Matcher matcher = pattern.matcher(commandLine);

        while (matcher.find()) {
            String match = null;
            if (matcher.group(1) != null) {
                match = matcher.group(1).replaceAll("\\\\([\\\\\"])", "$1");
            } else if (matcher.group(2) != null) {
                match = matcher.group(2);
            }

            if (match != null) {
                args.add(match);
            }
        }
        return args.toArray(new String[0]);
    }

}
