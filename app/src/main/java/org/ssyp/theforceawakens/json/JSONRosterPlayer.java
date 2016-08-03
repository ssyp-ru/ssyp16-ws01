package org.ssyp.theforceawakens.json;

import org.ssyp.theforceawakens.game.Player;
import org.ssyp.theforceawakens.game.Team;

public class JSONRosterPlayer {
    private String name;
    private Team team;

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getName() {
        return name;
    }

    public Team getTeam() {
        return team;
    }

    public JSONRosterPlayer(Player player) {
        this.name = player.getName();
        this.team = player.getTeam();
    }

    @Override
    public String toString() {
        return this.name + " " + this.team;
    }
}
