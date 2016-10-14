package com.jqyzyh.learn;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import jqyzyh.iee.cusomwidget.canvasview.CanvasView;

public class PathPaintActivity extends AppCompatActivity {

    CanvasView canvasView;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_paint);
        canvasView = (CanvasView) findViewById(R.id.cv);
        imageView = (ImageView) findViewById(R.id.iv);

    }

    public void claerPath(View v){
        canvasView.clearPath();
    }

    public void save(View v){
        imageView.setImageBitmap(canvasView.createBitmap());
        imageView.setDrawingCacheEnabled(true);
        imageView.getDrawingCache();
        File file = Environment.getExternalStoragePublicDirectory("learn");
        if(!file.exists()){
            file.mkdir();
        }

        file = new File(file, "test.jpg");
        if(file.exists()){
            file.delete();
        }
        try {
            FileOutputStream os = new FileOutputStream(file);

            Bitmap bitmap = canvasView.createBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
