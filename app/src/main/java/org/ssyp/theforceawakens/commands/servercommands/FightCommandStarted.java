package org.ssyp.theforceawakens.commands.servercommands;

import org.ssyp.theforceawakens.commands.Command;
import org.ssyp.theforceawakens.game.Player;
import org.ssyp.theforceawakens.game.fight.FightStage;
import org.ssyp.theforceawakens.json.JSONPlayer;

public class FightCommandStarted extends Command {
    public final static String FIGHT_COMMAND_STARTED = "fight_started";
    public JSONPlayer jedi;
    public JSONPlayer sith;
    public FightCommandStarted(Player jedi, Player sith) {
        super(FIGHT_COMMAND_STARTED);
        this.jedi = new JSONPlayer(jedi);
        this.sith = new JSONPlayer(sith);
    }
}
