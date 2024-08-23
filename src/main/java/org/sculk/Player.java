package org.sculk;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.data.AttributeData;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityFlag;
import org.cloudburstmc.protocol.bedrock.data.skin.SerializedSkin;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.sculk.entity.Attribute;
import org.sculk.entity.AttributeFactory;
import org.sculk.entity.HumanEntity;
import org.sculk.entity.data.SyncedEntityData;
import org.sculk.event.player.PlayerChatEvent;
import org.sculk.form.Form;
import org.sculk.player.PlayerInterface;
import org.sculk.player.client.ClientChainData;
import org.sculk.player.client.LoginChainData;
import org.sculk.utils.TextFormat;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

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
public class Player extends HumanEntity implements PlayerInterface {

    private final BedrockServerSession serverSession;
    private final SyncedEntityData data = new SyncedEntityData(this);
    private LoginChainData loginChainData;

    private AtomicInteger formId;
    private Int2ObjectOpenHashMap<Form> forms;
    private List<AttributeData> attributeMap;

    private String displayName;
    private String username;

    protected int messageCounter = 2;
    protected int MAX_CHAT_CHAR_LENGTH = 512;
    protected int MAX_CHAT_BYTES_LENGTH = MAX_CHAT_CHAR_LENGTH * 2;

    public Player(BedrockServerSession session, ClientChainData data) {
        this.serverSession = session;
        this.loginChainData = data;

        this.formId = new AtomicInteger(0);
        this.forms = new Int2ObjectOpenHashMap<>();

        this.displayName = data.getUsername();
        this.username = data.getUsername();

        System.out.println(this.username);

        initEntity();
    }

    @Override
    public void initEntity() {
        super.initEntity();
        System.out.println("init Entity");

    }

    public void updateFlags() {
        this.data.setFlags(EntityFlag.BREATHING, true);
        this.data.updateFlag();
    }

    public void kick(String message) {
        DisconnectPacket packet = new DisconnectPacket();
        packet.setKickMessage(message);
        sendDataPacket(packet);
    }

    public void processLogin() {
        getServer().getLogger().info("process login call");

    }

    public void completeLogin() {
        ResourcePacksInfoPacket resourcePacksInfoPacket = new ResourcePacksInfoPacket();
        sendDataPacket(resourcePacksInfoPacket);

        ResourcePackClientResponsePacket resourcePackClientResponsePacket2 = new ResourcePackClientResponsePacket();
        resourcePackClientResponsePacket2.setStatus(ResourcePackClientResponsePacket.Status.HAVE_ALL_PACKS);
        sendDataPacket(resourcePackClientResponsePacket2);

        ResourcePackClientResponsePacket resourcePackClientResponsePacket = new ResourcePackClientResponsePacket();
        resourcePackClientResponsePacket.setStatus(ResourcePackClientResponsePacket.Status.COMPLETED);
        sendDataPacket(resourcePackClientResponsePacket);
        Server.getInstance().getLogger().info("call pack stack");

        ResourcePackStackPacket resourcePackStackPacket = new ResourcePackStackPacket();
        resourcePackStackPacket.setForcedToAccept(false);
        resourcePackStackPacket.setGameVersion("*");
        sendDataPacket(resourcePackStackPacket);
        Server.getInstance().getLogger().info("resourcePackStackPacket");


        //sendDataPacket(startGamePacket);
        Server.getInstance().getLogger().info("call startgame");

        this.getServer().addOnlinePlayer(this);
        getServer().onPlayerCompleteLogin(this);
    }

    public long getUniqueId() {
        return UUID.randomUUID().getMostSignificantBits();
    }

    public long getRuntimeId() {
        return UUID.randomUUID().getMostSignificantBits();
    }

    @Override
    public String getName() {
        return this.username;
    }

    @Override
    public UUID getServerId() {
        return null;
    }

    @Override
    public Server getServer() {
        return Server.getInstance();
    }

    public boolean sendDataPacket(BedrockPacket packet) {
        sendPacketInternal(packet);
        return true;
    }

