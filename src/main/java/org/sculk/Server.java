package org.sculk;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import org.apache.logging.log4j.Logger;
import org.cloudburstmc.protocol.bedrock.packet.PlayerListPacket;
import org.sculk.command.CommandSender;
import org.sculk.command.SimpleCommandMap;
import org.sculk.config.Config;
import org.sculk.config.ServerProperties;
import org.sculk.config.ServerPropertiesKeys;
import org.sculk.console.TerminalConsole;
import org.sculk.event.EventManager;
import org.sculk.event.command.CommandEvent;
import org.sculk.event.player.PlayerCreationEvent;
import org.sculk.lang.Language;
import org.sculk.lang.LanguageKeys;
import org.sculk.lang.LocalManager;
import org.sculk.network.BedrockInterface;
import org.sculk.network.Network;
import org.sculk.network.SourceInterface;
import org.sculk.network.protocol.ProtocolInfo;
import org.sculk.network.session.SculkServerSession;
import org.sculk.player.Player;
import org.sculk.player.client.ClientChainData;
import org.sculk.plugin.PluginManager;
import org.sculk.scheduler.Scheduler;
import org.sculk.utils.TextFormat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;

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
@Log4j2
public class Server {

    private static Server instance = null;

    private final Logger logger;
    private final TerminalConsole console;
    private final EventManager eventManager;
    private final PluginManager pluginManager;
    private final Injector injector;

    private final Scheduler scheduler;
    private final SimpleCommandMap simpleCommandMap;
    @Getter
    private final Language language;

    private Network network;

    private final LocalManager localManager;

    private final Path dataPath;
    private final Path pluginDataPath;

    private final ServerProperties properties;
    private final Config config;
    private final Config operators;
    private final Config whitelist;
    private final Config banByName;
    private final Config banByIp;

    private final Map<UUID, Player> playerList = new HashMap<>();
    private final Map<SocketAddress, Player> players = new HashMap<>();

    private String motd;
    private String submotd;
    private int maxPlayers;
    private String defaultGamemode;
    private UUID serverId;
    private long nextTick;
    private int tickCounter;

    public volatile boolean shutdown = false;

    @SneakyThrows
    public Server(LocalManager localManager, Logger logger, String dataPath) {
        instance = this;
        this.logger = logger;
        this.dataPath = Paths.get(dataPath);
        this.properties = new ServerProperties(this.dataPath);
        Path pluginsPath = Path.of(dataPath, "plugins");
        this.pluginDataPath = Path.of(dataPath, "plugin_data");
        Path worldsPath = Path.of(dataPath, "worlds");
        Path resourcePath = Path.of(dataPath, "resource_packs");
        Path playerPath = Path.of(dataPath, "players");

        if(!pluginsPath.toFile().exists()) pluginsPath.toFile().mkdirs();
        if(!this.pluginDataPath.toFile().exists()) this.pluginDataPath.toFile().mkdirs();
        if(!worldsPath.toFile().exists()) worldsPath.toFile().mkdirs();
        if(!resourcePath.toFile().exists()) resourcePath.toFile().mkdirs();
        if(!playerPath.toFile().exists()) playerPath.toFile().mkdirs();

        this.localManager = localManager;
        this.language = localManager.getLanguage(this.properties.get(ServerPropertiesKeys.LANGUAGE, "eng"));

        this.logger.info(this.language.translate(LanguageKeys.SCULK_SERVER_LOADING, TextFormat.AQUA + "sculk.yml" + TextFormat.WHITE));
        this.config = new Config(this.dataPath + "/sculk.yml");

        this.logger.info(this.language.translate(LanguageKeys.SCULK_SERVER_LOADING, List.of(TextFormat.AQUA + "server.properties" + TextFormat.WHITE)));
        this.motd = this.properties.get(ServerPropertiesKeys.MOTD, "A Sculk Server Software");
        this.submotd = this.properties.get(ServerPropertiesKeys.SUB_MOTD, "Powered by Sculk");

        this.injector = Guice.createInjector(Stage.PRODUCTION, new SculkModule(this));
        this.eventManager = injector.getInstance(EventManager.class);
        this.scheduler = injector.getInstance(Scheduler.class);
        this.pluginManager = new PluginManager(this);

        this.logger.info(this.language.translate(LanguageKeys.SCULK_SERVER_LOADING_COMMANDS));
        this.simpleCommandMap = new SimpleCommandMap(this);

        this.operators = new Config(this.dataPath.resolve("op.txt").toString(), Config.ENUM);
        this.whitelist = new Config(this.dataPath.resolve("whitelist.txt").toString(), Config.ENUM);
        this.banByName = new Config(this.dataPath.resolve("banned-players.txt").toString(), Config.ENUM);
        this.banByIp = new Config(this.dataPath.resolve("banned-ip.txt").toString(), Config.ENUM);

        this.logger.info(this.language.translate(LanguageKeys.SCULK_SERVER_SELECTED_LANGUAGE, List.of(this.language.getName())));
        this.logger.info(this.language.translate(LanguageKeys.SCULK_SERVER_STARTING_VERSION, List.of(TextFormat.AQUA + Sculk.MINECRAFT_VERSION + TextFormat.WHITE)));

        this.console = new TerminalConsole(this);
        this.start();
    }

