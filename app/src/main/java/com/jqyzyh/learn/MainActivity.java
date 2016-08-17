package com.jqyzyh.learn;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import jqyzyh.iee.cusomwidget.iospupopmenu.IOSPupopMenu;
import jqyzyh.iee.schedulemanager.CalendarUtils;

public class MainActivity extends AppCompatActivity {
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar calendar =Calendar.getInstance();


        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Log.d("mylog", "1===>" +format.format(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        Log.d("mylog", "2===>" +format.format(calendar.getTime()));
        Log.d("mylog", "2 week===>" + calendar.get(Calendar.WEEK_OF_YEAR));
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        Log.d("mylog", "8===>" +format.format(calendar.getTime()));
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        Log.d("mylog", "8===>" +format.format(calendar.getTime()));


        Calendar calendar1 =Calendar.getInstance();
        Log.d("mylog", "111===>" +format.format(calendar1.getTime()));
        Log.d("mylog", "week 1 ===>" +calendar1.get(Calendar.YEAR) + "," +calendar1.get(Calendar.WEEK_OF_YEAR));

        Calendar calendar2 =Calendar.getInstance();
        calendar2.set(Calendar.WEEK_OF_YEAR, 1);
        calendar2.add(Calendar.WEEK_OF_YEAR, -1);
        Log.d("mylog", "222===>" +format.format(calendar2.getTime()));
        Log.d("mylog", "week 2 ===>" +calendar2.get(Calendar.YEAR) + "," + calendar2.get(Calendar.WEEK_OF_YEAR));


        Calendar calendar3 =Calendar.getInstance();
        calendar3.add(Calendar.YEAR, 1);
        calendar3.set(Calendar.WEEK_OF_YEAR, 1);
        calendar3.add(Calendar.WEEK_OF_YEAR, -1);
        Log.d("mylog", "333===>" +format.format(calendar3.getTime()));
        Log.d("mylog", "week 3 ===>" +calendar3.get(Calendar.YEAR) + "," +calendar3.get(Calendar.WEEK_OF_YEAR));

        Calendar calendar4 =Calendar.getInstance();
        calendar4.set(Calendar.WEEK_OF_YEAR, 22);
        Log.d("mylog", "444===>" +format.format(calendar4.getTime()));
        Log.d("mylog", "week 4 ===>"+calendar4.get(Calendar.YEAR) + ","  +calendar4.get(Calendar.WEEK_OF_YEAR));

        Log.d("mylog", "count1==>" + CalendarUtils.getWeekOffset(calendar1, calendar2));
        Log.d("mylog", "count2==>" + CalendarUtils.getWeekOffset(calendar1, calendar3));
        Log.d("mylog", "count3==>" + CalendarUtils.getWeekOffset(calendar1, calendar4));


        calendar2.add(Calendar.WEEK_OF_YEAR, -CalendarUtils.getWeekOffset(calendar1, calendar2));
        Log.d("mylog", "week 22 ===>" +calendar2.get(Calendar.YEAR) + "," + calendar2.get(Calendar.WEEK_OF_YEAR));
        calendar3.add(Calendar.WEEK_OF_YEAR, -CalendarUtils.getWeekOffset(calendar1, calendar3));
        Log.d("mylog", "week 33 ===>" +calendar3.get(Calendar.YEAR) + "," + calendar3.get(Calendar.WEEK_OF_YEAR));
        calendar4.add(Calendar.WEEK_OF_YEAR, -CalendarUtils.getWeekOffset(calendar1, calendar4));
        Log.d("mylog", "week 44 ===>" +calendar4.get(Calendar.YEAR) + "," + calendar4.get(Calendar.WEEK_OF_YEAR));






    }

    public void showimage(View v){
        startActivity(new Intent(this, ImageActivity.class));
    }

    public void schedule(View v){
        startActivity(new Intent(this, ScheduleActivity.class));
    }

    public void camera(View v){
        startActivity(new Intent(this, CameraActivity.class));
    }

    public void webview(View v){
        startActivity(new Intent(this, WebViewActivity.class));
    }

    public void test(View v){

        new AlertDialog.Builder(this).setTitle("test").setMessage("登录是家乐福四级联考积分可").create().show();

        startActivity(new Intent(this, PathPaintActivity.class));
        if(true){
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
                    byte[] buffer = new byte[4*1024];
                    int len ;

                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    while((len = is.read(buffer)) != -1){
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

    public void sendBinBin(){
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
            byte[] buffer = new byte[4*1024];
            int len ;
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            while((len = is.read(buffer)) != -1){
                os.write(buffer, 0, len);
                os.flush();
            }
            final String string = new String(os.toByteArray());
            Log.d("mylog", "response ====>" + string);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
