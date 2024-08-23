package org.sculk.command;


import org.sculk.exception.CommandException;

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
public abstract class Command {

    private String name;
    private String nextLabel;
    private String label;
    private String description;
    private String usageMessage;

    private List<String> aliases = new ArrayList<>();
    private List<String> activeAliases = new ArrayList<>();

    private List<String> permissions = new ArrayList<>();
    private String permissionMessage = null;

    private CommandMap commandMap = null;

    public Command(String name, String description, String usageMessage, List<String> aliases) {
        this.name = name;
        this.setLabel(name);
        this.setDescription(description);
        this.usageMessage = usageMessage != null ? usageMessage : "/" + name;
        this.setAliases(aliases);
    }

    public abstract void execute(CommandSender sender, String commandLabel, List<String> args) throws CommandException;

    public String getName() {
        return name;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        for(String perm : permissions) {
            // Checked if permission exists
        }
        this.permissions = permissions;
    }

    public void setPermission(String permission) {
        setPermissions(permission == null ? new ArrayList<>() : List.of(permission.split(";")));
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setUsage(String usage) {
        this.usageMessage = usage;
    }

    public String getUsage() {
        return usageMessage;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases != null ? aliases : new ArrayList<>();
    }

    public List<String> getAliases() {
        return aliases;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String name) {
        this.nextLabel = name;
        if(!isRegistered()) {
            this.label = name;
        }
    }

    public boolean register(CommandMap commandMap) {
        if(allowChangeFrom(commandMap)) {
            this.commandMap = commandMap;
            return true;
        }
        return false;
    }

    public boolean unregister(CommandMap commandMap) {
        if(allowChangeFrom(commandMap)) {
            this.commandMap = null;
            this.activeAliases = new ArrayList<>(aliases);
            this.label = nextLabel;
            return true;
        }
        return false;
    }

    private boolean allowChangeFrom(CommandMap commandMap) {
        return this.commandMap == null || this.commandMap == commandMap;
    }

    public boolean isRegistered() {
        return this.commandMap != null;
    }

    @Override
    public String toString() {
        return name;
    }

}
