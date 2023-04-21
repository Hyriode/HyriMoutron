package fr.hyriode.moutron.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.leaderboard.IHyriLeaderboardProvider;
import fr.hyriode.api.leveling.NetworkLeveling;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.HyriGameType;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.util.HyriGameMessages;
import fr.hyriode.hyrame.game.util.HyriRewardAlgorithm;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.LocationWrapper;
import fr.hyriode.moutron.HyriMoutron;
import fr.hyriode.moutron.api.MTStatistics;
import fr.hyriode.moutron.config.MTConfig;
import fr.hyriode.moutron.language.MTMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.function.Function;

/**
 * Created by AstFaster
 * on 17/12/2022 at 09:53
 */
public class MTGame extends HyriGame<MTPlayer> {

    public static final String BLOCK_METADATA = "MoutronBlock";
    public static final String TNT_RADIUS_METADATA = "MoutronTNTRadius";

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
    public void handleLogin(Player player) {
        super.handleLogin(player);

        final MTPlayer gamePlayer = this.getPlayer(player);
        final MTStatistics statistics = MTStatistics.get(gamePlayer.getUniqueId());

        gamePlayer.setStatistics(statistics);
    }

    @Override
    public void handleLogout(Player player) {
        super.handleLogout(player);

        final MTPlayer gamePlayer = this.getPlayer(player);
        final IHyriPlayer account = gamePlayer.asHyriPlayer();
        final MTStatistics statistics = gamePlayer.getStatistics();
        final MTStatistics.Data statisticsData = statistics.getData(this.getType());

        if (!this.getState().isAccessible()) {
            statisticsData.addGamesPlayed(1);
            statisticsData.addKills(gamePlayer.getKills());
        }

        statistics.update(account);

        if (this.getState() == HyriGameState.PLAYING) {
            if (!gamePlayer.isSpectator()) {
                gamePlayer.lose();
            }
        }
    }

    @Override
    public void start() {
        super.start();

        // Teleport players to their spawn
        final List<LocationWrapper> spawns = this.config.getSpawns();

        Collections.shuffle(spawns);

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
                   Bukkit.getScheduler().runTaskTimer(plugin, () -> updateSheeps(), 0L, 1L);
                   HyriMoutron.get().getPowerUpHandler().start();
               } else if (this.index <= 3){
                   this.alert(target -> ChatColor.AQUA + String.valueOf(this.index), Sound.NOTE_PLING, 0.2f * (5 - this.index), 0.5f);
               } else {
                   this.alert(target -> ChatColor.DARK_AQUA + String.valueOf(this.index), Sound.NOTE_PLING, this.index == 5 ? 0.2f : 0.2f * (5 - this.index), 0.5f);
               }

               this.index--;
           }

           private void alert(Function<Player, String> message, Sound sound, float volume, float pitch) {
               for (MTPlayer gamePlayer : players) {
                   if (!gamePlayer.isOnline()) {
                       continue;
                   }

                   final Player player = gamePlayer.getPlayer();

                   player.playSound(player.getLocation(), sound, volume, pitch);
                   Title.sendTitle(player, message.apply(player), "", 0, 25, 0);
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

        for (HyriGamePlayer player : winner.getPlayers()) {
            final MTPlayer gamePlayer = player.cast();

            gamePlayer.getStatistics().getData(this.getType()).addVictories(1);
        }

        this.sendWinMessage(winner);
    }

    private void sendWinMessage(HyriGameTeam winner) {
        final List<HyriLanguageMessage> positions = Arrays.asList(
                HyriLanguageMessage.get("message.game.end.1"),
                HyriLanguageMessage.get("message.game.end.2"),
                HyriLanguageMessage.get("message.game.end.3")
        );

        final List<MTPlayer> top = new ArrayList<>(this.players);

        top.sort(Comparator.comparingInt(MTPlayer::getPosition));

        final Function<Player, List<String>> positionLinesProvider = player -> {
            final List<String> positionLines = new ArrayList<>();

            for (int i = 0; i <= 2; i++) {
                final String positionLine = HyriLanguageMessage.get("message.game.end.position").getValue(player).replace("%position%", positions.get(i).getValue(player));

                if (top.size() > i){
                    final MTPlayer topPlayer = top.get(i);

                    positionLines.add(positionLine.replace("%player%", topPlayer.formatNameWithTeam()));
                    continue;
                }

                positionLines.add(positionLine.replace("%player%", HyriLanguageMessage.get("message.game.end.nobody").getValue(player)));
            }
            return positionLines;
        };

        // Send message to not-playing players
        for (Player player : Bukkit.getOnlinePlayers()) {
            final MTPlayer gamePlayer = this.getPlayer(player);

            if (gamePlayer == null) {
                player.spigot().sendMessage(HyriGameMessages.createWinMessage(this, player, winner, positionLinesProvider.apply(player), null));
            }
        }

        for (MTPlayer gamePlayer : this.players) {
            final IHyriPlayer account = gamePlayer.asHyriPlayer();
            final UUID playerId = gamePlayer.getUniqueId();
            final int kills = gamePlayer.getKills();
            final boolean isWinner = winner.contains(gamePlayer);
            final long hyris = account.getHyris().add(HyriRewardAlgorithm.getHyris(kills, gamePlayer.getPlayTime(), isWinner)).withMessage(false).exec();
            final double xp = account.getNetworkLeveling().addExperience(HyriRewardAlgorithm.getXP(kills, gamePlayer.getPlayTime(), isWinner));

            // Update leaderboards
            final IHyriLeaderboardProvider provider = HyriAPI.get().getLeaderboardProvider();

            provider.getLeaderboard(NetworkLeveling.LEADERBOARD_TYPE, "rotating-game-experience").incrementScore(playerId, xp);
            provider.getLeaderboard(HyriMoutron.ID, "kills").incrementScore(playerId, kills);

            if (isWinner) {
                provider.getLeaderboard(HyriMoutron.ID, "victories").incrementScore(playerId, 1);
            }

            account.update();

            // Send message
            final String rewardsLine = ChatColor.LIGHT_PURPLE + "+" + hyris + " Hyris " + ChatColor.GREEN + "+" + xp + " XP";

            if (gamePlayer.isOnline()) {
                final Player player = gamePlayer.getPlayer();

                player.spigot().sendMessage(HyriGameMessages.createWinMessage(this, gamePlayer.getPlayer(), winner, positionLinesProvider.apply(player), rewardsLine));
            } else if (HyriAPI.get().getPlayerManager().isOnline(playerId)) {
                HyriAPI.get().getPlayerManager().sendMessage(playerId, HyriGameMessages.createOfflineWinMessage(this, account, rewardsLine));
            }
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

    @Override
    public MTGameType getType() {
        return (MTGameType) super.getType();
    }

}
