package fr.hyriode.moutron.util;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.packet.PacketUtil;
import fr.hyriode.moutron.HyriMoutron;
import fr.hyriode.moutron.game.MTGame;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by AstFaster
 * on 19/12/2022 at 13:22
 */
public class WorldUtil {

    public static void createExplosion(Location location, int radius) {
        final TNTPrimed tnt = (TNTPrimed) IHyrame.WORLD.get().spawnEntity(location, EntityType.PRIMED_TNT);

        PacketUtil.sendPacket(new PacketPlayOutEntityDestroy(tnt.getEntityId()));

        tnt.setMetadata(MTGame.TNT_RADIUS_METADATA, new FixedMetadataValue(HyriMoutron.get(), radius));
        tnt.setFuseTicks(0);
    }

}
