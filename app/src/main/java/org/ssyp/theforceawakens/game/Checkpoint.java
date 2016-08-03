package org.ssyp.theforceawakens.game;

import org.ssyp.theforceawakens.game.fight.Fight;

import java.util.LinkedList;
import java.util.List;

import static org.ssyp.theforceawakens.game.CheckpointType.Base;
import static org.ssyp.theforceawakens.game.PlayerState.Capturing;
import static org.ssyp.theforceawakens.game.PlayerState.Dead;
import static org.ssyp.theforceawakens.game.Team.Jedi;
import static org.ssyp.theforceawakens.game.Team.Neutral;
import static org.ssyp.theforceawakens.game.Team.Sith;

public class Checkpoint implements Positionable {
    public final static int MAX_HP = 100;
    public final static int CAPTURE_SPEED = 1;
    public final static float CHECKPOINT_RANGE = 0.0001f;

    private String name;
    private Position location;
    private Team allegiance;
    private Team capturingTeam;
    private CheckpointType type;
    private List<Checkpoint> neighbours;
    private int hp;

    public Checkpoint(String name, List<Checkpoint> neighbours, Team allegiance, CheckpointType type, Position location) {
        this.name = name;
        this.location = location;
        if (allegiance == Neutral) {
            this.hp = 0;
        } else {
            this.hp = MAX_HP;
        }
        this.type = type;
        this.allegiance = allegiance;
        this.capturingTeam = allegiance;
        this.neighbours = neighbours;
    }

    public void capture(List<Player> listOfPlayers) {
        List<Player> listOfAliveJedi = new LinkedList<>();
        List<Player> listOfAliveSith = new LinkedList<>();
        for (Player player : listOfPlayers) {
            if (player.getPlayerState() != Dead) {
                (player.getTeam() == Jedi ? listOfAliveJedi : listOfAliveSith).add(player);
            } else if (this.type == Base && this.allegiance == player.getTeam() && player.getTimeToRespawn() < System.currentTimeMillis()) {
                System.out.println(player + " is alive now");
                player.setPlayerState(Capturing);
            }
        }
        int jediCount = listOfAliveJedi.size();
        int sithCount = listOfAliveSith.size();

        if (jediCount + sithCount == 0) {
            this.resetCheckpoint();
        } else {
            System.out.println(this);

            Team mostPlayersTeam;
            if (jediCount > sithCount) {
                mostPlayersTeam = Jedi;
            } else {
                mostPlayersTeam = Sith;
            }

            if (countOfCapturedNeighboursBy(mostPlayersTeam) >= (this.type == Base ? neighbours.size() : 1)) { // this check
                int difference = Math.abs(jediCount - sithCount);
                if (difference == 0) {
                    int randomJedi = (int) (Math.random() * jediCount);
                    int randomSith = (int) (Math.random() * sithCount);
                    new Fight(listOfAliveJedi.get(randomJedi), listOfAliveSith.get(randomSith));
                    // FIXME (v.bog): other duelant should be on checkpoint you're able to capture - so as you're going to other team base, fight ain't going to start
                    // Don't really understand why it crashed when there was no that my last fix, but now the check above (about ablility to capture) seems to work correctly
                    // If there will happen anything horrible, I would be glad to help fix anything in charge to lessen the quanity of our Ladder.
                }

                if (this.capturingTeam.equals(mostPlayersTeam)) {
                    if (this.type != Base) {
                        hp += difference * CAPTURE_SPEED;
                        if (hp >= MAX_HP) {
                            hp = MAX_HP;
                            this.allegiance = mostPlayersTeam;
                        }
                    }
                } else {
                    this.hp -= difference * CAPTURE_SPEED;
                    if (this.hp < 0) {
                        if (this.type != Base) {
                            this.hp = -this.hp;
                            this.allegiance = Neutral;
                        }
                        this.capturingTeam = mostPlayersTeam;
                    }
                }
            }
        }

    }

    public void resetCheckpoint() {
        if (allegiance == Neutral) {
            hp -= CAPTURE_SPEED;
            if (hp < 0) {
                hp = 0;
            }
        }
    }

    public boolean isPlayerInRange(Player player) {
        return player.getPosition().distanceTo(this.getPosition()) <= CHECKPOINT_RANGE;
    }

    @Override
    public Position getPosition() {
        return location;
    }

    public Team getAllegiance() {
        return this.allegiance;
    }

    public Team getCapturingTeam() {
        return capturingTeam;
    }

    public void setCapturingTeam(Team team) {
        this.capturingTeam = team;
    }

    public int getHP() {
        return this.hp;
    }

    public void setHP(int hp) {
        this.hp = hp;
    }

    public CheckpointType getType() {
        return type;
    }

    @Override
    public String toString() {
        return this.name + " " + this.hp + " " + this.allegiance + " " + this.capturingTeam;
    }

    public String getName() {
        return name;
    }

    public void setNeighbours(List<Checkpoint> neighbours) {
        this.neighbours = neighbours;
    }

    public int countOfCapturedNeighboursBy(Team team) {
        int countCaptured = 0;

        for (Checkpoint checkpoint : neighbours) {
            if (checkpoint.allegiance == team) {
                countCaptured++;
            }
        }

        return countCaptured;
    }
}
