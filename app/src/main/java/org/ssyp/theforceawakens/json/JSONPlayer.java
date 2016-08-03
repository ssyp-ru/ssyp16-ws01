package org.ssyp.theforceawakens.json;

import org.ssyp.theforceawakens.game.Player;
import org.ssyp.theforceawakens.game.PlayerState;
import org.ssyp.theforceawakens.game.Position;
import org.ssyp.theforceawakens.game.Team;

public class JSONPlayer {
    private String name;
    private Team team;
    private long respawnTime;

    public String getName() {
        return name;
    }

    public Team getTeam() {
        return team;
    }

    public Position getPosition() {
        return position;
    }

    public long getRespawnTime() {
        return respawnTime;
    }

    public PlayerState getState() {
        return state;
    }

    private Position position;
    private PlayerState state;

    public JSONPlayer(String name, Team team, Position position, PlayerState state) {
        this.name = name;
        this.team = team;
        this.position = position;
        this.state = state;
    }

    public JSONPlayer(Player player) {
        this.name = player.getName();
        this.team = player.getTeam();
        this.position = player.getPosition();
        this.state = player.getPlayerState();
        this.respawnTime = player.getTimeToRespawn();
    }
}
