package org.ssyp.theforceawakens.commands.clientcommands;

import org.ssyp.theforceawakens.commands.Command;

public class AuthCommand extends Command {
    public final static String AUTH_COMMAND = "auth";

    public String nickname = null;

    public AuthCommand(String nick) {
        super(AUTH_COMMAND);
        this.nickname = nick;
    }
}
