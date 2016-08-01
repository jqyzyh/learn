package com.jqyzyh.learn;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    Runnable runnable;

    public void click(View view) {
        if (runnable != null) {
            return;
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                InputStream is = null;
                try {
                    Log.d("mylog", "start=========>" + System.currentTimeMillis());
                    URL url = new URL("http://demo.enorth.cn:9000/gov_open/imgcode");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setReadTimeout(10 * 60000);

                    connection.setConnectTimeout(10 * 60000);
                    connection.connect();
                    Log.d("mylog", "connect=========>" + System.currentTimeMillis());

                    is = connection.getInputStream();
                    byte[] buffer = new byte[8 * 1024];
                    int len;
                    while((len = is.read(buffer)) != -1){
                        Log.d("mylog", "download=========>" + System.currentTimeMillis());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if(is != null){
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(connection != null){
                        connection.disconnect();
                    }
                }


                runnable = null;
            }
        };


        new Thread(runnable).start();
    }


}
