package org.ssyp.theforceawakens.commands.clientcommands;

import org.ssyp.theforceawakens.commands.Command;
import org.ssyp.theforceawakens.game.Position;

public class ReportPositionCommand extends Command {
    public final static String REPORT_POSITION_COMMAND = "report_pos";

    public Position newPosition = null;

    public ReportPositionCommand(Position newPosition) {
        super(REPORT_POSITION_COMMAND);
        this.newPosition = newPosition;
    }
}
