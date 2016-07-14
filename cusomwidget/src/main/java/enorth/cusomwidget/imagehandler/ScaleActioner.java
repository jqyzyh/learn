package enorth.cusomwidget.imagehandler;

import android.os.SystemClock;

/**
 * Created by jqyzyh on 2016/7/14.
 */
public class ScaleActioner implements Runnable {
    public interface ScaleDelegate{
        void postRunnable(Runnable runnable);
        void scale(float scale, float px, float py);
    }

    private int _scaleTime = 500;

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
        if(_delegate != null){
            _delegate.postRunnable(this);
        }
    }

    public void stop(){

    }

    @Override
    public void run() {
        if(!_running || _delegate == null){
            return;
        }

        long delta = SystemClock.elapsedRealtime() - _startTime;
        if(delta < 0){
            delta = 0;
        }
        float scale;
        if(delta >= _scaleTime){
            scale = _targetScale;
        }else{
            scale = _startScale + (_targetScale - _startScale) * delta / _scaleTime;
            _delegate.postRunnable(this);
        }
        _delegate.scale(scale, _px, _py);
    }
}
