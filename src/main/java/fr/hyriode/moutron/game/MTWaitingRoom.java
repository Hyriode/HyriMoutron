package fr.hyriode.moutron.game;

import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.moutron.HyriMoutron;
import fr.hyriode.moutron.util.MTHead;

/**
 * Created by AstFaster
 * on 23/07/2022 at 12:54
 */
public class MTWaitingRoom extends HyriWaitingRoom {

    public MTWaitingRoom(MTGame game) {
        super(game, ItemBuilder.asHead(MTHead.BLUE_SHEEP).build(), HyriMoutron.get().getConfiguration().getWaitingRoom());
    }

}
