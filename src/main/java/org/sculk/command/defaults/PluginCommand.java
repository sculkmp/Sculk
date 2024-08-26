package org.sculk.command.defaults;


import org.sculk.Server;
import org.sculk.command.Command;
import org.sculk.command.CommandSender;
import org.sculk.permission.DefaultPermissionNames;
import org.sculk.player.text.RawTextBuilder;
import org.sculk.player.text.TextBuilder;
import org.sculk.player.text.TranslaterBuilder;
import org.sculk.plugin.Plugin;

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
public class PluginCommand extends Command {

    public PluginCommand() {
        super("plugins", "See the list of plugins on the server", "/plugins", List.of("pl"));
    }

    @Override
    protected void prepare() {
        setPermission(DefaultPermissionNames.COMMAND_PLUGINS);
    }

    @Override
    public void onRun(CommandSender sender, String commandLabel, Map<String, Object> args) {
        Map<String, Plugin> pluginMap = Server.getInstance().getPluginManager().getPluginMap();
        StringBuilder list = new StringBuilder();
        for(Plugin plugin : pluginMap.values()) {
            if(!list.isEmpty()) {
                list.append("§f, ");
            }
            list.append(plugin.isEnabled() ? "§a" : "§c");
            list.append(plugin.getDescription().getName());
        }
        sender.sendMessage(new RawTextBuilder().add(new TranslaterBuilder().setTranslate("Plugins (%%s): %%s").addWith(String.valueOf(new RawTextBuilder()
                .add(new TextBuilder().setText(String.valueOf(pluginMap.size())))
                .add(new TextBuilder().setText(String.valueOf(list))))
        )));
    }

}
