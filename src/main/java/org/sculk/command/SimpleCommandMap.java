package org.sculk.command;


import org.sculk.Server;

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
public class SimpleCommandMap implements CommandMap {

    protected Map<String, Command> knownCommands = new HashMap<>();
    private final Server server;

    public SimpleCommandMap(Server server) {
        this.server = server;
        this.setDefaultCommands();
    }

    private void setDefaultCommands() {
        registerAll("sculk", List.of(

        ));
    }

    public void registerAll(String fallbackPrefix, List<Command> commands) {
        for(Command command : commands) {
            register(fallbackPrefix, command, null);
        }
    }

    public void register(String fallbackPrefix, Command command, String label) {
        if(command.getPermissions().isEmpty()) {
            throw new IllegalArgumentException("Commands must have a permission set");
        }
        if(label == null) {
            label = command.getLabel();
        }
        label = label.trim();
        fallbackPrefix = fallbackPrefix.toLowerCase().trim();

        boolean registered = this.registerAlias(command, false, fallbackPrefix, label);

        List<String> aliases = command.getAliases();
        for(int index = 0; index < aliases.size(); index++) {
            String alias = aliases.get(index);
            if (!registerAlias(command, true, fallbackPrefix, alias)) {
                aliases.remove(index);
            }
        }
        command.setAliases(aliases);

        if(!registered) {
            command.setAliases(Collections.singletonList(fallbackPrefix + ":" + label));
        }
        command.register(this);
    }

    public boolean unregister(Command command) {
        for(String label : knownCommands.keySet()) {
            if(knownCommands.get(label) == command) {
                knownCommands.remove(label);
            }
        }
        command.unregister(this);
        return true;
    }

    private boolean registerAlias(Command command, boolean b, String fallbackPrefix, String label) {
        return true;
    }

    public boolean dispatch(CommandSender sender, String commandLine) {
        return false;
    }

    public void clearCommands() {

    }

    public Command getCommand(String name) {
        return knownCommands.get(name);
    }

    public List<Command> getCommands() {
        return new ArrayList<>(knownCommands.values());
    }

    public void registerServerAliases() {

    }

}