    public void sendPacketInternal(BedrockPacket packet) {
        this.serverSession.sendPacket(packet);
    }

    public SerializedSkin getSerializedSkin() {
        return ((ClientChainData) this.loginChainData).getSerializedSkin();
    }

    public void sendAttributes() {
        UpdateAttributesPacket updateAttributesPacket = new UpdateAttributesPacket();
        updateAttributesPacket.setRuntimeEntityId(this.getRuntimeId());
        List<AttributeData> attributes = updateAttributesPacket.getAttributes();

        Attribute hunger = AttributeFactory.getINSTANCE().mustGet(Attribute.HUNGER);
        attributes.add(new AttributeData(hunger.getId(), hunger.getMinValue(), hunger.getMaxValue(), hunger.getCurrentValue(), hunger.getDefaultValue()));

        Attribute experienceLevel = AttributeFactory.getINSTANCE().mustGet(Attribute.EXPERIENCE_LEVEL);
        attributes.add(new AttributeData(experienceLevel.getId(), experienceLevel.getMinValue(), experienceLevel.getMaxValue(), experienceLevel.getCurrentValue(), experienceLevel.getDefaultValue()));

        Attribute experience = AttributeFactory.getINSTANCE().mustGet(Attribute.EXPERIENCE);
        attributes.add(new AttributeData(experience.getId(), experience.getMinValue(), experience.getMaxValue(), experience.getCurrentValue(), experience.getDefaultValue()));

        updateAttributesPacket.setAttributes(attributes);
        sendDataPacket(updateAttributesPacket);
        System.out.println(updateAttributesPacket);
    }

    public long getPing() {
        return -1;
    }

    /**
     *
     * Used to send forms to the player
     *
     * @param form The form sent to the player
     */
    public int openForm(Form form) {
        int id = this.formId.getAndIncrement();
        this.forms.put(id, form);

        ModalFormRequestPacket packet = new ModalFormRequestPacket();
        packet.setFormId(id);
        packet.setFormData(form.toJson().toString());

        this.sendDataPacket(packet);
        return id;
    }

    /**
     *
     * Retrieve an already opened form from the map.
     * The form will be deleted from the map upon retrieval.
     *
     * @param id The id given when opening the form
     * @return {@link Form}
     */
    public Form getForm(int id) {
        return this.forms.remove(id);
    }

    @Override
    public void onUpdate() {
        this.messageCounter = 2;
        super.onUpdate();
    }

    public boolean onChat(String message) {
        /*if(messageCounter <= 0) {
            return false;
        }
        int maxTotalLenght = this.messageCounter * (MAX_CHAT_BYTES_LENGTH + 1);
        if(message.length() > maxTotalLenght) {
            return false;
        }
        String[] messageParts = message.split("\n", this.messageCounter + 1);
        for(String messagePart : messageParts) {
            if(!messagePart.trim().isEmpty() && messagePart.length() <= MAX_CHAT_BYTES_LENGTH &&
                    messagePart.codePointCount(0, messagePart.length()) <= MAX_CHAT_CHAR_LENGTH && messageCounter-- > 0) {

                System.out.println(messagePart);
                if(messagePart.startsWith("./")) {
                    messagePart = messagePart.substring(1);
                }
                if(messagePart.startsWith("/")) {
                    // TODO command
                } else {
                    // call event
                    TextPacket textFormat = new TextPacket();
                    textFormat.setType(TextPacket.Type.CHAT);
                    textFormat.setXuid("");
                    textFormat.setSourceName("");
                    textFormat.setMessage(message);
                    this.sendDataPacket(textFormat);
                }
            }
        }*/
        PlayerChatEvent playerChatEvent = new PlayerChatEvent(this, message);
        playerChatEvent.call();
        if(playerChatEvent.isCancelled()) {
            return false;
        }
        TextPacket textFormat = new TextPacket();
        textFormat.setType(TextPacket.Type.CHAT);
        textFormat.setXuid("");
        textFormat.setSourceName(this.getName());
        textFormat.setMessage(message);

        this.sendDataPacket(textFormat);
        return true;
    }

}
