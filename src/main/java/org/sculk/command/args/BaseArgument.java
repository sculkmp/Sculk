package org.sculk.command.args;

import lombok.Getter;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParam;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamData;
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
public abstract class BaseArgument {
    @Getter
    protected String name;
    @Getter
    public boolean optional = false;
    private CommandParamData commandParam;

   public BaseArgument(String name) {
       this.name = name;
       this.optional = false;
       this.commandParam = new CommandParamData();
       this.commandParam.setName(name);
       this.commandParam.setType(this.getTypeParam());
       this.commandParam.setOptional(false);
   }
   public BaseArgument(String name, boolean optional) {
       this.name = name;
       this.optional = optional;
       this.commandParam = new CommandParamData();
       this.commandParam.setName(name);
       this.commandParam.setType(this.getTypeParam());
       this.commandParam.setOptional(optional);
   }

   /**
    * Retrieves the type parameter associated with the argument.
    *
    * @return the {@link CommandParam} type that represents the argument.
    */
   abstract public CommandParam getTypeParam();

    /**
     * Determines if the given input string can be parsed based on the specific argument type.
     *
     * @param testString the string to test for parsing compatibility
     * @param sender the CommandSender attempting to parse the string
     * @return true if the string can be parsed, false otherwise
     */
    abstract public boolean canParse(String testString, CommandSender sender);

    /**
     * Parses a given argument string and returns the corresponding Object based on the implementation.
     *
     * @param argument the argument to parse as a string
     * @param sender the sender executing the command
     * @return the parsed Object corresponding to the argument
     */
    abstract public Object parse(String argument, CommandSender sender);

    /**
     * Returns the type name of the argument.
     *
     * @return the type name of the argument as a string
     */
    abstract public String getTypeName();

    /**
     * Returns the length of the span associated with the argument.
     * The default implementation in the base class returns a fixed span length of 1.
     * Subclasses may override this method to provide their specific span length.
     *
     * @return the span length as an integer
     */
    public int getSpanLength() {
        return 1;
    }

    /**
     * Retrieves the {@link CommandParamData} associated with this argument.
     *
     * @return the {@link CommandParamData} object that contains metadata about the argument,
     *         such as its name, type, and whether it is optional.
     */
    public CommandParamData getNetworkParameterData() {
        return this.commandParam;
    }

}
