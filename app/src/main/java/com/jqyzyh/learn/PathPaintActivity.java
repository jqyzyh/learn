package com.jqyzyh.learn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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
    }
}
