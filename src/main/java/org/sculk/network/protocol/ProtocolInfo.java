package org.sculk.network.protocol;

import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v712.Bedrock_v712;

/*
 *   ____             _ _              __  __ ____
 *  / ___|  ___ _   _| | | __         |  \/  |  _ \
 *  \___ \ / __| | | | | |/ /  _____  | |\/| | |_) |
 *   ___) | (__| |_| | |   <  |_____| | |  | |  __/
 *  |____/ \___|\__,_|_|_|\_\         |_|  |_|_|
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author: SculkTeams
 * @link: http://www.sculkmp.org/
 */
public interface ProtocolInfo {

    int CURRENT_PROTOCOL = Integer.parseInt("712");

    BedrockCodec CODEC = Bedrock_v712.CODEC;

    String MINECRAFT_VERSION_NETWORK = "1.21.20";
    String MINECRAFT_VERSION = "v" + MINECRAFT_VERSION_NETWORK;

}
