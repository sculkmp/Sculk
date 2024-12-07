package org.sculk.command.network;

import lombok.NonNull;
import lombok.ToString;
import org.cloudburstmc.protocol.bedrock.data.command.*;
import org.sculk.command.BaseSubCommand;
import org.sculk.command.Command;
import org.sculk.command.args.BaseArgument;
import org.sculk.player.Player;


import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Function;

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
public class CreatorCommandData {
    private final String name;
    private String desc;
    private List<String> perms;
    private List<String> aliases;
    private List<BaseArgument[]> overloads;
    private Map<String, BaseSubCommand> subCommands;
    private Player player;

    public CreatorCommandData(Player player, @NonNull Command command) {
        this.name = command.getName();
        this.player = player;
        this.setDescription(command.getDescription());
        this.setAliases(command.getAliases());
        this.setPermissions(command.getPermissions());
        this.setSubCommands(command.getSubCommands());
        ArrayList<BaseArgument[]> baseArguments = new ArrayList<>();
        for (List<BaseArgument> arguments: command.getArgumentList().values())
            baseArguments.add(arguments.toArray(BaseArgument[]::new));
        this.setParameters(baseArguments);
    }

    public CommandData toNetwork() {
        return new CommandData(this.name, this.player.getLanguage().translate(this.desc), Collections.emptySet(), CommandPermission.ANY, new CreatorCommandEnum(name, aliases).toNetwork(), Collections.emptyList(), this.generateOverloads().toArray(CommandOverloadData[]::new));
    }

    public ArrayList<CommandOverloadData> generateOverloadsSubCommand() {
        ArrayList<CommandOverloadData> overloads = new ArrayList<>();
        for (Map.Entry<String, BaseSubCommand> subCommand: subCommands.entrySet()) {
            ArrayList<CommandParamData> subCommandParamData = new ArrayList<>();
            String label = subCommand.getKey();
            BaseSubCommand command = subCommand.getValue();
            if (!Objects.equals(command.getName(), label))
                continue;
            CommandParamData commandParamData = new CommandParamData();
            commandParamData.setName(label);
            commandParamData.setOptional(false);
            commandParamData.setEnumData(new CreatorCommandEnum(label, List.of(label)).toNetwork());
            subCommandParamData.add(commandParamData);
            ArrayList<CommandOverloadData> subCommandOverloads = new CreatorCommandData(this.player, command).generateOverloads();
            if (subCommandOverloads.isEmpty()){
                overloads.add(new CommandOverloadData(false, subCommandParamData.toArray(CommandParamData[]::new)));
            }else {
                subCommandOverloads.forEach(commandOverloadData -> {
                    subCommandParamData.addAll(Arrays.asList(commandOverloadData.getOverloads()));
                    overloads.add(new CommandOverloadData(false, subCommandParamData.toArray(CommandParamData[]::new)));
                });
            }
        }
        return overloads;
    }
    public ArrayList<CommandOverloadData> generateOverloads() {
        String description = this.desc;
        ArrayList<CommandOverloadData> overloadData = new ArrayList<>(generateOverloadsSubCommand());
        for (BaseArgument[] arguments: overloads) {
            overloadData.add(new CommandOverloadData(false, Arrays.stream(arguments).map(BaseArgument::getNetworkParameterData).toArray(CommandParamData[]::new)));
        }
        return overloadData;
    }

    private void setDescription(@NonNull String description) {
        this.desc = description;
    }

    private void setPermissions(@NonNull List<String> permissions) {
        this.perms = new ArrayList<>(permissions);
    }

    private void addPermissions(@NonNull List<String> permissions) {
        this.perms.addAll(permissions);
    }
    private void setAliases(@NonNull List<String> aliases) {
        this.aliases = new ArrayList<>(aliases);
    }

    private void setParameters(@NonNull List<BaseArgument[]> parameters) {
        this.overloads = parameters;
    }
    private void setParameters(@NonNull BaseArgument[]... paramSet) {
        this.overloads = Arrays.asList(paramSet);
    }
    private void setSubCommands(@NonNull Map<String, BaseSubCommand> parameters) {
        this.subCommands = parameters;
    }
}
