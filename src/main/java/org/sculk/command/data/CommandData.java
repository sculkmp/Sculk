package org.sculk.command.data;

import lombok.NonNull;
import lombok.ToString;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOverloadData;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamData;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;
import org.cloudburstmc.protocol.bedrock.data.command.CommandPermission;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
@ToString
public class CommandData {
    private final String cmdName;
    private final String description;
    private final String usage;
    private final String permMsg;
    private final List<String> permissions;
    private final CommandEnum aliases;
    private final List<CommandParameter[]> overloads;
    private String registeredName;

    private CommandData(String cmdName, String description, String usage, String permissionMessage, List<String> permissions, CommandEnum aliases, List<CommandParameter[]> overloads) {
        this.cmdName = cmdName;
        this.description = description;
        this.usage = usage;
        this.permMsg = permissionMessage;
        this.permissions = permissions;
        this.aliases = aliases;
        this.overloads = overloads;
    }

    public static Builder builder(@NonNull String commandName) {
        return new Builder(commandName);
    }

    public String getRegisteredName() {
        return this.registeredName == null ? cmdName : registeredName;
    }

    public void setRegisteredName(String name) {
        this.registeredName = name;
    }

    public String getDescription() {
        return this.description;
    }

    public List<String> getPermissions() {
        return this.permissions;
    }

    public List<String> getAliases() {
        return this.aliases.getValues();
    }

    public void removeAlias(String alias) {
        this.aliases.getValues().remove(alias);
    }

    public org.cloudburstmc.protocol.bedrock.data.command.CommandData toNetwork() {
        String description = this.description;

        CommandOverloadData[] overloadData = new CommandOverloadData[this.overloads.size()];


        for (int i = 0; i < overloadData.length; i++) {
            CommandParameter[] parameters = this.overloads.get(i);
            CommandParamData[] params = new CommandParamData[parameters.length];
            for (int i2 = 0; i2 < parameters.length; i2++) {
                params[i2] = parameters[i2].toNetwork();
            }
            overloadData[i] = new CommandOverloadData(false, params);
        }

        return new org.cloudburstmc.protocol.bedrock.data.command.CommandData(this.getRegisteredName(), description, Collections.emptySet(),
                CommandPermission.ANY, this.aliases.toNetwork(), Collections.emptyList(), overloadData);
    }

    public List<CommandParameter[]> getOverloads() {
        return this.overloads;
    }

    public String getPermissionMessage() {
        return this.permMsg;
    }

    public String getUsage() {
        return this.usage;
    }

    public static class Builder {
        private final String name;
        private String desc = "";
        private String usage = "";
        private String permMsg = "";
        private List<String> perms = new ArrayList<>();
        private List<String> aliases = new ArrayList<>();
        private List<CommandParameter[]> overloads = new ArrayList<>();

        public Builder(@NonNull String name) {
            this.name = name.toLowerCase();
        }

        public CommandData build() {
            return new CommandData(name, desc, usage, permMsg, perms, new CommandEnum(name, aliases), overloads);
        }

        public Builder setDescription(@NonNull String description) {
            this.desc = description;
            return this;
        }

        public Builder setUsageMessage(@NonNull String usage) {
            this.usage = usage;
            return this;
        }

        public Builder setPermissionMessage(@NonNull String message) {
            this.permMsg = message;
            return this;
        }

        public Builder setPermissions(@NonNull String... permissions) {
            this.perms = Arrays.asList(permissions);
            return this;
        }

        public Builder setPermissions(@NonNull List<String> permissions) {
            this.perms = new ArrayList<String>(permissions);
            return this;
        }

        public Builder addPermission(@NonNull String permission) {
            this.perms.add(permission);
            return this;
        }

        public Builder addPermissions(@NonNull String... permissions) {
            this.perms.addAll(Arrays.asList(permissions));
            return this;
        }

        public Builder addPermissions(@NonNull List<String> permissions) {
            this.perms.addAll(permissions);
            return this;
        }

        public Builder setAliases(@NonNull String... aliases) {
            this.aliases = Arrays.asList(aliases);
            return this;
        }

        public Builder setAliases(@NonNull List<String> aliases) {
            this.aliases = new ArrayList<String>(aliases);
            return this;
        }

        public Builder addAlias(@NonNull String alias) {
            this.aliases.add(alias);
            return this;
        }

        public Builder addAliases(@NonNull String... aliases) {
            this.aliases.addAll(Arrays.asList(aliases));
            return this;
        }
        public Builder addAliases(@NonNull List<String> aliases) {
            this.aliases.addAll(aliases);
            return this;
        }

        public Builder setParameters(@NonNull CommandParameter[]... paramSet) {
            this.overloads = Arrays.asList(paramSet);
            return this;
        }

        public Builder setParameters(@NonNull List<CommandParameter[]> parameters) {
            this.overloads = parameters;
            return this;
        }

        public Builder addParameters(@NonNull CommandParameter[]... paramSet) {
            this.overloads.addAll(Arrays.asList(paramSet));
            return this;
        }
    }

}
