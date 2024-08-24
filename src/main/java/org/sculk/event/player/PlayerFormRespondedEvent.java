package org.sculk.event.player;

import lombok.Getter;
import org.sculk.player.Player;
import org.sculk.form.Form;
import org.sculk.form.response.Response;

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

@Getter
public class PlayerFormRespondedEvent extends PlayerEvent {
    protected final int formId;
    protected final Form form;
    protected final Response response;

    public PlayerFormRespondedEvent(Player player, int formId, Form form, Response response) {
        super(player);

        this.formId = formId;
        this.form = form;
        this.response = response;
    }
}