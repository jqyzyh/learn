package com.jqyzyh.learn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Random;

import jqyzyh.iee.cusomwidget.WordWrapLayout;

public class WrapLayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrap_layout);
        WordWrapLayout layout = (WordWrapLayout) findViewById(R.id.wraplayout);
        layout.setAdapter(new Adapter());
    }

    public String getTextString() {
        int rand = 1 + new Random().nextInt(10);
        String ret = "";
        for (int i = 0; i < rand; i++) {
            ret += "一";
        }
        return ret;
    }

    class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv;
            if (convertView == null) {
                tv = new TextView(WrapLayoutActivity.this);
                convertView = tv;
                tv.setBackgroundColor(0x80ff0000);
            } else {
                tv = (TextView) convertView;
            }
            tv.setText(getTextString());
            return tv;
        }
    }
}
