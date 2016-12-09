package jqyzyh.iee.cusomwidget.canfullmedia;

import android.media.MediaPlayer;
import android.view.Surface;

/**
 * Created by jqyzyh on 2016/12/9.
 */

public interface IMediaPlayerDelegate {

    MediaPlayer getMediaPlayer();

    void setSurface(Surface surface);

    void destorySurface(Surface surface);

    void start();
    void stop();
    void pause();
    void release();

    int     getDuration();
    int     getCurrentPosition();
    void    seekTo(int pos);
    int     getBufferPercentage();

    boolean isPlaying();
}
