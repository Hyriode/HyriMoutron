package fr.hyriode.moutron.game;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.packet.PacketUtil;
import fr.hyriode.hyrame.reflection.Reflection;
import fr.hyriode.hyrame.utils.BroadcastUtil;
import fr.hyriode.moutron.HyriMoutron;
import fr.hyriode.moutron.game.scoreboard.MTScoreboard;
import fr.hyriode.moutron.language.MTMessage;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSheep;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;

/**
 * Created by AstFaster
 * on 17/12/2022 at 09:54
 */
public class MTPlayer extends HyriGamePlayer {

    private Sheep sheep;
    private EntitySheep nmsSheep;

    private final MTGame game;

    public MTPlayer(Player player) {
        super(player);
        this.game = HyriMoutron.get().getGame();
    }

    public void spawn(Location spawn) {
        // Show scoreboard
        new MTScoreboard(this.player).show();

        // Teleport to his spawn
        this.player.teleport(spawn);

        // Spawn the sheep
        this.sheep = (Sheep) IHyrame.WORLD.get().spawnEntity(spawn, EntityType.SHEEP);
        this.sheep.setColor(this.getTeam().getColor().getDyeColor());
        this.sheep.setPassenger(this.player);
        this.nmsSheep = ((CraftSheep) this.sheep).getHandle();

        // Remove pathfinders
        Reflection.setField("b", this.nmsSheep.goalSelector, new ArrayList<>());
        Reflection.setField("c", this.nmsSheep.goalSelector, new ArrayList<>());
    }

    @SuppressWarnings("deprecation")
    public void updateSheep() {
        if (!this.isOnline() || this.isSpectator() || this.sheep == null) {
            return;
        }

        final Location sheepLocation = this.sheep.getLocation();
        final Location newLocation = sheepLocation.clone();

        newLocation.add(sheepLocation.getDirection().multiply(0.85D));

        final Block locationBlock = IHyrame.WORLD.get().getBlockAt(newLocation);

        if (locationBlock.getType() != Material.AIR) {
            // Check for stairs made from blocks
            final Block upperBlock = locationBlock.getRelative(BlockFace.UP);

            if (upperBlock.getType() == Material.AIR) {
                newLocation.setY(upperBlock.getY());
            } else { // The sheep will collide a wall, so the player is eliminated
                this.setSpectator(true);
                this.lose();
                return;
            }
        }

        final NavigationAbstract navigation = this.nmsSheep.getNavigation();
        final PathEntity path = navigation.a(newLocation.getX(), newLocation.getY(), newLocation.getZ()); // Create a path to the location

        this.nmsSheep.setPositionRotation(newLocation.getX(), newLocation.getY(), newLocation.getZ(), this.player.getLocation().getYaw(), 0); // Set entity track player's yaw

        navigation.a(path, 1.0f); // Move the entity to the created path with a given speed

        // Place glass blocks
        Bukkit.getScheduler().runTaskLater(HyriMoutron.get(), () -> {
            final Block lastLocationBlock = IHyrame.WORLD.get().getBlockAt(newLocation);
            final Block upperBlock = lastLocationBlock.getRelative(BlockFace.UP);

            if (lastLocationBlock.getType() == Material.AIR && upperBlock.getType() == Material.AIR) {
                // Set to glass
                lastLocationBlock.setType(Material.STAINED_GLASS);
                upperBlock.setType(Material.STAINED_GLASS);

                // Apply color
                final byte color = this.getTeam().getColor().getDyeColor().getData();

                lastLocationBlock.setData(color);
                upperBlock.setData(color);

                // Add metadata
                final FixedMetadataValue metadata = new FixedMetadataValue(HyriMoutron.get(), this.uniqueId);

                lastLocationBlock.setMetadata(MTGame.BLOCK_METADATA, metadata);
                upperBlock.setMetadata(MTGame.BLOCK_METADATA, metadata);
            }
        }, 6L);
    }

    public void lose() {
        this.explode();

        this.sheep.remove();
        this.sheep = null;

        IHyrame.get().getScoreboardManager().getScoreboards(MTScoreboard.class).forEach(MTScoreboard::update);

        BroadcastUtil.broadcast(target -> MTMessage.PLAYER_ELIMINATED.asString(target).replace("%player%", this.formatNameWithTeam()));

        this.game.win(this.game.getWinner());
    }

    private void explode() {
        final Location location = this.sheep.getLocation();
        final TNTPrimed tnt = (TNTPrimed) IHyrame.WORLD.get().spawnEntity(location, EntityType.PRIMED_TNT);

        PacketUtil.sendPacket(new PacketPlayOutEntityDestroy(tnt.getEntityId()));

        tnt.setFuseTicks(0);
    }

    public Sheep getSheep() {
        return this.sheep;
    }

}
