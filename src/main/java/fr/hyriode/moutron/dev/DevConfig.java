package fr.hyriode.moutron.dev;

import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.utils.LocationWrapper;
import fr.hyriode.moutron.config.MTConfig;

import java.util.Arrays;

/**
 * Created by AstFaster
 * on 23/07/2022 at 11:49
 */
public class DevConfig extends MTConfig {

    public DevConfig() {
        super(new HyriWaitingRoom.Config(
                new LocationWrapper(0.5, 201, 0.5, 0, 0),
                new LocationWrapper(3, 205, 3),
                new LocationWrapper(-3, 196, -3),
                new LocationWrapper(-1.5, 201, 2.5, 135, 0)
        ), Arrays.asList(
                new LocationWrapper(0.5, 121, -29.5, 0, 0),
                new LocationWrapper(0.5, 121, 29.5, 180, 0),
                new LocationWrapper(30.5, 121, 30.5, 135, 0),
                new LocationWrapper(-29.5, 121, 30.5, -135, 0),
                new LocationWrapper(30.5, 121, 0, 90, 0),
                new LocationWrapper(-29.5, 121, 0.5, -90, 0),
                new LocationWrapper(-29.5, 121, -29.5, -45, 0),
                new LocationWrapper(30.5, 121, -29.5, 45, 0)
        ));
    }

}
