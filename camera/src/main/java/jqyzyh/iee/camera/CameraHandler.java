package jqyzyh.iee.camera;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.MediaCodec;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

/**
 * Created by jqyzyh on 2016/7/20.
 */
public class CameraHandler {

    static final String LOG_TAG = "CameraHandler";

    private Camera _camra;

    private boolean _isReady;

    private SurfaceHolder _surface;

    private byte[] buffers;

    private Point mPreviewSize;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void openCamera(Context contexts) {

        if (ContextCompat.checkSelfPermission(contexts, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            /*校验权限否则6.0系统可能会crash！！！*/
            return;
        }
        _camra = Camera.open();
        initParamenters(_camra);

        _isReady = true;
        if (_surface != null) {
            setPreviewToCamera(_surface);
            _surface = null;
        }

        Surface surface = null;


        try {
            MediaCodec codec = MediaCodec.createDecoderByType("video/avc");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void initParamenters(Camera camera) {
        Camera.Parameters params = camera.getParameters();
        camera.setDisplayOrientation(90);
        params.setPreviewSize(params.getSupportedPreviewSizes().get(0).width, params.getSupportedPreviewSizes().get(0).height);
        /*看名字像关闭闪光灯*/
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        /*看名字像自动对焦*/
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        Log.d("mylog", "format==>" + params.getPreviewFormat());
        camera.setParameters(params);
    }

    public boolean isReady() {
        return _isReady;
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    void setPreviewToCamera(final SurfaceHolder surface) {
        if (_camra == null) {
            return;
        }
        try {
            _camra.setPreviewDisplay(surface);
            if (surface != null) {
                Point size = getBestPreviewSize(_camra.getParameters(), new Point(surface.getSurfaceFrame().width(), surface.getSurfaceFrame().height()));
                _camra.getParameters().setPreviewSize(size.x, size.y);
                buffers = new byte[((size.x * size.y) << 3) >> 2];
                _camra.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                    @TargetApi(Build.VERSION_CODES.FROYO)
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        Log.d(LOG_TAG, "onPreviewFrame");
                        _camra.addCallbackBuffer(buffers);
                    }
                });
                _camra.addCallbackBuffer(buffers);
                _camra.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setPreview(SurfaceHolder surface) {
        if (_isReady) {
            setPreviewToCamera(surface);
        } else {
            _surface = surface;
        }
    }

    public void close() {
        setPreviewToCamera(null);
        if (_camra != null) {
            _camra.setPreviewCallbackWithBuffer(null);
            _camra.release();
        }
    }

    static Point getBestPreviewSize(Camera.Parameters parameters, Point surfaceSize){
        List<Size> supportSizes = parameters.getSupportedPreviewSizes();

        if(supportSizes == null || supportSizes.isEmpty()){
            return surfaceSize;
        }

        Point ret = new Point();

        float rote = surfaceSize.x * 1.0f / surfaceSize.y;

        for(Size size : supportSizes){
            if(ret == null){
                ret.x = size.width;
                ret.y = size.height;
            }else{
                if(Math.abs(rote - size.width * 1.0f / size.height) < Math.abs(rote - ret.x * 1.0f / ret.y)){
                    ret.x = size.width;
                    ret.y = size.height;
                }
            }
        }

        return ret;
    }
}
