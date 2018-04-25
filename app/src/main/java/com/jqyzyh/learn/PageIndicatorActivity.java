package com.jqyzyh.learn;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jqyzyh.iee.cusomwidget.indicator.ColorIndicator;

/**
 * @author yuhang
 */

public class PageIndicatorActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_indicator);

        ViewPager vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(new MyAdapter());

        ColorIndicator indicator = (ColorIndicator) findViewById(R.id.indicator);
        indicator.attachViewPager(vp);
    }

    class MyAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView view = new TextView(PageIndicatorActivity.this);
            view.setText(String.valueOf(position));
            view.setGravity(Gravity.CENTER);
            view.setTextSize(30);
            view.setTextColor(0xff333333);
            view.setTypeface(Typeface.DEFAULT_BOLD);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "标题" + position;
        }
    }
}
