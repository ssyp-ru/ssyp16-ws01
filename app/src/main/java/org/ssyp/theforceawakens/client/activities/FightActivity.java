package org.ssyp.theforceawakens.client.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.ssyp.theforceawakens.R;
import org.ssyp.theforceawakens.commands.Command;
import org.ssyp.theforceawakens.commands.clientcommands.FightRespondCommand;
import org.ssyp.theforceawakens.commands.servercommands.FightCommand;
import org.ssyp.theforceawakens.connection.Client;
import org.ssyp.theforceawakens.connection.ConnectionFailureCause;
import org.ssyp.theforceawakens.connection.ConnectionListener;
import org.ssyp.theforceawakens.game.fight.FightRespond;
import org.ssyp.theforceawakens.game.fight.FightStage;

import java.util.Timer;
import java.util.TimerTask;

import static org.ssyp.theforceawakens.commands.servercommands.FightCommand.FIGHT_COMMAND;

public class FightActivity extends AppCompatActivity {
    public final static int FIGHT_CLOSE_TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Magic. Do not touch!
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_fight);
    }

    protected void onResume() {
        super.onResume();
        Client.getInstance().connection.setConnectionListener(new ConnectionListener() {

            @Override
            public void onCommandReceived(Command jsonCommand) {
                switch (jsonCommand.command) {


                    /*Client.getInstance().connection.sendCommand(new FightRespondCommand
                                (FightRespond.Down : FightRespond.Up))*/
                    case FIGHT_COMMAND:
                        FightStage stage = ((FightCommand) jsonCommand).stage;
                        switch (stage) {
                            // TODO for Agul: place your animations here
                            case SithHurts:
                                break;

                            case JediHurts:
                                break;

                            case SithWins:
                                new Timer("Fight close timer").schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        FightActivity.this.finish();
                                    }
                                }, FIGHT_CLOSE_TIME);
                                break;

                            case JediWins:
                                new Timer("Fight close timer").schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        FightActivity.this.finish();
                                    }
                                }, FIGHT_CLOSE_TIME);
                                break;

                            case TimerStarted:
                                //Start timer animation
                                break;

                            default:
                                break;

                        }
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onConnectionFailed(ConnectionFailureCause cause) {
                Client.getInstance().connection = null;
                AuthActivity.setFailureCause(cause);
                FightActivity.this.finish();
            }
        });
    }

    public void sendPlus(View view) {
        Client.getInstance().connection.sendCommand(new FightRespondCommand(FightRespond.Up));
    }

    public void sendMinus(View view) {
        Client.getInstance().connection.sendCommand(new FightRespondCommand(FightRespond.Down));
    }
}
