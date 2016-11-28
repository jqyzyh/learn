package com.jqyzyh.learn;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;

import jqyzyh.iee.cusomwidget.utils.LogUtils;

public class PlaySoundActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    private TextView tv_name;

    SeekBar sb;

    boolean touchseek;

    boolean running = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_sound);
        sb = (SeekBar) findViewById(R.id.sb);
        tv_name = (TextView) findViewById(R.id.tv_name);
        initMediaPlayer();
    }

    @Override
    protected void onDestroy() {
        stop(null);
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initMediaPlayer() {

        Cursor c = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA}, null, null, null);
        String path = "";
        String str1 = null;
        String str2 = null;
        while (c.moveToNext()) {
            path = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
            LogUtils.d("PlaySoundActivity", "initMediaPlayer==>" + path);
            if (str1 == null) {
                str1 = path;
            }else{

                if (str2 == null) {
                    str2 = path;
                }
            }
        }
        tv_name.setText(path);

//        mediaPlayer = MediaPlayer.create(this, Uri.fromFile(new File(str1)));

        mediaPlayer = MediaPlayer.create(this, Uri.parse("http://60.205.104.120:3030/historical/atths/org/000/000/040/694/audio/291aaf8f-2eea-4889-a595-4bf420be2d57.mp3"));

        int duration = mediaPlayer.getDuration();
        sb.setMax(duration);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                touchseek = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
                touchseek = false;
            }
        });

        mediaPlayer.setNextMediaPlayer(MediaPlayer.create(this, Uri.fromFile(new File(str2))));

        mediaPlayer.setOnTimedTextListener(new MediaPlayer.OnTimedTextListener() {
            @Override
            public void onTimedText(MediaPlayer mp, TimedText text) {

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });
    }

    Handler handler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            long now = SystemClock.uptimeMillis();
            long next = now + (1000 - now % 1000);
            if (mediaPlayer.isPlaying() && !touchseek) {

                sb.setProgress(mediaPlayer.getCurrentPosition());

            }
            if(running){
                handler.postAtTime(this, next);
            }
        }
    };

    public void start(View v) {
        running = true;
        mediaPlayer.start();
        handler.postDelayed(runnable, 1000);
    }

    public void stop(View v) {
        running = false;
        mediaPlayer.stop();
    }

    public void tingtong(View v) {
        LogUtils.d("PlaySoundActivity", "tingtong");
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(false);//关闭扬声器
//        audioManager.setRouting(AudioManager.MODE_NORMAL, AudioManager.ROUTE_EARPIECE, AudioManager.ROUTE_ALL);
//        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        //把声音设定成Earpiece（听筒）出来，设定为正在通话中
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    public void yangshengqi(View v) {
        LogUtils.d("PlaySoundActivity", "yangshengqi");
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(true);
    }
}
