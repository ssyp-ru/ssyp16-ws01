package org.ssyp.theforceawakens.game;

import org.ssyp.theforceawakens.commands.Command;
import org.ssyp.theforceawakens.commands.clientcommands.AuthCommand;
import org.ssyp.theforceawakens.commands.clientcommands.FightRespondCommand;
import org.ssyp.theforceawakens.commands.clientcommands.MsgCommand;
import org.ssyp.theforceawakens.commands.clientcommands.ReportPositionCommand;
import org.ssyp.theforceawakens.commands.servercommands.AuthBusyCommand;
import org.ssyp.theforceawakens.commands.servercommands.AuthOKCommand;
import org.ssyp.theforceawakens.commands.servercommands.AuthTakenCommand;
import org.ssyp.theforceawakens.commands.servercommands.GameEndedCommand;
import org.ssyp.theforceawakens.commands.servercommands.GameStartedCommand;
import org.ssyp.theforceawakens.commands.servercommands.RosterUpdateCommand;
import org.ssyp.theforceawakens.commands.servercommands.WorldUpdateCommand;
import org.ssyp.theforceawakens.connection.Connection;
import org.ssyp.theforceawakens.connection.ConnectionFailureCause;
import org.ssyp.theforceawakens.connection.ConnectionListener;
import org.ssyp.theforceawakens.connection.PSocket;
import org.ssyp.theforceawakens.connection.Server;
import org.ssyp.theforceawakens.json.JSONRoster;
import org.ssyp.theforceawakens.map.Map1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.ssyp.theforceawakens.commands.clientcommands.AuthCommand.AUTH_COMMAND;
import static org.ssyp.theforceawakens.commands.clientcommands.ChangeTeamCommand.CHANGE_TEAM_COMMAND;
import static org.ssyp.theforceawakens.commands.clientcommands.FightRespondCommand.FIGHT_RESPOND_COMMAND;
import static org.ssyp.theforceawakens.commands.clientcommands.ListenRosterUpdateCommand.LISTEN_ROSTER_UPDATE_COMMAND;
import static org.ssyp.theforceawakens.commands.clientcommands.ListenWorldUpdateCommand.LISTEN_WORLD_UPDATE_COMMAND;
import static org.ssyp.theforceawakens.commands.clientcommands.MsgCommand.MSG_COMMAND;
import static org.ssyp.theforceawakens.commands.clientcommands.ReportPositionCommand.REPORT_POSITION_COMMAND;
import static org.ssyp.theforceawakens.game.CheckpointType.Base;
import static org.ssyp.theforceawakens.game.GameStage.Play;
import static org.ssyp.theforceawakens.game.GameStage.Wait;
import static org.ssyp.theforceawakens.game.PlayerState.Capturing;
import static org.ssyp.theforceawakens.game.PlayerState.Dead;
import static org.ssyp.theforceawakens.game.PlayerState.Fighting;
import static org.ssyp.theforceawakens.game.PlayerState.Roaming;
import static org.ssyp.theforceawakens.game.Team.Jedi;
import static org.ssyp.theforceawakens.game.Team.Neutral;
import static org.ssyp.theforceawakens.game.Team.Sith;

public class World {
    public final static int RESPAWN_TIME = 60000;
    private final static int MAX_PLAYERS_IN_TEAM = 1; //https://youtu.be/Tim5nU3DwIE?t=19s
    private final static int NUMBER_OF_CHECKPOINTS = 7;
    private final static int THREAD_TICK_SLEEP = 100;

    private static World instance = null;
    private final List<Player> players = new LinkedList<>();
    private final List<Checkpoint> checkpoints = new ArrayList<>(NUMBER_OF_CHECKPOINTS);
    private GameStage gameStage = Wait;

