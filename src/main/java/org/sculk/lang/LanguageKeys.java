package org.sculk.lang;

public enum LanguageKeys {
    SCULK_SERVER_STARTING("sculk.server.starting"),
    SCULK_SERVER_LOADING("sculk.server.loading"),
    SCULK_SERVER_LOADING_COMMANDS("sculk.server.loading.commands"),
    SCULK_SERVER_SELECTED_LANGUAGE("sculk.server.selected.language"),
    SCULK_SERVER_STARTING_VERSION("sculk.server.starting.version"),
    SCULK_SERVER_LOADING_PLUGINS("sculk.server.loading.plugins"),
    SCULK_SERVER_SUCCESS_PLUGIN("sculk.server.success.plugin"),
    SCULK_SERVER_PLUGINS_LOADED("sculk.server.plugins.loaded"),
    SCULK_SERVER_NETWORK_INTERFACE_RUNNING("sculk.server.network.interface.running"),
    SCULK_SERVER_FAILED_BIND("sculk.server.failed.bind"),
    SCULK_SERVER_SERVER_ALREADY_RUNNING("sculk.server.server.already.running"),
    SCULK_SERVER_ONLINE_MODE_ENABLED("sculk.server.online.mode.enabled"),
    SCULK_SERVER_ONLINE_MODE_DISABLED("sculk.server.online.mode.disabled"),
    SCULK_SERVER_DISTRIBUTED_UNDER("sculk.server.distributed.under"),
    SCULK_SERVER_ENABLE_ALL_PLUGINS("sculk.server.enable.all.plugins"),
    SCULK_SERVER_ALL_PLUGINS_ENABLED("sculk.server.all.plugins.enabled"),
    SCULK_SERVER_DONE("sculk.server.done"),
    SCULK_SERVER_STOPPING("sculk.server.stopping"),
    SCULK_PLUGINS_DISABLING("sculk.plugins.disabling"),
    SCULK_PLUGINS_DISABLED("sculk.plugins.disabled"),
    SCULK_NETWORK_INTERFACES_STOPPING("sculk.network.interfaces.stopping"),
    SCULK_CONSOLE_CLOSING("sculk.console.closing"),
    SCULK_THREADS_STOPPING("sculk.threads.stopping"),

    MINECRAFT_PLAYER_JOIN("multiplayer.player.joined");

    private final String key;

    LanguageKeys(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}