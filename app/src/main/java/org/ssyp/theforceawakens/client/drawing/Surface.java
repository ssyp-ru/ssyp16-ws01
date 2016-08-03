package org.ssyp.theforceawakens.client.drawing;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class Surface extends Thread {
    private boolean running = false;
    private View view;

    public Surface(View view) {
            this.view = view;
        }

    public void setRunning(boolean run) {
            running = run;
        }

    public void run() {
        Canvas canvas;
        SurfaceHolder holder = view.getHolder();

        while (running) {
            canvas = null;

            try {
                canvas = holder.lockCanvas(null);
                if (canvas == null)
                    continue;
                view.onDraw(canvas);
//
//                for (Checkpoint c : Map1.getCheckpoints()) {
//                    JSONCheckpoint jcp = new JSONCheckpoint(c);
//                    ((MapView) view).drawCheckpoint(canvas, jcp);
//                }
//
//                double diff = (Math.random() - 0.5) * 0.001;
//                JSONPlayer player = new JSONPlayer("tester", Team.Sith, new Position(54.614010 + diff, 82.727908 + diff), PlayerState.Roaming);
//                ((MapView) view).drawPlayer(canvas, player);
//
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
