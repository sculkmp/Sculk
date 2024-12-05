package org.sculk.command;


import lombok.Getter;
import org.sculk.command.args.BaseArgument;
import org.sculk.command.args.TextArgument;
import org.sculk.command.error.ErrorParsingArgument;

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
public abstract class Command {

    @Getter
    private String name;
    private String nextLabel;
    private String label;
    @Getter
    private String description;
    @Getter
    private String usageMessage;

    @Getter
    private List<String> aliases = new ArrayList<>();
    private final List<String> activeAliases = new ArrayList<>();
    @Getter
    private List<String> permissions = new ArrayList<>();
    @Getter
    private String permissionMessage = null;
    @Getter
    private final Map<Integer, List<BaseArgument>> argumentList = new HashMap<>();
    private final Map<Integer, Boolean> requiredArgumentCount = new HashMap<>();
    @Getter
    private final Map<String, BaseSubCommand> subCommands = new HashMap<>();

    private CommandMap commandMap = null;

    public Command(String name, String description, String usageMessage, List<String> aliases) {
        this.name = name;
        this.setLabel(name);
        this.setDescription(description);
        this.usageMessage = usageMessage != null ? usageMessage : "/" + name;
        this.setAliases(aliases);
    }

    abstract protected void prepare();

    public void execute(CommandSender sender, String commandLabel, List<String> args){

        Command cmd = this;
        Map<String, Object> passArgs = null;
        if (!args.isEmpty()) {
            String label = args.getFirst();
            if (subCommands.containsKey(label)) {
                args.removeFirst();
                subCommands.get(label).execute(sender, label, args);
                return;
            }
            passArgs = attemptArgumentParsing(cmd, sender, args);
        } else if (hasRequiredArguments()) {
            sendError(sender, ErrorParsingArgument.Type.ERR_INSUFFICIENT_ARGUMENTS);
            return;
        } else {
            passArgs = new HashMap<>();
        }
        if (passArgs != null)
            cmd.onRun(sender, commandLabel, passArgs);
    }
    abstract public void onRun(CommandSender sender, String commandLabel, Map<String, Object> args);

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
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

    public void register(CommandMap commandMap) {
        if (!isRegistered())
            this.commandMap = commandMap;
    }

    public void unregister(CommandMap commandMap) {
        if (this.commandMap == commandMap)
            this.commandMap = null;
    }

    private boolean allowChangeFrom(CommandMap commandMap) {
        return this.commandMap == null || this.commandMap == commandMap;
    }

    public boolean isRegistered() {
        return this.commandMap != null;
    }

    public void registerArgument(int position, BaseArgument argument) {
        if (position < 0){
            throw new RuntimeException("You cannot register arguments at negative positions");
        }
        if(position > 0 && this.argumentList.get(position - 1) == null) {
            throw new RuntimeException("There were no arguments before $position");
        }
        if (position > 0) {
            for (BaseArgument arg : this.argumentList.get(position - 1)) {
                if (arg instanceof TextArgument) {
                    throw new RuntimeException("No other arguments can be registered after a TextArgument");
                }
                if (arg.isOptional() && !argument.isOptional()) {
                    throw new RuntimeException("You cannot register a required argument after an optional argument");
                }
            }
        }
        this.argumentList.putIfAbsent(position, new ArrayList<>());
        this.argumentList.get(position).add(argument);
        if (!argument.isOptional()) {
            requiredArgumentCount.put(position, true);
        }
    }

    public void registerSubCommand(BaseSubCommand subCommand) {
        List<String> keys = new ArrayList<>(subCommand.getAliases());
        keys.add(subCommand.getName());
        Set<String> uniqueKeys = new LinkedHashSet<>(keys);
        for (String key : uniqueKeys) {
            if (!subCommands.containsKey(key)) {
                if (subCommand.getParent() == null) {
                    subCommand.prepare();
                    subCommand.setParent(this);
                }
                subCommands.put(key, subCommand);
            } else {
                throw new IllegalArgumentException("SubCommand with the same name/alias for '" + key + "' already exists");
            }
        }
    }

