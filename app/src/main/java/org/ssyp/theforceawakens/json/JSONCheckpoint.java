package org.ssyp.theforceawakens.json;

import org.ssyp.theforceawakens.game.Checkpoint;
import org.ssyp.theforceawakens.game.CheckpointType;
import org.ssyp.theforceawakens.game.Position;
import org.ssyp.theforceawakens.game.Team;

public class JSONCheckpoint {
    private String name;

    public String getName() {
        return name;
    }

    public Position getPosition() {
        return position;
    }

    public CheckpointType getType() {
        return type;
    }

    public Team getAllegiance() {
        return allegiance;
    }

    public Team getCapturingTeam() {
        return capturingTeam;
    }

    public int getHp() {
        return hp;
    }

    private Position position;
    private CheckpointType type;
    private Team allegiance;
    private Team capturingTeam;
    private int hp;

    public JSONCheckpoint(Checkpoint checkpoint) {
        this.name = checkpoint.getName();
        this.position = checkpoint.getPosition();
        this.type = checkpoint.getType();
        this.allegiance = checkpoint.getAllegiance();
        this.capturingTeam = checkpoint.getCapturingTeam();
        this.hp = checkpoint.getHP();
    }
}
