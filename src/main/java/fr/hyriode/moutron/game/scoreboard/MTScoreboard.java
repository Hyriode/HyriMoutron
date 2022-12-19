package fr.hyriode.moutron.game.scoreboard;

import fr.hyriode.hyrame.game.scoreboard.HyriGameScoreboard;
import fr.hyriode.moutron.HyriMoutron;
import fr.hyriode.moutron.game.MTGame;
import fr.hyriode.moutron.language.MTMessage;
import org.bukkit.entity.Player;

/**
 * Project: HyriPearlControl
 * Created by AstFaster
 * on 05/02/2022 at 11:22
 */
public class MTScoreboard extends HyriGameScoreboard<MTGame> {

    public MTScoreboard(Player player) {
        super(HyriMoutron.get(), HyriMoutron.get().getGame(), player, "moutron");

        this.addLines();

        this.addCurrentDateLine(0);
        this.addBlankLine(1);
        this.addBlankLine(3);
        this.addBlankLine(5);
        this.addGameTimeLine(6, MTMessage.SCOREBOARD_TIME.asString(this.player));
        this.addBlankLine(7);
        this.addHostnameLine();
    }

    private void addLines() {
        this.setLine(2, MTMessage.SCOREBOARD_POWER_UP.asString(this.player).replace("%time%", String.valueOf(HyriMoutron.get().getPowerUpHandler().getTimer())));
        this.addPlayersLine(4, MTMessage.SCOREBOARD_PLAYERS.asString(this.player), true);
    }

    public void update() {
        this.addLines();
        this.updateLines();
    }

}
