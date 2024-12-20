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

    void sendMessage(String message);
    void sendMessage(RawTextBuilder textBuilder);
    void sendMessage(Translatable<?> translatable);
    Server getServer();
    String getName();
    Locale getLocale();
    Language getLanguage();
}
