package org.sculk.event.player;

import lombok.Getter;
import org.sculk.Player;
import org.sculk.form.Form;

@Getter
public class PlayerFormRespondedEvent extends PlayerEvent {
    protected final int formId;
    protected final Form form;

    public PlayerFormRespondedEvent(Player player, int formId, Form form) {
        super(player);

        this.formId = formId;
        this.form = form;
    }
}