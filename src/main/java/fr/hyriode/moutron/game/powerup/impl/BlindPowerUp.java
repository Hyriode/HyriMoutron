package fr.hyriode.moutron.game.powerup.impl;

import fr.hyriode.moutron.HyriMoutron;
import fr.hyriode.moutron.game.MTPlayer;
import fr.hyriode.moutron.game.powerup.MTPowerUp;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by AstFaster
 * on 19/12/2022 at 10:02
 */
public class BlindPowerUp extends MTPowerUp {

    private static final int MAX_TIME = 8 * 20;

    public BlindPowerUp(HyriMoutron plugin) {
        super(plugin, "blind", new ItemStack(Material.SPIDER_EYE));
    }

    @Override
    public void handle(MTPlayer gamePlayer) {
        // TODO Add particles

        new BukkitRunnable() {

            private int index = 0;

            @Override
            public void run() {
                if (this.index >= MAX_TIME) {
                    this.cancel();
                    return;
                }

                final Location location = gamePlayer.getPlayer().getLocation();

                for (MTPlayer gameTarget : HyriMoutron.get().getGame().getPlayers()) {
                    final Player target = gameTarget.getPlayer();

                    if (gameTarget == gamePlayer || gameTarget.isSpectator() || target.getLocation().distance(location) >= 10.0D) {
                        continue;
                    }

                    target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 20, 0));
                }

                this.index += 5;
            }
        }.runTaskTimer(HyriMoutron.get(), 0L, 5L);
    }

}
