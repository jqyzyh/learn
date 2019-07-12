package jqyzyh.iee.cusomwidget.wheelloop;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

/**
 * 一个仿照ios滚轮控件
 *
 * @author jqyzyh
 */

public class RollerView extends View implements GestureDetector.OnGestureListener {

    private Paint mTextPaint;//文字画笔
    private Paint mPaintIndicator;//线的画笔

    private GestureDetector gestureDetector;

    private int mTextHeight;//文字高度

    private int mPaperHeight;//平面的高度
    private float initZ;//摄像机Z初始位置
    private float mCameraTranslateZ;//摄像机Z轴偏移 mTextHeight * 3

    private float mPaperOffsetY;//滚动的高度

    private RollerItem mRoller;

    private int mWindowCenterX;

    private FlingRunnable mFlingRunable;

    private float mLastTouchY;

    private float maxFlingSpeed;//惯性最大速度
    private float minFlingSpeed;//惯性最小速度 减速到这次速度一下 就停止并校准位置
    private float autoFlingSpeed;//自动滚动的速度

    private OnItemSelectorListener mItemSelectorListener;
    private Paint.FontMetrics mFontMetrics;//计算字体绘制区域

    public RollerView(Context context) {
        super(context);
        init(context, null);
    }

    public RollerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RollerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RollerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (mTextPaint != null) {
            return;
        }

        //惯性速度
        maxFlingSpeed = TypedValue.applyDimension(COMPLEX_UNIT_DIP, 400, context.getResources().getDisplayMetrics());
        minFlingSpeed = TypedValue.applyDimension(COMPLEX_UNIT_DIP, 25, context.getResources().getDisplayMetrics());
        autoFlingSpeed = TypedValue.applyDimension(COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics());

        initZ = -TypedValue.applyDimension(COMPLEX_UNIT_DIP, 7, context.getResources().getDisplayMetrics());
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(0xff333333);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mPaintIndicator = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintIndicator.setColor(0xffc5c5c5);
        measureText();

        gestureDetector = new GestureDetector(getContext(), this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureHeght = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == MeasureSpec.AT_MOST) {
            measureHeght = mTextHeight * 7;
        }

