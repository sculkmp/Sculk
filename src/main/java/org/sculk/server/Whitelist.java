package org.sculk.server;

public interface Whitelist {

    /**
     * Adds a player to the whitelist.
     *
     * @param name the name of the player to add
     */
    void add(String name);

    /**
     * Removes a player from the whitelist.
     *
     * @param name the name of the player to remove
     */
    void remove(String name);

    /**
     * Checks if a player is whitelisted.
     *
     * @param name the name of the player to check
     * @return true if the player is whitelisted, false otherwise
     */
    boolean isWhitelisted(String name);

    /**
     * Gets the list of whitelisted players.
     *
     * @return an array of player names
     */
    String[] getPlayers();
}
