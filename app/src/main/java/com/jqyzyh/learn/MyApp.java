package com.jqyzyh.learn;

import android.app.Application;

/**
 * @author yuhang
 */

public class MyApp extends Application{

    public static MyApp static_instance;

    @Override
    public void onCreate() {
        super.onCreate();
        static_instance = this;
    }
}
