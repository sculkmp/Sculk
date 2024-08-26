package org.sculk.command.defaults;


import org.sculk.command.Command;
import org.sculk.command.CommandSender;
import org.sculk.command.args.TextArgument;
import org.sculk.console.TerminalConsole;
import org.sculk.permission.DefaultPermissionNames;
import org.sculk.player.Player;

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
public class SayCommand extends Command {

    public SayCommand() {
        super("say", "Broadcasts the given message as the sender", "/say [text]", List.of());
    }

    @Override
    protected void prepare() {
        this.setPermission(DefaultPermissionNames.COMMAND_SAY);
        this.registerArgument(0, new TextArgument("text", false));
    }

    @Override
    public void onRun(CommandSender sender, String commandLabel, Map<String, Object> args) {
        String text = args.get("text").toString();
        String name = "Console";
        if(sender instanceof Player) {
            name = sender.getName();
        }
        sender.getServer().broadcastMessage("§d[" + sender.getName() + "] " + text);
    }

}
