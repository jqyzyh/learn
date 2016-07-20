package jqyzyh.iee.camera;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by Administrator on 2016/7/20.
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class HolderCallback implements SurfaceHolder.Callback2 {
    static final String LOG_TAG = "HolderCallback";

    private CameraHandler _cameraHandler;

    public void setCamera(CameraHandler camera){
        _cameraHandler = camera;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(LOG_TAG, "surfaceCreated");
        if(_cameraHandler != null){
            _cameraHandler.setPreview(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(LOG_TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(LOG_TAG, "surfaceDestroyed");
        if(_cameraHandler != null){
            _cameraHandler.close();
        }
    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {
        Log.d(LOG_TAG, "surfaceRedrawNeeded");
    }
}
