package enorth.cusomwidget.imagehandler;

import android.os.SystemClock;

/**
 * Created by jqyzyh on 2016/7/14.
 */
public class ScaleActioner implements Runnable {
    public interface ScaleDelegate{
        void postRunnable(Runnable runnable);
        boolean postScale(float deltaScale, float targetScale, float px, float py);
        float getScale();
    }

    private float _speed = 0.005f;
    private int _vector = 1;

    private float _startScale;
    private float _targetScale;
    private float _px;
    private float _py;

    private long _startTime;

    private ScaleDelegate _delegate;

    private boolean _running;

    public ScaleActioner(ScaleDelegate delegate){
        _delegate = delegate;
    }

    public void startScale(float startScale, float targetScale, float px, float py){
        _startScale = startScale;
        _targetScale = targetScale;
        _px = px;
        _py = py;
        _startTime = SystemClock.elapsedRealtime();
        _running = true;

        if(targetScale < startScale){
            _vector = -1;
        }else{
            _vector = 1;
        }

        if(_delegate != null){
            _delegate.postRunnable(this);
        }
    }

    public void stop(){
        _running = false;
    }

    public boolean isRunning(){
        return _running;
    }

    @Override
    public void run() {
        if(!_running || _delegate == null){
            return;
        }

        long delta = SystemClock.elapsedRealtime() - _startTime;
        _startTime = SystemClock.elapsedRealtime();
        if(delta < 0){
            delta = 0;
        }
        float scale = delta * _speed * _vector;
        boolean flag = _delegate.postScale(1 + scale, _targetScale, _px, _py);
        if(_delegate.getScale() * _vector >= _targetScale * 1 || flag){
            stop();
        }else{
            _delegate.postRunnable(this);
        }
    }
}
