package fr.hyriode.moutron.game.powerup.impl;

import fr.hyriode.hyrame.title.Title;
import fr.hyriode.moutron.HyriMoutron;
import fr.hyriode.moutron.game.MTPlayer;
import fr.hyriode.moutron.game.powerup.MTPowerUp;
import fr.hyriode.moutron.language.MTMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by AstFaster
 * on 19/12/2022 at 12:23
 */
public class TeleportationPowerUp extends MTPowerUp {

    public TeleportationPowerUp(HyriMoutron plugin) {
        super(plugin, "teleportation", new ItemStack(Material.ENDER_PEARL));
    }

    @Override
    public void handle(MTPlayer gamePlayer) {
        final MTPlayer gameTarget = this.getRandomPlayer(gamePlayer);
        final Player player = gamePlayer.getPlayer();
        final Player target = gameTarget.getPlayer();

        Title.sendTitle(target, MTMessage.POWER_UP_TELEPORTATION_TITLE.asString(target), MTMessage.POWER_UP_TELEPORTATION_SUBTITLE.asString(target).replace("%player%", gamePlayer.formatNameWithTeam()), 5, 60, 5);
        Title.sendTitle(player, MTMessage.POWER_UP_TELEPORTATION_TITLE.asString(player), MTMessage.POWER_UP_TELEPORTATION_SUBTITLE.asString(player).replace("%player%", gameTarget.formatNameWithTeam()), 5, 60, 5);

        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0f, 1.2f);
        target.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0f, 1.2f);

        // TODO Add particles

        gamePlayer.setForcedLocation(gameTarget.getSheep().getLocation());
        gameTarget.setForcedLocation(gamePlayer.getSheep().getLocation());
    }

    private MTPlayer getRandomPlayer(MTPlayer exception) {
        final List<MTPlayer> players = HyriMoutron.get().getGame().getPlayers();

        MTPlayer gamePlayer = null;
        while (gamePlayer == null) {
            gamePlayer = players.get(ThreadLocalRandom.current().nextInt(0, players.size()));

            if (gamePlayer.isSpectator() || gamePlayer == exception) {
                gamePlayer = null;
            }
        }
        return gamePlayer;
    }

}
