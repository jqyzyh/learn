package jqyzyh.iee.cusomwidget.selector.rolling;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import jqyzyh.iee.cusomwidget.R;
import jqyzyh.iee.cusomwidget.selector.CanSelectItem;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

/**
 * 滚动选择器
 *
 * @author jqyzyh on 2019/7/12
 */
public class RollingSelector extends View implements GestureDetector.OnGestureListener {
    public static final int STATE_NONE = 0;//静止状态
    public static final int STATE_TOUCH = 1;//触摸状态
    public static final int STATE_FLING = 2;//滚动状态

    private int mState = STATE_NONE;

    private float maxFlingSpeed;//惯性最大速度
    private float minFlingSpeed;//惯性最小速度 减速到这次速度一下 就停止并校准位置
    private float autoFlingSpeed;//自动滚动的速度
    private FlingRunnable mFlingRunnable;

    private RollingSkin skin;

    private float mLastTouchY;//上次触摸的坐标
    private GestureDetector gestureDetector;

    private float mPaperOffsetY;//滚动的高度

    private int mLineHeight;//行高

    private CanSelectItem canSelectItem;

    private OnSelectItemListener selectItemListener;

    public RollingSelector(Context context) {
        super(context);
        init(context, null);
    }

    public RollingSelector(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RollingSelector(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RollingSelector(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (skin != null) {
            return;
        }
        //惯性速度
        maxFlingSpeed = TypedValue.applyDimension(COMPLEX_UNIT_DIP, 400, context.getResources().getDisplayMetrics());
        minFlingSpeed = TypedValue.applyDimension(COMPLEX_UNIT_DIP, 25, context.getResources().getDisplayMetrics());
        autoFlingSpeed = TypedValue.applyDimension(COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics());

        if (attrs == null) {
            skin = new LikeIOSSkin(this);
        } else {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RollingSelector);
            int style = array.getInt(R.styleable.RollingSelector_rollerSkin, 0);
            if (style == 1) {
                skin = new MiddleBigSkin(this, array);
            } else {
                skin = new LikeIOSSkin(this);
            }
        }
        gestureDetector = new GestureDetector(context, this);
        mLineHeight = skin.getLineHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = skin.measureHeight(widthMeasureSpec, heightMeasureSpec);
        if (height > 0) {
            setMeasuredDimension(getMeasuredWidth(), height + getPaddingTop() + getPaddingBottom());
        }
    }

    public static float getTextBaseLine(float y, Paint.FontMetrics fm) {
        if (fm == null) {
            return y;
        }
        return y + (fm.bottom - fm.top) / 2 - fm.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int saveCount = canvas.save();
        canvas.clipRect(new Rect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom()));
        canvas.translate(getPaddingLeft(), getPaddingTop());
        skin.draw(canvas, canSelectItem, mPaperOffsetY);
        canvas.restoreToCount(saveCount);
    }

    public CanSelectItem getCurSelectItem() {
        return canSelectItem;
    }

    public <T> void setCanSelect(CanSelectItem<T> item, OnSelectItemListener<T> listener) {
        canSelectItem = item;
        selectItemListener = listener;
        invalidate();
    }

    private void onSelectItem(CanSelectItem item) {
        if (canSelectItem == item) {
            return;
        }
        canSelectItem = item;

        if (selectItemListener != null) {
            selectItemListener.onSelectItem(this, canSelectItem);
        }
    }

    private void onChangeState(int state) {
        if (mState == state) {
            return;
        }

        if (selectItemListener != null) {
            selectItemListener.onSelectChangeState(this, state);
        }
    }

