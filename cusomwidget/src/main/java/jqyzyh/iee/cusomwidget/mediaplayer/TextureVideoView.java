package jqyzyh.iee.cusomwidget.mediaplayer;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.MediaController;

import jqyzyh.iee.cusomwidget.utils.LogUtils;

/**
 * Created by yuhang on 2016/4/14.
 * 用textureview于mediaplay实现视频播放
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class TextureVideoView extends TextureView implements MediaController.MediaPlayerControl {
    static final String LOG_TAG = "TextureVideoView";
    // all possible internal states
    /** 错误*/
    private static final int STATE_ERROR              = -1;
    /** 空闲*/
    private static final int STATE_IDLE               = 0;
    /** 加载中*/
    private static final int STATE_PREPARING          = 1;
    /** 加载完*/
    private static final int STATE_PREPARED           = 2;
    /** 播放中*/
    private static final int STATE_PLAYING            = 3;
    /** 暂停*/
    private static final int STATE_PAUSED             = 4;
    /** 播放完*/
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    private int mCurrentState = STATE_IDLE;
    private int mTargetState  = STATE_IDLE;
    private MediaPlayer mMediaPlayer;
    private int         mAudioSession;
    private Uri mUri;
    private Surface mSurface;

    private int         mVideoWidth;
    private int         mVideoHeight;
    private int mSurfaceWidth, mSurfaceHeight;

    private int mSeekWhenPrepared;

    private int mCurrentBufferPercentage;

    private MediaPlayer.OnPreparedListener onPreparedListener;
    private MediaPlayer.OnCompletionListener onCompletionListener;
    private MediaPlayer.OnErrorListener onErrorListener;

    private MediaController mMediaController;

    private SurfaceTextureListener mSurfaceTextureListener = new SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            LogUtils.d(LOG_TAG, "onSurfaceTextureAvailable");
            mSurfaceWidth = width;
            mSurfaceHeight = height;
            mSurface = new Surface(surface);
            if(mMediaPlayer != null){
                mMediaPlayer.setSurface(mSurface);
            }
            if(mCurrentState == STATE_IDLE){
                openVideoAync();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            LogUtils.d(LOG_TAG, "onSurfaceTextureSizeChanged");
            mSurfaceWidth = width;
            mSurfaceHeight = height;
            configureTransform();
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            LogUtils.d(LOG_TAG, "onSurfaceTextureDestroyed");
            mSurface = null;
            release(true);
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(final MediaPlayer mp) {
            LogUtils.i(LOG_TAG, "onPrepared " + mTargetState);
            mCurrentState = STATE_PREPARED;
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

            post(new Runnable() {
                @Override
                public void run() {
                    configureTransform();
                    int seek = mSeekWhenPrepared;
                    if(seek != 0){
                        seekTo(seek);
                    }

                    if(mSurface != null){
                        mMediaPlayer.setSurface(mSurface);
                    }

                    if(mTargetState == STATE_PLAYING){
                        mMediaPlayer.start();
                    }
                    if (mMediaController != null) {
                        mMediaController.setEnabled(true);
                    }
                    if(onPreparedListener != null){
                        onPreparedListener.onPrepared(mp);
                    }
                }
            });
        }
    };

    private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            LogUtils.i(LOG_TAG, "onError");
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            if (mMediaController != null) {
                mMediaController.hide();
            }
            if(onErrorListener != null){
                if(onErrorListener.onError(mp, what, extra)){
                    return true;
                }
            }

            LogUtils.e(LOG_TAG, "mErrorListener:" + what + "," + extra);
            return true;
        }
    };

    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
            new MediaPlayer.OnBufferingUpdateListener() {
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    LogUtils.d(LOG_TAG, "onBufferingUpdate " + percent);
                    mCurrentBufferPercentage = percent;
                }
            };

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            LogUtils.d(LOG_TAG, "onCompletion");
            mCurrentState = STATE_PLAYBACK_COMPLETED;
            mTargetState = STATE_PLAYBACK_COMPLETED;
            if (mMediaController != null) {
                mMediaController.hide();
            }
            if(onCompletionListener != null){
                onCompletionListener.onCompletion(mp);
            }
        }
    };

    public TextureVideoView(Context context) {
        super(context);
        init(context);
    }

    public TextureVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TextureVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        setBackgroundColor(0xffff0000);
        setSurfaceTextureListener(mSurfaceTextureListener);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Log.i("@@@@", "onMeasure(" + MeasureSpec.toString(widthMeasureSpec) + ", "
        //        + MeasureSpec.toString(heightMeasureSpec) + ")");

        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        if (mVideoWidth > 0 && mVideoHeight > 0) {

            int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                // the size is fixed
                width = widthSpecSize;
                height = heightSpecSize;

                // for compatibility, we adjust size based on aspect ratio
                if ( mVideoWidth * height  < width * mVideoHeight ) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if ( mVideoWidth * height  > width * mVideoHeight ) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
            } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                // only the width is fixed, adjust the height to match aspect ratio if possible
                width = widthSpecSize;
                height = width * mVideoHeight / mVideoWidth;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    height = heightSpecSize;
                }
            } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                // only the height is fixed, adjust the width to match aspect ratio if possible
                height = heightSpecSize;
                width = height * mVideoWidth / mVideoHeight;
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    width = widthSpecSize;
                }
            } else {
                // neither the width nor the height are fixed, try to use actual video size
                width = mVideoWidth;
                height = mVideoHeight;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // too tall, decrease both width and height
                    height = heightSpecSize;
                    width = height * mVideoWidth / mVideoHeight;
                }
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // too wide, decrease both width and height
                    width = widthSpecSize;
                    height = width * mVideoHeight / mVideoWidth;
                }
            }
        } else {
            // no size yet, just adopt the given spec sizes
        }
        setMeasuredDimension(width, height);
    }

    public void setVideoPath(String path){
        loadUrl(Uri.parse(path));
    }

    public void loadUrl(Uri uri){
        mUri = uri;
        release(false);
        openVideoAync();
    }

    private void openVideoAync(){
        if(mCurrentState == STATE_PREPARING){
            return;
        }
        if(mUri == null){
            return;
        }
        mCurrentState = STATE_PREPARING;
        new Thread(new Runnable() {
            @Override
            public void run() {
                openVideo();
            }
        }).start();
    }

    private void openVideo(){
        try{
            final Context context = getContext();
            mMediaPlayer = MediaPlayer.create(context, mUri);
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            attachMediaController();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        LogUtils.d(LOG_TAG, "start " + mCurrentState);
        if (isInPlaybackState()) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
    }

    @Override
    public void pause() {
        LogUtils.d(LOG_TAG, "pause " + mCurrentState);
        if(isInPlaybackState()){
            mMediaPlayer.pause();
            mCurrentState = STATE_PAUSED;
        }
        mTargetState = STATE_PAUSED;
    }

    public void stopPlayback(){
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            mTargetState  = STATE_IDLE;
            AudioManager am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            return mMediaPlayer.getDuration();
        }
        return -1;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return mMediaPlayer.getCurrentPosition();
        }
        return -1;
    }

    @Override
    public void seekTo(int pos) {
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(pos);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = pos;
        }
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        if (mAudioSession == 0) {
            MediaPlayer foo = new MediaPlayer();
            mAudioSession = foo.getAudioSessionId();
            foo.release();
        }
        return mAudioSession;
    }

    private void release(boolean cleartargetstate) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            if (cleartargetstate) {
                mTargetState  = STATE_IDLE;
            }
            AudioManager am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }
    }

    public void configureTransform(){
        if(mMediaPlayer == null){
            return;
        }

        int mediaWidth = mMediaPlayer.getVideoWidth();
        int mediaHeight = mMediaPlayer.getVideoHeight();
        LogUtils.d(LOG_TAG, "configureTransform " + mediaWidth + "," + mediaHeight+ "," + mSurfaceWidth+ "," + mSurfaceHeight);

        if(mediaWidth == 0 || mediaHeight == 0 || mSurfaceWidth == 0 || mSurfaceHeight == 0){
            return;
        }

//        Matrix m = new Matrix();
//        if(mSurfaceWidth * 1.0f / mSurfaceHeight < mediaWidth * 1.0f / mediaHeight){
//            m.setScale(1, mediaHeight * mSurfaceWidth / mSurfaceHeight  * 1.0f / mediaWidth, 0, mSurfaceHeight / 2);
//        }else{
//            m.setScale(mediaWidth * mSurfaceHeight / mSurfaceWidth  * 1.0f / mediaHeight, 1, mSurfaceWidth / 2, 0);
//        }
//
//        setTransform(m);
    }

    private boolean isInPlaybackState() {
        return (mMediaPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener) {
        this.onCompletionListener = onCompletionListener;
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener onPreparedListener) {
        this.onPreparedListener = onPreparedListener;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK &&
                keyCode != KeyEvent.KEYCODE_VOLUME_UP &&
                keyCode != KeyEvent.KEYCODE_VOLUME_DOWN &&
                keyCode != KeyEvent.KEYCODE_VOLUME_MUTE &&
                keyCode != KeyEvent.KEYCODE_MENU &&
                keyCode != KeyEvent.KEYCODE_CALL &&
                keyCode != KeyEvent.KEYCODE_ENDCALL;
        if (isInPlaybackState() && isKeyCodeSupported && mMediaController != null) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK ||
                    keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                if (mMediaPlayer.isPlaying()) {
                    pause();
                    mMediaController.show();
                } else {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                if (!mMediaPlayer.isPlaying()) {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                    || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                if (mMediaPlayer.isPlaying()) {
                    pause();
                    mMediaController.show();
                }
                return true;
            } else {
                toggleMediaControlsVisiblity();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isInPlaybackState() && mMediaController != null) {
            toggleMediaControlsVisiblity();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (isInPlaybackState() && mMediaController != null) {
            toggleMediaControlsVisiblity();
        }
        return false;
    }

    //====================== MediaController 操作========================

    private void toggleMediaControlsVisiblity() {
        if (mMediaController.isShowing()) {
            mMediaController.hide();
        } else {
            mMediaController.show();
        }
    }

    public void setMediaController(MediaController mediaController) {
        if(this.mMediaController != null){
            this.mMediaController.hide();
        }
        this.mMediaController = mediaController;
        attachMediaController();
    }

    private void attachMediaController() {
        if (mMediaPlayer != null && mMediaController != null) {
            mMediaController.setMediaPlayer(this);
            View anchorView = this.getParent() instanceof View ?
                    (View)this.getParent() : this;
            mMediaController.setAnchorView(anchorView);
            mMediaController.setEnabled(isInPlaybackState());
        }
    }
}
