package com.jqyzyh.learn;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Administrator on 2016/11/3.
 */

public class Love extends SurfaceView implements SurfaceHolder.Callback,
        Runnable {


    boolean mbloop = false;
    SurfaceHolder mSurfaceHolder = null;
    private Canvas canvas;
    int miCount = 0;
    int y = 50;

    public Love(Context context) {
        super(context);
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        this.setFocusable(true);
        this.setKeepScreenOn(true);
        mbloop = true;
    }

    public Love(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        this.setFocusable(true);
        this.setKeepScreenOn(true);
        mbloop = true;
    }

    public Love(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        this.setFocusable(true);
        this.setKeepScreenOn(true);
        mbloop = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Love(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        this.setFocusable(true);
        this.setKeepScreenOn(true);
        mbloop = true;
    }

    /*
    * (non-Javadoc)
    *
    * @see
    * android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder
    * , int, int, int)
    */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    /*
* (non-Javadoc)
*
* @see
* android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder
* )
*/
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        new Thread(this).start();
    }

    /*
* (non-Javadoc)
*
* @seeandroid.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.
* SurfaceHolder)
*/
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        mbloop = false;
    }

    /*
* (non-Javadoc)
*
* @see java.lang.Runnable#run()
*/
    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (mbloop) {
            try {
                Thread.sleep(200);
            } catch (Exception e) {
                // TODO: handle exception
            }
            synchronized (mSurfaceHolder) {
                Draw();
            }
        }
    }

    /**
     * Year:2011 Date:2011-7-27 Time:下午06:52:04 Author:CZ TODO
     */
    private void Draw() {
        // TODO Auto-generated method stub
        canvas = mSurfaceHolder.lockCanvas();
        try {
            if (mSurfaceHolder == null || canvas == null) {
                return;
            }
            if (miCount < 100) {
                miCount++;
            } else {
                miCount = 0;
            }
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            canvas.drawARGB(255, 0, 0, 0);
            switch (miCount % 6) {
                case 0:
                    paint.setColor(Color.BLUE);
                    break;
                case 1:
                    paint.setColor(Color.GREEN);
                    break;
                case 2:
                    paint.setColor(Color.RED);
                    break;
                case 3:
                    paint.setColor(Color.YELLOW);
                    break;
                case 4:
                    paint.setColor(Color.argb(255, 255, 181, 216));
                    break;
                case 5:
                    paint.setColor(Color.argb(255, 0, 255, 255));
                    break;
                default:
                    paint.setColor(Color.WHITE);
                    break;
            }
            int i, j;
            double x, y, r;

            for (i = 0; i <= 90; i++) {
                for (j = 0; j <= 90; j++) {
                    r = Math.PI / 45 * i * (1 - Math.sin(Math.PI / 45 * j))
                            * 50;
                    x = r * Math.cos(Math.PI / 45 * j)
                            * Math.sin(Math.PI / 45 * i) + getWidth() / 2;
                    y = -r * Math.sin(Math.PI / 45 * j) + getHeight() / 4;
                    canvas.drawPoint((float) x, (float) y, paint);
                }
            }

            paint.setTextSize(32);
            paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.ITALIC));

            RectF rect = new RectF(60, 400, 260, 405);
            canvas.drawRoundRect(rect, (float) 1.0, (float) 1.0, paint);
            canvas.drawText("Loving You", 75, 400, paint);
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        } catch (Exception e) {
        }

    }


}