    public void start() {
        this.console.getConsoleThread().start();

        this.logger.info(this.language.translate(LanguageKeys.SCULK_SERVER_LOADING_PLUGINS));
        pluginManager.loadAllPlugins();
        this.logger.info(this.language.translate(LanguageKeys.SCULK_SERVER_PLUGINS_LOADED));

        InetSocketAddress bindAddress = new InetSocketAddress(this.getProperties().get(ServerPropertiesKeys.SERVER_IP, "0.0.0.0"), this.getProperties().get(ServerPropertiesKeys.SERVER_PORT, 19132));
        this.serverId = UUID.randomUUID();
        this.network = new Network(this);
        this.network.setName(this.motd);
        try {
            this.network.registerInterface(new BedrockInterface(this));
            this.logger.info(this.language.translate(LanguageKeys.SCULK_SERVER_NETWORK_INTERFACE_RUNNING,  List.of(bindAddress)));
        } catch(Exception e) {
            this.logger.error(this.language.translate(LanguageKeys.SCULK_SERVER_FAILED_BIND, List.of(bindAddress)), e);
            this.logger.fatal(this.language.translate(LanguageKeys.SCULK_SERVER_SERVER_ALREADY_RUNNING));
            shutdown();
        }

        this.tickCounter = 0;
        if(this.properties.get(ServerPropertiesKeys.XBOX_AUTH, true)) {
            this.logger.info(this.language.translate(LanguageKeys.SCULK_SERVER_ONLINE_MODE_ENABLED));
        } else {
            this.logger.info(this.language.translate(LanguageKeys.SCULK_SERVER_ONLINE_MODE_DISABLED, TextFormat.RED));
        }
        this.logger.info(this.language.translate(LanguageKeys.SCULK_SERVER_DISTRIBUTED_UNDER, TextFormat.AQUA + "GNU GENERAL PUBLIC LICENSE"));

        this.logger.info(this.language.translate(LanguageKeys.SCULK_SERVER_ENABLE_ALL_PLUGINS));
        pluginManager.enableAllPlugins();
        this.logger.info(this.language.translate(LanguageKeys.SCULK_SERVER_ALL_PLUGINS_ENABLED));

        this.logger.info(this.language.translate(LanguageKeys.SCULK_SERVER_DONE, List.of((double) (System.currentTimeMillis() - Sculk.START_TIME) / 1000)));

        this.tickProcessor();
    }

    public void shutdown() {
        Map<UUID, Player> onlinePlayers = this.getOnlinePlayers();
        for(Map.Entry<UUID, Player> entry : onlinePlayers.entrySet()) {
            Player player = entry.getValue();
            player.getNetworkSession().disconnect("Server closed");
        }
        if(this.shutdown) {
            return;
        }
        this.logger.info(this.language.translate(LanguageKeys.SCULK_SERVER_STOPPING));

        this.shutdown = true;

        this.logger.info(this.language.translate(LanguageKeys.SCULK_PLUGINS_DISABLING));
        pluginManager.disableAllPlugins();
        this.logger.info(this.language.translate(LanguageKeys.SCULK_PLUGINS_DISABLED));

        Sculk.shutdown();

        this.logger.info(this.language.translate(LanguageKeys.SCULK_NETWORK_INTERFACES_STOPPING));
        for(SourceInterface sourceInterface : this.network.getInterfaces()) {
            sourceInterface.shutdown();
            this.network.unregisterInterface(sourceInterface);
        }

        this.logger.info(this.language.translate(LanguageKeys.SCULK_CONSOLE_CLOSING));
        this.console.getConsoleThread().interrupt();


        this.logger.info(this.language.translate(LanguageKeys.SCULK_THREADS_STOPPING));
    }

