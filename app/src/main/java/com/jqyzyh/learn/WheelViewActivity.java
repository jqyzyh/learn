package com.jqyzyh.learn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import jqyzyh.iee.cusomwidget.wheelloop.LoopView;

public class WheelViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel_view);

        LoopView loopView = (LoopView) findViewById(R.id.test);

        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            list.add("item " + i);
        }
        loopView.setItems(list);
        loopView.setTextSize(30);

    }
}
