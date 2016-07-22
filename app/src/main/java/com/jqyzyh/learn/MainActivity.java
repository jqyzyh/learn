package com.jqyzyh.learn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import jqyzyh.iee.schedulemanager.CalendarUtils;

public class MainActivity extends AppCompatActivity {

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
}
