package fr.hyriode.moutron.language;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Created by AstFaster
 * on 23/07/2022 at 09:51
 */
public enum MTMessage {

    GAME_DESCRIPTION("game.description"),

    PLAYER_ELIMINATED("player.eliminated"),

    SCOREBOARD_PLAYERS("scoreboard.players"),
    SCOREBOARD_TIME("scoreboard.time"),

    STARTING_GO("starting.go"),

    ;

    private HyriLanguageMessage languageMessage;

    private final String key;
    private final BiFunction<IHyriPlayer, String, String> formatter;

    MTMessage(String key, BiFunction<IHyriPlayer, String, String> formatter) {
        this.key = key;
        this.formatter = formatter;
    }

    MTMessage(String key, MTMessage prefix) {
        this.key = key;
        this.formatter = (target, input) -> prefix.asString(target) + input;
    }

    MTMessage(String key) {
        this(key, (target, input) -> input);
    }

    public HyriLanguageMessage asLang() {
        return this.languageMessage == null ? this.languageMessage = HyriLanguageMessage.get(this.key) : this.languageMessage;
    }

    public String asString(IHyriPlayer account) {
        return this.formatter.apply(account, this.asLang().getValue(account));
    }

    public String asString(Player player) {
        return this.asString(IHyriPlayer.get(player.getUniqueId()));
    }

    public List<String> asList(IHyriPlayer account) {
        return new ArrayList<>(Arrays.asList(this.asString(account).split("\n")));
    }

    public List<String> asList(Player player) {
        return this.asList(IHyriPlayer.get(player.getUniqueId()));
    }

}
