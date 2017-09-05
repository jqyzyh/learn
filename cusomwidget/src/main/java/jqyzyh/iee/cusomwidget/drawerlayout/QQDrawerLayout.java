package jqyzyh.iee.cusomwidget.drawerlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import jqyzyh.iee.cusomwidget.R;
import jqyzyh.iee.cusomwidget.utils.LogUtils;

/**
 * 励志做一个跟QQ一样的侧滑菜单栏
 *
 * @author jqyzyh
 */

public class QQDrawerLayout extends ViewGroup implements GestureDetector.OnGestureListener {
    final static String LOG_TAG = "QQDrawerLayout";
    final static boolean DEBUG = true;

    final static int STATE_NONE = 0;//正常状态
    final static int STATE_DRAGING_RIGHT = 1;//右划中
    final static int STATE_DRAGING_LEFT = 2;//左划中
    final static int STATE_SHOW_LEFT = 3;//已经展示左菜单
    final static int STATE_SHOW_RIGHT = 4;//已经展示右菜单

    public interface DrawerListener {
        public void onDrawerSlide(View drawerView, float slideOffset);
    }

    private View mLeftMenu;
    private View mRightMenu;
    private View mContentView;

    private int mState = STATE_NONE;

    private float mMenuOffset = 0.4f;

    private GestureDetector mGestureDetector;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private FlingMenuRunnable flingMenuRunnable;

    private int mFlingSeed = 2;

    public QQDrawerLayout(Context context) {
        super(context);
        setup(context, null);
    }

