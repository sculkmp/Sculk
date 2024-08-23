package org.sculk.event.player;


import org.sculk.player.Player;
import org.sculk.event.Cancellable;
import org.sculk.player.chat.ChatFormatter;

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
public class PlayerChatEvent extends PlayerEvent implements Cancellable {

    protected String message;
    protected ChatFormatter chatFormatter;

    public PlayerChatEvent(Player player, String message, ChatFormatter chatFormatter) {
        super(player);
        this.chatFormatter = chatFormatter;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ChatFormatter getChatFormatter() {
        return chatFormatter;
    }

    public void setChatFormatter(ChatFormatter chatFormatter) {
        this.chatFormatter = chatFormatter;
    }
}
