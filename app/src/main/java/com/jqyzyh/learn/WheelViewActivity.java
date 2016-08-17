package com.jqyzyh.learn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import jqyzyh.iee.cusomwidget.wheelview.CalanderDataWraper;
import jqyzyh.iee.cusomwidget.wheelview.WheelView;

public class WheelViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel_view);

        WheelView wheelView = (WheelView) findViewById(R.id.test);
        wheelView.setDataWraper(new CalanderDataWraper());
    }
}
