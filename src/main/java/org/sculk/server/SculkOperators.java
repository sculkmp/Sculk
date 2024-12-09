package org.sculk.server;

import org.sculk.Server;
import org.sculk.config.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SculkOperators implements Operators{

    private Config config = new Config(Server.getInstance().getDataPath().resolve("op.txt").toString(), Config.ENUM);

    public SculkOperators() {
        config.save();
    }

    /**
     * Adds a player to the operators list.
     *
     * @param name the name of the player to add
     */
    @Override
    public void add(String name) {
        if (!isOperator(name) && exists()) {
            List<String> players = new ArrayList<>(Arrays.asList(getOperators()));
            if (!players.contains(name)) {
                players.add(name);
                config.set("op", String.join(", ", players));
                config.save();
            }
        }
    }

    /**
     * Removes a player from the operators list.
     *
     * @param name the name of the player to remove
     */
    @Override
    public void remove(String name) {
        if (isOperator(name) && exists()) {
            List<String> players = new ArrayList<>(Arrays.asList(getOperators()));
            players.remove(name);
            config.set("op", String.join(", ", players));
            config.save();
        }
    }

    /**
     * Checks if a player is an operator.
     *
     * @param name the name of the player to check
     * @return true if the player is an operator, false otherwise
     */
    @Override
    public boolean isOperator(String name) {
        return Arrays.asList(getOperators()).contains(name);
    }

    /**
     * Gets the list of operators.
     *
     * @return an array of operator names
     */
    @Override
    public String[] getOperators() {
        if (exists()) {
            String content = config.getString("op");
            return content.split(", ");
        }
        return new String[0];
    }

    public boolean exists() {
        return config.exists("op");
    }
}
