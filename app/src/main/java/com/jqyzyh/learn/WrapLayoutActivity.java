package com.jqyzyh.learn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Random;

import jqyzyh.iee.cusomwidget.WrapLayout;

public class WrapLayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrap_layout);
        WrapLayout layout = (WrapLayout) findViewById(R.id.wraplayout);
        for(int i = 0; i < 20 ; i ++){
            TextView tv = new TextView(this);
            tv.setText(getTextString());
            tv.setBackgroundColor(0x80ff0000);
            layout.addView(tv);
        }
    }

    public String getTextString(){
        int rand = 1 + new Random().nextInt(10);
        String ret = "";
        for(int i = 0; i < rand; i ++){
            ret += "一";
        }
        return ret;
    }
}
