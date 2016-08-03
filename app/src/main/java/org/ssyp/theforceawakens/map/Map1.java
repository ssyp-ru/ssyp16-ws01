package org.ssyp.theforceawakens.map;

import org.ssyp.theforceawakens.game.Checkpoint;
import org.ssyp.theforceawakens.game.Position;

import java.util.ArrayList;
import java.util.List;

import static org.ssyp.theforceawakens.game.CheckpointType.Base;
import static org.ssyp.theforceawakens.game.CheckpointType.Checkpoint;
import static org.ssyp.theforceawakens.game.Team.Jedi;
import static org.ssyp.theforceawakens.game.Team.Neutral;
import static org.ssyp.theforceawakens.game.Team.Sith;

public class Map1 {
    public static final int JEDI_BASE = 6;
    public static final int SITH_BASE = 0;
    private static Checkpoint[] checkpoints = null;

    public static Checkpoint[] getCheckpoints() {
        if (checkpoints == null) {
            Checkpoint[] checkpoints = {
                    new Checkpoint("Sith base", null, Sith, Base, new Position(54.614788, 82.728169)),          // 0
                    new Checkpoint("Sith left", null, Neutral, Checkpoint, new Position(54.614266, 82.727594)),    // 1
                    new Checkpoint("Sith right", null, Neutral, Checkpoint, new Position(54.614187, 82.728151)),   // 2
                    new Checkpoint("Center", null, Neutral, Checkpoint, new Position(54.614010, 82.727908)),    // 3
                    new Checkpoint("Jedi left", null, Jedi, Checkpoint, new Position(54.613811, 82.727376)),    // 4
                    new Checkpoint("Jedi right", null, Jedi, Checkpoint, new Position(54.613737, 82.727978)),   // 5
                    new Checkpoint("Jedi base", null, Jedi, Base, new Position(54.613483, 82.727591))           // 6
            };

            List<Checkpoint> sithBaseNeighbours = new ArrayList<>();
            sithBaseNeighbours.add(checkpoints[1]);
            sithBaseNeighbours.add(checkpoints[2]);
            checkpoints[0].setNeighbours(sithBaseNeighbours);

            List<Checkpoint> sithLeftNeighbours = new ArrayList<>();
            sithLeftNeighbours.add(checkpoints[0]);
            sithLeftNeighbours.add(checkpoints[3]);
            checkpoints[1].setNeighbours(sithLeftNeighbours);

            List<Checkpoint> sithRightNeighbours = new ArrayList<>();
            sithRightNeighbours.add(checkpoints[0]);
            sithRightNeighbours.add(checkpoints[3]);
            checkpoints[2].setNeighbours(sithRightNeighbours);

            List<Checkpoint> centerNeighbours = new ArrayList<>();
            centerNeighbours.add(checkpoints[1]);
            centerNeighbours.add(checkpoints[2]);
            centerNeighbours.add(checkpoints[4]);
            centerNeighbours.add(checkpoints[5]);
            checkpoints[3].setNeighbours(centerNeighbours);

            List<Checkpoint> jediLeftNeighbours = new ArrayList<>();
            jediLeftNeighbours.add(checkpoints[3]);
            jediLeftNeighbours.add(checkpoints[6]);
            checkpoints[4].setNeighbours(jediLeftNeighbours);

            List<Checkpoint> jediRightNeighbours = new ArrayList<>();
            jediRightNeighbours.add(checkpoints[3]);
            jediRightNeighbours.add(checkpoints[6]);
            checkpoints[5].setNeighbours(jediRightNeighbours);

            List<Checkpoint> jediBaseNeighbours = new ArrayList<>();
            jediBaseNeighbours.add(checkpoints[4]);
            jediBaseNeighbours.add(checkpoints[5]);
            checkpoints[6].setNeighbours(jediBaseNeighbours);

            Map1.checkpoints = checkpoints;
        }

        return checkpoints;
    }

    public static void resetCheckpoints(){
        checkpoints = null;
        getCheckpoints();
    }
}
