package org.ssyp.theforceawakens.client.elements;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.AttributeSet;

import org.ssyp.theforceawakens.client.GPS.GPS;
import org.ssyp.theforceawakens.client.drawing.View;
import org.ssyp.theforceawakens.client.utils.Utils;
import org.ssyp.theforceawakens.game.Checkpoint;
import org.ssyp.theforceawakens.game.Position;
import org.ssyp.theforceawakens.json.JSONCheckpoint;
import org.ssyp.theforceawakens.json.JSONPlayer;

import java.util.ArrayList;
import java.util.List;

import static org.ssyp.theforceawakens.game.Team.Neutral;

public class MapView extends View {

    private static final int BACKGROUND_COLOR = Color.rgb(180, 180, 205);

    public static final int SITH_CHECKPOINT = Color.rgb(230, 50, 50);
    public static final int SITH_PLAYER = Color.rgb(200, 0, 0);
    public static final int CURRENT_PLAYER = Color.rgb(255, 255, 255);
    public static final int JEDI_PLAYER = Color.rgb(1, 1, 100);
    public static final int JEDI_CHECKPOINT = Color.rgb(90, 90, 200);
    public static final int NEUTRAL_TEAM = Color.rgb(150, 150, 150);

    private static final int CHECKPOINT_RADIUS = 30;
    private static final int PLAYER_RADIUS = 10;

    private float width, height, scaling;

    private List<JSONCheckpoint> checkpoints = new ArrayList<>();
    private List<JSONPlayer> players = new ArrayList<>();

    public MapView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    @Override
    public void onDraw(Canvas canvas) {
        width = getWidth();
        height = getHeight();
        scaling = width / 500.0f;

        paint.setColor(BACKGROUND_COLOR);
        canvas.drawRect(0, 0, width, height, paint);

        synchronized (this) {
            for (JSONCheckpoint checkpoint : checkpoints) {
                drawCheckpoint(canvas, checkpoint);
            }
            for (JSONPlayer player : players) {
                drawPlayer(canvas, player);
            }
        }
    }

    public void updateMap(List<JSONPlayer> players, List<JSONCheckpoint> checkpoints) {
        // FIXME: implement me
        synchronized (this) {
            this.players = players;
            this.checkpoints = checkpoints;
        }
    }

    public void drawCheckpoint(Canvas canvas, JSONCheckpoint checkpoint) {
        // FIXME: remove fucking magic
        float x = GPS.fromLongitude(checkpoint.getPosition().getLongitude(), scaling);
        float y = GPS.fromLatitude(checkpoint.getPosition().getLatitude(), scaling);

        int allegianceColor = Utils.getTeamCheckpointColor(checkpoint.getAllegiance());
        int capturingColor = Utils.getTeamCheckpointColor(checkpoint.getCapturingTeam());

        paint.setColor(capturingColor);


        int i = (int) ((float) checkpoint.getHp() / (float) Checkpoint.MAX_HP * 360.0f);

        float left = width / 2 + x - CHECKPOINT_RADIUS;
        float top = height / 2 - (y + CHECKPOINT_RADIUS);
        float right = width / 2 + x + CHECKPOINT_RADIUS;
        float bottom = height / 2 - (y - CHECKPOINT_RADIUS);

//        Log.e("TFA", "will draw to " + left + ", " + top + ", " + right + ", " + bottom + ".");

        canvas.drawArc(new RectF(left, top, right, bottom), 270, i, true, paint);
        if (capturingColor == allegianceColor) {
            paint.setColor(Utils.getTeamCheckpointColor(Neutral));
        } else {
            paint.setColor(allegianceColor);
        }
        canvas.drawArc(new RectF(width / 2 + x - CHECKPOINT_RADIUS,
                height / 2 - (y + CHECKPOINT_RADIUS),
                width / 2 + x + CHECKPOINT_RADIUS,
                height / 2 - (y - CHECKPOINT_RADIUS)), 270 + i, 360 - i, true, paint);
        paint.setColor(Color.WHITE);
    }

    public void drawPlayer(Canvas canvas, JSONPlayer player) {
        // FIXME: remove fucking magic
        float x = GPS.fromLongitude(player.getPosition().getLongitude(), scaling);
        float y = GPS.fromLatitude(player.getPosition().getLatitude(), scaling);

        if (player.getPosition().equals(new Position(x, y))) {
            paint.setColor(CURRENT_PLAYER);
        } else {
            paint.setColor(Utils.getTeamPlayerColor(player.getTeam()));
        }
        canvas.drawArc(new RectF(width / 2 + x - PLAYER_RADIUS,
                height / 2 - (y - PLAYER_RADIUS),
                width / 2 + x + PLAYER_RADIUS,
                height / 2 - (y + PLAYER_RADIUS)), 0, 360, true, paint);
        paint.setColor(Color.WHITE);
    }
}
