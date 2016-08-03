package org.ssyp.theforceawakens.connection;

import org.ssyp.theforceawakens.commands.clientcommands.AuthCommand;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import static org.ssyp.theforceawakens.connection.ConnectionFailureCause.ConnectTimeout;
import static org.ssyp.theforceawakens.connection.ConnectionFailureCause.SocketCreationIOException;
import static org.ssyp.theforceawakens.connection.ConnectionFailureCause.WrongIP;

public class Client {
    private static Client instance = null;
    public volatile Connection connection;

    private Client() { }

    public static Client getInstance() {
        if (instance == null) {
            instance = new Client();
        }
        return instance;
    }

    public void connect(final String ip, final String nickname, final ConnectionListener listener) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket client = new Socket();
                    client.connect(new InetSocketAddress(ip, Server.DEFAULT_PORT), 5000);
                    connection = new Connection(new PSocket(client), listener);
                    connection.sendCommand(new AuthCommand(nickname));
                } catch (SocketTimeoutException e) {
                    listener.onConnectionFailed(ConnectTimeout);
                } catch (UnknownHostException e) {
                    listener.onConnectionFailed(WrongIP);
                } catch (IOException e) {
                    listener.onConnectionFailed(SocketCreationIOException);
                }
            }
        }).start();
    }
}

