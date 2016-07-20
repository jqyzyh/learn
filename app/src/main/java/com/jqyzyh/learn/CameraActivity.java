package com.jqyzyh.learn;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaCodec;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import jqyzyh.iee.camera.CameraHandler;
import jqyzyh.iee.camera.HolderCallback;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    private SurfaceView surfaceView;

    private CameraHandler cameraHandler;
    private HolderCallback holderCallback;
    MediaRecorder mediaRecorder = new MediaRecorder();

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        SurfaceView sv = (SurfaceView) findViewById(R.id.sf_camera2);
        sv.getHolder().addCallback(this);

//        surfaceView = (SurfaceView) findViewById(R.id.sf_camera);
//        cameraHandler = new CameraHandler();
//        holderCallback = new HolderCallback();
//        surfaceView.getHolder().addCallback(holderCallback);
//        cameraHandler.openCamera(this);
//        holderCallback.setCamera(cameraHandler);


    }

    Paint paint = new Paint();

    SurfaceHolder surfaceHolder;

    Runnable runnable =new Runnable() {


        @Override
        public void run() {
            try {
                Thread.sleep(50);
                if(surfaceHolder!= null){
                    Canvas canvas = surfaceHolder.lockCanvas(new Rect(0,0,1000,1000));
                    if(canvas != null){
                        Log.d("mylog", "mylog");
                        paint.setColor(Color.WHITE);
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawARGB(255,255,255,255);
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        cameraHandler.setSurface2(holder);
        surfaceHolder = holder;
        new Thread(runnable).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
