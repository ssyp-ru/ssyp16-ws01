package org.ssyp.theforceawakens.connection;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class PSocket {
    private Socket client = null;
    private PrintWriter out = null;
    private DataInputStream in = null;

    public PSocket(Socket client) throws IOException {
        this.client = client;
        this.out = new PrintWriter(this.client.getOutputStream());
        this.in = new DataInputStream(this.client.getInputStream());
    }

    public void write(String s) throws Exception {
        try {
            this.out.println(s);
            this.out.flush();
        } catch (Exception e) {
            System.out.println("Exception happened at socket.write()");
            throw e;
        }
    }

    public String read() throws IOException {
        try {
            return this.in.readLine();
        } catch (IOException e) {
            System.out.println("IOException happened at socket.read()");
            throw e;
        }
    }

    public void close() {
        try {
            this.client.close();
        } catch (IOException e) {
            System.out.println("IOException happened at socket.close()");
        }
    }
}