package org.sculk.player;

import co.aikar.timings.Timings;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.protocol.bedrock.data.AttributeData;
import org.cloudburstmc.protocol.bedrock.data.DisconnectFailReason;
import org.cloudburstmc.protocol.bedrock.data.command.CommandData;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataMap;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataTypes;
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
import org.sculk.event.player.PlayerChangeSkinEvent;
import org.sculk.event.player.PlayerChatEvent;
import org.sculk.form.Form;
import org.sculk.lang.Language;
import org.sculk.lang.Translatable;
import org.sculk.network.session.SculkServerSession;
import org.sculk.player.chat.StandardChatFormatter;
import org.sculk.player.client.ClientChainData;
import org.sculk.player.client.LoginChainData;
import org.sculk.player.skin.Skin;
import org.sculk.player.text.RawTextBuilder;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
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

    public static final Player[] EMPTY_ARRAY = new Player[0];
    @Getter
    private final SculkServerSession networkSession;
    private final SyncedEntityData data = new SyncedEntityData(this);
    private final LoginChainData loginChainData;

    private final AtomicInteger formId;
    private final Int2ObjectOpenHashMap<Form> forms;
    private List<AttributeData> attributeMap;

    @Getter @Setter
    private String displayName;
    private final String username;
    @Getter
    private final String xuid;
    protected AtomicBoolean connected = new AtomicBoolean(true);

    protected int messageCounter = 2;
    protected int MAX_CHAT_CHAR_LENGTH = 512;
    protected int MAX_CHAT_BYTES_LENGTH = MAX_CHAT_CHAR_LENGTH * 2;

    public Player(SculkServerSession networkSession, ClientChainData data) {
        this.networkSession = networkSession;
        this.server = networkSession.getServer();
        this.loginChainData = data;

        this.formId = new AtomicInteger(0);
        this.forms = new Int2ObjectOpenHashMap<>();

        this.displayName = data.getUsername();
        this.username = data.getUsername();
        this.xuid = data.getXUID();
        this.setSkin(data.getSkin());
        this.uuid = data.getClientUUID();
        this.getServer().addOnlinePlayer(this);
        this.spawn(this);

        this.initEntity();
    }

    @Override
    public void initEntity() {
        super.initEntity();
    }

    /**
     * Called when a player changes their skin.
     * Plugin developers should not use this, use setSkin() and sendSkin() instead.
     */
    public boolean changeSkin(Skin skin, String newSkinName, String oldSkinName){
        PlayerChangeSkinEvent ev = new PlayerChangeSkinEvent(this, this.getSkin(), skin);
        ev.call();

        if(ev.isCancelled()){
            this.sendSkin(List.of(this));
            return true;
        }

        this.setSkin(ev.getNewSkin());
        this.sendSkin(List.copyOf(this.server.getOnlinePlayers().values()));
        return true;
    }

    /**
     * Sends the available commands data to the player.
     * <p>
     * This method collects all available commands from the server's command map and converts them
     * into a format suitable for network transmission. Each command entry is checked to ensure
     * the command's value name matches its key before it is processed. The processed commands
     * are then stored in a list within an AvailableCommandsPacket, which is subsequently sent
     * to the player using the sendDataPacket method.
     */
    public void sendCommandsData() {
        AvailableCommandsPacket availableCommandsPacket = new AvailableCommandsPacket();
        List<CommandData> commandData = availableCommandsPacket.getCommands();
        for (Map.Entry<String, Command> command : this.getServer().getCommandMap().getCommands().entrySet()) {
            if (!Objects.equals(command.getValue().getName(), command.getKey()))
                continue;
            commandData.add(new CreatorCommandData(this, command.getValue()).toNetwork());
        }
        sendDataPacket(availableCommandsPacket);
    }

    /**
     * Updates the player's entity flags to indicate they are currently breathing.
     * This method sets the BREATHE flag to true in the player's data, ensuring the
     * state is reflected on the server. It then triggers an update to communicate these
     * flag changes to the client.
     */
    public void updateFlags() {
        this.data.setFlags(EntityFlag.BREATHING, true);
        this.data.updateFlag();
    }

    /**
     * Disconnects the player from the server with the specified kick message.
     *
     * @param message the reason or explanation to be sent to the player upon disconnection
     */
    public void kick(String message) {
        DisconnectPacket packet = new DisconnectPacket();
        packet.setKickMessage(message);
        packet.setReason(DisconnectFailReason.KICKED);
        sendDataPacket(packet);
    }

    /**
     * Disconnects the player from the server with a specified reason and message.
     *
     * @param message the message explaining the reason for the disconnection to the player
     * @param disconnectFailReason the reason for disconnection that categorizes the failure
     */
    public void kick(String message, DisconnectFailReason disconnectFailReason) {
        DisconnectPacket packet = new DisconnectPacket();
        packet.setKickMessage(message);
        packet.setReason(disconnectFailReason);
        sendDataPacket(packet);
    }

    /**
     * Retrieves the username of the player.
     *
     * @return the player's username as a String.
     */
    @Override
    public String getName() {
        return this.username;
    }

    /**
     * Retrieves the current locale of the player based on their login chain data.
     * The locale is determined by splitting the language code string, which is expected
     * to be in the format of "language_country". If the country part is not specified,
     * an empty string is used.
     *
     * @return a Locale object representing the player's locale derived from the language code.
     */
    @Override
    public Locale getLocale() {
        String[] languagePart = this.loginChainData.getLanguageCode().split("_");
        return Locale.of(languagePart[0], languagePart.length > 1 ? languagePart[1] : "");
    }

    /**
     * Retrieves the language setting for the player based on their current locale.
     *
     * @return a Language object representing the player's language as determined by their locale.
     */
    @Override
    public Language getLanguage() {
        return Server.getInstance().getLocalManager().getLanguage(this.getLocale());
    }

    /**
     * Retrieves the server instance associated with this player.
     *
     * @return the current Server instance.
     */
    @Override
    public Server getServer() {
        return Server.getInstance();
    }

    /**
     * Checks if the player is currently online.
     *
     * @return true if the player is connected; false otherwise.
     */
    @Override
    public boolean isOnline() {
        return this.connected.get();
    }

    @Override
    public boolean isBanned() {
        return false;
    }

    @Override
    public void setBanned(boolean value) {
    }

    @Override
    public boolean isWhitelisted() {
        return false;
    }

    @Override
    public void setWhitelisted(boolean value) {
    }

    public void sendDataPacket(BedrockPacket packet) {
        sendPacketInternal(packet);
    }

    /**
     * Sends a packet to the player's network session using the internal transmission mechanism.
     *
     * This method directly delegates the packet transmission to the player's
     * associated network session, ensuring that the provided packet is sent through
     * the established network connection.
     *
     * @param packet the BedrockPacket object to be sent to the player's network session
     */
    public void sendPacketInternal(BedrockPacket packet) {
        this.networkSession.sendPacket(packet);
    }

    public SerializedSkin getSerializedSkin() {
        return ((ClientChainData) this.loginChainData).getSerializedSkin();
    }

    public ClientChainData getClientData() {
        return (ClientChainData) this.loginChainData;
    }

    /**
     * Sends updated attribute data for the player to the client.
     *
     * This method constructs an UpdateAttributesPacket containing the player's current
     * attributes, such as hunger, experience level, and experience. It retrieves these
     * attributes from the AttributeFactory and populates them into the packet as AttributeData
     * objects, capturing their respective IDs, minimum values, maximum values, current values,
     * and default values. Once all relevant attributes have been gathered and added to the packet,
     * the packet is sent to the player's client using the sendDataPacket method.
     *
     * This is typically used to synchronize attribute data between the server and
     * the client whenever there are updates on the player’s attributes.
     */
    public void sendAttributes() {
        UpdateAttributesPacket updateAttributesPacket = new UpdateAttributesPacket();
        updateAttributesPacket.setRuntimeEntityId(this.getEntityId());
        List<AttributeData> attributes = updateAttributesPacket.getAttributes();

        Attribute hunger = AttributeFactory.getINSTANCE().mustGet(Attribute.HUNGER);
        attributes.add(new AttributeData(hunger.getId(), hunger.getMinValue(), hunger.getMaxValue(), hunger.getCurrentValue(), hunger.getDefaultValue()));

        Attribute experienceLevel = AttributeFactory.getINSTANCE().mustGet(Attribute.EXPERIENCE_LEVEL);
        attributes.add(new AttributeData(experienceLevel.getId(), experienceLevel.getMinValue(), experienceLevel.getMaxValue(), experienceLevel.getCurrentValue(), experienceLevel.getDefaultValue()));

        Attribute experience = AttributeFactory.getINSTANCE().mustGet(Attribute.EXPERIENCE);
        attributes.add(new AttributeData(experience.getId(), experience.getMinValue(), experience.getMaxValue(), experience.getCurrentValue(), experience.getDefaultValue()));

        updateAttributesPacket.setAttributes(attributes);
        sendDataPacket(updateAttributesPacket);
        //todo delete this
        //System.out.println(updateAttributesPacket);
    }

    /**
     * Synchronizes the network data by updating the player's data properties.
     * This method overrides the parent's syncNetworkData method and adds
     * additional synchronization for player-specific data.
     *
     * @param properties The EntityDataMap containing the data properties for the player.
     */
    @Override
    protected void syncNetworkData(EntityDataMap properties) {
        super.syncNetworkData(properties);
        properties.put(EntityDataTypes.PLAYER_FLAGS, (byte) 2);
    }

    public long getPing() {
        return -1;
    }

    /**
     * Opens a form for the player by sending a modal form request packet.
     * Assigns a unique identifier to the form and stores it in the player's forms map.
     *
     * @param form The form to be opened and sent to the player.
     * @return The unique identifier assigned to the opened form.
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
     * Retrieves and removes a form from the player's collection of forms using the specified form ID.
     *
     * @param id The unique identifier of the form to be retrieved.
     * @return The form associated with the specified ID, or null if no form with that ID exists.
     */
    public Form getForm(int id) {
        return this.forms.remove(id);
    }

    @Override
    public void onUpdate() {
        this.messageCounter = 2;
        super.onUpdate();
    }

    /**
     * Handles a chat message or command input from a player.
     *
     * @param message the message or command input from the player. If the message starts with "./",
     *                it is interpreted as a command and the initial "." is removed before processing.
     *                If it starts with "/", it is directly processed as a command. Otherwise, it is
     *                handled as a regular chat message.
     */
    public void onChat(String message) {
        if (message.startsWith("./")) {
            message = message.substring(1);
        }
        if (message.startsWith("/")) {
            String command = message.substring(1);
            Timings.playerCommandTimer.startTiming();
            this.getServer().dispatchCommand(this, command, false);
            Timings.playerCommandTimer.stopTiming();
        } else {
            PlayerChatEvent playerChatEvent = new PlayerChatEvent(this, message, new StandardChatFormatter());
            playerChatEvent.call();
            if (!playerChatEvent.isCancelled()) {
                // TODO please change for use this.messageCount
                String messageFormat = playerChatEvent.getChatFormatter().format(this.getName(), message);
                this.getNetworkSession().onChatMessage(messageFormat);
                this.getServer().getLogger().info(messageFormat);
            }
        }
    }

    /**
     * Sends a chat message through the player's network session.
     *
     * @param message The message to be sent to the network session.
     */
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
