package org.ssyp.theforceawakens.commands.servercommands;

import org.ssyp.theforceawakens.commands.Command;
import org.ssyp.theforceawakens.game.Team;

public class GameEndedCommand extends Command {
    public final static String GAME_ENDED_COMMAND = "game_ended";
    public Team wonTeam;

    public GameEndedCommand(Team wonTeam) {
        super(GAME_ENDED_COMMAND);
        this.wonTeam = wonTeam;
    }
}
