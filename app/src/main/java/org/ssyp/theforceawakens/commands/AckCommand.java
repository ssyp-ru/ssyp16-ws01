package org.ssyp.theforceawakens.commands;


public class AckCommand extends Command{
    public final static String ACK_COMMAND = "ack";
    public AckCommand(int id) {
        super(ACK_COMMAND);
        this.id = id;
    }

}
