package org.sculk.network.handler;

import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.sculk.network.session.SculkServerSession;

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
public class SculkPacketHandler  implements BedrockPacketHandler {

    protected SculkServerSession session;

    public SculkPacketHandler(SculkServerSession session){
        this.session = session;
    }

    public void setUp() {

    }
}
