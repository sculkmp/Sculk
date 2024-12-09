/*
 * Copyright 2022 WaterdogTEAM
 * Licensed under the GNU General Public License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sculk.network.channel.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;
import org.cloudburstmc.netty.channel.raknet.RakPing;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.sculk.network.BedrockInterface;

@Log4j2
public class RakNetPingHandler extends SimpleChannelInboundHandler<RakPing> {
    public static final String NAME = "rak-ping-handler";

    private final BedrockInterface bedrockInterface;

    public RakNetPingHandler(BedrockInterface proxy) {
        this.bedrockInterface = proxy;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RakPing rakPing) throws Exception {

        long guid = ctx.channel().config().getOption(RakChannelOption.RAK_GUID);

        ctx.writeAndFlush(rakPing.reply(guid, this.bedrockInterface.getBedrockPong().toByteBuf()));
    }
}
