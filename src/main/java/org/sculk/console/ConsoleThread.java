package org.sculk.console;

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
public class ConsoleThread extends Thread {

    private final TerminalConsole console;

    public ConsoleThread(TerminalConsole console) {
        super("Sculk-MP Minecraft: Bedrock Edition");
        this.console = console;
    }

    @Override
    public void run() {
        this.console.start();
    }
}
