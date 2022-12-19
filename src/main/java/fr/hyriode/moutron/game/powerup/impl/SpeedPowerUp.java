package fr.hyriode.moutron.game.powerup.impl;

import fr.hyriode.moutron.HyriMoutron;
import fr.hyriode.moutron.game.MTPlayer;
import fr.hyriode.moutron.game.powerup.MTPowerUp;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by AstFaster
 * on 19/12/2022 at 10:02
 */
public class SpeedPowerUp extends MTPowerUp {

    public SpeedPowerUp(HyriMoutron plugin) {
        super(plugin, "speed", new ItemStack(Material.IRON_BOOTS));
    }

    @Override
    public void handle(MTPlayer gamePlayer) {
        gamePlayer.setSpeed(1.7D);
        gamePlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 1, true, true));

        // TODO Add particles

        Bukkit.getScheduler().runTaskLaterAsynchronously(HyriMoutron.get(), gamePlayer::resetSpeed, 5 * 20L);
    }

}
