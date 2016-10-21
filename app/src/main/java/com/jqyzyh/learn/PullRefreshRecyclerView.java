package com.jqyzyh.learn;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yuhang on 2016/10/17.
 */

public class PullRefreshRecyclerView extends ViewGroup implements NestedScrollingParent {

    private NestedScrollingParentHelper mNestedScrollingParentHelper;

    private int offset;

    private RecyclerView mContentView;

    public PullRefreshRecyclerView(Context context) {
        super(context);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    }

    public PullRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    }

    public PullRefreshRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PullRefreshRecyclerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        mContentView.layout(left, offset, right, offset + bottom);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContentView = (RecyclerView) getChildAt(0);
    }

    private void layoutChildren() {
//        int offsetY = mHeaderController.getCurPosition();
//        int paddingLeft = getPaddingLeft();
//        int paddingTop = getPaddingTop();
//
//        if (mHeaderView != null) {
//            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
//            final int left = paddingLeft + lp.leftMargin;
//            final int top = paddingTop + lp.topMargin;
//            final int right = left + mHeaderView.getMeasuredWidth();
//            final int bottom = top + mHeaderView.getMeasuredHeight();
//            mHeaderView.layout(left, top, right, bottom);
//        }
//
//        if (mContent != null) {
//            MarginLayoutParams lp = (MarginLayoutParams) mContent.getLayoutParams();
//            final int left = paddingLeft + lp.leftMargin;
//            final int top = paddingTop + lp.topMargin + offsetY;
//            final int right = left + mContent.getMeasuredWidth();
//            final int bottom = top + mContent.getMeasuredHeight();
//            mContent.layout(left, top, right, bottom);
//        }
//
//        if (mActionView != null) {
//            final int center = ACTION_BUTTON_CENTER;
//            int halfWidth = (mActionView.getMeasuredWidth() + 1) / 2;
//            int halfHeight = (mActionView.getMeasuredHeight() + 1) / 2;
//
//            mActionView.layout(center - halfWidth , offsetY - halfHeight,
//                    center + halfWidth, offsetY + halfHeight);
//
//            halfWidth = (mFlyView.getMeasuredWidth() + 1) / 2;
//            halfHeight = (mFlyView.getMeasuredHeight() + 1) / 2;
//            mFlyView.layout(center - halfWidth, offsetY - halfHeight,
//                    center + halfWidth, offsetY + halfHeight);
//        }

    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        Log.d("onStartNestedScroll", "onStartNestedScroll child==>"+ child.getClass().getSimpleName()) ;
        Log.d("onStartNestedScroll", "onStartNestedScroll target==>"+ target.getClass().getSimpleName()) ;
        Log.d("onStartNestedScroll", "onStartNestedScroll==>"+ nestedScrollAxes) ;
        return true;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {

    }

    @Override
    public void onStopNestedScroll(View target) {
        offset = 0;
        requestLayout();
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        offset -= dyUnconsumed;
        mContentView.offsetTopAndBottom(-dyUnconsumed * Math.max(0, (400 - Math.abs(offset))) / 400);
        Log.d("onNestedScroll", "onNestedScroll==>"+ dyConsumed + "," + dyUnconsumed) ;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {

    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return 0;
    }
}
