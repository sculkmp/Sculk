package org.sculk.form;

import org.cloudburstmc.protocol.bedrock.packet.ModalFormRequestPacket;
import org.sculk.Player;
import org.sculk.utils.json.Serializable;

public interface IForm extends Serializable {

    default void send(Player player, int id) {
        ModalFormRequestPacket packet = new ModalFormRequestPacket();
        packet.setFormId(id);
        packet.setFormData(this.toJson().toString());

        player.sendDataPacket(packet);
    }

}
