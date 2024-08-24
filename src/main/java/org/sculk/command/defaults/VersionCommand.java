package org.sculk.command.defaults;


import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;
import org.sculk.Sculk;
import org.sculk.command.Command;
import org.sculk.command.CommandSender;
import org.sculk.command.data.CommandParameter;
import org.sculk.exception.CommandException;
import org.sculk.network.protocol.ProtocolInfo;
import org.sculk.permission.DefaultPermissionNames;
import org.sculk.player.text.RawTextBuilder;
import org.sculk.player.text.TextBuilder;
import org.sculk.player.text.TranslaterBuilder;

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
public class VersionCommand extends Command {

    public VersionCommand() {
        super("version", "Gets the version of this server including any plugins in use", "/version [plugin name]", List.of("ver", "about"));
        this.setPermission(DefaultPermissionNames.COMMAND_VERSION);
    }

    @Override
    public void execute(CommandSender sender, String commandLabel, List<String> args) throws CommandException {
        sender.sendMessage(new RawTextBuilder().add(
                new TranslaterBuilder()
                        .setTranslate("§fThis server is running §a%%s\n§fServer version: §a%%s\n§fCompatible Minecraft version: §a%%s §f(protocol version: §a%%s§f)\nOperating system: §a%%s")
                        .setWith(new RawTextBuilder()
                                .add(new TextBuilder().setText(Sculk.CODE_NAME)) // software name
                                .add(new TextBuilder().setText(Sculk.CODE_VERSION)) // software version
                                .add(new TextBuilder().setText(ProtocolInfo.MINECRAFT_VERSION)) // Minecraft version
                                .add(new TextBuilder().setText(String.valueOf(ProtocolInfo.CURRENT_PROTOCOL))) // software protocol
                                .add(new TextBuilder().setText(System.getProperty("os.name").toLowerCase())) // system
                        ))
        );
    }

}
