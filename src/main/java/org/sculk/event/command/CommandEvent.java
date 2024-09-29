package org.sculk.event.command;


import lombok.Getter;
import lombok.Setter;
import org.sculk.command.CommandSender;
import org.sculk.event.Cancellable;
import org.sculk.event.ServerEvent;

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
public class CommandEvent extends ServerEvent implements Cancellable {

    @Getter
    @Setter
    protected CommandSender sender;
    @Getter
    @Setter
    protected String command;
    
    public CommandEvent(CommandSender sender, String command) {
        this.sender = sender;
        this.command = command;
    }

}
