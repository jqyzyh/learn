package jqyzyh.iee.cusomwidget.mediaplayer;

/**
 * Created by jqyzyh on 2016/8/2.
 */
public interface MediaPlayerControl {
    /**
     * 播放
     */
    void start();

    /**
     * 暂停
     */
    void pause();

    /**
     * 获取时长
     *
     * @return
     */
    int getDuration();

    /**
     * 获取当前播放位置
     *
     * @return
     */
    int getCurrentPosition();

    /**
     * 跳转到pos
     *
     * @param pos 跳转的进度
     */
    void seekTo(int pos);

    /**
     * 是否在播放
     *
     * @return
     */
    boolean isPlaying();

    int getBufferPercentage();

    /**
     * 是否可以暂停
     *
     * @return
     */
    boolean canPause();

    boolean canSeekBackward();

    boolean canSeekForward();

    /**
     * Get the audio session id for the player used by this VideoView. This can be used to
     * apply audio effects to the audio track of a video.
     * @return The audio session, or 0 if there was an error.
     */
    int getAudioSessionId();
}
