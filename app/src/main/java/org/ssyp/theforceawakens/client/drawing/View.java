package org.ssyp.theforceawakens.client.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class View extends SurfaceView implements SurfaceHolder.Callback, android.view.View.OnTouchListener {
    private Surface drawThread;
    protected Paint paint;

    public View(Context context, AttributeSet attr) {
        super(context, attr);

        getHolder().addCallback(this);
        setOnTouchListener(this);

        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(24);
        paint.setTextAlign(Paint.Align.LEFT);
    }

    public abstract void onDraw(Canvas canvas);

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread = new Surface(this);
        drawThread.setRunning(true);
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        drawThread.setRunning(false);
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public boolean onTouch(android.view.View v, MotionEvent event) {
        return false;
    }
}
