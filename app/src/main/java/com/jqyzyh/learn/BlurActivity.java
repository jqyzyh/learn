package com.jqyzyh.learn;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

public class BlurActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur);

        getWindow().getDecorView().getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                Log.d("mylog", "onDraw");
            }
        });
        TextView tv = (TextView) findViewById(R.id.tv);
        tv.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                Log.d("mylog", "tv onPreDraw");
                return true;
            }
        });
        tv.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                Log.d("mylog", "tv onDraw");
            }
        });
    }

    public void blur(View view){
        Log.d("mylog", "blur");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView tv = (TextView) findViewById(R.id.tv);
                tv.setText(String.valueOf(System.currentTimeMillis()));
            }
        }, 2000);

    }
}
