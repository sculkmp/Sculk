package org.sculk.command.args;

import org.sculk.Server;
import org.sculk.command.Command;
import org.sculk.command.CommandSender;

public class CommandArgument extends RawStringArgument {
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
        return "command";
    }
}