package org.ssyp.theforceawakens.commands.clientcommands;

import org.ssyp.theforceawakens.commands.Command;
import org.ssyp.theforceawakens.game.fight.FightRespond;

public class FightRespondCommand extends Command {
    public final static String FIGHT_RESPOND_COMMAND = "fight_respond";
    public FightRespond respond;
    public FightRespondCommand(FightRespond respond) {
        super(FIGHT_RESPOND_COMMAND);
        this.respond = respond;
    }
}
