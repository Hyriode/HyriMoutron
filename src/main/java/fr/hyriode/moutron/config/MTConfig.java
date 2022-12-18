package fr.hyriode.moutron.config;

import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.utils.LocationWrapper;
import fr.hyriode.hystia.api.config.IConfig;

import java.util.List;

/**
 * Created by AstFaster
 * on 23/07/2022 at 11:48
 */
public class MTConfig implements IConfig {

    private final HyriWaitingRoom.Config waitingRoom;
    private final List<LocationWrapper> spawns;

    public MTConfig(HyriWaitingRoom.Config waitingRoom, List<LocationWrapper> spawns) {
        this.waitingRoom = waitingRoom;
        this.spawns = spawns;
    }

    public HyriWaitingRoom.Config getWaitingRoom() {
        return this.waitingRoom;
    }

    public List<LocationWrapper> getSpawns() {
        return this.spawns;
    }

}
