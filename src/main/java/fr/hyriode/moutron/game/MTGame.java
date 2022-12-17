package fr.hyriode.moutron.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGameType;
import fr.hyriode.moutron.HyriMoutron;
import fr.hyriode.moutron.language.MTMessage;

/**
 * Created by AstFaster
 * on 17/12/2022 at 09:53
 */
public class MTGame extends HyriGame<MTPlayer> {

    public MTGame() {
        super(IHyrame.get(),
                HyriMoutron.get(),
                HyriAPI.get().getConfig().isDevEnvironment() ? HyriAPI.get().getGameManager().createGameInfo(HyriMoutron.ID, HyriMoutron.NAME) : HyriAPI.get().getGameManager().getGameInfo(HyriMoutron.ID),
                MTPlayer.class,
                HyriAPI.get().getConfig().isDevEnvironment() ? MTGameType.SOLO : HyriGameType.getFromData(MTGameType.values()));
        this.description = MTMessage.GAME_DESCRIPTION.asLang();
    }

}
