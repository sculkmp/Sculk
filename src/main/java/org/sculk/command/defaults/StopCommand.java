package org.sculk.command.defaults;


import org.cloudburstmc.protocol.bedrock.data.DisconnectFailReason;
import org.sculk.command.Command;
import org.sculk.command.CommandSender;
import org.sculk.exception.CommandException;
import org.sculk.permission.DefaultPermissionNames;
import org.sculk.player.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
public class StopCommand extends Command {

    public StopCommand() {
        super("stop", "Stops the server", "/stop", List.of());
        this.setPermission(DefaultPermissionNames.COMMAND_STOP);
    }

    @Override
    public void execute(CommandSender sender, String commandLabel, List<String> args) throws CommandException {
        sender.sendMessage("Stopping th server");
        sender.getServer().shutdown();
    }
}
