package fr.hyriode.moutron.game;

import fr.hyriode.hyrame.game.team.HyriGameTeamColor;

/**
 * Project: HyriPearlControl
 * Created by AstFaster
 * on 04/02/2022 at 21:13
 */
public enum MTTeam {

    BLUE("blue", HyriGameTeamColor.BLUE),
    RED("red", HyriGameTeamColor.RED),
    GREEN("green", HyriGameTeamColor.GREEN),
    YELLOW("yellow", HyriGameTeamColor.YELLOW),
    AQUA("aqua", HyriGameTeamColor.CYAN),
    PINK("pink", HyriGameTeamColor.PINK),
    WHITE("white", HyriGameTeamColor.WHITE),
    GRAY("gray", HyriGameTeamColor.GRAY),

    ;

    private final String teamName;
    private final HyriGameTeamColor teamColor;

    MTTeam(String teamName, HyriGameTeamColor teamColor) {
        this.teamName = teamName;
        this.teamColor = teamColor;
    }

    public String getName() {
        return this.teamName;
    }

    public HyriGameTeamColor getColor() {
        return this.teamColor;
    }

}
