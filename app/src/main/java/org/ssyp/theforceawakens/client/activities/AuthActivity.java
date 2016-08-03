package org.ssyp.theforceawakens.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import org.ssyp.theforceawakens.R;
import org.ssyp.theforceawakens.client.utils.Utils;
import org.ssyp.theforceawakens.commands.Command;
import org.ssyp.theforceawakens.connection.Client;
import org.ssyp.theforceawakens.connection.ConnectionFailureCause;
import org.ssyp.theforceawakens.connection.ConnectionListener;

import static org.ssyp.theforceawakens.commands.servercommands.AuthBusyCommand.AUTH_BUSY_COMMAND;
import static org.ssyp.theforceawakens.commands.servercommands.AuthOKCommand.AUTH_OK_COMMAND;
import static org.ssyp.theforceawakens.commands.servercommands.AuthTakenCommand.AUTH_TAKEN_COMMAND;
import static org.ssyp.theforceawakens.connection.ConnectionFailureCause.NicknameTaken;
import static org.ssyp.theforceawakens.connection.ConnectionFailureCause.ServerBusy;

public class AuthActivity extends AppCompatActivity {
    private static volatile ConnectionFailureCause cause = null;

    public static void setFailureCause(ConnectionFailureCause cause) {
        AuthActivity.cause = cause;
    }

    private Thread notificationThread;

    ////////////////////////////////////////////////////////

    private boolean isAuthorizing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Magic. Do not touch!
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_auth);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        notificationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                    if (AuthActivity.cause != null) {
                        Utils.alert(AuthActivity.cause, AuthActivity.this);

                        AuthActivity.cause = null;
                    }
            }
        }, "client-notification-thread");
        notificationThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        notificationThread.interrupt();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void sendAuthorizeRequest(View view) throws Exception {
        synchronized (this) {
            if (isAuthorizing) return;
            isAuthorizing = true;
        }

        if (Client.getInstance().connection != null)
            if (Client.getInstance().connection.isEnabled())
                return;

        EditText nicknameEditText = (EditText) findViewById(R.id.editNickname);
        EditText ipEditText = (EditText) findViewById(R.id.editIP);

        String nickname = nicknameEditText.getText().toString();
        String ip = ipEditText.getText().toString();

        ConnectionListener authListener = new ConnectionListener() {

            @Override
            public void onCommandReceived(Command jsonCommand) {
                switch (jsonCommand.command) {
                    case AUTH_OK_COMMAND:
                        Intent intent = new Intent(AuthActivity.this, WaitingActivity.class);
                        AuthActivity.this.startActivity(intent);
                        break;

                    case AUTH_BUSY_COMMAND:
                        AuthActivity.setFailureCause(ServerBusy);
                        break;

                    case AUTH_TAKEN_COMMAND:
                        AuthActivity.setFailureCause(NicknameTaken);
                        break;

                    default:
                        break;
                }

                synchronized (this) {
                    isAuthorizing = false;
                }
            }

            @Override
            public void onConnectionFailed(ConnectionFailureCause cause) {
                synchronized (this) {
                    isAuthorizing = false;
                }
                Client.getInstance().connection = null;
                AuthActivity.setFailureCause(cause);
            }
        };
        Client.getInstance().connect(ip, nickname, authListener);
    }
}
