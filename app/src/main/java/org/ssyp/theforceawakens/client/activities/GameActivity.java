package org.ssyp.theforceawakens.client.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.ssyp.theforceawakens.R;
import org.ssyp.theforceawakens.client.GPS.GPS;
import org.ssyp.theforceawakens.client.GPS.GPSCallback;
import org.ssyp.theforceawakens.client.elements.MapView;
import org.ssyp.theforceawakens.commands.Command;
import org.ssyp.theforceawakens.commands.clientcommands.ListenWorldUpdateCommand;
import org.ssyp.theforceawakens.commands.clientcommands.MsgCommand;
import org.ssyp.theforceawakens.commands.clientcommands.ReportPositionCommand;
import org.ssyp.theforceawakens.commands.servercommands.WorldUpdateCommand;
import org.ssyp.theforceawakens.connection.Client;
import org.ssyp.theforceawakens.connection.ConnectionFailureCause;
import org.ssyp.theforceawakens.connection.ConnectionListener;
import org.ssyp.theforceawakens.game.Position;

import static org.ssyp.theforceawakens.commands.clientcommands.MsgCommand.MSG_COMMAND;
import static org.ssyp.theforceawakens.commands.servercommands.FightCommandStarted.FIGHT_COMMAND_STARTED;
import static org.ssyp.theforceawakens.commands.servercommands.GameEndedCommand.GAME_ENDED_COMMAND;
import static org.ssyp.theforceawakens.commands.servercommands.WorldUpdateCommand.WORLD_UPDATE_COMMAND;
import static org.ssyp.theforceawakens.connection.ConnectionFailureCause.Disconnected;

public class GameActivity extends Activity implements View.OnTouchListener {
    private LinearLayout msgList;
    private TextView chatTitle;
    private int screenHeight;
    private MapView mapView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Magic. Do not touch!
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Still magic
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


        setContentView(R.layout.activity_game);

        screenHeight = getResources().getDisplayMetrics().heightPixels;

        msgList = (LinearLayout) findViewById(R.id.msg_list);
        chatTitle = (TextView) findViewById(R.id.chat_title);
        mapView = (MapView) findViewById(R.id.map_view);
        chatTitle.setOnTouchListener(this);

        Button sendButton = (Button) findViewById(R.id.button_send);
        sendButton.setOnTouchListener(this);

        // setup GPS
        new GPS(this, new GPSCallback() {
            @Override
            public void signalLocation(double lat, double lon) {
                Log.e("TFA", "signalling location update, lat: " + lat + ", lon: " + lon + ".");
                Client.getInstance().connection.sendCommand(new ReportPositionCommand(new Position(lat, lon)));
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (Client.getInstance().connection != null)
                if (Client.getInstance().connection.isEnabled())
                    Client.getInstance().connection.close(Disconnected);

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Client.getInstance().connection == null) {
            this.finish();
            return;
        }

        Client.getInstance().connection.setConnectionListener(new ConnectionListener() {

            @Override
            public void onCommandReceived(Command jsonCommand) {
                switch (jsonCommand.command) {
                    case WORLD_UPDATE_COMMAND:
                        WorldUpdateCommand command = (WorldUpdateCommand) jsonCommand;
                        mapView.updateMap(command.players, command.checkpoints);
                        break;

                    case MSG_COMMAND:
                        MsgCommand msgCommand = (MsgCommand) jsonCommand;
                        modMessage(msgCommand.message);
                        break;

                    case GAME_ENDED_COMMAND:
                        GameActivity.this.finish();
                        break;

                    case FIGHT_COMMAND_STARTED:
                        GameActivity.this.startActivity(new Intent(GameActivity.this, FightActivity.class));
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onConnectionFailed(ConnectionFailureCause cause) {
                AuthActivity.setFailureCause(cause);
                Client.getInstance().connection = null;

                GameActivity.this.finish();
            }
        });
        Client.getInstance().connection.sendCommand(new ListenWorldUpdateCommand());
    }

    public void modMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout msgList = (LinearLayout) findViewById(R.id.msg_list);
                TextView textView = new TextView(msgList.getContext());
                textView.setText(message);
                msgList.addView(textView);
            }
        });
    }

    /**
     * Handles touches in Chat: opening the chat and sending messages.
     */
    @Override
    public boolean onTouch(View v, MotionEvent e) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            if (v.getId() == R.id.chat_title) {
                int teh = chatTitle.getHeight();
                LinearLayout l = (LinearLayout) findViewById(R.id.chat_layout);
                l.setY((int) (screenHeight - teh - (l.getHeight() - teh) * (Math.floor((l.getY() + teh) / screenHeight) % 2)));
            } else {
                EditText editText = (EditText) findViewById(R.id.edit_text);
                String text = editText.getText().toString();
                editText.setText("");
                Client.getInstance().connection.sendCommand(new MsgCommand(text));
            }
            ScrollView sv = (ScrollView) findViewById(R.id.msg_scroll);
            sv.scrollTo(0, msgList.getHeight());
        }
        return true;
    }
}