package org.sculk.command;


import lombok.Getter;
import lombok.NonNull;
import org.sculk.command.data.CommandData;
import org.sculk.command.data.CommandParameter;
import org.sculk.exception.CommandException;

import java.util.ArrayList;
import java.util.Arrays;
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
    @Getter
    private String description;
    private String usageMessage;

    @Getter
    private List<String> aliases = new ArrayList<>();
    private List<String> activeAliases = new ArrayList<>();

    private List<String> permissions = new ArrayList<>();
    private String permissionMessage = null;

    private CommandMap commandMap = null;
    @Getter
    private CommandData commandData;
    @Getter
    private List<CommandParameter[]> parameters = new ArrayList<>();

    public Command(String name, String description, String usageMessage, List<String> aliases) {
        this.name = name;
        this.setLabel(name);
        this.setDescription(description);
        this.usageMessage = usageMessage != null ? usageMessage : "/" + name;
        this.setAliases(aliases);
    }

    public void registerParameter(@NonNull CommandParameter paramSet) {
        List<CommandParameter[]> parameters1 = new ArrayList<>();
        parameters1.add(new CommandParameter[]{paramSet});
        this.parameters.addAll(parameters1);
    }

    public abstract void execute(CommandSender sender, String commandLabel, List<String> args) throws CommandException;

    public String getName() {
        return name;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions.addAll(permissions);
    }

    public void setPermission(String permission) {
        setPermissions(permission == null ? new ArrayList<>() : List.of(permission.split(";")));
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUsage(String usage) {
        this.usageMessage = usage;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases != null ? aliases : new ArrayList<>();
    }

    public String getLabel() {
        return this.label;
    }

    public boolean setLabel(String name) {
        this.nextLabel = name;
        if(!isRegistered()) {
            this.label = name;
            return true;
        }
        return false;
    }

    public boolean register(CommandMap commandMap) {
        return false;
    }

    public boolean unregister(CommandMap commandMap) {
        return false;
    }

    private boolean allowChangeFrom(CommandMap commandMap) {
        return this.commandMap == null || this.commandMap == commandMap;
    }

    public boolean isRegistered() {
        return this.commandMap != null;
    }


    public final void buildCommand() {
        this.commandData = CommandData.builder(name)
                .setDescription(description)
                .setUsageMessage(usageMessage)
                .setAliases(aliases)
                .setPermissionMessage(this.permissionMessage == null ? "" : this.permissionMessage)
                .setParameters(this.getParameters())
                .setPermissions(this.getPermissions())
                .build();
    }
}
