package org.ssyp.theforceawakens.connection;

import org.ssyp.theforceawakens.commands.AckCommand;
import org.ssyp.theforceawakens.commands.Command;
import org.ssyp.theforceawakens.commands.servercommands.RosterUpdateCommand;
import org.ssyp.theforceawakens.commands.servercommands.WorldUpdateCommand;
import org.ssyp.theforceawakens.game.Player;
import org.ssyp.theforceawakens.json.JSONTransformer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static org.ssyp.theforceawakens.commands.AckCommand.ACK_COMMAND;
import static org.ssyp.theforceawakens.connection.ConnectionFailureCause.AcknowledgeTimeout;
import static org.ssyp.theforceawakens.connection.ConnectionFailureCause.ReadFailed;
import static org.ssyp.theforceawakens.connection.ConnectionFailureCause.ReadNull;
import static org.ssyp.theforceawakens.connection.ConnectionFailureCause.ReadParseFailed;
import static org.ssyp.theforceawakens.connection.ConnectionFailureCause.Write;

public class Connection {
    public final static long ACK_WAIT_TIME = 3000;

    private Player player;
    private PSocket socket;
    private Thread socketListener;
    private HashMap<Integer, Timer> listOfCommands = new HashMap<>();
    private int sequenceNumber = 0;
    private ConnectionListener connectionListener;

    private volatile boolean enabled;

    public boolean isEnabled() {
        return this.enabled;
    }

    public Connection(PSocket socket, final ConnectionListener connectionListener) {
        this.socket = socket;
        this.connectionListener = connectionListener;
        this.connectionListener.setConnection(this);

        this.enabled = true;

        this.socketListener = new Thread(new Runnable() {
            @Override
            public void run() {
                connectionListener.onConnectionStarted();
                while (true) {
                    synchronized (this) {
                        try {
                            String commandCaught = Connection.this.socket.read();
                            //System.out.println("RECV (" + (player != null ? player.getName() : "") + "): " + commandCaught);

                            if (commandCaught != null) {
                                Command receivedCommand = JSONTransformer.parseCommand(commandCaught);
                                if (receivedCommand != null) {
                                    if (!receivedCommand.command.equals(ACK_COMMAND)) {
                                        Connection.this.sendCommand(new AckCommand(receivedCommand.id));
                                        Connection.this.connectionListener.onCommandReceived(receivedCommand);
                                    } else {
                                        listOfCommands.get(receivedCommand.id).cancel();
                                        listOfCommands.remove(receivedCommand.id);
                                    }
                                } else {
                                    Connection.this.close(ReadParseFailed);
                                    return;
                                }
                            } else {
                                Connection.this.close(ReadNull);
                                return;
                            }
                        } catch (IOException e) {
                            Connection.this.close(ReadFailed);
                            return;
                        }
                    }
                }
            }
        });
        this.socketListener.start();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void sendCommand(final Command command) {
        if (command == null) return;
        if ((command instanceof WorldUpdateCommand) && (!this.player.getListenWorldUpdate()))
            return;
        if ((command instanceof RosterUpdateCommand) && (!this.player.getListenRosterUpdate()))
            return;
        try {
            if (!command.command.equals(ACK_COMMAND)) {
                command.id = sequenceNumber;
                sequenceNumber++;
                Timer timer = new Timer("Timer from message id:" + sequenceNumber);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Connection.this.close(AcknowledgeTimeout);
                    }
                }, ACK_WAIT_TIME);
                listOfCommands.put(command.id, timer);
            }

            String serializedCommand = JSONTransformer.createCommand(command);
            System.out.println("SEND (" + (this.player != null ? this.player.getName() : "") + "): " + serializedCommand);
            this.socket.write(serializedCommand);
        } catch (Exception e) {
            this.close(Write);
        }
    }

    public void close(ConnectionFailureCause cause) {
        synchronized (this) {
            this.socket.close();
            this.socketListener.interrupt();

            if (this.enabled) {
                this.connectionListener.onConnectionFailed(cause);
                this.enabled = false;
            }
        }
    }

    public ConnectionListener getConnectionListener() {
        return this.connectionListener;
    }

    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }
}
