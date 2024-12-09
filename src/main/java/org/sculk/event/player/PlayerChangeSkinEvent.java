package org.sculk.event.player;

import lombok.Getter;
import lombok.Setter;
import org.sculk.event.Cancellable;
import org.sculk.player.Player;
import org.sculk.player.skin.Skin;

public class PlayerChangeSkinEvent extends PlayerEvent implements Cancellable {
    @Getter
    private Skin oldSkin;
    @Getter @Setter
    private Skin newSkin;

    public PlayerChangeSkinEvent(Player player, Skin oldSkin, Skin newSkin) {
        super(player);
        this.oldSkin = oldSkin;
        this.newSkin = newSkin;
    }

}
