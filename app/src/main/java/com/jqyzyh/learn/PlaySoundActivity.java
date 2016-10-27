package com.jqyzyh.learn;

import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import jqyzyh.iee.cusomwidget.utils.LogUtils;

public class PlaySoundActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    private TextView tv_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_sound);
        tv_name = (TextView) findViewById(R.id.tv_name);
        initMediaPlayer();
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.stop();
        super.onDestroy();
    }

    private void initMediaPlayer(){

        Cursor c = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,  new String[] { MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA }, null, null, null);
        String path = "";
        while (c.moveToNext()){
            path = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
            LogUtils.d("PlaySoundActivity", "initMediaPlayer==>" + path);
        }
        tv_name.setText(path);

        mediaPlayer = MediaPlayer.create(this, Uri.fromFile(new File(path)));

    }

    public void start(View v){
        mediaPlayer.start();
    }

    public void stop(View v){
        mediaPlayer.stop();
    }

    public void tingtong(View v){
        LogUtils.d("PlaySoundActivity", "tingtong" );
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(false);//关闭扬声器
//        audioManager.setRouting(AudioManager.MODE_NORMAL, AudioManager.ROUTE_EARPIECE, AudioManager.ROUTE_ALL);
//        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        //把声音设定成Earpiece（听筒）出来，设定为正在通话中
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    public void yangshengqi(View v){
        LogUtils.d("PlaySoundActivity", "yangshengqi");
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(true);
    }
}