        setMeasuredDimension(measureWidth, measureHeght);
    }

    void measureText() {
        mFontMetrics = mTextPaint.getFontMetrics();
        Rect rect = new Rect();
        mTextPaint.getTextBounds("方块", 0, 2, rect);
        mTextHeight = rect.height();

        mPaperHeight = mTextHeight * 9;
        mCameraTranslateZ = mTextHeight * 4F;
    }

    float getTextBaseLine(float y) {
        return getTextBaseLine(y, mFontMetrics);
    }

    public static float getTextBaseLine(float y, Paint.FontMetrics fm) {
        if (fm == null) {
            return y;
        }
        return y + (fm.bottom - fm.top) / 2 - fm.bottom;
    }

    void drawText(Canvas canvas, String text, float inPaperY, Paint paint) {
        drawText(canvas, text, inPaperY, paint, 1);
    }

    /**
     * 绘制文字
     *
     * @param canvas   画布
     * @param text     要绘制的文字
     * @param inPaperY 绘制未在在平面中的位置
     * @param paint    画笔
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    void drawText(Canvas canvas, String text, float inPaperY, Paint paint, float scale) {
        inPaperY += mPaperOffsetY;
        float angle = 90 - (inPaperY * 180 / mPaperHeight);
        if (angle >= 90 || angle <= -90) {
            return;
        }

        Camera camera = new Camera();
        camera.setLocation(0, 0, initZ);
        //下面三行代码可以实现滚轮的效果，需要发挥一些想象力
        camera.translate(0, 0, mCameraTranslateZ);
        camera.rotateX(angle);
        camera.translate(0, 0, -mCameraTranslateZ);
        int saveTranslate = canvas.save();
        canvas.translate(mWindowCenterX, getHeight() / 2);
        int saveCamera = canvas.save();
        camera.applyToCanvas(canvas);
        canvas.translate(-mWindowCenterX + getWidth() / 2, 0);
        if (scale != 1) {
            canvas.scale(scale, scale);
        }
        canvas.drawText(text, 0, getTextBaseLine(0), paint);

        canvas.restoreToCount(saveCamera);
        canvas.restoreToCount(saveTranslate);
    }

    void selectRoller(RollerItem item) {
        if (mRoller == item) {
            return;
        }
        mRoller = item;

        if (mItemSelectorListener != null) {
            mItemSelectorListener.onItemSelect(this, mRoller);
        }
    }

    boolean scrollPaper(float dy) {
        float offsetY = mPaperOffsetY + dy;

        RollerItem temp;
        RollerItem result = mRoller;
        boolean isEnd = false;
        if (offsetY < 0) {//向上
            while (offsetY < -mTextHeight) {
                temp = result.next();
                if (temp == null) {
                    offsetY = 0;
                    isEnd = true;
                    break;
                }
                //下面一个为选中
                result = temp;
                offsetY += mTextHeight;
            }
            temp = result.next();
            if (temp == null) {
                offsetY = 0;
                isEnd = true;
            }
        } else if (offsetY > 0) {//向下
            while (offsetY > mTextHeight) {
                temp = result.last();
                if (temp == null) {
                    offsetY = 0;
                    isEnd = true;
                    break;
                }
                //上面面一个为选中
                result = temp;
                offsetY -= mTextHeight;
            }
            temp = result.last();
            if (temp == null) {
                offsetY = 0;
                isEnd = true;
            }
        }

        mPaperOffsetY = offsetY;
        selectRoller(result);
        invalidate();
        return isEnd;
    }

    /**
     * 惯性滚动
     * @param velocityY 初始速度
     */
    protected final void flingPaper(float velocityY) {
        cancelFling();
        post(mFlingRunable = new FlingRunnable(velocityY));
    }

    /**
     * 自动校准
     */
    protected final void autoCenter() {
        cancelFling();
        float vy = 0;
        if (mPaperOffsetY < 0) {
            if (mPaperOffsetY < -mTextHeight / 2) {
                vy = -autoFlingSpeed;
            } else {
                vy = autoFlingSpeed;
            }
        } else if (mPaperOffsetY > 0) {
            if (mPaperOffsetY > mTextHeight / 2) {
                vy = autoFlingSpeed;
            } else {
                vy = -autoFlingSpeed;
            }
        }
        if (vy != 0) {
            mFlingRunable = new FlingRunnable(vy, true);
            post(mFlingRunable);
        }
    }

    public void cancelFling() {
        if (mFlingRunable != null) {
            removeCallbacks(mFlingRunable);
            mFlingRunable = null;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int[] location = new int[2];
        getLocationInWindow(location);
        mWindowCenterX = getResources().getDisplayMetrics().widthPixels / 2 - location[0];
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mRoller == null) {
            return;
        }
        int saveCount;
        canvas.drawARGB(0x44, 0xff, 0, 0);
        float centerHeight = mTextHeight * 1.5F;
        float lineY = getHeight() / 2 - centerHeight / 2;

        mTextPaint.setColor(0xff999999);
        //画上边
        saveCount = canvas.save();
        canvas.clipRect(0, 0, getWidth(), lineY);
        if (mPaperOffsetY <= 0) {
            drawText(canvas, mRoller.getText(), mPaperHeight / 2, mTextPaint);
        }
        RollerItem temp;
        temp = mRoller;
        for (int i = 0; i < 4; i++) {
            temp = temp.last();
            if (temp == null) {
                break;
            }
            drawText(canvas, temp.getText(), mPaperHeight / 2 - (i + 1) * mTextHeight, mTextPaint);
        }
        canvas.restoreToCount(saveCount);

        //画下边
        saveCount = canvas.save();
        canvas.clipRect(0, lineY + centerHeight, getWidth(), getHeight());
        if (mPaperOffsetY > 0) {
            drawText(canvas, mRoller.getText(), mPaperHeight / 2, mTextPaint);
        }
        temp = mRoller;
        for (int i = 0; i < 4; i++) {
            temp = temp.next();
            if (temp == null) {
                break;
            }

            drawText(canvas, temp.getText(), mPaperHeight / 2 + (i + 1) * mTextHeight, mTextPaint);
        }
        canvas.restoreToCount(saveCount);

        //画中间
        mTextPaint.setColor(0xff333333);
        saveCount = canvas.save();
        canvas.clipRect(0, lineY, getWidth(), lineY + centerHeight);
        if (mPaperOffsetY < 0) {//向上滚动了 画下边的第一项
            temp = mRoller.next();
            if (temp != null) {
                drawText(canvas, temp.getText(), mPaperHeight / 2 + mTextHeight, mTextPaint, 1.1f);
            }
        } else if (mPaperOffsetY > 0) {//向下滚动了 画上边的第一项
            temp = mRoller.last();
            if (temp != null) {
                drawText(canvas, temp.getText(), mPaperHeight / 2 - mTextHeight, mTextPaint, 1.1f);
            }
        }
        drawText(canvas, mRoller.getText(), mPaperHeight / 2, mTextPaint, 1.1f);
        canvas.restoreToCount(saveCount);
//
        canvas.drawLine(0, lineY, getWidth(), lineY, mPaintIndicator);
        canvas.drawLine(0, lineY + centerHeight, getWidth(), lineY + centerHeight, mPaintIndicator);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) {
            if (mFlingRunable == null) {
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
                if (mFlingRunable == null) {
                    autoCenter();
                }
                break;
        }
        mLastTouchY = event.getRawY();
        return true;
    }

    public RollerItem getSelectItem() {
        return mRoller;
    }

    public void setItemSelectorListener(OnItemSelectorListener itemSelectorListener) {
        this.mItemSelectorListener = itemSelectorListener;
    }

    /**
     * 修改字号
     *
     * @param textSize 字号大小 单位dp
     */
    public void setTextSizeSp(float textSize) {
        if (textSize > 0.0F) {
            textSize = (int) (getResources().getDisplayMetrics().density * textSize);
            setTextSize(textSize);
        }
    }

    /**
     * 修改字号
     *
     * @param textSize 字号大小 单位xp
     */
    public void setTextSize(float textSize) {
        mTextPaint.setTextSize(textSize);
        measureText();
        requestLayout();
    }

    public void setRollerItem(RollerItem item) {
        this.mRoller = item;
        invalidate();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        cancelFling();
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

    public interface RollerItem {
        Object getItem();

        String getText();

        RollerItem last();

        RollerItem next();
    }

    public interface OnItemSelectorListener {
        void onItemSelect(RollerView view, RollerItem item);
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

            if (!autoFling) {//自动滚动是不减速的
                if (velocityY < 0) {
                    velocityY += time * 0.5f;
                    if (velocityY > -minFlingSpeed) {
                        autoCenter();
                        return;
                    }
                } else if (velocityY > 0) {
                    velocityY -= time * 0.5f;
                    if (velocityY < minFlingSpeed) {
                        autoCenter();
                        return;
                    }
                }
            }

            float dy = velocityY * time / 1000;
            if (!scrollPaper(dy)) {
                if (autoFling) {
                    if (Math.abs(dy) >= Math.abs(mPaperOffsetY)) {
                        mPaperOffsetY = 0;
                        velocityY = 0;
                        return;
                    }
                }
                postDelayed(this, 10);
            }
        }
    }
}
