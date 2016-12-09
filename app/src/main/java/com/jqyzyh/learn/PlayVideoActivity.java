package com.jqyzyh.learn;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;

import java.io.File;

import jqyzyh.iee.cusomwidget.canfullmedia.IMediaPlayerDelegate;
import jqyzyh.iee.cusomwidget.canfullmedia.MediaPlayerManagener;
import jqyzyh.iee.cusomwidget.canfullmedia.YHVideoView;

import static com.jqyzyh.learn.FullWindowVideoActivity.EXTRA_PATH;

public class PlayVideoActivity extends Activity {

    YHVideoView videoView;

    private String path;

    IMediaPlayerDelegate media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        videoView = (YHVideoView) findViewById(R.id.videoview);

        openVideo();
    }

    private void openVideo(){

        File file = new File(Environment.getExternalStorageDirectory(), "DCIM");
        path = "file://" + new File(file, "abcd.mp4").getAbsolutePath();
        media = MediaPlayerManagener.getInstance().getMedia(this, path);
        videoView.setMediaPlayerDelegate(media);
    }

    public void play(View v){
        media.start();
    }

    public void full(View v){
        Intent intent = new Intent(this, FullWindowVideoActivity.class);
        intent.putExtra(EXTRA_PATH, path);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        MediaPlayerManagener.getInstance().releaseMedia(path);
        super.onDestroy();
    }
}
