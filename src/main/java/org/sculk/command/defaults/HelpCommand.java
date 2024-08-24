package org.sculk.command.defaults;

import com.google.gson.Gson;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;
import org.sculk.Server;
import org.sculk.command.Command;
import org.sculk.command.CommandSender;
import org.sculk.command.data.CommandParameter;
import org.sculk.exception.CommandException;
import org.sculk.permission.DefaultPermissionNames;
import org.sculk.player.text.RawTextBuilder;
import org.sculk.player.text.TextBuilder;
import org.sculk.player.text.TranslaterBuilder;

import java.util.ArrayList;
import java.util.List;
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
public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "Show the help menu", "/help [page|command name]", List.of("?"));
        this.setPermission(DefaultPermissionNames.COMMAND_HELP);
        this.registerParameter(new CommandParameter("page", CommandParamType.INT, true));
        this.registerParameter(new CommandParameter("command", CommandParamType.TEXT, true));
    }

    @Override
    public void execute(CommandSender sender, String commandLabel, List<String> args) throws CommandException {
        StringBuilder builder = new StringBuilder();
        List<String> commandSending = new ArrayList<>();
        Server.getInstance().getCommandMap().getCommands().forEach((s, command) -> {
            String commandName = s.contains(":") ? s.substring(s.indexOf(':') + 1) : s;
            if(!commandSending.contains(commandName)) {
                commandSending.add(commandName);
            }
        });

        int totalCommands = commandSending.size();
        int commandsPerPage = 5;
        int actualPage = 1;
        int totalPage = (int) Math.ceil((double) totalCommands / commandsPerPage);

        int startIndex = 0;
        int endIndex = Math.min(startIndex + commandsPerPage, totalCommands);

        if (!args.isEmpty()) {
            try {
                actualPage = Integer.parseInt(args.getFirst());
                if (actualPage < 1 || actualPage > totalPage) {
                    actualPage = 1;
                }
            } catch (NumberFormatException e) {
                actualPage = 1;
                String commandName = args.getFirst();
                Command command = Server.getInstance().getCommandMap().getCommand(commandName);
                try {
                    sender.sendMessage(command.getLabel());
                    sender.sendMessage(new RawTextBuilder().add(new TranslaterBuilder()
                            .setTranslate("§6/%%s: §f%%s\nUsage: §e%%s")
                            .setWith(new RawTextBuilder()
                                    .add(new TextBuilder().setText(command.getLabel()))
                                    .add(new TextBuilder().setText(command.getDescription()))
                                    .add(new TextBuilder().setText(command.getUsageMessage()))
                            )
                    ));
                } catch(RuntimeException exception) {
                    sender.sendMessage(new RawTextBuilder().add(new TranslaterBuilder().setTranslate("§4/%%s§c does not seem to exist, checked the list of commands with §4/help§c.").setWith(new RawTextBuilder()
                            .add(new TextBuilder().setText(args.getFirst()))
                    )));
                }
                return;
            }

            startIndex = (actualPage - 1) * commandsPerPage;
            endIndex = Math.min(startIndex + commandsPerPage, totalCommands);
        }

        for (int i = startIndex; i < endIndex; i++) {
            String commandName = commandSending.get(i);
            Command command = Server.getInstance().getCommandMap().getCommand(commandName);
            if (command != null) {
                builder.append("§6/").append(commandName).append(":§f ").append(command.getDescription()).append("\n");
            }
        }

        TranslaterBuilder translaterBuilder = new TranslaterBuilder();
        translaterBuilder.setTranslate("§6-------------- §fHelp - %%s command(s) §7[%%s/%%s] §6--------------\n%%s");
        translaterBuilder.setWith(new RawTextBuilder()
                .add(new TextBuilder()
                        .setText(Integer.toString(totalCommands)))
                .add(new TextBuilder()
                        .setText(Integer.toString(actualPage)))
                .add(new TextBuilder()
                        .setText(Integer.toString(totalPage)))
                .add(new TextBuilder()
                        .setText(builder.substring(0, builder.length() - 1)))
        );
        sender.sendMessage(new RawTextBuilder().add(translaterBuilder));
    }

}
