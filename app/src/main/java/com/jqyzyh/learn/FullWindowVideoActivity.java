package com.jqyzyh.learn;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import jqyzyh.iee.cusomwidget.canfullmedia.IMediaPlayerDelegate;
import jqyzyh.iee.cusomwidget.canfullmedia.MediaPlayerManagener;
import jqyzyh.iee.cusomwidget.canfullmedia.YHVideoView;

/**
 * Created by jqyzyh on 2016/12/9.
 */

public class FullWindowVideoActivity extends Activity {
    public static final String EXTRA_PATH = "path";

    private YHVideoView videoView;

    IMediaPlayerDelegate delegate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullwindow_video);

        videoView = (YHVideoView) findViewById(R.id.yhmeida_videoview);

        delegate = MediaPlayerManagener.getInstance().getMedia(this, getIntent().getStringExtra(EXTRA_PATH));
        if(delegate == null){
            Log.e("mylog", "没有资源");
            finish();
            return;
        }

        videoView.setMediaPlayerDelegate(delegate);
        videoView.post(new Runnable() {
            @Override
            public void run() {
                delegate.start();
            }
        });
    }

    public void play(View v){
        delegate.start();
    }

}
