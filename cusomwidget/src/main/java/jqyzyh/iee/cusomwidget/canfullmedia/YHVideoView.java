package jqyzyh.iee.cusomwidget.canfullmedia;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

/**
 * Created by jqyzyh on 2016/12/9.
 */

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class YHVideoView extends TextureView{

    private boolean isPlaying;

    private Surface mSurface;

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

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
        if(mediaPlayerDelegate != null && getSurfaceTexture() != null){
            mediaPlayerDelegate.setSurface(new Surface(getSurfaceTexture()));
        }
    }
}
