package com.jqyzyh.learn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.widget.EditText;

import jqyzyh.iee.commen.inpututils.FirstCannotSpaceInputFilter;

public class InputFilterActivity extends AppCompatActivity {
    static final String TAG = "InputFilterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_filter);

        EditText et= (EditText) findViewById(R.id.et);
        et.setFilters(new InputFilter[]{new FirstCannotSpaceInputFilter()});
    }

    class MyLIF extends InputFilter.LengthFilter{

        public MyLIF(int max) {
            super(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Log.d(TAG, "filter source==>" + source);
            Log.d(TAG, "filter  start==>" + start);
            Log.d(TAG, "filter    end==>" + end);
            Log.d(TAG, "filter   dest==>" + dest);
            Log.d(TAG, "filter dstart==>" + dstart);
            Log.d(TAG, "filter   dend==>" + dend);
            CharSequence ret = super.filter(source, start, end, dest, dstart, dend);
            Log.d(TAG, "filter    ret==>" + ret);
            return ret;
        }
    }
}
