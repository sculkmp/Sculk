package org.sculk.network;

import lombok.extern.log4j.Log4j2;
import org.sculk.Server;

import java.util.HashSet;
import java.util.Set;

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
public class Network {

    private final Server server;
    private final Set<SourceInterface> interfaces = new HashSet<>();
    private final Set<AdvancedSourceInterface> advancedInterfaces = new HashSet<AdvancedSourceInterface>();

    private String name;
    private int maxPlayers;

    public Network(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return server;
    }

    public void setName(String name) {
        if(name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Server name cannot be null or empty");
        }
        this.name = name;
    }

    public void setMaxPlayers(int maxPlayers) {
        if (maxPlayers <= 0) {
            throw new IllegalArgumentException("Max players must be greater than zero");
        }
        this.maxPlayers = maxPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getName() {
        return name;
    }

    public Set<SourceInterface> getInterfaces() {
        return interfaces;
    }

    public void processInterfaces() {
        for(SourceInterface sourceInterface : this.interfaces) {
            try {
                sourceInterface.process();
            } catch(Exception exception) {
                sourceInterface.emergencyShutdown();
                this.unregisterInterface(sourceInterface);
                log.fatal("Unregister source interface: " + sourceInterface.getClass().getName());
            }
        }
    }

    public void registerInterface(SourceInterface interfaz) {
        this.interfaces.add(interfaz);
        if (interfaz instanceof AdvancedSourceInterface) {
            this.advancedInterfaces.add((AdvancedSourceInterface) interfaz);
            ((AdvancedSourceInterface) interfaz).setNetwork(this);
        }
        interfaz.setName(this.name + "!@#" + this.getName());
    }

    public void unregisterInterface(SourceInterface sourceInterface) {
        this.interfaces.remove(sourceInterface);
        if (sourceInterface instanceof AdvancedSourceInterface) {
            this.advancedInterfaces.remove(sourceInterface);
        }
    }

}
