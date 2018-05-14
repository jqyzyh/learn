package jqyzyh.iee.cusomwidget.indicator;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import jqyzyh.iee.cusomwidget.utils.ViewKits;
import jqyzyh.iee.cusomwidget.viewpager.LunBoAdapter;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;

/**
 * @author yuhang
 */

public class CircleIndicator extends LinearLayout implements ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;

    public CircleIndicator(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
    }

    public CircleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CircleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setOrientation(HORIZONTAL);
    }

    @TargetApi(JELLY_BEAN)
    public void setupWidthViewPager(ViewPager viewPager) {
        if (mViewPager != null) {
            mViewPager.removeOnPageChangeListener(this);
        }
        if (viewPager == null || viewPager.getAdapter() == null) {
            return;
        }
        mViewPager = viewPager;
        viewPager.addOnPageChangeListener(this);
        this.removeAllViews();

        int len = viewPager.getAdapter().getCount();
        if(viewPager.getAdapter() instanceof LunBoAdapter){
            len = ((LunBoAdapter) viewPager.getAdapter()).getRealCount();
        }else{
            len = viewPager.getAdapter().getCount();
        }
        for(int i = 0; i < len; i ++){
            View view = new View(getContext());
            if (Build.VERSION.SDK_INT < JELLY_BEAN){
                view.setBackground(ViewKits.createStateListDrawable(new float[]{20, 20, 20, 20, 20, 20, 20, 20}, Color.WHITE, 0xFFDE3031, 0xFFDE3031));
            }else{
                view.setBackground(ViewKits.createStateListDrawable(new float[]{20, 20, 20, 20, 20, 20, 20, 20}, Color.WHITE, 0xFFDE3031, 0xFFDE3031));
            }
            LayoutParams lp = new LayoutParams(ViewKits.dip2Px(getContext(), 4), ViewKits.dip2Px(getContext(), 4));
            if(i > 0){
                lp.leftMargin = ViewKits.dip2Px(getContext(), 4);
            }
            addView(view, lp);
            view.setTag(i);
        }
        onPageSelected(mViewPager.getCurrentItem());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int len = getChildCount();
        if(mViewPager.getAdapter() instanceof LunBoAdapter){
            position = ((LunBoAdapter) mViewPager.getAdapter()).getRealPosition(position);
        }
        for(int i = 0; i < len; i ++){
            View view = getChildAt(i);
            view.setEnabled(!view.getTag().equals(position));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
