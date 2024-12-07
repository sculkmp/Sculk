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

   abstract public CommandParam getTypeParam();


    /**
     * @return boolean
     */
    abstract public boolean canParse(String testString, CommandSender sender);

    abstract public Object parse(String argument, CommandSender sender);

    abstract public String getTypeName();

    public int getSpanLength() {
        return 1;
    }

    public CommandParamData getNetworkParameterData() {
        return this.commandParam;
    }

}
