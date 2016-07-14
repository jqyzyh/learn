package enorth.cusomwidget.imagehandler;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import enorth.cusomwidget.utils.LogUtils;

/**
 * Created by jqyzyh on 2016/7/14.
 */
public class ImageHandler implements View.OnTouchListener,
        ViewTreeObserver.OnGlobalLayoutListener,
        GestureDetector.OnGestureListener,
        ScaleGestureDetector.OnScaleGestureListener,
        GestureDetector.OnDoubleTapListener,
        ScaleActioner.ScaleDelegate{
    static final String LOG_TAG = "ImageHandler";
    static final float MAX_SCALE = 4;
    private ImageView _imageView;
    /**
     * 是否初始化过
     */
    private boolean _isFirst;

    /** 图片矩阵*/
    private Matrix _matrix;
    /** 最大缩放比例*/
    private float _maxScale;
    /** 初始缩放比例*/
    private float _initScale;
    /** 获取矩阵信息的数组容器*/
    private float[] _matrixArray = new float[9];
    /** 图片是否已经移动到边界*/
    private boolean _isBorder;

    private GestureDetector _gestureDetector;
    private ScaleGestureDetector _scaleGestureDetector;

    //起始缩放值
    private float _startScale;
    //目标缩放值
    private float _tagartScale;
    //缩放中心点
    private float _scaleCenterX, _scaleCenterY;
    private long _time;
    private ScaleActioner _scaleActioner;

    public ImageHandler(Context context){
        _gestureDetector = new GestureDetector(context, this);
        _gestureDetector.setOnDoubleTapListener(this);
        _scaleGestureDetector = new ScaleGestureDetector(context, this);
        _scaleActioner = new ScaleActioner(this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void attchImageView(ImageView imageView){
        _isFirst = true;
        if(_imageView != null){
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
                _imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }else{
                _imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
            _imageView.setOnTouchListener(null);
        }

        this._imageView = imageView;
        if(_imageView != null){
            _imageView.setScaleType(ImageView.ScaleType.MATRIX);
            _imageView.getViewTreeObserver().addOnGlobalLayoutListener(this);
            _imageView.setOnTouchListener(this);
        }
    }

    private boolean resetScale(){
        LogUtils.d(LOG_TAG, "resetScale start");
        if(_imageView == null){
            return false;
        }
        Drawable d = getDrawable();
        if (d == null) {
            return false;
        }
        //获得图片绘制宽高
        int dw = d.getIntrinsicWidth();
        int dh = d.getIntrinsicHeight();
        //获得控件宽高
        int w = _imageView.getWidth();
        int h = _imageView.getHeight();
        float scale;
        if (dw > w && dh <= h) {
            //图片宽度大于控件但是高度小于控件，缩小回宽度跟控件一致
            scale = w * 1.0f / dw;
        } else if (dh > h && dw <= w) {
            //图片高度大于控件但是宽度小于控件，缩小回高度跟控件一致
            scale = h * 1.0f / dh;
        } else {
            //取高度比例跟宽度比例的最小值作为初始缩放值，保持全部图片都在控件内显示
            scale = Math.min(w * 1.0f / dw, h * 1.0f / dh);
        }
        _initScale = scale;
        _maxScale = scale * MAX_SCALE;
        _matrix = new Matrix();
        _matrix.setTranslate((w - dw) / 2, (h - dh) / 2);
        _matrix.postScale(scale, scale, w / 2, h / 2);
        _imageView.setImageMatrix(_matrix);
        LogUtils.d(LOG_TAG, "resetScale done==>" + _initScale + "," + _maxScale);
        return true;
    }

    protected Drawable getDrawable(){
        return _imageView == null ? null : _imageView.getDrawable();
    }

    public float getScale() {
        _matrix.getValues(_matrixArray);
        return _matrixArray[Matrix.MSCALE_X];
    }

    public float getTranslationX(){
        _matrix.getValues(_matrixArray);
        return _matrixArray[Matrix.MTRANS_X];
    }

    public float getTranslationY(){
        _matrix.getValues(_matrixArray);
        return _matrixArray[Matrix.MTRANS_Y];
    }

    /**
     * 获取图片所缩放后的位置矩形
     * @return 图片位置矩形
     */
    private RectF getMatrixRectF()
    {
        Matrix matrix = _matrix;
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d)
        {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    /**
     * 保持图片不会移动出屏幕
     * @return 0是正常， 1 左边顶头  2右边顶头
     */
    private int checkBorderAndCenter(){
        if(_imageView == null){
            return 0;
        }
        //获得矩阵换算后的图片位置大小
        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = _imageView.getWidth();
        int height = _imageView.getHeight();
        int ret = 0;
        if(rect.width() >= width){
            if(rect.left >= 0){
                deltaX = -rect.left;
                ret = 1;
            }
            if(rect.right <= width){
                deltaX = width - rect.right;
                ret = 2;
            }
        }else{
            deltaX = 0.5f * width - rect.right + 0.5f * rect.width();
        }


        if(rect.height() >=height){
            if(rect.top > 0){
                deltaY = -rect.top;
            }
            if(rect.bottom < height){
                deltaY = height - rect.bottom;
            }
        }else{
            deltaY = 0.5f * height - rect.bottom + 0.5f*rect.height();
        }

        _matrix.postTranslate(deltaX, deltaY);
        return ret;
    }

    /**
     * 调用{@link #_imageView}父控件的{@link ViewParent#requestDisallowInterceptTouchEvent(boolean)}方法
     * @see ViewParent#requestDisallowInterceptTouchEvent(boolean)
     * @param value
     */
    void setParentInterceptTouchEvent(boolean value){
        if(_imageView == null){
            return;
        }
        ViewParent vp = _imageView.getParent();
        if(vp != null){
            vp.requestDisallowInterceptTouchEvent(value);
        }
    }

    /**
     * 给父控件一个结束touch跟按下touch时间，让父控件模拟重新touch
     * @param ev 触发该操作的事件
     */
    void parentTouch(MotionEvent ev){
        if(_imageView == null){
            return;
        }
        View vp = (View)_imageView.getParent();
        if(vp != null){
            //保存当前事件类型
            int action = ev.getAction();
            //变为canel事件
            ev.setAction(MotionEvent.ACTION_CANCEL);
            vp.onTouchEvent(ev);
            //变为down事件
            ev.setAction(MotionEvent.ACTION_DOWN);
            vp.onTouchEvent(ev);
            //还原事件类型
            ev.setAction(action);
        }
    }

    /**
     * 在当前比例下缩放
     * @param deltaScale 缩放比例
     * @param px 缩放中心点
     * @param py 缩放中心点
     */
    void postImageScale(float deltaScale, float px, float py){
        if(_imageView == null){
            return;
        }
        LogUtils.d(LOG_TAG, "postImageScale==>" + deltaScale + "," + px + "," + py);
        float scale = getScale();
        LogUtils.d(LOG_TAG, "postImageScale scale==>" + scale);
        //如果缩放比例没有超过边界值
        if (scale < _maxScale && deltaScale > 1.0f || scale > _initScale
                && deltaScale < 1.0f) {

            //与边界值取齐
            if (deltaScale * scale < _initScale) {
                deltaScale = _initScale / scale;
            }

            if (deltaScale * scale > _maxScale) {
                deltaScale = _maxScale / scale;
            }

            _matrix.postScale(deltaScale, deltaScale, px, py);
            checkBorderAndCenter();
            _imageView.setImageMatrix(_matrix);
        }
    }

    /**
     * 缩放图片
     * @param targetScale 缩放的比例
     * @param px 缩放中心点 为控件坐标
     * @param py 缩放中心点 为控件坐标
     */
    void scaleImage(float targetScale, float px, float py){
        LogUtils.d(LOG_TAG, "scaleImage " + targetScale + "," + px + "," + py);
        Drawable d = getDrawable();
        if(d == null){
            return;
        }
        if(targetScale < _initScale){
            targetScale = _initScale;
        }
        if(targetScale > _maxScale){
            targetScale = _maxScale;
        }

        int dw = d.getIntrinsicWidth();
        int dh = d.getIntrinsicHeight();

        _matrix.setTranslate((_imageView.getWidth() - dw) / 2, (_imageView.getHeight() - dh) / 2);
        LogUtils.d(LOG_TAG, "scaleImage==>"+getTranslationX() + ","+ getTranslationY());
        _matrix.postScale(targetScale, targetScale, px, py);
        _imageView.setImageMatrix(_matrix);
    }

    /**
     * 滚动图片
     * @param distanceX x轴方向滚动距离
     * @param distanceY y轴方向滚动距离
     * @return true图片没有移动到边界，否则返回false
     */
    boolean scrollImage(float distanceX, float distanceY){
        _matrix.postTranslate(distanceX, distanceY);
        int local = checkBorderAndCenter();
        boolean ret = true;
        _imageView.setImageMatrix(_matrix);
        if(distanceX < 0 && local == 1){//向右移动 并且到边界
            Log.d(LOG_TAG, "右划");
            ret = false;
        }
        if(distanceX > 0 && local == 2){//向左移动 并且到边界
            Log.d(LOG_TAG, "左划");
            ret = false;
        }
        return ret;
    }

    public void resetImage(){
        if(_imageView == null){
            return;
        }
        scaleImage(_initScale, _imageView.getWidth() / 2, _imageView.getHeight() / 2);
    }

    @Override
    public void onGlobalLayout() {
        if(_isFirst){
            _isFirst = false;
            resetScale();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean sgdRet = _scaleGestureDetector.onTouchEvent(event);
        boolean gdRet = _gestureDetector.onTouchEvent(event);

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            setParentInterceptTouchEvent(true);
        }
        return true;
    }

    //TODO ========================= OnGestureListener =================================
    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (getDrawable() == null)
            return false;
        boolean flag = scrollImage(-distanceX, -distanceY);
        setParentInterceptTouchEvent(flag);
        if(_isBorder == flag){
            _isBorder = !flag;
            parentTouch(e2);
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    //TODO ========================= OnScaleGestureListener =================================
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = 1 + (detector.getScaleFactor() - 1) * 2;
        if (getDrawable() == null)
            return true;
        postImageScale(scaleFactor, detector.getFocusX(), detector.getFocusY());
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    //TODO ========================= OnDoubleTapListener =================================
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if(_imageView == null){
            return false;
        }
        float scale = getScale();
        float targetScale = 0;
        if(scale > (_maxScale + _initScale) / 2){
            targetScale = _initScale;
        }else{
            targetScale = _maxScale;
        }
        _scaleActioner.startScale(scale, targetScale, e.getX(), e.getY());
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }
    //TODO======================= ScaleDelegate ============================

    @Override
    public void postRunnable(Runnable runnable) {
        if(_imageView != null){
            _imageView.post(runnable);
        }
    }

    @Override
    public void scale(float scale, float px, float py) {
        scaleImage(scale, px, py);
    }
}
