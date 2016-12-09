package jqyzyh.iee.cusomwidget.canfullmedia;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import jqyzyh.iee.cusomwidget.utils.LogUtils;

/**
 * Created by jqyzyh on 2016/12/9.
 */

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class YHVideoView extends TextureView implements MediaPlayer.OnVideoSizeChangedListener {

    private boolean isPlaying;

    private Surface mSurface;

    private int mVideoWidth;
    private int mVideoHeight;

    private SurfaceTextureListener mSurfaceTextureListener = new SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            mSurface = new Surface(surface);
            if(mMediaPlayerDelegate != null){
                mMediaPlayerDelegate.setSurface(mSurface);
                if(isPlaying){
                    mMediaPlayerDelegate.start();
                }
                isPlaying = false;
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//            configureTransform();
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if(mMediaPlayerDelegate != null){
                isPlaying = mMediaPlayerDelegate.isPlaying();
                mMediaPlayerDelegate.destorySurface(mSurface);
            }
            mSurface = null;
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    IMediaPlayerDelegate mMediaPlayerDelegate;

    public YHVideoView(Context context) {
        super(context);
        _init(context, null);
    }

    public YHVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init(context, attrs);
    }

    public YHVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _init(context, attrs);
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public YHVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        _init(context, attrs);
    }

    private void _init(Context context, AttributeSet attrs){
        if(isInEditMode()){
            return;
        }
        setSurfaceTextureListener(mSurfaceTextureListener);
    }

    public void setMediaPlayerDelegate(IMediaPlayerDelegate mediaPlayerDelegate) {
        this.mMediaPlayerDelegate = mediaPlayerDelegate;
        if(mediaPlayerDelegate != null){
            if(mSurface != null){
                mediaPlayerDelegate.setSurface(mSurface);
            }
            mediaPlayerDelegate.addVideoChangeSizeListener(this);
            MediaPlayer mediaPlayer = mediaPlayerDelegate.getMediaPlayer();
            if(mediaPlayer != null){
                mVideoWidth = mediaPlayer.getVideoWidth();
                mVideoHeight = mediaPlayer.getVideoHeight();
                requestLayout();
            }
        }
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        LogUtils.d("mylog", "onVideoSizeChanged" + width + "," + height);
        requestLayout();
    }

    @Override
    protected void onDetachedFromWindow() {
        if(mMediaPlayerDelegate != null){
            mMediaPlayerDelegate.removeVideoChangeSizeListener(this);
        }
        super.onDetachedFromWindow();
    }
}
