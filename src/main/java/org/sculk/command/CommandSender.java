package org.sculk.command;


import org.sculk.Server;
import org.sculk.lang.Language;
import org.sculk.lang.Translatable;
import org.sculk.permission.Permissible;
import org.sculk.player.text.RawTextBuilder;

import java.util.Locale;

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
public interface CommandSender extends Permissible {

    /**
     * Sends a message to the command sender.
     *
     * @param message the message to be sent
     */
    void sendMessage(String message);
    /**
     * Sends a message to the receiver using the specified {@link RawTextBuilder}.
     *
     * @param textBuilder the RawTextBuilder object containing the message to be sent
     */
    void sendMessage(RawTextBuilder textBuilder);
    /**
     * Sends a translatable message to the intended recipient.
     *
     * @param translatable the translatable message object containing the text and optional parameters to be sent
     */
    void sendMessage(Translatable<?> translatable);
    /**
     * Retrieves the server instance associated with this command sender.
     *
     * @return the {@code Server} instance linked to this command sender.
     */
    Server getServer();
    /**
     * Retrieves the name associated with the command sender.
     *
     * @return the name of the command sender as a {@code String}.
     */
    String getName();
    /**
     * Retrieves the locale associated with the command sender.
     *
     * @return the Locale of the command sender.
     */
    Locale getLocale();
    /**
     * Retrieves the language associated with the current object or entity.
     *
     * @return the {@code Language} object containing language-specific information such as name, locale, and translations.
     */
    Language getLanguage();
}
