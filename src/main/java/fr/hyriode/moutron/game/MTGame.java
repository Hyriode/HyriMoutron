package fr.hyriode.moutron.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGameType;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.LocationWrapper;
import fr.hyriode.moutron.HyriMoutron;
import fr.hyriode.moutron.config.MTConfig;
import fr.hyriode.moutron.language.MTMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.function.Function;

/**
 * Created by AstFaster
 * on 17/12/2022 at 09:53
 */
public class MTGame extends HyriGame<MTPlayer> {

    public static final String BLOCK_METADATA = "MoutronBlock";

    private final MTConfig config;

    public MTGame() {
        super(IHyrame.get(),
                HyriMoutron.get(),
                HyriAPI.get().getConfig().isDevEnvironment() ? HyriAPI.get().getGameManager().createGameInfo(HyriMoutron.ID, HyriMoutron.NAME) : HyriAPI.get().getGameManager().getGameInfo(HyriMoutron.ID),
                MTPlayer.class,
                HyriAPI.get().getConfig().isDevEnvironment() ? MTGameType.SOLO : HyriGameType.getFromData(MTGameType.values()));
        this.description = MTMessage.GAME_DESCRIPTION.asLang();
        this.waitingRoom = new MTWaitingRoom(this);
        this.config = HyriMoutron.get().getConfiguration();

        this.registerTeams();
    }

    private void registerTeams() {
        for (MTTeam team : MTTeam.values()) {
            this.registerTeam(new HyriGameTeam(team.getName(), team.getColor().getDisplay(), team.getColor(), 1));
        }
    }

    @Override
    public void start() {
        super.start();

        // Teleport players to their spawn
        final List<LocationWrapper> spawns = this.config.getSpawns();

        int spawnIndex = 0;
        for (MTPlayer player : this.players) {
            player.spawn(spawns.get(spawnIndex).asBukkit());
            spawnIndex++;
        }

        // Start timer
        new BukkitRunnable() {

           private int index = 5;

           @Override
           public void run() {
               if (this.index == 0) {
                   this.alert(MTMessage.STARTING_GO::asString, Sound.ENDERDRAGON_GROWL, 1.0f, 0.5f);
                   this.cancel();

                   // Update sheep every tick
                   Bukkit.getScheduler().runTaskTimer(plugin, () -> updateSheeps(), 10L, 1L);
               } else if (this.index <= 3){
                   this.alert(target -> ChatColor.AQUA + String.valueOf(this.index), Sound.NOTE_PLING, 0.2f * (5 - this.index), 0.5f);
               } else {
                   this.alert(target -> ChatColor.DARK_AQUA + String.valueOf(this.index), Sound.NOTE_PLING, this.index == 5 ? 0.2f : 0.2f * (5 - this.index), 0.5f);
               }

               this.index--;
           }

           private void alert(Function<Player, String> message, Sound sound, float volume, float pitch) {
               for (MTPlayer gamePlayer : players) {
                   final Player player = gamePlayer.getPlayer();

                   player.playSound(player.getLocation(), sound, volume, pitch);
                   Title.sendTitle(player, message.apply(player), "", 0, 20, 0);
               }
           }

        }.runTaskTimer(this.plugin, 0L, 20L);
    }

    @Override
    public void win(HyriGameTeam winner) {
        if (winner == null) {
            return;
        }

        super.win(winner);

        for (MTPlayer player : this.players) {
            final Sheep sheep = player.getSheep();

            if (sheep == null) {
                continue;
            }

            sheep.remove();
        }
    }

    private void updateSheeps() {
        for (MTPlayer player : this.players) {
            player.updateSheep();
        }
    }

    public HyriGameTeam getWinner() {
        HyriGameTeam winner = null;
        for (HyriGameTeam team : this.teams) {
            if (team.hasPlayersPlaying()) { // Check if team has players playing
                if (winner != null) { // If a winner has already been set, then 2 teams are playing yet
                    return null;
                }

                winner = team;
            }
        }
        return winner;
    }

}
