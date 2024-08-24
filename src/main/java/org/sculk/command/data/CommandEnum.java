package org.sculk.command.data;

import lombok.ToString;
import org.cloudburstmc.protocol.bedrock.data.command.CommandEnumConstraint;
import org.cloudburstmc.protocol.bedrock.data.command.CommandEnumData;

import java.util.*;

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
@ToString
public class CommandEnum {

    private final String name;
    private final List<String> values;

    public CommandEnum(String name, List<String> values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public List<String> getValues() {
        return values;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public CommandEnumData toNetwork() {
        String[] aliases;
        if (!values.isEmpty()) {
            List<String> aliasList = new ArrayList<>(values);
            aliasList.add(this.name);
            aliases = aliasList.toArray(new String[0]);
        } else {
            aliases = new String[]{this.name};
        }
        return new CommandEnumData(this.name + "Aliases", toNetwork(aliases), false);
    }

    private static LinkedHashMap<String, Set<CommandEnumConstraint>> toNetwork(String[] values) {
        LinkedHashMap<String, Set<CommandEnumConstraint>> map = new LinkedHashMap<>();
        for (String value : values) {
            map.put(value, Collections.emptySet());
        }
        return map;
    }
}
