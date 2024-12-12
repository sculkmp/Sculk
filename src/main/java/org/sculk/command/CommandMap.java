package org.sculk.command;


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
public interface CommandMap {

    /**
     * Registers all provided commands with a specified fallback prefix.
     * This method associates a list of commands with the fallback prefix
     * to ensure proper handling during execution.
     *
     * @param fallbackPrefix the fallback prefix used for registration in case
     *                       a command conflicts or cannot be resolved.
     * @param commands       the list of commands that need to be registered.
     */
    void registerAll(String fallbackPrefix, List<Command> commands);
    /**
     * Registers a command to the command system with an optional fallback prefix.
     *
     * @param fallbackPrefix A prefix used as a fallback for the command if no other prefix is provided.
     * @param command The command object to be registered.
     * @param label The primary name or identifier for the command.
     */
    void register(String fallbackPrefix, Command command, String label);
    /**
     * Executes a command based on the provided sender and command line input.
     *
     * @param sender   The source or originator of the command, typically a player or console.
     * @param cmdLine  The command string to be executed, including arguments.
     * @return true if the command execution was successful, false otherwise.
     */
    boolean dispatch(CommandSender sender, String cmdLine);
    /**
     * Removes all commands currently registered in the command map.
     * This operation clears the internal storage used for command management
     * and ensures that no commands remain available for dispatch.
     */
    void clearCommands();
    /**
     * Retrieves a command object based on the provided command name.
     *
     * @param name the name of the command to retrieve
     * @return the command object corresponding to the given name
     */
    Command getCommand(String name);

}
