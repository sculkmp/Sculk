package org.sculk.command.defaults;

import org.sculk.Server;
import org.sculk.command.Command;
import org.sculk.command.CommandSender;
import org.sculk.command.args.CommandArgument;
import org.sculk.command.args.IntegerArgument;
import org.sculk.player.text.TextBuilder;
import org.sculk.permission.DefaultPermissionNames;
import org.sculk.player.text.RawTextBuilder;
import org.sculk.player.text.TranslaterBuilder;

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
public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "commands.help.description", "/help [page|command name]", List.of("?"));
    }

    @Override
    protected void prepare() {
        this.setPermission(DefaultPermissionNames.COMMAND_HELP);
        this.registerArgument(0, new IntegerArgument("page", true));
        this.registerArgument(0, new CommandArgument("command", true));
    }

    @Override
    public void onRun(CommandSender sender, String commandLabel, Map<String, Object> args) {
        StringBuilder builder = new StringBuilder();
        Set<String> commandSending = new HashSet<>();
        Server.getInstance().getCommandMap().getCommands().forEach((s, command) -> {
            String commandName = s.contains(":") ? s.substring(s.indexOf(':') + 1) : s;
            commandSending.add(commandName);
        });

        int totalCommands = commandSending.size();
        int commandsPerPage = 5;
        int actualPage = 1;
        int totalPage = (int) Math.ceil((double) totalCommands / commandsPerPage);

        if (args.containsKey("page")) {
            actualPage = Integer.parseInt(args.get("page").toString());
            if (actualPage < 1 || actualPage > totalPage) {
                actualPage = 1;
            }
        } else if (args.containsKey("command")) {
            String commandName = args.get("command").toString();
            Command command = Server.getInstance().getCommandMap().getCommand(commandName);
            if (command != null) {
                sender.sendMessage(new RawTextBuilder().add(new TranslaterBuilder<RawTextBuilder>()
                        .setTranslate("§6/%%s: §f%%s\nUsage: §e%%s")
                        .setWith(new RawTextBuilder()
                                .add(new TextBuilder().setText(command.getLabel()))
                                .add(new TranslaterBuilder<Void>().setTranslate(command.getDescription()))
                                .add(new TextBuilder().setText(command.getUsageMessage()))
                        )
                ));
            } else {
                sender.sendMessage(new RawTextBuilder().add(new TranslaterBuilder<RawTextBuilder>().setTranslate("§4/%%s§c does not seem to exist, check the list of commands with §4/help§c.")
                        .setWith(new RawTextBuilder().add(new TextBuilder().setText(commandName)))));
            }
            return;
        }
        RawTextBuilder commands = RawTextBuilder.create();
        int startIndex = (actualPage - 1) * commandsPerPage;
        int endIndex = Math.min(startIndex + commandsPerPage, totalCommands);
        TranslaterBuilder<RawTextBuilder> commandSentence = new TranslaterBuilder<>();
        commandSentence.setTranslate("§6/%%s:§f %%s\n");
        for (int i = startIndex; i < endIndex; i++) {
            String commandName = commandSending.toArray(new String[0])[i];
            Command command = Server.getInstance().getCommandMap().getCommand(commandName);
            if (i == endIndex - 1)
                commandSentence.setTranslate("§6/%%s:§f %%s");
            if (command != null) {
                TranslaterBuilder<RawTextBuilder> clone = commandSentence.clone();
                commands.add(clone.setWith(RawTextBuilder.create(new TextBuilder().setText(commandName), new TranslaterBuilder<>().setTranslate(command.getDescription()))));
            }
        }
        TranslaterBuilder<RawTextBuilder> translaterBuilder = new TranslaterBuilder<>();
        translaterBuilder.setTranslate("commands.help.header");
        translaterBuilder.setWith(
                RawTextBuilder.create(
                new TextBuilder().setText(Integer.toString(totalCommands)),
                new TextBuilder().setText(Integer.toString(actualPage)),
                new TextBuilder().setText(Integer.toString(totalPage)),
                commands)
        );
        sender.sendMessage(new RawTextBuilder().add(translaterBuilder));
    }

}