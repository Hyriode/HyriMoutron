package fr.hyriode.moutron.game;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameSpectator;
import fr.hyriode.hyrame.packet.PacketUtil;
import fr.hyriode.hyrame.reflection.Reflection;
import fr.hyriode.hyrame.utils.BroadcastUtil;
import fr.hyriode.hyrame.utils.block.BlockUtil;
import fr.hyriode.moutron.HyriMoutron;
import fr.hyriode.moutron.api.MTStatistics;
import fr.hyriode.moutron.game.powerup.MTPowerUp;
import fr.hyriode.moutron.game.scoreboard.MTScoreboard;
import fr.hyriode.moutron.language.MTMessage;
import fr.hyriode.moutron.util.WorldUtil;
import net.minecraft.server.v1_8_R3.EntitySheep;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSheep;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by AstFaster
 * on 17/12/2022 at 09:54
 */
public class MTPlayer extends HyriGamePlayer {

    private MTStatistics statistics;

    private final List<Block> blocks = new ArrayList<>();

    private int position;

    private int kills;
    private MTPlayer killer;

    private MTPowerUp powerUp;
    private double speed = 1.0d;
    private Location forcedLocation;

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

        // Setup inventory
        final ItemStack barrier = new ItemStack(Material.BARRIER);
        final PlayerInventory inventory = this.player.getInventory();

        inventory.setChestplate(barrier);
        inventory.setLeggings(barrier);
        inventory.setBoots(barrier);

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

        if (this.forcedLocation != null) {
            this.nmsSheep.setPositionRotation(this.forcedLocation.getX(), this.forcedLocation.getY(), this.forcedLocation.getZ(), this.forcedLocation.getYaw(), 0); // Move the entity to the forced location
            this.forcedLocation = null;
            return;
        }

        this.sheep.setPassenger(this.player);

        final Location sheepLocation = this.sheep.getLocation();
        final Location newLocation = sheepLocation.clone();

        newLocation.add(sheepLocation.getDirection().multiply(0.85D * this.speed));

        final Block locationBlock = IHyrame.WORLD.get().getBlockAt(newLocation);

        if (locationBlock.getType() != Material.AIR) {
            // Check for stairs made from blocks
            final Block upperBlock = locationBlock.getRelative(BlockFace.UP);

            if (upperBlock.getType() == Material.AIR) {
                newLocation.setY(upperBlock.getY());
            } else { // The sheep will collide a wall, so the player is eliminated
                if (locationBlock.hasMetadata(MTGame.BLOCK_METADATA)) {
                    final UUID killerId = (UUID) locationBlock.getMetadata(MTGame.BLOCK_METADATA).get(0).value();

                    this.killer = this.game.getPlayer(killerId);

                    if (this.killer == this) {
                        this.killer = null;
                    }

                    if (this.killer != null) {
                        this.killer.kills++;
                    }
                }

                this.lose();
                return;
            }
        }

        PacketUtil.sendPacket(new PacketPlayOutEntityHeadRotation(this.nmsSheep, (byte) (this.player.getLocation().getYaw() * 256.0F / 360.0F))); // Set entity track player's yaw

        this.nmsSheep.setPositionRotation(newLocation.getX(), newLocation.getY(), newLocation.getZ(), this.player.getLocation().getYaw(), 0); // Move the entity

        // Place glass blocks
        Bukkit.getScheduler().runTaskLater(HyriMoutron.get(), () -> {
            if (this.isSpectator() || !this.isOnline()) {
                return;
            }

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

                // Save blocks
                this.blocks.add(lastLocationBlock);
                this.blocks.add(upperBlock);
            }
        }, 6L);
    }

    @SuppressWarnings("deprecation")
    public void lose() {
        this.setSpectator(true);

        this.position = (int) this.game.getPlayers().stream().filter(gamePlayer -> !gamePlayer.isSpectator()).count() + 1;

        WorldUtil.createExplosion(this.sheep.getLocation(), 5);

        this.sheep.remove();
        this.sheep = null;

        // Remove player's blocks
        for (Block block : this.blocks) {
            if (block.getType() == Material.STAINED_GLASS && block.getData() == this.getTeam().getColor().getDyeColor().getData()) {
                BlockUtil.setBlockFaster(block, 0, 0);
            }
        }

        this.blocks.clear();

        IHyrame.get().getScoreboardManager().getScoreboards(MTScoreboard.class).forEach(MTScoreboard::update);

        if (this.killer != null) {
            BroadcastUtil.broadcast(target -> MTMessage.PLAYER_ELIMINATED_KILL.asString(target)
                    .replace("%player%", this.formatNameWithTeam())
                    .replace("%killer%", this.formatNameWithTeam()));
        } else {
            BroadcastUtil.broadcast(target -> MTMessage.PLAYER_ELIMINATED.asString(target)
                    .replace("%player%", this.formatNameWithTeam()));
        }

        this.statistics.getData(this.game.getType()).addDeaths(1);
        this.game.win(this.game.getWinner());
    }

    public Sheep getSheep() {
        return this.sheep;
    }

    public MTPowerUp getPowerUp() {
        return this.powerUp;
    }

    public void setPowerUp(MTPowerUp powerUp) {
        this.powerUp = powerUp;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void resetSpeed() {
        this.speed = 1.0d;
    }

    public void setForcedLocation(Location forcedLocation) {
        this.forcedLocation = forcedLocation;
    }

    public MTStatistics getStatistics() {
        return this.statistics;
    }

    public void setStatistics(MTStatistics statistics) {
        this.statistics = statistics;
    }

    public int getKills() {
        return this.kills;
    }

    public int getPosition() {
        return this.position;
    }

}
