package jqyzyh.iee.cusomwidget.indicator;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import jqyzyh.iee.cusomwidget.R;

/**
 * @author yuhang
 * @attr ret R.styleable#ColorIndicator_IndicatorTextSize
 * @attr ret R.styleable#ColorIndicator_NormalTextSize
 * @attr ret R.styleable#ColorIndicator_NormalColor
 * @attr ret R.styleable#ColorIndicator_IndicatorColor
 * @attr ret R.styleable#ColorIndicator_NormalTypeface
 * @attr ret R.styleable#ColorIndicator_IndicatorTypeface
 * @attr ret R.styleable#ColorIndicator_TabSpace
 */

public class ColorIndicator extends View implements ViewPager.OnPageChangeListener, GestureDetector.OnGestureListener {
    static final String LOG_TAG = "ColorIndicator";

    static class TabItem {
        String text;
        int width;

        public TabItem(String text) {
            this.text = text;
        }
    }

    private class PagerAdapterObserver extends DataSetObserver {
        PagerAdapterObserver() {
        }

        @Override
        public void onChanged() {
            setupTabs();
        }

        @Override
        public void onInvalidated() {
            setupTabs();
        }
    }

    private boolean mInited = false;

    private ViewPager mViewPager;
    private PagerAdapter mAdapter;
    private DataSetObserver mDataSetObserver;

    private Paint mNormalPaint;
    private Paint mIndicatorPaint;

    private int mNormalColor;
    private int mIndicatorColor;

    private List<TabItem> itemList = new ArrayList<>();

    private int mAllWidth = 0;//全部文字宽度
    private int mTextHeight = 0;//文字的高度
    private int mIndicatorTextHeight = 0;//选中文字的高度
    private int mTabSpace = 20;//文字间距

    private int mSelectedPosition;//当前选中项
    private int mIndicatorPosition;//选中项位置
    private float mIndicatorOffset;//当前选中项便宜

    private float mOffsetX;

    private FlingRunnable mFlingRunnable;

    private GestureDetector mGestureDetector;

    public ColorIndicator(Context context) {
        super(context);
        setup(context, null);
    }

