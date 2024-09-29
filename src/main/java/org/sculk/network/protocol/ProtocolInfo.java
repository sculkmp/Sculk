package org.sculk.network.protocol;

import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v712.Bedrock_v712;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import static com.google.common.base.Preconditions.checkNotNull;

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
public class ProtocolInfo {

    public static int CURRENT_PROTOCOL = Integer.parseInt("712");

    public static BedrockCodec CODEC = Bedrock_v712.CODEC;

    private static final Set<BedrockCodec> CODECS = ConcurrentHashMap.newKeySet();

    public static String MINECRAFT_VERSION_NETWORK = "1.21.21";
    public static String MINECRAFT_VERSION = "v" + MINECRAFT_VERSION_NETWORK;

    static {
        CODECS.add(checkNotNull(CODEC, "packetCodec"));
    }

    public static BedrockCodec getPacket(int protocol) {
        for(BedrockCodec codec : CODECS) {
            if(codec.getProtocolVersion() == protocol) {
                return codec;
            }
        }
        return null;
    }

}
