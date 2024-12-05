package org.sculk.player;

import co.aikar.timings.Timings;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import org.cloudburstmc.protocol.bedrock.data.AttributeData;
import org.cloudburstmc.protocol.bedrock.data.DisconnectFailReason;
import org.cloudburstmc.protocol.bedrock.data.command.*;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityFlag;
import org.cloudburstmc.protocol.bedrock.data.skin.SerializedSkin;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.sculk.Server;
import org.sculk.command.Command;
import org.sculk.command.CommandSender;
import org.sculk.command.network.CreatorCommandData;
import org.sculk.entity.Attribute;
import org.sculk.entity.AttributeFactory;
import org.sculk.entity.HumanEntity;
import org.sculk.entity.data.SyncedEntityData;
import org.sculk.event.player.PlayerChatEvent;
import org.sculk.form.Form;
import org.sculk.lang.Language;
import org.sculk.lang.Translatable;
import org.sculk.network.session.SculkServerSession;
import org.sculk.player.chat.StandardChatFormatter;
import org.sculk.player.client.ClientChainData;
import org.sculk.player.client.LoginChainData;
import org.sculk.player.text.RawTextBuilder;

import java.util.*;
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
public class Player extends HumanEntity implements PlayerInterface, CommandSender {

    @Getter
    private final SculkServerSession networkSession;
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

    public Player(SculkServerSession networkSession, ClientChainData data) {
        this.networkSession = networkSession;
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
        sendCommandsData();
        this.getServer().addOnlinePlayer(this);
        System.out.println(getServer().getOnlinePlayers());
    }

    public void sendCommandsData() {
        AvailableCommandsPacket availableCommandsPacket = new AvailableCommandsPacket();
        List<CommandData> commandData = availableCommandsPacket.getCommands();
        for(Map.Entry<String, Command> command : this.getServer().getCommandMap().getCommands().entrySet()) {
            if (!Objects.equals(command.getValue().getName(), command.getKey()))
                continue;
            commandData.add(new CreatorCommandData(command.getValue()).toNetwork(this.getLanguage()::translate));
        }
        sendDataPacket(availableCommandsPacket);
    }

    public void updateFlags() {
        this.data.setFlags(EntityFlag.BREATHING, true);
        this.data.updateFlag();
    }

    public void kick(String message) {
        DisconnectPacket packet = new DisconnectPacket();
        packet.setKickMessage(message);
        packet.setReason(DisconnectFailReason.KICKED);
        sendDataPacket(packet);
    }

    public void kick(String message, DisconnectFailReason disconnectFailReason) {
        DisconnectPacket packet = new DisconnectPacket();
        packet.setKickMessage(message);
        packet.setReason(disconnectFailReason);
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
    public Locale getLocale() {
        return Locale.forLanguageTag("fr_FR");
    }

    @Override
    public Language getLanguage() {
        return Server.getInstance().getLocalManager().getLanguage(this.getLocale());
    }

    @Override
    public UUID getServerId() {
        return null;
    }

    @Override
    public Server getServer() {
        return Server.getInstance();
    }

    public void sendDataPacket(BedrockPacket packet) {
        sendPacketInternal(packet);
    }

    public void sendPacketInternal(BedrockPacket packet) {
        this.networkSession.sendPacket(packet);
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

    public void onChat(String message) {
        if(message.startsWith("./")) {
            message = message.substring(1);
        }
        if(message.startsWith("/")) {
            String command = message.substring(1);
            Timings.playerCommandTimer.startTiming();
            this.getServer().dispatchCommand(this, command, false);
            Timings.playerCommandTimer.stopTiming();
        } else {
            PlayerChatEvent playerChatEvent = new PlayerChatEvent(this, message, new StandardChatFormatter());
            playerChatEvent.call();
            if(!playerChatEvent.isCancelled()) {
                // TODO please change for use this.messageCount
                String messageFormat = playerChatEvent.getChatFormatter().format(this.getName(), message);
                this.getNetworkSession().onChatMessage(messageFormat);
                this.getServer().getLogger().info(messageFormat);
            }
        }
    }

    public void sendMessage(String message) {
        this.getNetworkSession().onChatMessage(message);
    }

    @Override
    public void sendMessage(RawTextBuilder textBuilder) {
        this.getNetworkSession().onChatMessage(textBuilder);
    }

    public void sendMessage(Translatable<?> translatable) {
        this.getNetworkSession().onChatMessage(translatable);
    }

    public void sendJukeboxPopup(String message) {
        this.getNetworkSession().onJukeboxPopup(message);
    }

    public void sendPopup(String message) {
        this.getNetworkSession().onPopup(message);
    }

    public void sendTip(String message) {
        this.getNetworkSession().onTip(message);
    }

    public void sendAnnouncement(String message) {
        this.getNetworkSession().onAnnouncement(message);
    }

    public void sendAnnouncement(RawTextBuilder rawTextBuilder) {
        this.getNetworkSession().onAnnouncement(rawTextBuilder);
    }

    public void sendMessageSystem(String message) {
        this.getNetworkSession().onMessageSystem(message);
    }

    public void sendWhisper(String message) {
        this.getNetworkSession().onWhisper(message);
    }

    public void sendWhisper(RawTextBuilder rawTextBuilder) {
        this.getNetworkSession().onWhisper(rawTextBuilder);
    }

    public void sendMessageTranslation(String translate, List<String> parameters) {
        this.getNetworkSession().onMessageTranslation(translate, parameters);
    }

}
