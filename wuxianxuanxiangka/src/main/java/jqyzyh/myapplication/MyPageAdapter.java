package jqyzyh.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by jqyzyh on 2016/9/6.
 */
public class MyPageAdapter extends FragmentStatePagerAdapter {
    public MyPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        PageFragment fragment = new PageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("text", "页面" + position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return 20;
    }
}
