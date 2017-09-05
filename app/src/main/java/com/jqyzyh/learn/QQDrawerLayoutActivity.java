package com.jqyzyh.learn;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

/**
 * @author jqyyzh
 */

public class QQDrawerLayoutActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qq_drawer_layout);
        Log.d("QQDrawerLayoutActivity", " onCreate");
    }
}
