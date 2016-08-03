package org.ssyp.theforceawakens.connection;

import org.ssyp.theforceawakens.connection.PSocket;
import org.ssyp.theforceawakens.game.World;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static final int DEFAULT_PORT = 32280;
    private ServerSocket listenSocket;
    private Thread listenThread;

    public boolean isAlive() {
        return (!listenSocket.isClosed() && listenThread.isAlive());
    }

    public Server(int port) {
        try {
            this.listenSocket = new ServerSocket(port);
            this.listenThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Socket clientConnection = listenSocket.accept();
                            World.getInstance().makeConnection(new PSocket(clientConnection));
                        } catch (Exception e) {
                            System.out.println("Exception happened at server.listenThread");
                            break;
                        }
                    }
                }
            });
            this.listenThread.start();
        } catch (IOException e) {
            System.out.println("IOException happened at server.listenSocket");
        }
    }
}