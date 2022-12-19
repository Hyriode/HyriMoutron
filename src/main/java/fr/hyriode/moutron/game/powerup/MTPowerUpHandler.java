package fr.hyriode.moutron.game.powerup;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.moutron.HyriMoutron;
import fr.hyriode.moutron.game.MTPlayer;
import fr.hyriode.moutron.game.scoreboard.MTScoreboard;
import fr.hyriode.moutron.language.MTMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by AstFaster
 * on 19/12/2022 at 09:59
 */
public class MTPowerUpHandler {

    private static final int TIME = 15;

    private int timer = TIME;

    private final MTPowerUpRegistry registry;

    public MTPowerUpHandler() {
        this.registry = new MTPowerUpRegistry();
    }

    public void start() {
        Bukkit.getScheduler().runTaskTimer(HyriMoutron.get(), () -> {
            if (this.timer == 0) {
                final List<MTPowerUp> powerUps = new ArrayList<>(this.registry.getPowerUps().values());

                for (MTPlayer gamePlayer : HyriMoutron.get().getGame().getPlayers()) {
                    if (gamePlayer.isSpectator() || gamePlayer.getPowerUp() != null) {
                        continue;
                    }

                    final MTPowerUp powerUp = powerUps.get(ThreadLocalRandom.current().nextInt(0, powerUps.size()));
                    final Player player = gamePlayer.getPlayer();

                    IHyrame.get().getItemManager().giveItem(player, 4, powerUp.getName());

                    player.getInventory().setHeldItemSlot(4);
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                    gamePlayer.setPowerUp(powerUp);

                    Title.sendTitle(player, String.valueOf(ChatColor.AQUA) + ChatColor.MAGIC + "||" + ChatColor.RESET + " " + ChatColor.AQUA + powerUp.getDisplay().get().getValue(player) + " " + ChatColor.MAGIC + "||", MTMessage.POWER_UP_NEW_SUBTITLE.asString(player), 5, 40, 5);
                }

                this.timer = TIME;
            }

            IHyrame.get().getScoreboardManager().getScoreboards(MTScoreboard.class).forEach(MTScoreboard::update);

            this.timer--;
        }, 0L, 20L);
    }

    public MTPowerUpRegistry getRegistry() {
        return this.registry;
    }

    public int getTimer() {
        return this.timer;
    }

}
