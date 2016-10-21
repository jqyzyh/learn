package com.jqyzyh.rvex;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jqyzyh on 2016/10/20.
 */

public class HeaderRecyclerView extends ViewGroup implements NestedScrollingParent{
    private RecyclerView mRecyclerView;
    private NestedScrollingParentHelper mParentHelper;

    public HeaderRecyclerView(Context context) {
        super(context);
        if(mRecyclerView == null){
            mRecyclerView = new RecyclerView(context);
            addView(mRecyclerView);
        }
        initView();
    }

    public HeaderRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(mRecyclerView == null){
            mRecyclerView = new RecyclerView(context, attrs);
            addView(mRecyclerView);
        }
        initView();
    }

    public HeaderRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(mRecyclerView == null){
            mRecyclerView = new RecyclerView(context, attrs, defStyleAttr);
            addView(mRecyclerView);
        }
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HeaderRecyclerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if(mRecyclerView == null){
            mRecyclerView = new RecyclerView(context, attrs, defStyleAttr);
            addView(mRecyclerView);
        }
        initView();
    }

    private void initView(){
        if(mParentHelper != null){
            return;
        }
        mParentHelper = new NestedScrollingParentHelper(this);
    }

    public RecyclerView getRecyclerView(){
        return mRecyclerView;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mRecyclerView.layout(l, t, r, b);
    }

    //TODO ============================== NestedScrollingParent ===========================================
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        Log.d("HeaderRecyclerView", "onStartNestedScroll==>"+nestedScrollAxes);
        mParentHelper.onStopNestedScroll(target);
        return true;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        Log.d("HeaderRecyclerView", "onNestedScrollAccepted==>"+nestedScrollAxes);
        mParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(View target) {
        Log.d("HeaderRecyclerView", "onStopNestedScroll==>");
        mParentHelper.onStopNestedScroll(target);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.d("HeaderRecyclerView", "onNestedScroll==>"+ dxConsumed + "," + dyConsumed + "," + dxUnconsumed + "," + dyUnconsumed);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        String c = "[";
        for(int i : consumed){
            c += "," + i;
        }
        c+="]";
        Log.d("HeaderRecyclerView", "onNestedPreScroll==>"+ dx + "," + dy + "," + c);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        Log.d("HeaderRecyclerView", "onNestedFling==>"+ velocityX + "," + velocityY + "," + consumed);
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        Log.d("HeaderRecyclerView", "onNestedPreFling==>"+ velocityX + "," + velocityY);
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        int ret = mParentHelper.getNestedScrollAxes();
        Log.d("HeaderRecyclerView", "getNestedScrollAxes==>" + ret);
        return ret;
    }
}
