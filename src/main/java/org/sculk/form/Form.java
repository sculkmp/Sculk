package org.sculk.form;

import org.cloudburstmc.protocol.bedrock.packet.ModalFormResponsePacket;
import org.sculk.Player;
import org.sculk.form.response.Response;
import org.sculk.utils.json.Serializable;

public interface Form extends Serializable {
    Response processResponse(Player player, ModalFormResponsePacket packet);
}
