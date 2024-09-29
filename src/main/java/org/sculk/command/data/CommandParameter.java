package org.sculk.command.data;

import com.google.common.collect.ImmutableMap;
import lombok.ToString;
import org.cloudburstmc.protocol.bedrock.data.command.*;

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
public class CommandParameter {

    private static final ImmutableMap<CommandParamType, CommandParam> PARAM_MAPPINGS = ImmutableMap.<CommandParamType, CommandParam>builder()
            .put(CommandParamType.INT, CommandParam.INT)
            .put(CommandParamType.FLOAT, CommandParam.FLOAT)
            .put(CommandParamType.VALUE, CommandParam.VALUE)
            .put(CommandParamType.WILDCARD_INT, CommandParam.WILDCARD_INT)
            .put(CommandParamType.OPERATOR, CommandParam.OPERATOR)
            .put(CommandParamType.TARGET, CommandParam.TARGET)
            .put(CommandParamType.WILDCARD_TARGET, CommandParam.WILDCARD_TARGET)
            .put(CommandParamType.FILE_PATH, CommandParam.FILE_PATH)
            .put(CommandParamType.INT_RANGE, CommandParam.INT_RANGE)
            .put(CommandParamType.STRING, CommandParam.STRING)
            .put(CommandParamType.POSITION, CommandParam.POSITION)
            .put(CommandParamType.BLOCK_POSITION, CommandParam.BLOCK_POSITION)
            .put(CommandParamType.MESSAGE, CommandParam.MESSAGE)
            .put(CommandParamType.TEXT, CommandParam.TEXT)
            .put(CommandParamType.JSON, CommandParam.JSON)
            .put(CommandParamType.COMMAND, CommandParam.COMMAND)
            .build();

    public String name;
    public CommandParamType type;
    public boolean optional;
    public byte options = 0;

    public CommandEnum enumData;
    public String postFix;

    public CommandParameter(String name, CommandParamType type, boolean optional) {
        this.name = name;
        this.type = type;
        this.optional = optional;
    }

    public CommandParameter(String name, boolean optional) {
        this(name, CommandParamType.TEXT, optional);
    }

    public CommandParameter(String name) {
        this(name, false);
    }

    public CommandParameter(String name, boolean optional, String enumType) {
        this.name = name;
        this.type = CommandParamType.TEXT;
        this.optional = optional;
        this.enumData = new CommandEnum(enumType, new ArrayList<>());
    }

    public CommandParameter(String name, boolean optional, String[] enumValues) {
        this.name = name;
        this.type = CommandParamType.TEXT;
        this.optional = optional;
        this.enumData = new CommandEnum(name + "Enums", Arrays.asList(enumValues));
    }

    public CommandParameter(String name, String enumType) {
        this(name, false, enumType);
    }

    public CommandParameter(String name, String[] enumValues) {
        this(name, false, enumValues);
    }

    protected CommandParamData toNetwork() {
        CommandParamData data = new CommandParamData();
        data.setName(this.name);
        data.setOptional(this.optional);
        data.setEnumData(this.enumData != null ? new CommandEnumData(this.name, toNetwork(this.enumData.getValues()), false) : null);
        data.setType(PARAM_MAPPINGS.get(this.type));
        data.setPostfix(this.postFix);

        return data;
    }

    private static LinkedHashMap<String, Set<CommandEnumConstraint>> toNetwork(List<String> values) {
        LinkedHashMap<String, Set<CommandEnumConstraint>> map = new LinkedHashMap<>();
        for (String value : values) {
            map.put(value, Collections.emptySet());
        }
        return map;
    }

    protected static CommandParamType fromString(String param) {
        return switch (param) {
            case "string", "stringenum" -> CommandParamType.STRING;
            case "target" -> CommandParamType.TARGET;
            case "blockpos" -> CommandParamType.POSITION;
            case "rawtext" -> CommandParamType.TEXT;
            case "int" -> CommandParamType.INT;
            default -> CommandParamType.TEXT;
        };

    }
}
