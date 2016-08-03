package org.ssyp.theforceawakens.client.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import org.ssyp.theforceawakens.R;
import org.ssyp.theforceawakens.connection.ConnectionFailureCause;
import org.ssyp.theforceawakens.game.Team;

import static org.ssyp.theforceawakens.client.elements.MapView.JEDI_CHECKPOINT;
import static org.ssyp.theforceawakens.client.elements.MapView.JEDI_PLAYER;
import static org.ssyp.theforceawakens.client.elements.MapView.NEUTRAL_TEAM;
import static org.ssyp.theforceawakens.client.elements.MapView.SITH_CHECKPOINT;
import static org.ssyp.theforceawakens.client.elements.MapView.SITH_PLAYER;

public class Utils {
    public static void alert(final ConnectionFailureCause cause, final Activity context) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                String message = "";
                switch (cause) {
                    case Disconnected:
                        message = "Disconnected from server.";
                        break;
                    case ServerBusy:
                        message = "Server is busy now, choose another one.";
                        break;
                    case NicknameTaken:
                        message = "Your nickname is already busy, choose another one.";
                        break;
                    case ConnectTimeout:
                        message = "Connection to server timed out.";
                        break;
                    case ReadFailed:
                        message = "Disconnected from server (reading error).";
                        break;
                    case ReadNull:
                        message = "Disconnected from server (null string).";
                        break;
                    case ReadParseFailed:
                        message = "Failed parsing incoming message.";
                        break;
                    case Write:
                        message = "Disconnected from server (writing error).";
                        break;
                    case SocketCreationIOException:
                        message = "Internal error (failed creating socket).";
                        break;
                    case WrongIP:
                        message = "Can't find remote host.";
                        break;
                    case AcknowledgeTimeout:
                        message = "Remove end hasn't acknowledged a message.";
                        break;
                }

                builder.setMessage(message);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.show();
            }
        });
    }

    public static int getTeamCheckpointColor(Team team) {
        int color = NEUTRAL_TEAM;
        switch (team) {
            case Jedi:
                color = JEDI_CHECKPOINT;
                break;

            case Sith:
                color = SITH_CHECKPOINT;
                break;

            default:
                break;
        }

        return color;
    }

    public static int getTeamPlayerColor(Team team) {
        int color = NEUTRAL_TEAM;
        switch (team) {
            case Jedi:
                color = JEDI_PLAYER;
                break;

            case Sith:
                color = SITH_PLAYER;
                break;

            default:
                break;
        }

        return color;
    }

}
