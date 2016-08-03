package org.ssyp.theforceawakens.game;

import org.ssyp.theforceawakens.commands.clientcommands.MsgCommand;
import org.ssyp.theforceawakens.connection.Connection;
import org.ssyp.theforceawakens.game.fight.Fight;
import org.ssyp.theforceawakens.game.fight.FightRespond;

import static org.ssyp.theforceawakens.game.PlayerState.Dead;
import static org.ssyp.theforceawakens.game.PlayerState.Fighting;
import static org.ssyp.theforceawakens.game.Team.Jedi;
import static org.ssyp.theforceawakens.game.Team.Sith;
import static org.ssyp.theforceawakens.game.World.*;

public class Player implements Positionable {
    private Position position;
    private String name;
    private Team team;
    private PlayerState playerState;
    private Connection connection;
    private boolean listenWorldUpdate = false;
    private boolean listenRosterUpdate = false;
    private long timeToRespawn = 0;
    private Fight fight;

    public Player(String name, Connection connection) {
        this.name = name;
        this.connection = connection;
        this.team = Team.Neutral;
        this.position = new Position(0, 0);
    }

    public long getTimeToRespawn() {
        return timeToRespawn;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getName() {
        return name;
    }

    public Team getTeam() {
        return team;
    }

    /* game mechanics */

    public void setTeam(Team team) {
        this.team = team;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
        if(playerState == Dead) {
            this.timeToRespawn = System.currentTimeMillis() + RESPAWN_TIME;
            World.getInstance().notifyPlayers(new MsgCommand("Player " + this.name + " is now dead."));
        }
    }

    public void endFight(PlayerState state) {
        this.fight = null;
        this.setPlayerState(state);
    }

    public void startFight(Fight fight) {
        this.fight = fight;
        this.setPlayerState(Fighting);
    }

    public void onFightRespond(FightRespond respond) {
        if(this.fight == null) return;
        this.fight.playerAttack(this, respond);
    }

    @Override
    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return this.name + " " + this.position + " " + this.team;
    }

    public void changeTeam() {
        this.team = (this.team == Jedi) ? Sith : Jedi;
    }

    public boolean getListenWorldUpdate() {
        return listenWorldUpdate;
    }

    public void setListenWorldUpdate(boolean isReady) {
        listenWorldUpdate = isReady;
        if (isReady) {
            listenRosterUpdate = false;
        }
    }

    public boolean getListenRosterUpdate() {
        return listenRosterUpdate;
    }

    public void setListenRosterUpdate(boolean isReady) {
        listenRosterUpdate = isReady;
        if (isReady) {
            listenWorldUpdate = false;
        }
    }
}
