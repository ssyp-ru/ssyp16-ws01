package org.ssyp.theforceawakens.json;

import android.support.annotation.Nullable;

import com.google.gson.Gson;

import org.ssyp.theforceawakens.commands.AckCommand;
import org.ssyp.theforceawakens.commands.clientcommands.FightRespondCommand;
import org.ssyp.theforceawakens.commands.servercommands.AuthBusyCommand;
import org.ssyp.theforceawakens.commands.clientcommands.AuthCommand;
import org.ssyp.theforceawakens.commands.servercommands.AuthOKCommand;
import org.ssyp.theforceawakens.commands.servercommands.AuthTakenCommand;
import org.ssyp.theforceawakens.commands.clientcommands.ChangeTeamCommand;
import org.ssyp.theforceawakens.commands.Command;
import org.ssyp.theforceawakens.commands.DisconnectCommand;
import org.ssyp.theforceawakens.commands.servercommands.FightCommand;
import org.ssyp.theforceawakens.commands.servercommands.GameEndedCommand;
import org.ssyp.theforceawakens.commands.servercommands.GameStartedCommand;
import org.ssyp.theforceawakens.commands.clientcommands.ListenRosterUpdateCommand;
import org.ssyp.theforceawakens.commands.clientcommands.ListenWorldUpdateCommand;
import org.ssyp.theforceawakens.commands.clientcommands.MsgCommand;
import org.ssyp.theforceawakens.commands.clientcommands.ReportPositionCommand;
import org.ssyp.theforceawakens.commands.servercommands.RosterUpdateCommand;
import org.ssyp.theforceawakens.commands.servercommands.WorldUpdateCommand;

import static org.ssyp.theforceawakens.commands.AckCommand.ACK_COMMAND;
import static org.ssyp.theforceawakens.commands.clientcommands.FightRespondCommand.FIGHT_RESPOND_COMMAND;
import static org.ssyp.theforceawakens.commands.servercommands.AuthBusyCommand.AUTH_BUSY_COMMAND;
import static org.ssyp.theforceawakens.commands.clientcommands.AuthCommand.AUTH_COMMAND;
import static org.ssyp.theforceawakens.commands.servercommands.AuthOKCommand.AUTH_OK_COMMAND;
import static org.ssyp.theforceawakens.commands.servercommands.AuthTakenCommand.AUTH_TAKEN_COMMAND;
import static org.ssyp.theforceawakens.commands.clientcommands.ChangeTeamCommand.CHANGE_TEAM_COMMAND;
import static org.ssyp.theforceawakens.commands.DisconnectCommand.DISCONNECT_COMMAND;
import static org.ssyp.theforceawakens.commands.servercommands.FightCommand.FIGHT_COMMAND;
import static org.ssyp.theforceawakens.commands.servercommands.GameEndedCommand.GAME_ENDED_COMMAND;
import static org.ssyp.theforceawakens.commands.servercommands.GameStartedCommand.GAME_STARTED_COMMAND;
import static org.ssyp.theforceawakens.commands.clientcommands.ListenRosterUpdateCommand.LISTEN_ROSTER_UPDATE_COMMAND;
import static org.ssyp.theforceawakens.commands.clientcommands.ListenWorldUpdateCommand.LISTEN_WORLD_UPDATE_COMMAND;
import static org.ssyp.theforceawakens.commands.clientcommands.MsgCommand.MSG_COMMAND;
import static org.ssyp.theforceawakens.commands.clientcommands.ReportPositionCommand.REPORT_POSITION_COMMAND;
import static org.ssyp.theforceawakens.commands.servercommands.RosterUpdateCommand.ROSTER_UPDATE_COMMAND;
import static org.ssyp.theforceawakens.commands.servercommands.WorldUpdateCommand.WORLD_UPDATE_COMMAND;

public class JSONTransformer {
    private static Gson gson = new Gson();

    public static String createCommand(Command command) {
        return gson.toJson(command);
    }

    @Nullable
    public static Command parseCommand(String cmd) {
        Command deliveredCommand = gson.fromJson(cmd, Command.class);

        switch (deliveredCommand.command) {
            case ROSTER_UPDATE_COMMAND:
                return gson.fromJson(cmd, RosterUpdateCommand.class);

            case ACK_COMMAND:
                return gson.fromJson(cmd, AckCommand.class);

            case MSG_COMMAND:
                return gson.fromJson(cmd, MsgCommand.class);

            case AUTH_COMMAND:
                return gson.fromJson(cmd, AuthCommand.class);

            case REPORT_POSITION_COMMAND:
                return gson.fromJson(cmd, ReportPositionCommand.class);

            case AUTH_BUSY_COMMAND:
                return gson.fromJson(cmd, AuthBusyCommand.class);

            case AUTH_OK_COMMAND:
                return gson.fromJson(cmd, AuthOKCommand.class);

            case AUTH_TAKEN_COMMAND:
                return gson.fromJson(cmd, AuthTakenCommand.class);

            case CHANGE_TEAM_COMMAND:
                return gson.fromJson(cmd, ChangeTeamCommand.class);

            case DISCONNECT_COMMAND:
                return gson.fromJson(cmd, DisconnectCommand.class);

            case GAME_STARTED_COMMAND:
                return gson.fromJson(cmd, GameStartedCommand.class);

            case LISTEN_ROSTER_UPDATE_COMMAND:
                return gson.fromJson(cmd, ListenRosterUpdateCommand.class);

            case LISTEN_WORLD_UPDATE_COMMAND:
                return gson.fromJson(cmd, ListenWorldUpdateCommand.class);

            case WORLD_UPDATE_COMMAND:
                return gson.fromJson(cmd, WorldUpdateCommand.class);

            case GAME_ENDED_COMMAND:
                return gson.fromJson(cmd, GameEndedCommand.class);

            case FIGHT_COMMAND:
                return gson.fromJson(cmd, GameEndedCommand.class);

            case FIGHT_RESPOND_COMMAND:
                return gson.fromJson(cmd, GameEndedCommand.class);

            default:
                return deliveredCommand;
        }
    }
}
