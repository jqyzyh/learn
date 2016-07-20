package jqyzyh.iee.camera;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by jqyzyh on 2016/7/20.
 */
public class CameraHandler {

    static final String LOG_TAG = "CameraHandler";

    private Camera _camra;

    private boolean _isReady;

    private SurfaceHolder _surface;

    private byte[] buffers;

    private Camera.Size mPreviewSize;

    private SurfaceHolder _surface2;

    public void setSurface2(SurfaceHolder surface2) {
        _surface2 = surface2;
    }

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
        params.setPreviewSize(params.getSupportedPreviewSizes().get(0).width, params.getSupportedPreviewSizes().get(0).height);
        params.setRotation(90);
        /*看名字像关闭闪光灯*/
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        /*看名字像自动对焦*/
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        Log.d("mylog", "format==>" + params.getPreviewFormat());
        camera.setParameters(params);
        mPreviewSize = params.getPreviewSize();
        buffers = new byte[params.getPreviewSize().width * params.getPreviewSize().height];
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
                surface.setFixedSize(_camra.getParameters().getPreviewSize().width, _camra.getParameters().getPreviewSize().height);
                buffers = new byte[surface.getSurfaceFrame().width() * surface.getSurfaceFrame().height() * 2];
                _camra.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                    @TargetApi(Build.VERSION_CODES.FROYO)
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        Log.d(LOG_TAG, "onPreviewFrame");
                        int w = mPreviewSize.width;
                        int h = mPreviewSize.height;
                        YuvImage image = new YuvImage(data, camera.getParameters().getPreviewFormat(), w, h, null);
                        ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
                        if (image.compressToJpeg(new Rect(0, 0, w, h), 100, os)) {
                            byte[] tmp = os.toByteArray();
                            Bitmap bmp = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);
                            if (_surface2 != null) {
                                Canvas canvas = _surface2.lockCanvas();
                                Log.d(LOG_TAG, "onPreviewFrame 1");
                                if (canvas != null) {
                                    Log.d(LOG_TAG, "onPreviewFrame 2");
                                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                                    paint.setColor(Color.RED);
                                    canvas.drawRect(0, 0, 100, 100, paint);
//                                    canvas.drawBitmap(bmp, 0, 0, paint);
                                    _surface2.unlockCanvasAndPost(canvas);
                                }
                            }

                        }

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
}
