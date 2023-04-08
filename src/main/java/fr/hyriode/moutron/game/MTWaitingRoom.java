package fr.hyriode.moutron.game;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.leaderboard.HyriLeaderboardScope;
import fr.hyriode.api.leveling.NetworkLeveling;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.DurationFormatter;
import fr.hyriode.hyrame.utils.Symbols;
import fr.hyriode.moutron.HyriMoutron;
import fr.hyriode.moutron.api.MTStatistics;
import fr.hyriode.moutron.util.MTHead;
import org.bukkit.ChatColor;

import java.util.function.Function;

/**
 * Created by AstFaster
 * on 23/07/2022 at 12:54
 */
public class MTWaitingRoom extends HyriWaitingRoom {

    private static final Function<String, HyriLanguageMessage> LANG_DATA = name -> HyriLanguageMessage.get("waiting-room.npc." + name);

    public MTWaitingRoom(MTGame game) {
        super(game, ItemBuilder.asHead(MTHead.BLUE_SHEEP).build(), HyriMoutron.get().getConfiguration().getWaitingRoom());

        this.addLeaderboard(new Leaderboard(NetworkLeveling.LEADERBOARD_TYPE, "rotating-game-experience",
                player -> HyriLanguageMessage.get("leaderboard.experience.display").getValue(player))
                .withScopes(HyriLeaderboardScope.DAILY, HyriLeaderboardScope.WEEKLY, HyriLeaderboardScope.MONTHLY));
        this.addLeaderboard(new Leaderboard(HyriMoutron.ID, "kills", player -> HyriLanguageMessage.get("leaderboard.kills.display").getValue(player)));
        this.addLeaderboard(new Leaderboard(HyriMoutron.ID, "victories", player -> HyriLanguageMessage.get("leaderboard.victories.display").getValue(player)));

        this.addStatistics(22, MTGameType.SOLO);
    }

    private void addStatistics(int slot, MTGameType gameType) {
        final NPCCategory normal = new NPCCategory(HyriLanguageMessage.from(gameType.getDisplayName()));

        normal.addData(new NPCData(LANG_DATA.apply("kills"), account -> String.valueOf(this.getStatistics(gameType, account).getKills())));
        normal.addData(new NPCData(LANG_DATA.apply("deaths"), account -> String.valueOf(this.getStatistics(gameType, account).getDeaths())));
        normal.addData(new NPCData(LANG_DATA.apply("victories"), account -> String.valueOf(this.getStatistics(gameType, account).getVictories())));
        normal.addData(new NPCData(LANG_DATA.apply("games-played"), account -> String.valueOf(this.getStatistics(gameType, account).getGamesPlayed())));
        normal.addData(new NPCData(LANG_DATA.apply("played-time"), account -> this.formatPlayedTime(account, account.getStatistics().getPlayTime(HyriMoutron.ID + "#" + gameType.getName()))));

        this.addNPCCategory(slot, normal);
    }

    private String formatPlayedTime(IHyriPlayer account, long playedTime) {
        return playedTime < 1000 ? ChatColor.RED + Symbols.CROSS_STYLIZED_BOLD : new DurationFormatter()
                .withSeconds(false)
                .format(account.getSettings().getLanguage(), playedTime);
    }

    private MTStatistics.Data getStatistics(MTGameType gameType, IHyriPlayer account) {
        return ((MTPlayer) this.game.getPlayer(account.getUniqueId())).getStatistics().getData(gameType);
    }

}
