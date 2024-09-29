package org.sculk.command.defaults;

import com.google.gson.Gson;
import org.sculk.Server;
import org.sculk.command.Command;
import org.sculk.command.CommandSender;
import org.sculk.exception.CommandException;
import org.sculk.permission.DefaultPermissionNames;
import org.sculk.player.text.RawTextBuilder;
import org.sculk.player.text.TextBuilder;
import org.sculk.player.text.TranslaterBuilder;

import java.util.ArrayList;
import java.util.List;

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
public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "Show the help menu", "/help [page|command name]", List.of("?"));
        this.setPermission(DefaultPermissionNames.COMMAND_HELP);
    }

    @Override
    public void execute(CommandSender sender, String commandLabel, List<String> args) throws CommandException {
        StringBuilder builder = new StringBuilder();
        TranslaterBuilder translaterBuilder = new TranslaterBuilder();
        List<String> commandSending = new ArrayList<>();
        translaterBuilder.setTranslate("§6-------------- §fHelp - %%s command(s) §6--------------\n%%s");
        Server.getInstance().getCommandMap().getCommands().forEach((s, command) -> {
        String commandName = s.contains(":") ? s.substring(s.indexOf(':') + 1) : s;
            if(!commandSending.contains(commandName)) {
                builder.append("§6/").append(commandName).append(":§f ").append(command.getDescription()).append("\n");
                commandSending.add(commandName);
            }
        });

        translaterBuilder.setWith(new RawTextBuilder()
                .add(new TextBuilder()
                        .setText(Integer.toString(commandSending.size())))
                .add(new TextBuilder()
                        .setText(builder.substring(0, builder.length() - 2)))
        );
        sender.sendMessage(new RawTextBuilder().add(translaterBuilder));

    }

}
