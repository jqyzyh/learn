package com.jqyzyh.learn;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jqyzyh.iee.cusomwidget.indicator.ColorIndicator;
import jqyzyh.iee.cusomwidget.utils.LogUtils;

/**
 * @author yuhang
 */

public class PageIndicatorActivity extends FragmentActivity {

    int size = 20;
    PagerAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_indicator);


        ViewPager vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(adapter = new EasyAdapter());
        add(null);

        ColorIndicator indicator = (ColorIndicator) findViewById(R.id.indicator);
        indicator.attachViewPager(vp);
    }

    public void add(View v){
        size += 2;
        adapter.notifyDataSetChanged();
    }

    public static class FragmentA extends Fragment{
        String str;
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            TextView tv = new TextView(getContext());
            tv.setText(str);
            return tv;
        }
    }

    class EasyAdapter extends FragmentPagerAdapter{

        public EasyAdapter() {
            super(getSupportFragmentManager());
        }

        @Override
        public Fragment getItem(int position) {
            FragmentA f= new FragmentA();
            f.str = String.valueOf(position);
            return f;
        }

        @Override
        public int getCount() {
            return size;
        }
    }

    class MyAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return size;
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
            LogUtils.d("mylog", "instantiateItem=>"+ position);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            LogUtils.d("mylog", "destroyItem=>" +position);
            container.removeView((View) object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "标题" + position;
        }
    }
}
