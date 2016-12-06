package jqyzyh.iee.cusomwidget.repetpager;

import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Created by jqyzyh on 2016/12/6.
 */

public class AutoScrollNextViewPager extends ViewPager {

    public interface OnMyPagerTouchListener{
        void setLockParent(boolean lock);
    }

    private static final int AUTO_SCROLL_DELAY = 5000;

    private OnMyPagerTouchListener onMyPagerTouchListener;

    /** 触摸时按下的点 */
    private PointF mDownPosition = new PointF();
    private PointF mCurPosition = new PointF();

    private boolean mLockParent;//锁住父控件
    private boolean mRunningAutoSrcoll;//是否开启自动翻页

    private Handler mAutoSrcollHandler = new Handler();

    private Runnable mAutoSrcollTask = new Runnable() {
        @Override
        public void run() {
            if(!mRunningAutoSrcoll){
                return;
            }

            if(getAdapter() == null){
                return;
            }

            setCurrentItem(getCurrentItem() + 1, true);

            mAutoSrcollHandler.postDelayed(mAutoSrcollTask, AUTO_SCROLL_DELAY);
        }
    };

    public AutoScrollNextViewPager(Context context) {
        super(context);
    }

    public AutoScrollNextViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        _startAutoSrcoll();
    }

    @Override
    protected void onDetachedFromWindow() {
        _stopAutoSrcoll();
        super.onDetachedFromWindow();
    }

    public void setOnMyPagerTouchListener(OnMyPagerTouchListener onMyPagerTouchListener) {
        this.onMyPagerTouchListener = onMyPagerTouchListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 记录按下时候的坐标，切记不可用 downP = curP ，这样在改变curP的时候，downP也会改变
            mDownPosition.x = ev.getX();
            mDownPosition.y = ev.getY();
            lockParent(true);
            mLockParent = false;
            _stopAutoSrcoll();
        }

        if (ev.getAction() == MotionEvent.ACTION_MOVE) {

            // 每次进行onTouch事件都记录当前的按下的坐标
            mCurPosition.x = ev.getX();
            mCurPosition.y = ev.getY();
            float distanceX = Math.abs(mCurPosition.x - mDownPosition.x);
            float distanceY = Math.abs(mCurPosition.y - mDownPosition.y);
            int distance = ViewConfiguration.get(getContext()).getScaledTouchSlop();

            if(!mLockParent && distanceY >= distance){
                _startAutoSrcoll();
                lockParent(false);
            }
            if(distanceX >= distance){
                mLockParent = true;
            }
        }

        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            _startAutoSrcoll();
            lockParent(false);
        }


        return super.onTouchEvent(ev);
    }

    private void _startAutoSrcoll(){
        mRunningAutoSrcoll = true;
        mAutoSrcollHandler.removeCallbacks(mAutoSrcollTask);
        mAutoSrcollHandler.postDelayed(mAutoSrcollTask, AUTO_SCROLL_DELAY);
    }

    private void _stopAutoSrcoll(){
        mRunningAutoSrcoll = false;
        mAutoSrcollHandler.removeCallbacks(mAutoSrcollTask);
    }

    void lockParent(boolean lock){
        if(onMyPagerTouchListener != null){
            onMyPagerTouchListener.setLockParent(lock);
        }
    }
}
