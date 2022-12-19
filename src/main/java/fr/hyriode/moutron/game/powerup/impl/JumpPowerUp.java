package fr.hyriode.moutron.game.powerup.impl;

import fr.hyriode.moutron.HyriMoutron;
import fr.hyriode.moutron.game.MTPlayer;
import fr.hyriode.moutron.game.powerup.MTPowerUp;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;

/**
 * Created by AstFaster
 * on 19/12/2022 at 10:53
 */
public class JumpPowerUp extends MTPowerUp {

    public JumpPowerUp(HyriMoutron plugin) {
        super(plugin, "jump", new ItemStack(Material.FEATHER));
    }

    @Override
    public void handle(MTPlayer gamePlayer) {
        final Player player = gamePlayer.getPlayer();
        final Sheep sheep = gamePlayer.getSheep();

        player.playSound(player.getLocation(), Sound.ENDERDRAGON_WINGS, 2.0f, 1.5f);
        sheep.setVelocity(sheep.getLocation().getDirection().setY(1.0D));

        // TODO Add particles
    }

}
