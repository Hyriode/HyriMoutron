package fr.hyriode.moutron.game.powerup.impl;

import fr.hyriode.moutron.HyriMoutron;
import fr.hyriode.moutron.game.MTPlayer;
import fr.hyriode.moutron.game.powerup.MTPowerUp;
import fr.hyriode.moutron.util.WorldUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;

/**
 * Created by AstFaster
 * on 19/12/2022 at 12:23
 */
public class ExplosionPowerUp extends MTPowerUp {

    public ExplosionPowerUp(HyriMoutron plugin) {
        super(plugin, "explosion", new ItemStack(Material.TNT));
    }

    @Override
    public void handle(MTPlayer gamePlayer) {
        final Sheep sheep = gamePlayer.getSheep();
        final Location sheepLocation = sheep.getLocation();
        final Location tntLocation = sheepLocation.clone().add(sheepLocation.getDirection().multiply(0.85D * 2));

        WorldUtil.createExplosion(tntLocation, 10);

        // TODO Add particles
    }

}
