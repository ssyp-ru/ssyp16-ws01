package org.ssyp.theforceawakens.commands.clientcommands;

import org.ssyp.theforceawakens.commands.Command;

public class MsgCommand extends Command {
    public final static String MSG_COMMAND = "msg";

    public String message;

    public MsgCommand(String msg){
        super(MSG_COMMAND);
        this.message = msg;
    }
}
