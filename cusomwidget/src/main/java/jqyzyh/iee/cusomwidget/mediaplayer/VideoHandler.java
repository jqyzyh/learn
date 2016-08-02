package jqyzyh.iee.cusomwidget.mediaplayer;

import android.media.MediaPlayer;

/**
 * Created by jqyzyh on 2016/8/2.
 *
 */
public class VideoHandler {

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

    /**
     * 当前状态
     */
    private int mCurrentState = STATE_IDLE;
    /**
     * 线程中操作不能理解切换状态，这里存着需要切换的状态
     */
    private int mTargetState  = STATE_IDLE;
    /**
     * 播放器
     */
    private MediaPlayer mMediaPlayer;



}
