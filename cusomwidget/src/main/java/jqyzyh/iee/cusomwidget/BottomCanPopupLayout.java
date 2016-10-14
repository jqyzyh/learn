package jqyzyh.iee.cusomwidget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by yuhang on 2016/10/11.
 * 一个放在底部的layout 初始有最大高度限制 可以向上弹出显示全部内容
 */

public class BottomCanPopupLayout extends LinearLayout implements GestureDetector.OnGestureListener {
    private int mInitMaxHeight = 0;//初始最大高度

    private int mContentMaxHeight;//内容高度

    private GestureDetector mGestureDetector;

    private int mFangxiangOffset = 1;//正在移动的方向偏移

    private long startTime;//动画开始时间

    private int moveSpeed = 1;//移动速度

    private Handler mAutoMoveHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    if(mFangxiangOffset == 0){
                        return;
                    }

                    long delta = System.currentTimeMillis() - startTime;
                    startTime = System.currentTimeMillis() - (delta % moveSpeed);

                    if(scrollLayout((int) (delta * moveSpeed) * mFangxiangOffset)){
                        mFangxiangOffset = 0;
                    }


                    if(mFangxiangOffset != 0){
                        mAutoMoveHandler.sendEmptyMessage(1);
                    }
                    break;
            }
        }
    };

    public BottomCanPopupLayout(Context context) {
        super(context);
        moveSpeed = (int) (getResources().getDisplayMetrics().density + 0.5f);
    }

    public BottomCanPopupLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        moveSpeed = (int) (getResources().getDisplayMetrics().density + 0.5f);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public BottomCanPopupLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        moveSpeed = (int) (getResources().getDisplayMetrics().density + 0.5f);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BottomCanPopupLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        moveSpeed = (int) (getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST));

        if (mInitMaxHeight == 0) {/*如果初始最高高度为0 初始化为150dp*/
            setmInitMaxHeight((int) (getResources().getDisplayMetrics().density * 150));
        }

        View child = getChildAt(0);
        if (child != null) {/*获取第一个view，该控件也只能有一个子view*/
            //这部还需要么。。。
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST));
            /*获取contentview的高度， 最大不能超过屏幕的3/4*/
            int contentHieght = Math.min(child.getMeasuredHeight(), getResources().getDisplayMetrics().heightPixels * 3 / 4);
            if (mContentMaxHeight != contentHieght) {/*如果内容高度变化了 重置整个view的状态*/
                mContentMaxHeight = contentHieght;
                mFangxiangOffset = 0;
                ViewGroup.LayoutParams lp = getLayoutParams();
                if (mContentMaxHeight > this.mInitMaxHeight) {/*高度超过初始最大高度 说明有未显示部分 可以向上弹出*/
                    lp.height = mInitMaxHeight;
                }else{/*内容高度小于初始最大高度，layout_height直接改为自适应*/
                    lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                }
                invalidate();
                return;
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int getmInitMaxHeight() {
        return mInitMaxHeight;
    }

    public void setmInitMaxHeight(int mInitMaxHeight) {
        this.mInitMaxHeight = mInitMaxHeight;
        requestLayout();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(getContext(), this);
        }

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            mFangxiangOffset = 0;
        }

        event.setLocation(event.getX(), event.getY() + getTop());/*把touch事件的坐标转换为父控件坐标*/
        boolean ret = mGestureDetector.onTouchEvent(event);
        event.setLocation(event.getX(), event.getY() - getTop());/*把touch事件的坐标转换转换回来*/

        if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
            if(mFangxiangOffset == 0){
                scrollToEnd(false);
            }
        }

        return ret;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        View view = getChildAt(0);
        return view != null && mContentMaxHeight > mInitMaxHeight;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        scrollToEnd(mFangxiangOffset == 0 ? getLayoutParams().height < mContentMaxHeight : mFangxiangOffset < 0);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        scrollLayout((int) distanceY);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        scrollToEnd(velocityY < 0);
        return false;
    }

    boolean scrollLayout(int dy){
        ViewGroup.LayoutParams lp = getLayoutParams();
        int height = lp.height + dy;
        lp.height = Math.min(mContentMaxHeight, Math.max(height, mInitMaxHeight));
        requestLayout();
        return lp.height != height;
    }

    void scrollToEnd(boolean isUp){
        startTime = System.currentTimeMillis();
        mFangxiangOffset = isUp ? 1 : -1;
        mAutoMoveHandler.sendEmptyMessage(1);
    }
}
