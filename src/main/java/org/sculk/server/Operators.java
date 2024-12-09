package org.sculk.server;

public interface Operators {

        /**
        * Adds a player to the operators list.
        *
        * @param name the name of the player to add
        */
        void add(String name);

        /**
        * Removes a player from the operators list.
        *
        * @param name the name of the player to remove
        */
        void remove(String name);

        /**
        * Checks if a player is an operator.
        *
        * @param name the name of the player to check
        * @return true if the player is an operator, false otherwise
        */
        boolean isOperator(String name);

        /**
        * Gets the list of operators.
        *
        * @return an array of operator names
        */
        String[] getOperators();
}
