package org.sculk.form;

import org.cloudburstmc.protocol.bedrock.packet.ModalFormResponsePacket;
import org.sculk.form.response.Response;
import org.sculk.utils.json.Serializable;

public interface Form extends Serializable {
    Response processResponse(ModalFormResponsePacket packet);
}