    private World() {
        System.out.println("Creating new world with server!");
        new Server(Server.DEFAULT_PORT);
        Thread worldChangeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long time = System.currentTimeMillis();
                    if (gameStage == Play) {
                        World.this.playUpdate();
                    }

                    long deltaTime = System.currentTimeMillis() - time;
                    if (deltaTime < THREAD_TICK_SLEEP) {
                        try {
                            Thread.sleep(THREAD_TICK_SLEEP - deltaTime);
                        } catch (InterruptedException e) {
                            System.out.println("Thread interrupted");
                        }
                    }

                    if (isBaseDestroyed(checkpoints)) {
                        System.out.println("THE GAME ENDS");
                        for (Player player : players) {
                            if (player.getConnection() != null) {
                                player.getConnection().sendCommand(new GameEndedCommand(whoDidWin()));
                            }
                        }
                        gameStage = Wait;

                        Map1.resetCheckpoints();
                        List<Checkpoint> newCheckpoints = new ArrayList<>(NUMBER_OF_CHECKPOINTS);
                        for (Checkpoint checkpoint : checkpoints) {
                            newCheckpoints.add(checkpoint);
                        }
                        for (Checkpoint checkpoint : newCheckpoints) {
                            checkpoints.remove(checkpoint);
                        }

                        Collections.addAll(checkpoints, Map1.getCheckpoints());
                        List<Player> newPlayers = new LinkedList<>();
                        for (Player player : players) {
                            newPlayers.add(player);
                        }
                        for (Player player : newPlayers) {
                            players.remove(player);
                        }
                    }
                }
            }
        });
        worldChangeThread.start();
    }

    public static World getInstance() {
        if (instance == null) {
            instance = new World();
        }
        return instance;
    }

    public List<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    public void makeConnection(PSocket socket) {
        ConnectionListener connectionListener = new ConnectionListener() {
            @Override
            public void onCommandReceived(Command jsonCommand) {
                synchronized (players) {
                    switch (jsonCommand.command) {
                        case AUTH_COMMAND:
                            World.this.setConnectionWithPlayer(this.getConnection(), (AuthCommand) jsonCommand);
                            break;

                        case MSG_COMMAND:
                            for (Player p : players) {
                                if (p.getTeam() == this.getConnection().getPlayer().getTeam()) {
                                    p.getConnection().sendCommand(new MsgCommand(this.connection.getPlayer().getName() + ": " + ((MsgCommand)jsonCommand).message));
                                }
                            }
                            if (((MsgCommand) jsonCommand).message.equals("Players state")) {
                                System.out.println("Players state");
                                for (Player p : players) {
                                    System.out.println(p.getPlayerState() + " " + p.getPosition().toString());
                                }
                            }
                            break;

                        case CHANGE_TEAM_COMMAND:
                            if (gameStage == Wait) {
                                if (canChangeTeam(this.connection.getPlayer())) {
                                    this.connection.getPlayer().changeTeam();
                                    rosterUpdate();
                                }
                            }
                            break;

                        case LISTEN_WORLD_UPDATE_COMMAND:
                            this.connection.getPlayer().setListenWorldUpdate(true);
                            break;

                        case LISTEN_ROSTER_UPDATE_COMMAND:
                            this.connection.getPlayer().setListenRosterUpdate(true);
                            rosterUpdate();
                            break;

                        case FIGHT_RESPOND_COMMAND:
                            FightRespondCommand fightRespondCommand = (FightRespondCommand) jsonCommand;
                            this.connection.getPlayer().onFightRespond(fightRespondCommand.respond);
                            break;

                        case REPORT_POSITION_COMMAND:
                            ReportPositionCommand positionReport = (ReportPositionCommand) jsonCommand;
                            this.connection.getPlayer().setPosition(positionReport.newPosition);
                            break;

                        default:
                            break;
                    }
                }
            }

            @Override
            public void onConnectionFailed(ConnectionFailureCause cause) {
                synchronized (players) {
                    if (gameStage == Wait) {
                        players.remove(this.connection.getPlayer());
                    }
                }
                rosterUpdate();
            }
        };

        new Connection(socket, connectionListener);
    }

    private boolean canChangeTeam(Player traitor) {
        return canChangeTeamFrom(traitor, Jedi) ||
                canChangeTeamFrom(traitor, Sith);
    }

    private boolean canChangeTeamFrom(Player traitor, Team requestedTeam) {
        return playersInTeam(requestedTeam) < MAX_PLAYERS_IN_TEAM &&
                traitor.getTeam() != requestedTeam;
    }

    public int playersInTeam(Team team) {
        return getForceUsers(team).size();
    }

    private void rosterUpdate() {
        RosterUpdateCommand rosterUpdateCommand = new RosterUpdateCommand(new JSONRoster());
        synchronized (this.players) {
            for (Player player : this.players) {
                rosterUpdateCommand.data.addPlayer(player);
            }
            boolean gameStageChanged = false;
            if ((gameStage == Wait) && isTeamsFull()) {
                gameStage = Play;
                for (Player player : players){
                    player.setListenWorldUpdate(true);
                }
                gameStageChanged = true;
            }

            for (Player player : players) {
                if (gameStageChanged) {
                    player.getConnection().sendCommand(new GameStartedCommand());
                }
                if (player.getListenRosterUpdate()) {
                    player.getConnection().sendCommand(rosterUpdateCommand);
                }
            }
        }
    }

    public boolean isTeamsFull() {
        int countOfPlayers = 0;
        for (Player player : players) {
            if (player.getConnection() != null) {
                countOfPlayers++;
            }
        }
        return countOfPlayers == 2 * MAX_PLAYERS_IN_TEAM;
    }

    private void playUpdate() {
        for (Checkpoint checkpoint : checkpoints) {
            List<Player> listOfPlayers = new LinkedList<>();
            for (Player player : getPlayersInRange(checkpoint)) {
                listOfPlayers.add(player);
            }
            checkpoint.capture(listOfPlayers);
        }
        for (Player player : players) {
            if (player.getPlayerState() != Fighting || player.getPlayerState() != Dead) {
                PlayerState state = Roaming;
                for (Checkpoint currentCheckpoint : checkpoints) {
                    if (currentCheckpoint.isPlayerInRange(player)) {
                        state = Capturing;
                        break;
                    }
                }
                player.setPlayerState(state);
            }
        }
        notifyPlayers(new WorldUpdateCommand(this.players, this.checkpoints));
    }

    public void notifyPlayers(Command command) {
        for (Player player : players) {
            player.getConnection().sendCommand(command);
        }
    }

    public List<Player> getForceUsers(Team team) {
        List<Player> forceUsers = new LinkedList<>();
        for (Player player : players) {
            if (player.getTeam() == team) {
                forceUsers.add(player);
            }
        }
        return forceUsers;
    }

    public List<Player> getPlayersInRange(Checkpoint checkpoint) {
        List<Player> playersInCapture = new LinkedList<>();
        for (Player player : this.players) {
            if (checkpoint.isPlayerInRange(player)) {
                playersInCapture.add(player);
            }
        }
        return playersInCapture;
    }

    public void setConnectionWithPlayer(Connection source, AuthCommand authCommand) {
        synchronized (this.players) {
            for (Player player : this.players) {
                if (player.getName().equals(authCommand.nickname) && player.getConnection() == null) {
                    source.sendCommand(new AuthOKCommand());
                    player.setConnection(source);
                    return;
                }
            }

            if (gameStage != Wait) {
                source.sendCommand(new AuthBusyCommand());
                return;
            }

            for (Player player : this.players) {
                if (player.getName().equals(authCommand.nickname)) {
                    source.sendCommand(new AuthTakenCommand());
                    return;
                }
            }

            Player player = new Player(authCommand.nickname, source);
            source.setPlayer(player);

            if (playersInTeam(Jedi) >= MAX_PLAYERS_IN_TEAM) {
                player.setTeam(Sith);
            } else if (playersInTeam(Sith) >= MAX_PLAYERS_IN_TEAM) {
                player.setTeam(Jedi);
            } else {
                player.setTeam(Math.random() < 0.5 ? Jedi : Sith);
            }

            player.setPosition(Map1.getCheckpoints()[player.getTeam() == Jedi ? Map1.JEDI_BASE : Map1.SITH_BASE].getPosition());

            player.getConnection().sendCommand(new AuthOKCommand());
            this.players.add(player);
            rosterUpdate();
        }
    }

    public boolean isBaseDestroyed(List<Checkpoint> checkpoints) {
        boolean isDestroyed = false;
        for (Checkpoint checkpoint : checkpoints) {
            if (checkpoint.getType() == Base && checkpoint.getHP() <= 0) {
                isDestroyed = true;
            }
        }
        return isDestroyed;
    }

    public void addCheckpoint(Checkpoint c) {
        this.checkpoints.add(c);
    }

    public Team whoDidWin() {
        for (Checkpoint checkpoint : checkpoints) {
            if (checkpoint.getType() == Base && (checkpoint.getAllegiance() == Neutral || checkpoint.getHP() == 0)) {
                return checkpoint.equals(checkpoints.get(0)) ? Jedi : Sith;
            }
        }
        return Neutral;
    }
}
