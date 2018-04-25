package com.jqyzyh.mdemo;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView iv = (ImageView)findViewById(R.id.iv);

//GlideApp.with(this).load("http://pic.ifore.com.cn/004/045/226/00404522620_ea1b1b64.jpg").into(iv);



        Log.e("mylog", "super==>" + super.getClass().getSuperclass());
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//
//        getResources().updateConfiguration(null, null);
    }

    public void clickweb(View v){
        startActivity(new Intent(this, WebActivity.class));
    }

    public void click1(View v){
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        Log.e("mylog", "click1==>");
        File f = Environment.getExternalStoragePublicDirectory("test");
        if (!f.exists()) {
            f.mkdir();
        }

        f = new File(f, "aaa.txt");
        if (f.exists()) {
            f.delete();
        }
        try {
            f.createNewFile();
            FileOutputStream os = new FileOutputStream(f);
            os.write("aldfakld".getBytes());
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        Camera.open();
    }

    public void click2(View v){
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
