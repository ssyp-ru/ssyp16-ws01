package org.ssyp.theforceawakens.connection;

import org.ssyp.theforceawakens.commands.Command;

public abstract class ConnectionListener {
    protected Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void onConnectionStarted() {}
    public void onCommandReceived(Command jsonCommand) {}
    public void onConnectionFailed(ConnectionFailureCause cause) {}
}
