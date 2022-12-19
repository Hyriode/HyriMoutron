package fr.hyriode.moutron.listener;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameSpectator;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.event.player.HyriGameLeaveEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameSpectatorEvent;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.moutron.HyriMoutron;
import fr.hyriode.moutron.game.MTGame;
import fr.hyriode.moutron.game.MTPlayer;
import fr.hyriode.moutron.game.scoreboard.MTScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AstFaster
 * on 17/12/2022 at 11:59
 */
public class GameListener extends HyriListener<HyriMoutron> {

    public GameListener(HyriMoutron plugin) {
        super(plugin);

        HyriAPI.get().getEventBus().register(this);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        final Entity entity = event.getEntity();

        if (entity instanceof Sheep) {
            event.setCancelled(true);
            return;
        }

        final HyriGamePlayer gamePlayer = this.plugin.getGame().getPlayer(entity.getUniqueId());

        if (gamePlayer != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        final Entity entity = event.getEntity();

        if (entity instanceof Player) {
            final HyriGamePlayer gamePlayer = this.plugin.getGame().getPlayer(entity.getUniqueId());

            if (gamePlayer != null) {
                Bukkit.getScheduler().runTaskLater(this.plugin, () -> event.getDismounted().setPassenger(entity), 1L);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        final Entity entity = event.getEntity();
        final int radius = entity.hasMetadata(MTGame.TNT_RADIUS_METADATA) ? entity.getMetadata(MTGame.TNT_RADIUS_METADATA).get(0).asInt() : 5;

        for (Block block : this.generateSphere(entity.getLocation(), radius)) {
            final FallingBlock fallingBlock = IHyrame.WORLD.get().spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
            final float x = (float) (Math.random());
            final float y = (float) (Math.random());
            final float z = (float) (Math.random());

            fallingBlock.setVelocity(new Vector(x, y, z));
            fallingBlock.setDropItem(false);
            fallingBlock.setMetadata(MTGame.BLOCK_METADATA, new FixedMetadataValue(HyriMoutron.get(), true));

            block.setType(Material.AIR);
        }

        event.blockList().clear();
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockChange(EntityChangeBlockEvent event) {
        final Entity entity = event.getEntity();

        if (entity.hasMetadata(MTGame.BLOCK_METADATA)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final MTPlayer gamePlayer = this.plugin.getGame().getPlayer(player);

        if (gamePlayer == null) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        final MTPlayer gamePlayer = this.plugin.getGame().getPlayer(player);

        if (gamePlayer == null) {
            return;
        }

        event.setCancelled(true);
    }

    @HyriEventHandler
    public void onSpectator(HyriGameSpectatorEvent event) {
        final HyriGameSpectator spectator = event.getSpectator();
        final Player player = spectator.getPlayer();

        player.teleport(this.plugin.getConfiguration().getWaitingRoom().getSpawn().asBukkit());

        if (!(spectator instanceof HyriGamePlayer)) { // Player is an outside spectator
            new MTScoreboard(player).show();
        }
    }

    @HyriEventHandler
    public void onLeave(HyriGameLeaveEvent event) {
        if (event.getGame().getState() == HyriGameState.PLAYING) {
            final MTPlayer gamePlayer = event.getGamePlayer().cast();

            if (!gamePlayer.isSpectator()) {
                gamePlayer.lose();
            }
        }
    }

    private List<Block> generateSphere(Location center, int radius) {
        final List<Block> blocks = new ArrayList<>();
        final int bX = center.getBlockX();
        final int bY = center.getBlockY();
        final int bZ = center.getBlockZ();

        for (int x = bX - radius; x <= bX + radius; x++) {
            for (int y = bY - radius; y <= bY + radius; y++) {
                for (int z = bZ - radius; z <= bZ + radius; z++) {
                    final double distance = (x - bX) * (x - bX) + (y - bY) * (y - bY) + (z - bZ) * (z - bZ);

                    if (distance <= radius * radius) {
                        final Block block = center.getWorld().getBlockAt(x, y, z);

                        if (block.hasMetadata(MTGame.BLOCK_METADATA)) {
                            blocks.add(block);
                        }
                    }
                }
            }
        }
        return blocks;
    }

}
