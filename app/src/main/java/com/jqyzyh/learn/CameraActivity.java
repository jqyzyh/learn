package com.jqyzyh.learn;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaCodec;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import jqyzyh.iee.camera.CameraHandler;
import jqyzyh.iee.camera.HolderCallback;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    private SurfaceView surfaceView;

    private CameraHandler cameraHandler;
    private HolderCallback holderCallback;
    MediaRecorder mediaRecorder = new MediaRecorder();

    SurfaceHolder surfaceHolder;

    ImageView ivPic;
    File imagePath;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ivPic = (ImageView) findViewById(R.id.iv_zhaopian);
//        surfaceView = (SurfaceView) findViewById(R.id.sf_camera);
//        cameraHandler = new CameraHandler();
//        holderCallback = new HolderCallback();
//        surfaceView.getHolder().addCallback(holderCallback);
//        cameraHandler.openCamera(this);
//        holderCallback.setCamera(cameraHandler);
        File dir = Environment.getExternalStoragePublicDirectory("pic");
        if (!dir.exists()) {
            dir.mkdir();
        }
        imagePath = new File(dir, "1234.jpg");
    }

    public void zhaoxiang(View v){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".FileProvider", imagePath);
        Log.e("mylog", "zhaoxiang=>" +uri.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (1 == requestCode){
            if (RESULT_OK == resultCode) {


                /*String path = MediaUtils.saveImage(getContext(), new File(mCameraPath));
                path = MediaUtils.uriToFilePath(getContext(), Uri.parse(path));*/
//                String path = MediaUtils.refreshPhoto(new File(mCameraPath), getContext());
//                getDelegate().onTaskPhoto(mCameraPath);
                ivPic.setImageBitmap(BitmapFactory.decodeFile(imagePath.getAbsolutePath()));
            }
        }
    }

    Paint paint = new Paint();

    Runnable runnable =new Runnable() {


        @Override
        public void run() {
            while(surfaceHolder != null){
                try {
                    Thread.sleep(50);
                    if(surfaceHolder!= null){
                        Canvas canvas = surfaceHolder.lockCanvas();
                        if(canvas != null){
                            Log.d("mylog", "mylog");
                            paint.setColor(Color.WHITE);
                            paint.setStyle(Paint.Style.FILL);
                            canvas.drawARGB(255,0,0,0);
                            canvas.drawARGB(255,255,255,255);
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        cameraHandler.setSurface2(holder);
//        surfaceHolder = holder;
        new Thread(runnable).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceHolder = null;
    }
}
