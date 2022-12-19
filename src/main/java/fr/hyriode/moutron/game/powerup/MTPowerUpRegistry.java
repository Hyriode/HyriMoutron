package fr.hyriode.moutron.game.powerup;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.moutron.HyriMoutron;
import fr.hyriode.moutron.game.powerup.impl.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by AstFaster
 * on 19/12/2022 at 09:43
 */
public class MTPowerUpRegistry {

    private final Map<String, MTPowerUp> powerUps = new HashMap<>();

    public MTPowerUpRegistry() {
        this.registerPowerUp(SpeedPowerUp.class);
        this.registerPowerUp(JumpPowerUp.class);
        this.registerPowerUp(BlindPowerUp.class);
        this.registerPowerUp(ExplosionPowerUp.class);
        this.registerPowerUp(TeleportationPowerUp.class);
    }

    public void registerPowerUp(Class<? extends MTPowerUp> powerUp) {
        this.powerUps.put(powerUp.getName(), IHyrame.get().getItemManager().getItem(powerUp));

        HyriMoutron.log("Registered '" + powerUp.getName() + "' power-up.");
    }

    public MTPowerUp getPowerUp(String name) {
        return this.powerUps.get(name);
    }

    public Map<String, MTPowerUp> getPowerUps() {
        return Collections.unmodifiableMap(this.powerUps);
    }

}