    /**
     * 滚动
     *
     * @param dy 滚动距离
     * @return
     */
    boolean scrollPaper(float dy) {
        float offsetY = mPaperOffsetY + dy;

        CanSelectItem temp;
        CanSelectItem result = canSelectItem;
        boolean isEnd = false;
        if (offsetY < 0) {//向上
            while (offsetY < -mLineHeight) {
                temp = result.next();
                if (temp == null) {
                    offsetY = 0;
                    isEnd = true;
                    break;
                }
                //下面一个为选中
                result = temp;
                offsetY += mLineHeight;
            }
            temp = result.next();
            if (temp == null) {
                offsetY = 0;
                isEnd = true;
            }
        } else if (offsetY > 0) {//向下
            while (offsetY > mLineHeight) {
                temp = result.last();
                if (temp == null) {
                    offsetY = 0;
                    isEnd = true;
                    break;
                }
                //上面面一个为选中
                result = temp;
                offsetY -= mLineHeight;
            }
            temp = result.last();
            if (temp == null) {
                offsetY = 0;
                isEnd = true;
            }
        }

        mPaperOffsetY = offsetY;
        onSelectItem(result);
        invalidate();
        return isEnd;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (skin != null) {
            skin.onLayout(changed, left, top, right, bottom);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) {
            if (mFlingRunnable == null) {
                autoCenter();
            }
        }

        switch (event.getActionMasked()) {
            case ACTION_DOWN:
                cancelFling();
                break;
            case ACTION_MOVE:
                scrollPaper(event.getRawY() - mLastTouchY);
                break;
            case ACTION_CANCEL:
            case ACTION_UP:
                if (mFlingRunnable == null) {
                    autoCenter();
                }
                break;
        }
        mLastTouchY = event.getRawY();
        return true;
    }

    /**
     * 取消滚动
     */
    public void cancelFling() {
        if (mFlingRunnable != null) {
            removeCallbacks(mFlingRunnable);
            mFlingRunnable = null;
        }
    }

    /**
     * 惯性滚动
     *
     * @param velocityY 初始速度
     */
    private final void flingPaper(float velocityY) {
        cancelFling();
        onChangeState(STATE_FLING);
        post(mFlingRunnable = new FlingRunnable(velocityY));
    }

    /**
     * 自动校准
     */
    private final void autoCenter() {
        cancelFling();
        float vy = 0;
        if (mPaperOffsetY < 0) {
            if (mPaperOffsetY < -mLineHeight / 2) {
                vy = -autoFlingSpeed;
            } else {
                vy = autoFlingSpeed;
            }
        } else if (mPaperOffsetY > 0) {
            if (mPaperOffsetY > mLineHeight / 2) {
                vy = autoFlingSpeed;
            } else {
                vy = -autoFlingSpeed;
            }
        }
        if (vy != 0) {
            onChangeState(STATE_FLING);
            mFlingRunnable = new FlingRunnable(vy, true);
            post(mFlingRunnable);
        } else {
            onChangeState(STATE_NONE);
        }
    }

    //==================== GestureDetector.OnGestureListener ====================
    @Override
    public boolean onDown(MotionEvent e) {
        cancelFling();
        onChangeState(STATE_TOUCH);
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
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (Math.abs(velocityY) < minFlingSpeed) {//初始速度太低没有惯性
            autoCenter();
        } else {
            flingPaper(velocityY);
        }
        return true;
    }


    class FlingRunnable implements Runnable {
        float velocityY;
        long startTime;
        boolean autoFling;

        FlingRunnable(float velocityY, boolean autoFling) {
            this.startTime = System.currentTimeMillis();
            if (velocityY < 0) {
                velocityY = Math.max(velocityY, -maxFlingSpeed);
            } else {
                velocityY = Math.min(velocityY, maxFlingSpeed);
            }
            this.velocityY = velocityY;
            this.autoFling = autoFling;
        }

        FlingRunnable(float velocityY) {
            this(velocityY, false);
        }

        @Override
        public void run() {
            long time = System.currentTimeMillis() - startTime;
            startTime = System.currentTimeMillis();
            boolean isMinSpeed = false;
            if (!autoFling) {//自动滚动是不减速的
                if (velocityY < 0) {
                    velocityY += time * 0.5f;
                    if (velocityY > -minFlingSpeed) {
//                        mFlingRunnable = new FlingRunnable(-autoFlingSpeed, true);
//                        post(mFlingRunnable);
//                        return;
                        velocityY = -minFlingSpeed;
                        isMinSpeed = true;
                    }
                } else if (velocityY > 0) {
                    velocityY -= time * 0.5f;
                    if (velocityY < minFlingSpeed) {
//                        mFlingRunnable = new FlingRunnable(autoFlingSpeed, true);
//                        post(mFlingRunnable);
//                        return;
                        velocityY = minFlingSpeed;
                        isMinSpeed = true;
                    }
                }
            }

            float dy = velocityY * time / 1000;
            if (!scrollPaper(dy)) {
                if (autoFling || isMinSpeed) {
                    if (Math.abs(dy) >= Math.abs(mPaperOffsetY)) {
                        mPaperOffsetY = 0;
                        velocityY = 0;
                        onChangeState(STATE_NONE);
                        return;
                    }
                }
                postDelayed(this, 10);
            }
        }
    }
}
