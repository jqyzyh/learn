package jqyzyh.iee.cusomwidget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import static android.view.Gravity.HORIZONTAL_GRAVITY_MASK;

/**
 * 自动换行控件
 *
 * @author jqyzyh
 */

public class WrapLayout extends ViewGroup {
    private int mHorizontalSpacing;
    private int mVerticalSpacing;
    private int mGravity = Gravity.LEFT;

    private boolean inited = false;

    private List<ChildLayout> childLayouts = new ArrayList<>();

    public WrapLayout(Context context) {
        super(context);
        init(context, null);

    }

    public WrapLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WrapLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WrapLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (inited) {
            return;
        }
        inited = true;
        if(attrs != null){
            final TypedArray a = context.obtainStyledAttributes(
                    attrs, R.styleable.WrapLayout);
            mHorizontalSpacing = a.getDimensionPixelSize(R.styleable.WrapLayout_horizontalSpacing, 0);
            mVerticalSpacing = a.getDimensionPixelSize(R.styleable.WrapLayout_verticalSpacing, 0);
            mGravity = a.getInt(R.styleable.WrapLayout_gravity, Gravity.LEFT);
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

        if (MeasureSpec.AT_MOST == widthMode) {
            measuredWidth = Math.min(realWidth, width) + getPaddingLeft() + getPaddingRight();
        } else {
            measuredWidth = width + getPaddingLeft() + getPaddingRight();
        }

        if (MeasureSpec.AT_MOST == heightMode) {
            measuredHeight = Math.max(childLayouts.get(childLayouts.size() - 1).pointY + curHeight, height) + getPaddingTop() + getPaddingBottom();
        } else {
            measuredHeight = height + getPaddingTop() + getPaddingBottom();
        }

        setMeasuredDimension(measuredWidth, measuredHeight);

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
                Log.i("mylog", i + "==>" + curX + "," + curRight);
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
            Log.d("mylog", "c " + attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            Log.d("mylog", width + "，" + width);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
            Log.d("mylog", "MarginLayoutParams");
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            Log.d("mylog", "ViewGroup LayoutParams");
        }
    }

    private class ChildLayout {
        int startIndex;
        int layoutWidth;
        int pointY;
    }
}
