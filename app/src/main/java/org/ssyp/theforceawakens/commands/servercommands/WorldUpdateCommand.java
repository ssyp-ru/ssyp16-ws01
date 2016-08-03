package org.ssyp.theforceawakens.commands.servercommands;

import org.ssyp.theforceawakens.commands.Command;
import org.ssyp.theforceawakens.game.Checkpoint;
import org.ssyp.theforceawakens.game.Player;
import org.ssyp.theforceawakens.json.JSONCheckpoint;
import org.ssyp.theforceawakens.json.JSONPlayer;

import java.util.ArrayList;
import java.util.List;

public class WorldUpdateCommand extends Command {
    public List<JSONPlayer> players = new ArrayList<>();
    public List<JSONCheckpoint> checkpoints = new ArrayList<>();
    public final static String WORLD_UPDATE_COMMAND = "world_update";

    public WorldUpdateCommand(List<Player> players, List<Checkpoint> checkpoints) {
        super(WORLD_UPDATE_COMMAND);

        for (Player p : players) {
            this.players.add(new JSONPlayer(p));
        }

        for (Checkpoint c : checkpoints) {
            this.checkpoints.add(new JSONCheckpoint(c));
        }
    }
}
