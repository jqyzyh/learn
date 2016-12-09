package jqyzyh.iee.cusomwidget.canfullmedia;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.view.Surface;

/**
 * Created by jqyzyh on 2016/12/9.
 */

public class MediaPlayerDelegateImpl implements IMediaPlayerDelegate,
        OnPreparedListener, OnCompletionListener,
        OnErrorListener, OnInfoListener,
        OnBufferingUpdateListener {

    /**
     * 错误
     */
    private static final int STATE_ERROR = -1;
    /**
     * 空闲
     */
    private static final int STATE_IDLE = 0;
    /**
     * 加载中
     */
    private static final int STATE_PREPARING = 1;
    /**
     * 加载完
     */
    private static final int STATE_PREPARED = 2;
    /**
     * 播放中
     */
    private static final int STATE_PLAYING = 3;
    /**
     * 暂停
     */
    private static final int STATE_PAUSED = 4;
    /**
     * 播放完
     */
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    private int mCurrentState = STATE_IDLE;//当前状态
    private int mTargetState = STATE_IDLE;//目标状态

    private MediaPlayer mMediaPlayer;

    private Context mContext;

    private Uri mUri;

    private Surface mTargetSurface;

    private Surface mCurSurface;

    private Handler mHandler = new Handler();

    private int mSeekWhenPrepared;
    private int mCurrentBufferPercentage;

    private OnPreparedListener onPreparedListener;
    private OnCompletionListener onCompletionListener;
    private OnErrorListener onErrorListener;
    private OnInfoListener onInfoListener;
    private OnBufferingUpdateListener onBufferingUpdateListener;

    public MediaPlayerDelegateImpl(Context context) {
        mContext = context.getApplicationContext();
    }

    public void loadPath(String path) {
        loadUri(Uri.parse(path));
    }

    public void loadUri(Uri uri) {
        mUri = uri;
        _openVideoAync();
    }

    private void _openVideoAync() {
        if (mCurrentState == STATE_PREPARING) {
            return;
        }

        if (mUri == null) {
            return;
        }

        mCurrentState = STATE_PREPARING;

        new Thread(new Runnable() {
            @Override
            public void run() {
                _openVideo();
            }
        }).start();
    }

    private void _openVideo() {
        try {
            mMediaPlayer = MediaPlayer.create(mContext, mUri);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isInPlaybackState() {
        return (mMediaPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }

    @Override
    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void setSurface(Surface surface) {
        if(mMediaPlayer == null){
            mTargetSurface = surface;
        }else{
            mMediaPlayer.setSurface(surface);
            mCurSurface = surface;
            mTargetSurface = null;
        }
    }

    @Override
    public void destorySurface(Surface surface) {
        if(mCurSurface != surface){
            return;
        }
        setSurface(null);
        if(isPlaying() || (mCurrentState == STATE_PREPARING && mTargetState == STATE_PLAYING)){
            pause();
        }
    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
            mCurrentState = STATE_PLAYING;
            mMediaPlayer.start();
        }
        mTargetState = STATE_PLAYING;
    }

    @Override
    public void stop() {
        if (mMediaPlayer != null) {
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            mCurrentState = STATE_PAUSED;
            mMediaPlayer.pause();
        }
        mTargetState = STATE_PAUSED;
    }

    @Override
    public void release() {
        if(mMediaPlayer != null){
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }
    }

    @Override
    public int getDuration() {
        if(isInPlaybackState()){
            return mMediaPlayer.getDuration();
        }
        return -1;
    }

    @Override
    public int getCurrentPosition() {
        if(isInPlaybackState()){
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        if(isInPlaybackState()){
            mMediaPlayer.seekTo(pos);
            mSeekWhenPrepared = 0;
        }else{
            mSeekWhenPrepared = pos;
        }
    }

    @Override
    public int getBufferPercentage() {
        if(mMediaPlayer != null){
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mCurrentState = STATE_PLAYBACK_COMPLETED;
        mTargetState = STATE_PLAYBACK_COMPLETED;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if(onErrorListener != null && onErrorListener.onError(mp, what, extra)){
            return true;
        }
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        if(onInfoListener != null){
            onInfoListener.onInfo(mp, what, extra);
        }

        return true;
    }

    @Override
    public void onPrepared(final MediaPlayer mp) {

        mCurrentState = STATE_PREPARED;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mTargetSurface != null){
                    setSurface(mTargetSurface);
                }
                if (mTargetState == STATE_PLAYING) {
                    start();
                }
                if(onPreparedListener != null){
                    onPreparedListener.onPrepared(mp);
                }
            }
        });

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        mCurrentBufferPercentage = percent;
        if(onBufferingUpdateListener != null){
            onBufferingUpdateListener.onBufferingUpdate(mp, percent);
        }
    }
}
