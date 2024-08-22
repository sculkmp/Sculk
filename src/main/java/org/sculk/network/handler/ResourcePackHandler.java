package org.sculk.network.handler;

import org.cloudburstmc.protocol.bedrock.packet.ResourcePackClientResponsePacket;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackStackPacket;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePacksInfoPacket;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.sculk.Player;
import org.sculk.network.session.SculkServerSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

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
public class ResourcePackHandler extends SculkPacketHandler {

    private final Consumer<Object> completeClosure;
    public ResourcePackHandler(SculkServerSession session, Consumer<Object> completeClosure) {
        super(session);
        this.completeClosure = completeClosure;
    }

    @Override
    public void setUp() {
        ResourcePacksInfoPacket resourcePacksInfoPacket = new ResourcePacksInfoPacket();
        session.sendPacket(resourcePacksInfoPacket);
    }




    public PacketSignal handle(ResourcePackClientResponsePacket packet) {
       switch (packet.getStatus()) {
           case REFUSED -> {
               session.disconnect("You must accept resource packs to join this server", false);
               break;
           }
           case SEND_PACKS -> {
               break;
           }
           case HAVE_ALL_PACKS -> {
               ArrayList<ResourcePackStackPacket.Entry> entries = new ArrayList<>();

               ResourcePackStackPacket stackPacket = new ResourcePackStackPacket();
               //todo: getForceRessource Pack in server.properties
               stackPacket.setForcedToAccept(false);
               stackPacket.setExperimentsPreviouslyToggled(false);
               stackPacket.setGameVersion("*");
               //todo: create ResourcePackManager return packs
               session.sendPacket(stackPacket);
               break;
           }
           case COMPLETED -> {
                this.completeClosure.accept(null);
               break;
           }
       }
        return PacketSignal.HANDLED;
    }

}
