package enorth.cusomwidget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

/**
 * 一个简单的支持缩放的imageview
 * @author yuhang
 */
public class PreviewImageView extends ImageView implements OnGestureListener,
		OnScaleGestureListener, OnGlobalLayoutListener, GestureDetector.OnDoubleTapListener {

	static final String LOG_TAG = "PreviewImageView";

	/** 最大缩放比例*/
	static final float MAX_SCALE = 2;
    /** 一般手势控制*/
	private GestureDetector _gd;
	/** 缩放手势控制*/
	private ScaleGestureDetector _sgd;
	/** 用于缩放图片的矩阵*/
	private Matrix _matrix;
	/** 最大缩放比例*/
	private float _maxScale;
	/** 初始缩放比例*/
	private float _initScale;
	private float _scale;
	/** 获取矩阵信息的数组容器*/
	private float[] _matrixArray = new float[9];
	/** 图片是否已经移动到边界*/
	private boolean mIsBorder;
	/** 缩放动画开始时间*/
	private long mStartTime;
	/** 缩放动画目标缩放值*/
	private float _targetScale;
	/** 缩放动画是否开启*/
	private boolean mScaleRunning;
	/** 是否初始化过*/
	private boolean isFirst = true;

	private float mScaleCenterX, mScaleCenterY;

	private Runnable mScaleRunnable = new Runnable() {
		@Override
		public void run() {
			//计算动画播放时长
			int delta = (int) (System.currentTimeMillis() - mStartTime);
			//获取当前缩放比例
			float scale = getScale();
			if(scale < _targetScale){//放大
				scale += delta * 0.001f;
				if(scale >= _targetScale){//超过目标值结束动画
					mScaleRunning = false;
					scale = _targetScale;
				}
			}else{//缩小
				scale -= delta * 0.001f;
				if(scale <= _targetScale){//超过目标值结束动画
					mScaleRunning = false;
					scale = _targetScale;
				}
			}

			if(mScaleRunning){
				postDelayed(mScaleRunnable, 5);
			}

			scaleImage(scale, getWidth() / 2, getHeight() / 2);//
		}
	};


	public PreviewImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setScaleType(ScaleType.MATRIX);
	}

	public PreviewImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setScaleType(ScaleType.MATRIX);
	}

	public PreviewImageView(Context context) {
		super(context);
		setScaleType(ScaleType.MATRIX);
	}

	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		getViewTreeObserver().removeGlobalOnLayoutListener(this);
		super.onDetachedFromWindow();
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
		//获得矩阵换算后的图片位置大小
        RectF rect = getMatrixRectF();  
        float deltaX = 0;  
        float deltaY = 0;  
  
        int width = getWidth();  
        int height = getHeight();
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
	
	@Override
	public void setImageDrawable(Drawable drawable) {
	
	    // TODO Auto-generated method stub
	    isFirst = true;
	    super.setImageDrawable(drawable);
	}

	@Override
	public void onGlobalLayout() {
		// TODO Auto-generated method stub
		if (isFirst) {
			Drawable d = getDrawable();
			if (d == null) {
				return;
			}
			isFirst = false;
			//获得图片绘制宽高
			int dw = d.getIntrinsicWidth();
			int dh = d.getIntrinsicHeight();
			//获得控件宽高
			int w = getWidth();
			int h = getHeight();
			if (dw > w && dh <= h) {
				//图片宽度大于控件但是高度小于控件，缩小回宽度跟控件一致
				_scale = w * 1.0f / dw;
			} else if (dh > h && dw <= w) {
				//图片高度大于控件但是宽度小于控件，缩小回高度跟控件一致
				_scale = h * 1.0f / dh;
			} else {
				//取高度比例跟宽度比例的最小值作为初始缩放值，保持全部图片都在控件内显示
				_scale = Math.min(w * 1.0f / dw, h * 1.0f / dh);
			}
			_initScale = _scale;
			_maxScale = _scale * MAX_SCALE;
			_matrix = new Matrix();
			_matrix.setTranslate((w - dw) / 2, (h - dh) / 2);
			_matrix.postScale(_scale, _scale, w / 2, h / 2);
			setImageMatrix(_matrix);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (_gd == null) {
			_gd = new GestureDetector(getContext(), this);
			_gd.setOnDoubleTapListener(this);
		}
		if (_sgd == null) {
			_sgd = new ScaleGestureDetector(getContext(), this);
		}
		boolean sgdRet = _sgd.onTouchEvent(event);
		boolean gdRet = _gd.onTouchEvent(event);

		if(event.getAction() == MotionEvent.ACTION_DOWN){
			setParentInterceptTouchEvent(true);
		}
		return true;
	}

	/**
	 *
	 * @param value
     */
	void setParentInterceptTouchEvent(boolean value){
		ViewParent vp = getParent();
		if(vp != null){
			vp.requestDisallowInterceptTouchEvent(value);
		}
	}

	/**
	 * 给父控件一个结束touch跟按下touch时间，让父控件模拟重新touch
	 * @param ev 触发该操作的事件
     */
	void parentTouch(MotionEvent ev){
		View vp = (View)getParent();
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
		float scale = getScale();
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
			setImageMatrix(_matrix);
		}
	}

	/**
	 * 缩放图片
	 * @param targetScale 缩放的比例
	 * @param px 缩放中心点
	 * @param py 缩放中心点
     */
	void scaleImage(float targetScale, float px, float py){
		Log.d(LOG_TAG, "scaleImage " + targetScale + "," + px + "," + py);
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

		_matrix.setTranslate(px - dw / 2, py - dh / 2);
		_matrix.postScale(targetScale, targetScale, px, py);
		checkBorderAndCenter();
		setImageMatrix(_matrix);
	}

	public void resetImage(){
		scaleImage(_initScale, getWidth() / 2, getHeight() / 2);
	}

	//TODO ================================= OnScaleGestureListener =========================================
	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * android.view.ScaleGestureDetector.OnScaleGestureListener#onScale(android
	 * .view.ScaleGestureDetector)
	 */
	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		float scaleFactor = 1 + (detector.getScaleFactor() - 1) * 2;
		if (getDrawable() == null)
			return true;
		mScaleRunning = false;
		postImageScale(scaleFactor, detector.getFocusX(), detector.getFocusY());
		return true;
	}

	//TODO ================================= OnGestureListener =========================================

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "onSingleTapUp");
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
							float distanceY) {
		// TODO Auto-generated method stub
		Drawable d = getDrawable();
		if(d == null){
			return false;
		}
		_matrix.postTranslate(-distanceX, -distanceY);
		int local = checkBorderAndCenter();
		boolean ret = true;
		setImageMatrix(_matrix);
		if(distanceX < 0 && local == 1){//向右移动 并且到边界
			Log.d(LOG_TAG, "右划");
			ret = false;
		}
		if(distanceX > 0 && local == 2){//向左移动 并且到边界
			Log.d(LOG_TAG, "左划");
			ret = false;
		}
		Log.d(LOG_TAG, "return " + distanceX);
		setParentInterceptTouchEvent(ret);
		if(mIsBorder == ret){
			mIsBorder = !ret;
			parentTouch(e2);
		}
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
						   float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	//TODO ================================= OnDoubleTapListener =========================================
	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
			performClick();
		}else{
			callOnClick();
		}
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		float scale = getScale();
		if(scale > (_maxScale + _initScale) / 2){
			_targetScale = _initScale;
		}else{
			_targetScale = _maxScale;
		}
		mScaleCenterX = e.getX();
		mScaleCenterY = e.getY();
		Log.d(LOG_TAG, "onDoubleTap" +  mScaleCenterX + "," + mScaleCenterY);
		mScaleRunning = true;
		mStartTime = System.currentTimeMillis();
		post(mScaleRunnable);
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}
}
