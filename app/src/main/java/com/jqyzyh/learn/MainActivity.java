package com.jqyzyh.learn;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.UiModeManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.widget.GridView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import jqyzyh.iee.cusomwidget.iospupopmenu.IOSPupopMenu;
import jqyzyh.iee.cusomwidget.utils.LogUtils;

public class MainActivity extends AppCompatActivity {

    static boolean isnight;

    private String a = "aaa";

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d("mylog", "onCreate==>" + a);
//        if(isnight){
//            Configuration config = getResources().getConfiguration();
//            config.uiMode = Configuration.UI_MODE_NIGHT_YES;
//            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
////            setTheme(R.style.AppTheme_Night);
//        }
        setContentView(R.layout.activity_main);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }


    public List<Method> getMethods(Class cls) {
        List<Method> ret = new ArrayList<>();
        Method[] ms = cls.getMethods();
        for (Method m : ms) {
            ret.add(m);
        }
        if (cls.getSuperclass() != null) {
            ret.addAll(getMethods(cls.getSuperclass()));
        }
        int n = Build.VERSION_CODES.N;
        return ret;

    }


    public void showimage(View v) {
        startActivity(new Intent(this, ImageActivity.class));
    }

    public void schedule(View v) {
        startActivity(new Intent(this, ScheduleActivity.class));
    }

    public void camera(View v) {
        startActivity(new Intent(this, CameraActivity.class));
    }

    public void webview(View v) {
        startActivity(new Intent(this, WebViewActivity.class));
    }

    public void inputFilter(View v) {
        startActivity(new Intent(this, ScrollingActivity.class));
    }

    public void love(View v){
        startActivity(new Intent(this, LoveActivity.class));
    }

    public void playvideo(View v){
        startActivity(new Intent(this, PlayVideoActivity.class));
    }

    public void test(View v) {

//        new AlertDialog.Builder(this).setTitle("test").setMessage("登录是家乐福四级联考积分可").create().show();
//
//        startActivity(new Intent(this, ListAnimHeaderActivity.class));

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.enorth.ifore"));
        startActivity(intent);

//        IOSPupopMenu menu = new IOSPupopMenu(this);
//        menu.addMenu("啊啊啊", Color.BLUE, null);
//        menu.addMenu("啊啊啊", Color.BLUE, null);
//        menu.addMenu("啊啊啊", Color.BLUE, null);
//        menu.show();
        if (true) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });

                try {
                    URL url = new URL("https://apijump.2000cms.cn/api/cms_api_60/Api!liveRooms.do");

                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.setSSLSocketFactory(MNTSSLSocketFactory.getSSLSocketFactory(MainActivity.this));
                    connection.setConnectTimeout(30000);

                    connection.connect();

                    int code = connection.getResponseCode();

                    Log.d("mylog", "getResponseCode==>" + code);

                    InputStream is = connection.getInputStream();
                    byte[] buffer = new byte[4 * 1024];
                    int len;

                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    while ((len = is.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                        os.flush();
                    }

                    String string = new String(os.toByteArray());
                    Log.d("mylog", "response ====>" + string);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    public void sendBinBin() {


        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<tad version=\"1.0\">");
        xml.append("<rq id=\"\" name=\"logon\">");
        xml.append("<info uid=\"setUid\" vcode=\"setVcode\" vid=\"setVid\" time=\"1470816450270\" rcode=\"\" ver=\"setVer\" ctoken=\"setCtoken+longtim\"/>");
        xml.append("</rq>");
        xml.append("</tad>");
        try {
            byte[] xmlbyte = xml.toString().getBytes("utf-8");
            URL url = new URL("http://60.30.156.6:1997/tradefront/trade");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);// 允许输出
            conn.setDoInput(true);
            conn.setUseCaches(false);// 不使用缓存
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Length",
                    String.valueOf(xmlbyte.length));
            conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
            conn.getOutputStream().write(xmlbyte);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();
            int code = conn.getResponseCode();
            Log.d("mylog", "response code ====>" + code);
            InputStream is = conn.getInputStream();
            byte[] buffer = new byte[4 * 1024];
            int len;
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
                os.flush();
            }
            final String string = new String(os.toByteArray());
            Log.d("mylog", "response ====>" + string);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void wheel(View v) {
        startActivity(new Intent(this, WheelViewActivity.class));
    }

    public void playsound(View view) {
        startActivity(new Intent(this, PlaySoundActivity.class));
    }

    public void wrapLayout(View view){
        startActivity(new Intent(this, WrapLayoutActivity.class));
    }

    public void blur(View view){
        startActivity(new Intent(this, BlurActivity.class));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void night(View view){
        LogUtils.d("mylog", "111");
        isnight = true;

//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        MyApp.static_instance.setTheme(R.style.AppTheme_Night);
//        setTheme(R.style.AppTheme_Night);
//        recreate();
//        if(UiModeManager.MODE_NIGHT_YES == modeManager.getCurrentModeType()){
//            modeManager.setNightMode(UiModeManager.MODE_NIGHT_NO);
//        }else{
//            modeManager.setNightMode(UiModeManager.MODE_NIGHT_YES);
//        }
        Configuration config = getResources().getConfiguration();
        config.uiMode ^= Configuration.UI_MODE_NIGHT_YES;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
//        createConfigurationContext(config);
        a = "bbb";
        recreate();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        LogUtils.d("mylog", "onSaveInstanceState");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtils.d("mylog", "onConfigurationChanged");
    }
}
