package org.ssyp.theforceawakens.commands.servercommands;

import org.ssyp.theforceawakens.commands.Command;
import org.ssyp.theforceawakens.json.JSONRoster;

public class RosterUpdateCommand extends Command {
    public final static String ROSTER_UPDATE_COMMAND = "roster_update";
    public JSONRoster data;
    public RosterUpdateCommand(JSONRoster data) {
        super(ROSTER_UPDATE_COMMAND);
        this.data = data;
    }
}