    public Map<String, Object> parseArguments(String[] rawArgs, CommandSender sender) {
        HashMap<String, Object> arguments = new HashMap<>();
        List<ErrorParsingArgument> errors = new ArrayList<>();
        int required = this.requiredArgumentCount.size();
        if (!this.hasArguments() && rawArgs.length > 0) {
            errors.add(new ErrorParsingArgument(ErrorParsingArgument.Type.ERR_NO_ARGUMENTS));
        }
        int offset = 0;
        String arg = "";
        if (rawArgs.length > 0) {
            for (int pos : argumentList.keySet()) {
                List<BaseArgument> possibleArguments = new ArrayList<>(argumentList.get(pos));
                possibleArguments.sort((BaseArgument a, BaseArgument b) -> a.getSpanLength() == Integer.MAX_VALUE ? 1 : -1);
                boolean parsed = false;
                boolean optional = true;

                for (BaseArgument argument : possibleArguments) {
                    int len = argument.getSpanLength();
                    String[] slice = Arrays.copyOfRange(rawArgs, offset, Math.min(len, rawArgs.length));
                    arg = String.join(" ", slice).trim();
                    if (!argument.isOptional())
                        optional = false;
                    if (!arg.isEmpty() && argument.canParse(arg, sender)) {
                        String nameArg = argument.getName();
                        Object parsedArg = argument.parse(arg, sender);
                        if (arguments.containsKey(nameArg) && arguments.get(nameArg).getClass().isArray()) {
                            arguments.computeIfPresent(nameArg, (k, old) -> List.of(old, parsedArg));
                        } else {
                            arguments.put(nameArg, parsedArg);
                        }
                        if (!optional)
                            --required;
                        offset += slice.length;
                        parsed = true;
                        break;
                    }
                    if (offset > rawArgs.length)
                        break;
                }
                if (!parsed &&! (optional && arg.isEmpty())) {
                    StringBuilder builder = new StringBuilder();
                    argumentList.get(offset).forEach(argument -> {
                        builder.append(argument.getTypeName()).append("|");
                    });
                    String expected = builder.toString();
                    ErrorParsingArgument error = new ErrorParsingArgument(ErrorParsingArgument.Type.ERR_INVALID_ARG_VALUE);
                    error.setValue(rawArgs[offset]);
                    error.setPosition(pos + 1);
                    error.setExpected(expected.substring(0, expected.length() - 1));
                    errors.add(error);
                }
            }
            if(offset < rawArgs.length)
                errors.add(new ErrorParsingArgument(ErrorParsingArgument.Type.ERR_TOO_MANY_ARGUMENTS));
            if(required > 0)
                errors.add(new ErrorParsingArgument(ErrorParsingArgument.Type.ERR_INSUFFICIENT_ARGUMENTS));
            if(errors.size() == 2 && errors.get(0).getType() == ErrorParsingArgument.Type.ERR_NO_ARGUMENTS &&
                    errors.get(1).getType()== ErrorParsingArgument.Type.ERR_TOO_MANY_ARGUMENTS){
                errors.clear();
                errors.add(new ErrorParsingArgument(ErrorParsingArgument.Type.ERR_INVALID_ARGUMENTS));
            }

        }
        HashMap<String, Object> result = new HashMap<>();
        result.put("errors", errors);
        result.put("arguments", arguments);
        return result;
    }

    private Map<String, Object> attemptArgumentParsing(Command ctx, CommandSender sender, List<String> args) {
        Map<String, Object> dat = ctx.parseArguments(args.toArray(String[]::new), sender);
        List<ErrorParsingArgument> errors = (List<ErrorParsingArgument>) dat.get("errors");

        if (!errors.isEmpty()) {
            for (ErrorParsingArgument error : errors) {
                if (error.getValue() != null)
                    this.sendError(sender, error.getType(), error.getValue(), error.getPosition(), error.getExpected());
                else
                    this.sendError(sender, error.getType());
            }
            return null;
        }

        return (Map<String, Object>) dat.get("arguments");
    }

    private void sendError(CommandSender sender, ErrorParsingArgument.Type type, String value, int position, String expected) {
        String str = ErrorParsingArgument.getErrorMessages().getOrDefault(type, "Unknown error code: " + type);
        if (str.contains("{value}"))
            str = str.replace("{value}", value);
        if (str.contains("{position}"))
            str = str.replace("{position}", String.valueOf(position));
        if (str.contains("{expected}"))
            str = str.replace("{expected}", expected);
        sender.sendMessage(str);
        sender.sendMessage(this.generateUsageMessage());
    }
    private void sendError(CommandSender sender, ErrorParsingArgument.Type type) {
        String str = ErrorParsingArgument.getErrorMessages().get(type);
        sender.sendMessage(str);
        sender.sendMessage(this.generateUsageMessage());
    }


    public String generateUsageMessage() {
        StringBuilder msg = new StringBuilder(this.getName() + " ");
        List<String> args = new ArrayList<>();
        for (List<BaseArgument> arguments : argumentList.values()) {
            boolean hasOptional = false;
            List<String> names = new ArrayList<>();
            for (BaseArgument argument : arguments) {
                names.add(argument.getName() + ":" + argument.getTypeName());
                if (argument.isOptional()) {
                    hasOptional = true;
                }
            }
            String namesString = String.join("|", names);
            if (hasOptional) {
                args.add("[" + namesString + "]");
            } else {
                args.add("<" + namesString + ">");
            }
        }
        msg.append(String.join(" ", args));
        return msg.toString();
    }

    public boolean hasArguments()  {
        return !this.argumentList.isEmpty();
    }
    public boolean hasRequiredArguments()  {
        return !this.requiredArgumentCount.isEmpty();
    }

}
