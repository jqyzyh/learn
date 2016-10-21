package com.jqyzyh.learn;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 2016/10/17.
 */

public class RecyclerViewBehavior extends CoordinatorLayout.Behavior<RecyclerView> {

    public RecyclerViewBehavior(){

    }

    public RecyclerViewBehavior(Context context, AttributeSet attrs){
        super();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, RecyclerView child, View dependency) {
        Log.d("RecyclerViewBehavior", "layoutDependsOn");
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, RecyclerView child, View dependency) {
        Log.d("RecyclerViewBehavior", "onDependentViewChanged");
        return super.onDependentViewChanged(parent, child, dependency);
    }

}