    public QQDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
    }

    public QQDrawerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public QQDrawerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpec = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpec = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (DEBUG) {
            Log.d(LOG_TAG, "onMeasure==>" + widthSize + "," + heightSize);
        }

        int measuredWidth, measuredHeight;

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        if (MeasureSpec.AT_MOST == widthSpec) {
            measuredWidth = widthSize;
        } else {
            measuredWidth = widthSize;
        }

        if (MeasureSpec.AT_MOST == heightSpec) {
            measuredHeight = heightSize;
        } else {
            measuredHeight = heightSize;
        }

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    private void setup(Context context, AttributeSet attr) {
        Log.d(LOG_TAG, "setup==>");
        if (mGestureDetector != null) {
            return;
        }
        mGestureDetector = new GestureDetector(context, this);

        mFlingSeed = Math.max(1, (int) (getResources().getDisplayMetrics().density * 1));
    }

    public void setMenuOffset(float menuOffset) {
        this.mMenuOffset = menuOffset;
        requestLayout();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if (DEBUG) {
            Log.d(LOG_TAG, "onLayout==>" + l + "," + t + "," + r + "," + b);
        }
        int len = getChildCount();

        for (int i = 0; i < len; i++) {
            View child = getChildAt(i);

            LayoutParams lp = (LayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            if (LayoutParams.TYPE_RIGHT == lp.drawerType) {
                int offset = getRightMenuOffset(mContentView.getLeft());
                child.layout(r - childWidth + offset, t, r + offset, t + childHeight);
            } else if (LayoutParams.TYPE_LEFT == lp.drawerType) {
                int offset = getLeftMenuOffset(mContentView.getLeft());
                child.layout(l + offset, t, l + childWidth + offset, t + childHeight);
            } else {
                if (DEBUG) {
                    Log.d(LOG_TAG, "onLayout child==>" + child.getX());
                }
                child.layout(l + lp.drawerLeft, t, l + childWidth + lp.drawerLeft, t + childHeight);
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int count = getChildCount();

        if (DEBUG) {
            Log.d(LOG_TAG, "onFinishInflate==>" + count);
        }
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (LayoutParams.TYPE_LEFT == lp.drawerType) {
                if (mLeftMenu == null) {
                    mLeftMenu = child;
                }
            } else if (LayoutParams.TYPE_RIGHT == lp.drawerType) {
                if (mRightMenu == null) {
                    mRightMenu = child;
                }
            } else if (LayoutParams.TYPE_CONTENT == lp.drawerType) {
                if (mContentView == null) {
                    mContentView = child;
                }
            }
        }
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return mDragHelper.shouldInterceptTouchEvent(ev);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = mGestureDetector.onTouchEvent(event);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (flingMenuRunnable != null) {
                    mHandler.removeCallbacks(flingMenuRunnable);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (flingMenuRunnable == null) {
                    int left = mContentView.getLeft();
                    if (left > 0){
                        flingMenuRunnable = new FlingMenuRunnable(left < mLeftMenu.getWidth() / 2 ? 0 : mLeftMenu.getWidth());
                        mHandler.post(flingMenuRunnable);
                    }else if(left < 0){
                        flingMenuRunnable = new FlingMenuRunnable(left > mRightMenu.getWidth() / -2 ? 0 : -mRightMenu.getWidth());
                        mHandler.post(flingMenuRunnable);
                    }
                }
                break;

        }
        return ret;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        static int TYPE_CONTENT = 0;
        static int TYPE_LEFT = 1;
        static int TYPE_RIGHT = 2;

        int drawerType;
        int drawerLeft;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray array = c.getResources().obtainAttributes(attrs, R.styleable.QQDrawerLayout);
            drawerType = array.getInt(R.styleable.QQDrawerLayout_DrawerType, TYPE_CONTENT);
            array.recycle();

        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    int getLeftMenuOffset(int contentLeft) {
        if (mLeftMenu == null || mMenuOffset <= 0) {
            return 0;
        }
        int childWidth = mLeftMenu.getMeasuredWidth();
        int offset = -(int) (childWidth * mMenuOffset * (childWidth - contentLeft) / childWidth);
        return offset;
    }

    int getRightMenuOffset(int contentLeft) {
        if (mRightMenu == null || mMenuOffset <= 0) {
            return 0;
        }
        int childWidth = mRightMenu.getMeasuredWidth();
        int offset = (int) (childWidth * mMenuOffset * (childWidth + contentLeft) / childWidth);
        return offset;
    }

    boolean scrollMenu(int dx) {
        int oldLeft = mContentView.getLeft();
        int left = oldLeft + dx;

        boolean isShowLeft;//是否显示左菜单

        if (oldLeft > 0) {//已经在右划了
            isShowLeft = true;
        } else if (oldLeft < 0) {//已经在左划了
            isShowLeft = false;
        } else {
            if (dx > 0) {//开始右划
                if (mLeftMenu == null) {
                    return false;
                }
                isShowLeft = true;
            } else {//开始左划
                if (mRightMenu == null) {
                    return false;
                }
                isShowLeft = false;
            }
        }

        if (isShowLeft) {
            int menuWidth = mLeftMenu.getWidth();
            left = Math.max(0, Math.min(menuWidth, left));
            int offset = getLeftMenuOffset(left);
            if (left == 0) {//左边到头了
                mState = STATE_NONE;
            } else if (left == menuWidth) {
                mState = STATE_SHOW_LEFT;
            } else {
                mState = STATE_DRAGING_LEFT;
            }
            ViewCompat.offsetLeftAndRight(mLeftMenu, offset - mLeftMenu.getLeft());
        } else {
            int menuWidth = mRightMenu.getWidth();
            left = Math.min(0, Math.max(-mRightMenu.getWidth(), left));
            int offset = getRightMenuOffset(left);
            if (left == 0) {//左边到头了
                mState = STATE_NONE;
            } else if (left == getWidth() - menuWidth) {
                mState = STATE_SHOW_RIGHT;
            } else {
                mState = STATE_DRAGING_RIGHT;
            }
            ViewCompat.offsetLeftAndRight(mRightMenu, (getWidth() - menuWidth) + offset - mRightMenu.getLeft());
        }

        ViewCompat.offsetLeftAndRight(mContentView, left - oldLeft);
        return true;
    }

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
        return scrollMenu(-(int) distanceX);
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        int oldLeft = mContentView.getLeft();

        if (DEBUG) {
            Log.d(LOG_TAG, "onFling==>"+ oldLeft + ", " + velocityX);
        }

        if (oldLeft > 0) {//已经向左

            if (velocityX > 1000) {
                flingMenuRunnable = new FlingMenuRunnable(mLeftMenu.getWidth());
                mHandler.post(flingMenuRunnable);
                return true;
            } else if (velocityX < -1000) {
                flingMenuRunnable = new FlingMenuRunnable(0);
                mHandler.post(flingMenuRunnable);
                return true;
            }

        } else if (oldLeft < 0) {//已经向右

            if (velocityX < -1000) {
                flingMenuRunnable = new FlingMenuRunnable(-mRightMenu.getWidth());
                mHandler.post(flingMenuRunnable);
                return true;
            } else if (velocityX > 1000) {
                flingMenuRunnable = new FlingMenuRunnable(0);
                mHandler.post(flingMenuRunnable);
                return true;
            }
        }

        return false;
    }

    class FlingMenuRunnable implements Runnable {

        int desX;
        long lastTime;
        boolean running;

        public FlingMenuRunnable(int desX) {
            running = true;
            this.desX = desX;
            lastTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            long dt = System.currentTimeMillis() - lastTime;
            int oldLeft = mContentView.getLeft();
            int dx = (int) (dt * mFlingSeed);
            lastTime = System.currentTimeMillis();

            int left;
            if (desX > oldLeft) {//向右动
                left = Math.min(desX, oldLeft + dx);
            } else {
                left = Math.max(desX, oldLeft - dx);
            }

            if (DEBUG) {
                Log.d(LOG_TAG, "FlingMenuRunnable dx==>" + dx);
                Log.d(LOG_TAG, "FlingMenuRunnable left==>" + left);
            }

            if (left == desX) {
                running = false;
            }

            scrollMenu(left - oldLeft);

            if (running) {
                mHandler.postDelayed(this, 10);
            } else {
                flingMenuRunnable = null;
            }
        }
    }
}