    public ColorIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
    }

    public ColorIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ColorIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(context, attrs);
    }

    private void setup(Context context, AttributeSet attrs) {
        if (mInited) {
            return;
        }
        mInited = true;
        mNormalPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mNormalPaint.setTextAlign(Paint.Align.CENTER);
        mIndicatorPaint.setTextAlign(Paint.Align.CENTER);
        if (attrs == null) {
            mNormalColor = 0xff333333;
            mIndicatorColor = 0xff0000ff;
            mNormalPaint.setTextSize(16 * getResources().getDisplayMetrics().density);
            mIndicatorPaint.setTextSize(16 * getResources().getDisplayMetrics().density);
            mTabSpace = (int) (10 * getResources().getDisplayMetrics().density);
        } else {
            TypedArray ary = context.obtainStyledAttributes(attrs, R.styleable.ColorIndicator);
            mNormalColor = ary.getColor(R.styleable.ColorIndicator_NormalColor, 0xff333333);
            mIndicatorColor = ary.getColor(R.styleable.ColorIndicator_IndicatorColor, 0xff0000ff);
            mNormalPaint.setTextSize(ary.getDimension(R.styleable.ColorIndicator_NormalTextSize, 16));
            mIndicatorPaint.setTextSize(ary.getDimension(R.styleable.ColorIndicator_IndicatorTextSize, 16));
            switch (ary.getInt(R.styleable.ColorIndicator_NormalTypeface, 0)) {
                case 1:
                    mNormalPaint.setTypeface(Typeface.DEFAULT_BOLD);
                    break;
                default:
                    mNormalPaint.setTypeface(Typeface.DEFAULT);
                    break;
            }
            switch (ary.getInt(R.styleable.ColorIndicator_IndicatorTypeface, 0)) {
                case 1:
                    mIndicatorPaint.setTypeface(Typeface.DEFAULT_BOLD);
                    break;
                default:
                    mIndicatorPaint.setTypeface(Typeface.DEFAULT);
                    break;
            }
            mTabSpace = ary.getDimensionPixelSize(R.styleable.ColorIndicator_TabSpace, (int) (10 * getResources().getDisplayMetrics().density));
            ary.recycle();
        }


        Rect rect = new Rect();
        mNormalPaint.getTextBounds("国口", 0, 2, rect);
        mTextHeight = rect.height();
        mIndicatorPaint.getTextBounds("国口", 0, 2, rect);
        mIndicatorTextHeight = rect.height();
    }

    public void attachViewPager(ViewPager viewPager) {
        if (mViewPager != null) {
            mViewPager.removeOnPageChangeListener(this);
        }
        mViewPager = viewPager;
        resetIndicator();
        if (viewPager == null) {
            return;
        }
        viewPager.addOnPageChangeListener(this);
        setPagerAdapter(viewPager.getAdapter(), true);
    }

    void setPagerAdapter(PagerAdapter adapter, boolean autRefresh) {
        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
        mAdapter = adapter;

        if (autRefresh && adapter != null){

        }
        if (adapter != null) {
            if (mDataSetObserver == null) {
                mDataSetObserver = new PagerAdapterObserver();
            }
            adapter.registerDataSetObserver(mDataSetObserver);
        }
        setupTabs();
    }

    /**
     * 设置选中项
     *
     * @param position
     */
    public void setCurrentItem(int position) {
        mViewPager.setCurrentItem(position);
        selectItem(position);
    }

    /**
     * 设置文字字号
     *
     * @param textSize
     */
    public void setTextSize(float textSize) {
        mNormalPaint.setTextSize(textSize);
        Rect rect = new Rect();
        mNormalPaint.getTextBounds("国口", 0, 2, rect);
        mTextHeight = rect.height();
        measureTabs();
        invalidate();
    }

    /**
     * 设置选中文字字号
     *
     * @param textSize
     */
    public void setIndicatorTextSize(float textSize) {
        mIndicatorPaint.setTextSize(textSize);
        Rect rect = new Rect();
        mIndicatorPaint.getTextBounds("国口", 0, 2, rect);
        mIndicatorTextHeight = rect.height();
        measureTabs();
        invalidate();
    }

    /**
     * 设置tab文字颜色
     *
     * @param color
     */
    public void setTabColor(int color) {
        mNormalColor = color;
        invalidate();
    }

    /**
     * 设置选中颜色
     *
     * @param color
     */
    public void setIndicatorColor(int color) {
        mIndicatorColor = color;
        invalidate();
    }

    /**
     * 设置边距
     *
     * @param tabSpace
     */
    public void setTabSpace(int tabSpace) {
        this.mTabSpace = tabSpace;
        invalidate();
    }

    /**
     * 设置字体
     *
     * @param typeface
     */
    public void setTypeface(Typeface typeface) {
        mNormalPaint.setTypeface(typeface);
        invalidate();
    }

    /**
     * 设置选中字体
     *
     * @param typeface
     */
    public void setIndicator(Typeface typeface) {
        mIndicatorPaint.setTypeface(typeface);
        invalidate();
    }

    private void resetIndicator() {
        mSelectedPosition = 0;
        mIndicatorPosition = 0;
        mIndicatorOffset = 0;
        mOffsetX = 0;
    }

    /**
     * 重置整个tab栏
     */
    public void setupTabs() {
        itemList.clear();
        if (mViewPager == null) {
            invalidate();
            return;
        }
        PagerAdapter adapter = mViewPager.getAdapter();

        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            String title = (String) adapter.getPageTitle(i);
            itemList.add(new TabItem(title));
        }
        measureTabs();
        if (mViewPager != null) {
            mSelectedPosition = mViewPager.getCurrentItem();
            selectItem(mSelectedPosition);
        }
        invalidate();
    }

    private void measureTabs() {
        mAllWidth = 0;
        Rect rect = new Rect();
        for (TabItem item : itemList) {
            mNormalPaint.getTextBounds(item.text, 0, item.text.length(), rect);
            item.width = rect.width();
            mAllWidth += item.width + mTabSpace + mTabSpace;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw){
            offsetIndicatorToMid(mSelectedPosition);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int len = itemList.size();//导航个数
        if (len == 0) {
            return;
        }
        int width = getWidth();//绘制区域宽度
        int height = getHeight();//绘制区域高度

        int startIndex = -1;//可视区域起始索引
        int endIndex = 0;//可视区域结束索引
        int clipLeft = 0;
        int clipRight = 0;

        Rect clipRect = new Rect(0, 0, 0, height);//绘制区域

        //计算一下
        int x = (int) -mOffsetX;//当前绘制的位置
        int end = 0;
        for (int i = 0; i < len; i++) {
            TabItem item = itemList.get(i);
            if (x > width) {//如果当前绘制位置在屏幕外右边则不在绘制
                break;
            }
            end = x + mTabSpace + mTabSpace + item.width;
            if (end < 0) {//如果当前绘制位置在屏幕外左边则直接绘制下一个
                x = end;
                continue;
            }
            if(startIndex == -1){
                startIndex = i;
            }
            endIndex = i;

            if (i == mIndicatorPosition) {//当前选中的项
                if (mIndicatorOffset > 0) {//正在滑动
                    clipLeft = (int) (x + mIndicatorOffset * (item.width + mTabSpace + mTabSpace));
                } else {
                    clipLeft = x;
                    clipRight = end;
                }
            } else if (i == mIndicatorPosition + 1) {//当前选中的项的下一项
                if (mIndicatorOffset > 0) {//正在滑动
                    clipRight = (int) (x + mIndicatorOffset * (item.width + mTabSpace + mTabSpace));
                } else {
                    clipRight = x;
                }
            }
            x = end;
        }
        endIndex += 1;

        x = (int) -mOffsetX;//当前绘制的位置
        end = 0;
        for (int i = 0; i < endIndex; i++) {
            TabItem item = itemList.get(i);
            end = x + mTabSpace + mTabSpace + item.width;

            if (i < startIndex){
                x = end;
                continue;
            }

            Paint paint = i == mSelectedPosition ? mIndicatorPaint : mNormalPaint;
            int textHeight =  i == mSelectedPosition ? mIndicatorTextHeight : mTextHeight;
            int textY = (height + textHeight) / 2;

            if(x <= clipLeft){
                clipRect.left = 0;
                clipRect.right = clipLeft;
                int saveCount = canvas.save();
                canvas.clipRect(clipRect);
                paint.setColor(mNormalColor);
                canvas.drawText(item.text, x + (end - x) / 2, textY, paint);
                canvas.restoreToCount(saveCount);
            }

            if(end >= clipRight){
                clipRect.left = clipRight;
                clipRect.right = width;
                int saveCount = canvas.save();
                canvas.clipRect(clipRect);
                paint.setColor(mNormalColor);
                canvas.drawText(item.text, x + (end - x) / 2, textY, paint);
                canvas.restoreToCount(saveCount);
            }

            if (x <= clipRight || end >= clipLeft){
                clipRect.left = clipLeft;
                clipRect.right = clipRight;
                int saveCount = canvas.save();
                canvas.clipRect(clipRect);
                paint.setColor(mIndicatorColor);
                canvas.drawText(item.text, x + (end - x) / 2, textY, paint);
                canvas.restoreToCount(saveCount);
            }
            x = end;
        }
    }

    /**
     * 设置当前选中项
     *
     * @param position
     */
    private void selectItem(int position) {
        if (position >= itemList.size()) {
            return;
        }
        mSelectedPosition = position;
        mIndicatorPosition = position;
        mIndicatorOffset = 0;
        offsetIndicatorToMid(position);
    }

    /**
     * 滚动view让 position对应的项显示在中间
     *
     * @param position
     */
    public void offsetIndicatorToMid(int position) {
        if (position >= itemList.size()) {
            return;
        }

        int len = itemList.size();
        int curStart = 0;
        int curEnd = 0;
        for (int i = 0; i < len; i++) {
            TabItem item = itemList.get(i);
            if (i == position) {
                curEnd = curStart + mTabSpace + mTabSpace + item.width;
                break;
            }
            curStart += mTabSpace + mTabSpace + item.width;
        }

        int width = getWidth();
        int textWidth = curEnd - curStart;

        setTabOffsetX(curStart - (width - textWidth) / 2);
    }

    /**
     * 设置控件的显示区域
     */
    private void setTabOffsetX(float offsetX) {
        if (offsetX < 0) {
            mOffsetX = 0;
            invalidate();
            return;
        }
        int max = Math.max(0, mAllWidth - getWidth());
        if (offsetX > max) {
            mOffsetX = max;
            invalidate();
            return;
        }
        mOffsetX = offsetX;
        invalidate();
    }

    /**
     * 移动控件的显示区域
     *
     * @param dx 偏移量
     * @return 是否到头了
     */
    private boolean moveTabOffsetX(float dx) {
        float offsetX = mOffsetX + dx;
        if (offsetX < 0) {
            mOffsetX = 0;
            invalidate();
            return dx < 0;
        }
        int max = Math.max(0, mAllWidth - getWidth());
        if (offsetX > max) {
            mOffsetX = max;
            invalidate();
            return dx > 0;
        }
        mOffsetX = offsetX;
        invalidate();
        return false;
    }

    private void stopFling() {
        if (mFlingRunnable != null) {
            mFlingRunnable.running = false;
            mFlingRunnable = null;
        }
    }

    //=============================== OnPageChangeListener ======================
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mViewPager == null) {
            return;
        }

        mIndicatorPosition = position;
        mIndicatorOffset = positionOffset;

        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        if (mViewPager == null) {
            return;
        }
        mSelectedPosition = position;
        offsetIndicatorToMid(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mViewPager == null) {
            return;
        }
        if (ViewPager.SCROLL_STATE_IDLE == state) {
            selectItem(mViewPager.getCurrentItem());
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        stopFling();
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(getContext(), this);
        }
        boolean ret = mGestureDetector.onTouchEvent(event);
        return ret;//super.onTouchEvent(event);
    }

    //=============================== OnGestureListener ======================
    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (mViewPager == null) {
            return false;
        }
        int touchX = (int) (mOffsetX + e.getX());
        int len = itemList.size();
        int start = 0;
        int end = 0;
        for (int i = 0; i < len; i++) {
            TabItem item = itemList.get(i);
            end = start + mTabSpace + mTabSpace + item.width;

            if (start < touchX && touchX < end) {
                mViewPager.setCurrentItem(i);
                return true;
            }

            start = end;
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return !moveTabOffsetX(distanceX);
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        mFlingRunnable = new FlingRunnable(velocityX);
        post(mFlingRunnable);
        return true;
    }

    /**
     *
     */
    class FlingRunnable implements Runnable {
        float velocityX;//当前移动速度
        long lastTime;//上一次计算时间
        boolean running = true;
        boolean isToLeft;//是否向左滚  手指从右向左

        public FlingRunnable(float velocityX) {
            if (velocityX < 0) {
                isToLeft = true;
                this.velocityX = -velocityX;
            } else {
                isToLeft = false;
                this.velocityX = velocityX;
            }
            this.lastTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            if (!running) {
                return;
            }

            long d = System.currentTimeMillis() - lastTime;//距离上一次移动的间隔
            lastTime = System.currentTimeMillis();

            float dx = d * velocityX / 1000;//计算移动的距离
            if (moveTabOffsetX(isToLeft ? dx : -dx)) {
                running = false;
                return;
            }

            velocityX -= d * 10;//减个速

            if (velocityX > 0) {//如果还有速度继续下一帧
                postDelayed(this, 10);
            } else {
                running = false;
            }
        }
    }
}
