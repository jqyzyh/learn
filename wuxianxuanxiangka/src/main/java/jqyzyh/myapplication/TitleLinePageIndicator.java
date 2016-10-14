package jqyzyh.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jqyzyh on 2016/9/19.
 */
public class TitleLinePageIndicator extends View implements ViewPager.OnPageChangeListener{
    private ViewPager mViewPager;
    private TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

    private List<Rect> mTextRects = new ArrayList<>();

    public TitleLinePageIndicator(Context context) {
        super(context);
    }

    public TitleLinePageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TitleLinePageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TitleLinePageIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView(Context context, AttributeSet attrs){
        if(attrs == null){
            return;
        }
//        TypedArray array = context.obtainStyledAttributes(attrs, null);
//
//        array.recycle();
    }

    public void setViewPager(ViewPager viewPager){
        if(mViewPager != null){
            mViewPager.removeOnPageChangeListener(this);
        }
        if(viewPager != null){
            viewPager.addOnPageChangeListener(this);
        }

        measureTitle();
        invalidate();
    }

    public void setPagerAdapter(PagerAdapter pagerAdapter){
        if(mViewPager != null){
            mViewPager.setAdapter(pagerAdapter);
        }
        measureTitle();
        invalidate();
    }

    void measureTitle(){
        mTextRects.clear();
        if(mViewPager == null){
            return;
        }
        PagerAdapter pagerAdapter = mViewPager.getAdapter();
        if(pagerAdapter == null){
            return;
        }

        int count = pagerAdapter.getCount();

        for(int i = 0; i < count; i ++){
            CharSequence title = pagerAdapter.getPageTitle(i);
            if(title == null){
                Rect rect =
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