    public CompletableFuture<Player> createPlayer(SculkServerSession session, ClientChainData info, boolean authenticated){
        return  CompletableFuture.supplyAsync(() -> {
            PlayerCreationEvent event = new PlayerCreationEvent(session);
            event.call();
            Class<? extends Player> clazz = event.getPlayerClass();
            Player player;

            try {
                Constructor<? extends Player> constructor = clazz.getConstructor(SculkServerSession.class, ClientChainData.class);
                player = constructor.newInstance(session, info);
                this.addPlayer(session.getSocketAddress(), player);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                this.logger.warn("Failed to create player", e);
                throw new RuntimeException(e);  // Propager l'exception dans le futur
            }

            return player;
        });
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public Injector getInjector() {
        return injector;
    }

    public boolean isRunning() {
        return !this.shutdown;
    }

    public static Server getInstance() {
        return instance;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getDataPath() {
        return dataPath;
    }

    public Path getPluginDataPath() {
        return pluginDataPath;
    }

    public Map<UUID, Player> getOnlinePlayers() {
        return Collections.unmodifiableMap(playerList);
    }

    public ServerProperties getProperties() {
        return properties;
    }

    public Config getConfig() {
        return config;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getDefaultGamemode() {
        return defaultGamemode;
    }

    public String getMotd() {
        return motd;
    }

    public UUID getServerId() {
        return serverId;
    }

    public void addPlayer(SocketAddress socketAddress, Player player) {
        this.players.put(socketAddress, player);
    }

    public void addOnlinePlayer(Player player) {
        this.playerList.put(player.getServerId(), player);
    }

    public void onPlayerCompleteLogin(Player player) {
        this.sendFullPlayerList(player);
    }

    public void sendFullPlayerList(Player player) {
        PlayerListPacket packet = new PlayerListPacket();
        packet.setAction(PlayerListPacket.Action.ADD);
        packet.getEntries().addAll(this.playerList.values().stream().map(p -> {
            PlayerListPacket.Entry entry = new PlayerListPacket.Entry(p.getServerId());
            entry.setEntityId(p.getUniqueId());
            entry.setName(p.getName());
            entry.setSkin(p.getSerializedSkin());
            entry.setPlatformChatId("");
            return entry;
        }).toList());
        player.sendDataPacket(packet);
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public String getVersion() {
        return ProtocolInfo.MINECRAFT_VERSION;
    }

    public LocalManager getLocalManager() {
        return this.localManager;
    }

    public boolean isXboxAuth() {
        return this.properties.get(ServerPropertiesKeys.XBOX_AUTH, true);
    }

    public void tickProcessor() {
        this.nextTick = System.currentTimeMillis();
        try {
            while(this.isRunning()) {
                try {
                    this.tick();
                    long next = this.nextTick;
                    long current = System.currentTimeMillis();
                    if(next - 0.1 > current) {
                        long allocated = next - current - 1;
                        if(allocated > 0) {
                            Thread.sleep(allocated, 900000);
                        }
                    }
                } catch(RuntimeException exception) {
                    log.error("Error whilst ticking server", exception);
                }
            }
        } catch(Throwable throwable) {
            log.fatal("Exception happened while ticking server", throwable);
        }
    }

    private boolean tick() {
        long tickTime = System.currentTimeMillis();
        long time = tickTime - this.nextTick;
        if(time < -25) {
            try {
                Thread.sleep(Math.max(5, -time - 25));
            } catch(InterruptedException exception) {
                log.error("Server interrupted whilst sleeping", exception);
            }
        }
        long tickTimeNano = System.nanoTime();
        if((tickTimeNano - this.nextTick) < -25) {
            return false;
        }
        try(Timing ignored = Timings.fullServerTickTimer.startTiming()) {
            ++this.tickCounter;
            try(Timing timing1 = Timings.fullServerTickTimer.startTiming()) {
                this.network.processInterfaces();
            }
            try(Timing timing1 = Timings.schedulerTimer.startTiming()) {
                this.scheduler.mainThread(this.tickCounter);
            }
        }
        return true;
    }

    public SimpleCommandMap getCommandMap() {
        return this.simpleCommandMap;
    }

    public void dispatchCommand(CommandSender sender, String commandLine, boolean internal) {
        if(!internal) {
            CommandEvent commandEvent = new CommandEvent(sender, commandLine);
            commandEvent.call();
            if(commandEvent.isCancelled()) {
                return;
            }
            commandLine = commandEvent.getCommand();
        }
        simpleCommandMap.dispatch(sender, commandLine);
    }

}
