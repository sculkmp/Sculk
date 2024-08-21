package org.sculk.form;

import org.cloudburstmc.protocol.bedrock.packet.ModalFormResponsePacket;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.sculk.Player;
import org.sculk.Server;
import org.sculk.event.player.PlayerFormRespondedEvent;
import org.sculk.form.response.Response;
import org.sculk.utils.json.Serializable;

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
public abstract class Form implements Serializable {
    public abstract Response processResponse(Player player, ModalFormResponsePacket packet);

    /**
     *
     * Method used within a {@link org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler} to handle a client's response
     * to a form sent by the server.
     *
     * @param player The player which sent the packet
     * @param packet The packet
     * @return {@link PacketSignal} to HANDLED to ensure a one-line implementation
     */
    public static PacketSignal handleIncomingPacket(Player player, ModalFormResponsePacket packet) {
        int formId = packet.getFormId();
        Form form = player.getForm(formId);

        if (form == null) {
            return PacketSignal.HANDLED;
        }

        Response response = form.processResponse(player, packet);

        PlayerFormRespondedEvent event = new PlayerFormRespondedEvent(player, formId, form, response);
        Server.getInstance().getEventManager().fire(event);

        return PacketSignal.HANDLED;
    }
}
