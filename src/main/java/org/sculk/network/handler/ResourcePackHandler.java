package org.sculk.network.handler;

import io.netty.buffer.Unpooled;
import org.cloudburstmc.protocol.bedrock.data.ResourcePackType;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.sculk.Server;
import org.sculk.config.ServerPropertiesKeys;
import org.sculk.network.session.SculkServerSession;
import org.sculk.resourcepack.ResourcePack;

import java.util.ArrayList;
import java.util.UUID;
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
        Server.getInstance().getResourcePackManager().getResourcePacks().forEach(resourcePack -> {
            resourcePacksInfoPacket.getResourcePackInfos().add(new ResourcePacksInfoPacket.Entry(
                    resourcePack.getUuid(),
                    resourcePack.getVersion(),
                    resourcePack.getSize(),
                    "",
                    "",
                    resourcePack.getUuid().toString(),
                    false,
                    false,
                    false,
                    ""
            ));
        });
        resourcePacksInfoPacket.setWorldTemplateId(UUID.randomUUID());
        resourcePacksInfoPacket.setWorldTemplateVersion("");
        resourcePacksInfoPacket.setForcedToAccept(Server.getInstance().getProperties().get(ServerPropertiesKeys.FORCE_RESOURCE_PACKS, false));
        resourcePacksInfoPacket.setHasAddonPacks(false);

        session.sendPacket(resourcePacksInfoPacket);
    }


    public PacketSignal handle(ResourcePackClientResponsePacket packet) {
       switch (packet.getStatus()) {
           case REFUSED -> {
               session.disconnect("You must accept resource packs to join this server", false);
               break;
           }
           case SEND_PACKS -> {

               for (ResourcePack resourcePack : Server.getInstance().getResourcePackManager().getResourcePacks()) {
                   long maxChunkSize = 1048576;

                   ResourcePackDataInfoPacket resourcePackDataInfoPacket = new ResourcePackDataInfoPacket();
                   resourcePackDataInfoPacket.setPackId(resourcePack.getUuid());
                   resourcePackDataInfoPacket.setMaxChunkSize(maxChunkSize);
                   resourcePackDataInfoPacket.setChunkCount(resourcePack.getSize() / maxChunkSize);
                   resourcePackDataInfoPacket.setCompressedPackSize(resourcePack.getSize());
                   resourcePackDataInfoPacket.setHash(resourcePack.getHash());
                   resourcePackDataInfoPacket.setType(ResourcePackType.RESOURCES);

                   session.sendPacket(resourcePackDataInfoPacket);
               }
               break;
           }
           case HAVE_ALL_PACKS -> {
               /**
               ArrayList<ResourcePackStackPacket.Entry> entries = new ArrayList<>();

               ResourcePackStackPacket stackPacket = new ResourcePackStackPacket();
               //todo: getForceRessource Pack in server.properties
               stackPacket.setForcedToAccept(false);
               stackPacket.setExperimentsPreviouslyToggled(false);
               stackPacket.setGameVersion("*");
                stackPacket.setHasEditorPacks(false);
               //todo: create ResourcePackManager return packs
               session.sendPacket(stackPacket);**/

               ResourcePackStackPacket resourcePackStackPacket = new ResourcePackStackPacket();
               resourcePackStackPacket.setExperimentsPreviouslyToggled(false);
               resourcePackStackPacket.setHasEditorPacks(false);
               resourcePackStackPacket.setForcedToAccept(Server.getInstance().getProperties().get(ServerPropertiesKeys.FORCE_RESOURCE_PACKS, false));

               ArrayList<ResourcePackStackPacket.Entry> entries = new ArrayList<>();

               for (ResourcePack resourcePack : Server.getInstance().getResourcePackManager().getResourcePacks()) {
                   entries.add(new ResourcePackStackPacket.Entry(
                           resourcePack.getUuid().toString(),
                           resourcePack.getVersion(),
                           ""
                   ));
               }

               resourcePackStackPacket.getResourcePacks().addAll(entries);
               resourcePackStackPacket.setGameVersion("*");

               session.sendPacket(resourcePackStackPacket);
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
