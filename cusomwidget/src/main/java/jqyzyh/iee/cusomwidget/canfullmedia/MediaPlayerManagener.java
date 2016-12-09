package jqyzyh.iee.cusomwidget.canfullmedia;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by jqyzyh on 2016/12/9.
 */

public class MediaPlayerManagener {

    private static MediaPlayerManagener static_instance;

    public static MediaPlayerManagener getInstance(){
        if(static_instance == null){
            static_instance = new MediaPlayerManagener();
        }
        return static_instance;
    }

    HashMap<String, IMediaPlayerDelegate> mediaPool = new HashMap<>();

    public IMediaPlayerDelegate getMedia(Context context, String path){
        IMediaPlayerDelegate ret = mediaPool.get(path);
        if(ret == null){
            MediaPlayerDelegateImpl delegate = new MediaPlayerDelegateImpl(context);
            delegate.loadPath(path);
            mediaPool.put(path, delegate);
            ret = delegate;
        }
        return ret;
    }

    public void releaseMedia(String path){
        IMediaPlayerDelegate delegate = mediaPool.get(path);
        if(delegate != null){
            delegate.stop();
            mediaPool.remove(path);
        }
    }
}
