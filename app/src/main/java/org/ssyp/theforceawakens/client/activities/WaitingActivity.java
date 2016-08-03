package org.ssyp.theforceawakens.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ssyp.theforceawakens.R;
import org.ssyp.theforceawakens.commands.Command;
import org.ssyp.theforceawakens.commands.clientcommands.ChangeTeamCommand;
import org.ssyp.theforceawakens.commands.clientcommands.ListenRosterUpdateCommand;
import org.ssyp.theforceawakens.commands.clientcommands.MsgCommand;
import org.ssyp.theforceawakens.commands.servercommands.RosterUpdateCommand;
import org.ssyp.theforceawakens.connection.Client;
import org.ssyp.theforceawakens.connection.ConnectionFailureCause;
import org.ssyp.theforceawakens.connection.ConnectionListener;
import org.ssyp.theforceawakens.json.JSONRoster;
import org.ssyp.theforceawakens.json.JSONRosterPlayer;

import java.util.List;

import static org.ssyp.theforceawakens.commands.clientcommands.MsgCommand.MSG_COMMAND;
import static org.ssyp.theforceawakens.commands.servercommands.GameStartedCommand.GAME_STARTED_COMMAND;
import static org.ssyp.theforceawakens.commands.servercommands.RosterUpdateCommand.ROSTER_UPDATE_COMMAND;
import static org.ssyp.theforceawakens.commands.servercommands.WorldUpdateCommand.WORLD_UPDATE_COMMAND;
import static org.ssyp.theforceawakens.connection.ConnectionFailureCause.Disconnected;

public class WaitingActivity extends AppCompatActivity implements View.OnTouchListener {
    private LinearLayout jediLinearLayout;
    private LinearLayout sithLinearLayout;
    private LinearLayout msgList_waiting;
    private TextView chatTitle_waiting;
    private int screenHeight_waiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Magic. Do not touch!
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_waiting);


        jediLinearLayout = ((LinearLayout) findViewById(R.id.jediLinearLayout));
        sithLinearLayout = ((LinearLayout) findViewById(R.id.sithLinearLayout));

        screenHeight_waiting = getResources().getDisplayMetrics().heightPixels;

        msgList_waiting = (LinearLayout) findViewById(R.id.msg_list_waiting);
        chatTitle_waiting = (TextView) findViewById(R.id.chat_title_waiting);
        chatTitle_waiting.setOnTouchListener(this);

        Button sendButton = (Button) findViewById(R.id.button_send_waiting);
        sendButton.setOnTouchListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
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

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Client.getInstance().connection.setConnectionListener(new ConnectionListener() {

            @Override
            public void onCommandReceived(Command jsonCommand) {
                switch (jsonCommand.command) {
                    case MSG_COMMAND:
                        MsgCommand msgCommand = (MsgCommand) jsonCommand;
                        modMessage(msgCommand.message);
                        break;

                    case ROSTER_UPDATE_COMMAND:
                        final JSONRoster roster = ((RosterUpdateCommand) jsonCommand).data;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                WaitingActivity.this.addPlayersToLinearLayout(jediLinearLayout, roster.getJediList());
                                WaitingActivity.this.addPlayersToLinearLayout(sithLinearLayout, roster.getSithList());
                            }
                        });

                        break;

                    case GAME_STARTED_COMMAND:
                        WaitingActivity.this.startActivity(new Intent(WaitingActivity.this, GameActivity.class));
                        break;

                    case WORLD_UPDATE_COMMAND:
                        WaitingActivity.this.startActivity(new Intent(WaitingActivity.this, GameActivity.class));
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onConnectionFailed(ConnectionFailureCause cause) {
                AuthActivity.setFailureCause(cause);
                Client.getInstance().connection = null;

                WaitingActivity.this.finish();
            }
        });
        Client.getInstance().connection.sendCommand(new ListenRosterUpdateCommand());
    }

    public void modMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout msgList = (LinearLayout) findViewById(R.id.msg_list_waiting);
                TextView textView = new TextView(msgList.getContext());
                textView.setText(message);
                msgList.addView(textView);
            }
        });
    }

    public void changeTeam(View view) {
        Client.getInstance().connection.sendCommand(new ChangeTeamCommand());
    }

    public void addPlayersToLinearLayout(LinearLayout where, List<JSONRosterPlayer> who) {
        where.removeAllViews();
        for (JSONRosterPlayer player : who) {
            TextView text = new TextView(where.getContext());
            text.setText(player.getName());
            text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            text.setTextAppearance(this, android.R.style.TextAppearance_Large);
            where.addView(text);
        }
    }

    public boolean onTouch(View v, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            if (v.getId() == R.id.button_send_waiting) {
                EditText editText = (EditText) findViewById(R.id.edit_text_waiting);
                String text = editText.getText().toString();
                editText.setText("");
                Client.getInstance().connection.sendCommand(new MsgCommand(text));
            }
        }
        return true;
    }
}
