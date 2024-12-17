package org.sculk.server;

import org.sculk.Server;
import org.sculk.api.server.Whitelist;
import org.sculk.config.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SculkWhitelist implements Whitelist {

    private Config config = new Config(Server.getInstance().getDataPath().resolve("whitelist.txt").toString(), Config.ENUM);

    public SculkWhitelist() {
        config.save();
    }

    /**
     * Adds a player to the whitelist.
     *
     * @param name the name of the player to add
     */
    @Override
    public void add(String name) {
        if (!isWhitelisted(name) && exists()) {
            List<String> players = new ArrayList<>(Arrays.asList(getPlayers()));
            if (!players.contains(name)) {
                players.add(name);
                config.set("whitelist", String.join(", ", players));
                config.save();
            }
        }
    }

    /**
     * Removes a player from the whitelist.
     *
     * @param name the name of the player to remove
     */
    @Override
    public void remove(String name) {
        if (isWhitelisted(name) && exists()) {
            List<String> players = new ArrayList<>(Arrays.asList(getPlayers()));
            players.remove(name);
            config.set("whitelist", String.join(", ", players));
            config.save();
        }
    }

    /**
     * Checks if a player is whitelisted.
     *
     * @param name the name of the player to check
     * @return true if the player is whitelisted, false otherwise
     */
    @Override
    public boolean isWhitelisted(String name) {
        return Arrays.asList(getPlayers()).contains(name);
    }

    /**
     * Gets the list of whitelisted players.
     *
     * @return an array of player names
     */
    @Override
    public String[] getPlayers() {
        if (exists()) {
            String content = config.getString("whitelist");
            return content.split(", ");
        }
        return new String[0];
    }

    public boolean exists() {
        return config.exists("whitelist");
    }
}
