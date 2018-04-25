package com.jqyzyh.learn;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author yuhang
 */

public class NestedParentView extends LinearLayout implements NestedScrollingParent {

    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    public NestedParentView(Context context) {
        super(context);
    }

    public NestedParentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedParentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NestedParentView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        Log.e("NestedParentView", "onStartNestedScroll==>" + nestedScrollAxes);
        return true;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        Log.e("NestedParentView", "onNestedScrollAccepted==>" + nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(View target) {
        Log.e("NestedParentView", "onStopNestedScroll==>");
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

        Log.e("NestedParentView", "onNestedScroll==>" + dxConsumed + "," + dyConsumed +"," + dxUnconsumed +"," +dyUnconsumed);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < consumed.length; i++) {
            if(i > 0){
                sb.append(",");
            }
            sb.append(consumed[i]);
        }
        sb.append("]");
        Log.e("NestedParentView", "onNestedPreScroll==>" + dx +"," + dy + "," + sb.toString());
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        Log.e("NestedParentView", "onNestedFling==>" + velocityX +"," + velocityY + "," + consumed);
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        Log.e("NestedParentView", "onNestedPreFling==>" + velocityX +"," + velocityY);
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        Log.e("NestedParentView", "getNestedScrollAxes==>");
        return SCROLL_AXIS_VERTICAL;
    }
}
