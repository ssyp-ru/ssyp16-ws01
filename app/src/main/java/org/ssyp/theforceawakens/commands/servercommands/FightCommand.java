package org.ssyp.theforceawakens.commands.servercommands;

import org.ssyp.theforceawakens.commands.Command;
import org.ssyp.theforceawakens.game.fight.FightStage;

public class FightCommand extends Command {
    public final static String FIGHT_COMMAND = "fight";
    public FightStage stage;
    public FightCommand(FightStage stage) {
        super(FIGHT_COMMAND);
        this.stage = stage;
    }
}
