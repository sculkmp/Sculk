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

    /**
     * Prepares the command for registration and execution.
     * This method is intended to perform any setup or initialization
     * required before the command is registered or used.
     * It must be implemented by subclasses to define specific preparation logic.
     */
    abstract protected void prepare();

    /**
     * Executes the command with the provided sender, command label, and arguments.
     *
     * @param sender       The entity executing the command, typically the user or system.
     * @param commandLabel The primary label of the command being executed.
     * @param args         A list of arguments passed to the command. May include subcommands or parameters.
     */
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
    /**
     * Executes the main logic of a command. This method is triggered when the command is invoked.
     *
     * @param sender       The entity that executed the command, such as a player or the console.
     * @param commandLabel The primary label of the command that was used by the sender.
     * @param args         A map containing parsed arguments and their corresponding values for the command.
     */
    abstract public void onRun(CommandSender sender, String commandLabel, Map<String, Object> args);

    /**
     * Sets the permissions required to execute this command.
     *
     * @param permissions the list of permissions to be assigned to the command
     */
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    /**
     * Sets the permissions for this command by parsing the given permission string.
     * If the input string is null, it assigns an empty list as the permissions.
     * Otherwise, splits the string by semicolon (;) to generate a list of permissions.
     *
     * @param permission The permission string to be set. Each permission should be separated by a semicolon (;).
     *                   If null, the permissions list is cleared.
     */
    public void setPermission(String permission) {
        setPermissions(permission == null ? new ArrayList<>() : List.of(permission.split(";")));
    }

    /**
     * Sets the description for the command.
     *
     * @param description the new description of the command
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the usage message for the command.
     *
     * @param usage the usage message to set for the command
     */
    public void setUsage(String usage) {
        this.usageMessage = usage;
    }

    /**
     * Sets the list of aliases for the command. If the provided list of aliases is null,
     * it initializes the field with an empty list to avoid null references.
     *
     * @param aliases a list of aliases associated with the command, or null if no aliases are provided
     */
    public void setAliases(List<String> aliases) {
        this.aliases = aliases != null ? aliases : new ArrayList<>();
    }

    /**
     * Retrieves the label associated with this command.
     *
     * @return the label of this command
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Sets the label for the command. If the command is not already registered,
     * this method updates the label and returns true. Otherwise, it maintains the
     * current label and returns false.
     *
     * @param name The new label to be set for the command.
     * @return true if the label is successfully changed, or false if the command
     *         is already registered and the label cannot be modified.
     */
    public boolean setLabel(String name) {
        this.nextLabel = name;
        if (!isRegistered()) {
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

    /**
     * Parses the given raw arguments against the defined command arguments,
     * performing syntax checking and validation. Extracted arguments and
     * detected errors are returned in a structured form.
     *
     * @param rawArgs the array of raw string arguments passed to the command
     * @param sender  the entity executing the command, such as a player or console
     * @return a map containing two keys: "arguments", a map of successfully parsed
     *         arguments; and "errors", a list of parsing errors, if any
     */
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

    /**
     * Attempts to parse and validate the provided command arguments.
     * If there are any errors during the parsing process, relevant error messages
     * are sent to the {@code sender}. If errors are present, the method returns {@code null}.
     * On successful parsing, the method returns a map containing the parsed arguments.
     *
     * @param ctx    The {@link Command} instance used for argument parsing logic.
     * @param sender The {@link CommandSender} who executed the command, used for sending error messages.
     * @param args   A list of strings representing the arguments provided with the command execution.
     * @return A map containing parsed arguments if parsing succeeds, or {@code null} if parsing fails due to errors.
     */
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

    /**
     * Sends an error message to the specified sender based on the provided error type
     * and its associated details. The error message is constructed dynamically by
     * replacing placeholders in the predefined error messages with the given values.
     *
     * @param sender   The recipient of the error message, typically a user or system sending the command.
     * @param type     The type of error encountered, used to determine the error message template.
     * @param value    The specific value related to the error (e.g., invalid input) that will be included in the message.
     * @param position The position of the argument where the error occurred, used in the error message.
     * @param expected The description of the expected value or format, included in the error message.
     */
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
    /**
     * Sends an error message to the specified command sender based on the error type.
     * Retrieves a predefined error message corresponding to the provided type and sends it
     * along with the generated usage message.
     *
     * @param sender The entity to which the error message will be sent, such as a player or console.
     * @param type   The type of error to be sent, represented by {@link ErrorParsingArgument.Type}.
     */
    private void sendError(CommandSender sender, ErrorParsingArgument.Type type) {
        String str = ErrorParsingArgument.getErrorMessages().get(type);
        sender.sendMessage(str);
        sender.sendMessage(this.generateUsageMessage());
    }


    /**
     * Generates a usage message for the command based on the registered arguments.
     * The message includes the command name followed by the required and optional arguments
     * formatted to illustrate their usage.
     *
     * Required arguments are enclosed in angle brackets ("<" and ">") and optional arguments
     * are enclosed in square brackets ("[" and "]"). For arguments with multiple options, their
     * names are separated by a pipe ("|").
     *
     * @return A formatted string representing the usage of the command, including its arguments.
     */
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

    /**
     * Checks whether this command has any arguments registered.
     *
     * @return true if there are arguments present in the argument list; false otherwise.
     */
    public boolean hasArguments()  {
        return !this.argumentList.isEmpty();
    }
    /**
     * Determines whether the command has any required arguments.
     *
     * @return true if the command has required arguments; false otherwise
     */
    public boolean hasRequiredArguments()  {
        return !this.requiredArgumentCount.isEmpty();
    }

}
