package org.ssyp.theforceawakens.test;

import org.ssyp.theforceawakens.commands.clientcommands.AuthCommand;
import org.ssyp.theforceawakens.commands.clientcommands.ChangeTeamCommand;
import org.ssyp.theforceawakens.commands.Command;
import org.ssyp.theforceawakens.commands.servercommands.GameEndedCommand;
import org.ssyp.theforceawakens.commands.clientcommands.ListenRosterUpdateCommand;
import org.ssyp.theforceawakens.commands.clientcommands.ListenWorldUpdateCommand;
import org.ssyp.theforceawakens.commands.clientcommands.MsgCommand;
import org.ssyp.theforceawakens.commands.clientcommands.ReportPositionCommand;
import org.ssyp.theforceawakens.commands.servercommands.RosterUpdateCommand;
import org.ssyp.theforceawakens.connection.Connection;
import org.ssyp.theforceawakens.connection.ConnectionFailureCause;
import org.ssyp.theforceawakens.connection.ConnectionListener;
import org.ssyp.theforceawakens.connection.PSocket;
import org.ssyp.theforceawakens.connection.Server;
import org.ssyp.theforceawakens.game.Position;
import org.ssyp.theforceawakens.json.JSONRosterPlayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import static org.ssyp.theforceawakens.commands.servercommands.AuthBusyCommand.AUTH_BUSY_COMMAND;
import static org.ssyp.theforceawakens.commands.servercommands.AuthOKCommand.AUTH_OK_COMMAND;
import static org.ssyp.theforceawakens.commands.servercommands.AuthTakenCommand.AUTH_TAKEN_COMMAND;
import static org.ssyp.theforceawakens.commands.servercommands.GameEndedCommand.GAME_ENDED_COMMAND;
import static org.ssyp.theforceawakens.commands.servercommands.GameStartedCommand.GAME_STARTED_COMMAND;
import static org.ssyp.theforceawakens.commands.clientcommands.MsgCommand.MSG_COMMAND;
import static org.ssyp.theforceawakens.commands.servercommands.RosterUpdateCommand.ROSTER_UPDATE_COMMAND;
import static org.ssyp.theforceawakens.commands.servercommands.WorldUpdateCommand.WORLD_UPDATE_COMMAND;

public class TestClient {
    public static boolean connectionWasFallen = false;

    public static void main(String[] args) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String nickname = bufferedReader.readLine();
        Socket client = new Socket("127.0.0.1", Server.DEFAULT_PORT);
        ConnectionListener connectionListener = new ConnectionListener() {
            @Override
            public void onConnectionStarted() {

            }

            @Override
            public void onCommandReceived(Command jsonCommand) {
                switch (jsonCommand.command) {
                    case MSG_COMMAND:
                        System.out.println(((MsgCommand) jsonCommand).message);
                        break;

                    case ROSTER_UPDATE_COMMAND:
                        System.out.println("Jedi list");
                        for (JSONRosterPlayer player : ((RosterUpdateCommand) jsonCommand).data.getJediList()) {
                            System.out.println(player);
                        }
                        System.out.println("Sith list");
                        for (JSONRosterPlayer player : ((RosterUpdateCommand) jsonCommand).data.getSithList()) {
                            System.out.println(player);
                        }
                        break;

                    case AUTH_OK_COMMAND:
                        System.out.println("Authorizing was successfully");
                        connection.sendCommand(new ListenRosterUpdateCommand());
                        break;

                    case AUTH_BUSY_COMMAND:
                        System.out.println("Authorizing was failed. Server is busy");
                        connectionWasFallen = true;
                        break;

                    case GAME_STARTED_COMMAND:
                        System.out.println("THE GAME BEGINS");
                        connection.sendCommand(new ListenWorldUpdateCommand());
                        connection.sendCommand(new ReportPositionCommand(new Position(0.0, 0.0)));
/*
                        Thread mockLocationThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (true) {
                                    try {
                                        Thread.sleep(3000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    double diff = (Math.random() - 0.5) * 0.001;
                                    connection.sendCommand(new ReportPositionCommand(new Position(54.614010 + diff, 82.727908 + diff)));
                                }
                            }
                        });
                        mockLocationThread.start();*/

                        break;

                    case GAME_ENDED_COMMAND:
                        System.out.println("THE GAME ENDED. " + ((GameEndedCommand)jsonCommand).wonTeam + " won");
                        connectionWasFallen = true;
                        break;

                    case AUTH_TAKEN_COMMAND:
                        System.out.println("This name is taken");
                        connectionWasFallen = true;
                        break;

                    case WORLD_UPDATE_COMMAND:
                        //System.out.println("World update received");
                        break;

                    default:
                        System.out.println("Unrecognized command received: \"" + jsonCommand.command + "\"");
                        connectionWasFallen = true;
                        break;
                }
            }

            @Override
            public void onConnectionFailed(ConnectionFailureCause cause) {

            }
        };
        Connection connection = new Connection(new PSocket(client), connectionListener);
        connection.sendCommand(new AuthCommand(nickname));

        while (!connectionWasFallen) {
            String s = bufferedReader.readLine();
            switch (s) {
                case "Change team":
                    connection.sendCommand(new ChangeTeamCommand());
                    break;

                case "pos":
                    String newPositionX = bufferedReader.readLine();
                    String newPositionY = bufferedReader.readLine();
                    connection.sendCommand(new ReportPositionCommand(new Position
                            (Float.parseFloat(newPositionX), Float.parseFloat(newPositionY))));
                    break;

                default:
                    connection.sendCommand(new MsgCommand(s));
                    break;
            }
        }
    }
}

