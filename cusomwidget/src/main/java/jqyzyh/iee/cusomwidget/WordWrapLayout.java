package jqyzyh.iee.cusomwidget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import static android.view.Gravity.HORIZONTAL_GRAVITY_MASK;

/**
 * 自动换行控件
 *
 * @author jqyzyh
 */

public class WordWrapLayout extends ViewGroup {
    private int mHorizontalSpacing;
    private int mVerticalSpacing;
    private int mGravity = Gravity.LEFT;

    private boolean inited = false;

    private List<ChildLayout> childLayouts = new ArrayList<>();

    private BaseAdapter mAdapter;

    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            updateLayout();
        }
    };

    public WordWrapLayout(Context context) {
        this(context, null);

    }

    public WordWrapLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WordWrapLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WordWrapLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (inited) {
            return;
        }
        inited = true;
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(
                    attrs, R.styleable.WordWrapLayout);
            mHorizontalSpacing = a.getDimensionPixelSize(R.styleable.WordWrapLayout_horizontalSpacing, 0);
            mVerticalSpacing = a.getDimensionPixelSize(R.styleable.WordWrapLayout_verticalSpacing, 0);
            mGravity = a.getInt(R.styleable.WordWrapLayout_gravity, Gravity.LEFT);
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();

        int childCount = getChildCount();

        childLayouts.clear();

        if (childCount == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int curHeight = 0;
        int realWidth = 0;

        ChildLayout layout = new ChildLayout();
        layout.startIndex = 0;
        layout.pointY = getPaddingTop();
        childLayouts.add(layout);
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);//计算子控件大小
            if (layout.layoutWidth > 0) {//如果当前行不为空
                if (layout.layoutWidth + child.getMeasuredWidth() + mHorizontalSpacing > width) {//判断是否需要换行
                    realWidth = Math.max(layout.layoutWidth, realWidth);
                    int pointY = layout.pointY + mVerticalSpacing + curHeight;
                    layout = new ChildLayout();
                    childLayouts.add(layout);
                    layout.pointY = pointY;
                    layout.startIndex = i;
                }
            }
            if (layout.layoutWidth > 0) {
                layout.layoutWidth += mHorizontalSpacing;
            }
            layout.layoutWidth += Math.max(0, child.getMeasuredWidth());
            curHeight = Math.max(curHeight, child.getMeasuredHeight());
        }

        int measuredWidth;
        int measuredHeight;

        if (MeasureSpec.AT_MOST == heightMode) {
            measuredWidth = Math.min(realWidth, width) + getPaddingLeft() + getPaddingRight();
        } else {
            measuredWidth = width + getPaddingLeft() + getPaddingRight();
        }

        if (MeasureSpec.EXACTLY == heightMode) {
            measuredHeight = height + getPaddingTop() + getPaddingBottom();
        } else {
            measuredHeight = Math.max(childLayouts.get(childLayouts.size() - 1).pointY + curHeight, height) + getPaddingTop() + getPaddingBottom();
        }

        setMeasuredDimension(measuredWidth, measuredHeight);

    }

    public void setAdapter(BaseAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(dataSetObserver);
        }
        this.mAdapter = adapter;
        if (adapter != null) {
            adapter.registerDataSetObserver(dataSetObserver);
        }
        updateLayout();
    }

    void updateLayout() {
        if (mAdapter == null) {
            removeAllViews();
            requestLayout();
            invalidate();
            return;
        }
        int len = getChildCount();
        int count = mAdapter.getCount();
        List<View> views = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            views.add(getChildAt(i));
        }

        for (int i = 0; i < len || i < count; i++) {
            View v = i < len ? views.get(i) : null;
            if (i < count) {
                View cv = mAdapter.getView(i, v, this);
                if (v != cv) {
                    removeView(v);
                    super.addView(cv, i, cv.getLayoutParams() == null ? new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) : cv.getLayoutParams());
                }
            } else {
                removeView(v);
            }
        }
        requestLayout();
        invalidate();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        throw new RuntimeException("不能addView");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        if (childCount == 0) {
            return;
        }
        int width = r - l;
        int childSpace = width - getPaddingLeft() - getPaddingRight();
        int paddingLeft = getPaddingLeft();
        int right = width - getPaddingRight();
        int curX;
        for (ChildLayout cl : childLayouts) {
            switch (mGravity & HORIZONTAL_GRAVITY_MASK) {
                case Gravity.CENTER_HORIZONTAL://水平居中
                    curX = paddingLeft + (childSpace - cl.layoutWidth) / 2;
                    break;
                case Gravity.RIGHT://水平居右
                    curX = right - cl.layoutWidth;
                    break;
                default:
                    curX = paddingLeft;
                    break;
            }
            for (int i = cl.startIndex; i < childCount; i++) {
                View child = getChildAt(i);
                int curRight = curX + child.getMeasuredWidth();
                child.layout(curX, cl.pointY, curRight, cl.pointY + child.getMeasuredHeight());
                curX = curRight + mHorizontalSpacing;
            }
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public int getHorizontalSpacing() {
        return mHorizontalSpacing;
    }

    public void setHorizontalSpacing(int horizontalSpacing) {
        this.mHorizontalSpacing = horizontalSpacing;
    }

    public int getVerticalSpacing() {
        return mVerticalSpacing;
    }

    public void setVerticalSpacing(int verticalSpacing) {
        this.mVerticalSpacing = verticalSpacing;
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }
    }

    private class ChildLayout {
        int startIndex;
        int layoutWidth;
        int pointY;
    }
}
