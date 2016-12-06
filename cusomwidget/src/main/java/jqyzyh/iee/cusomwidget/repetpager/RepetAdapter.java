package jqyzyh.iee.cusomwidget.repetpager;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jqyzyh on 2016/12/6.
 */

public abstract class RepetAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener{

    protected ViewPager mVp;

    public void attachViewPager(ViewPager vp){
        if(mVp != null){
            mVp.setAdapter(null);
            mVp.removeOnPageChangeListener(this);
        }
        mVp = vp;
        if(mVp != null){
            mVp.setAdapter(this);
            mVp.setCurrentItem(1, false);
            mVp.addOnPageChangeListener(this);
        }
    }

    @Override
    public final int getCount() {
        return getRealCount() == 0 ? 0 : getRealCount() + 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public final Object instantiateItem(ViewGroup container, int position) {
        return instantiateRealItem(container, getRealPosition(position));
    }

    @Override
    public final void destroyItem(ViewGroup container, int position, Object object) {
        destroyRealItem(container, getRealPosition(position), object);
    }

    protected int getRealPosition(int position){
        if(position == 0){
            position = getRealCount();
        }if(position == getRealCount() + 1){
            position = 0;
        }else{
            position -= 1;
        }
        return position;
    }

    public abstract int getRealCount();

    protected abstract Object instantiateRealItem(ViewGroup container, int position);

    protected abstract void destroyRealItem(ViewGroup container, int position, Object object);

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(mVp == null){
            return;
        }
        if(ViewPager.SCROLL_STATE_IDLE == state){
            int curItem = mVp.getCurrentItem();
            if(curItem == 0){//第一项自动跳到数据最后一项
                mVp.setCurrentItem(getRealCount(), false);
            }else if(curItem == getRealCount() + 1){//最后一项自动跳到数据第一项
                mVp.setCurrentItem(1, false);
            }
        }

    }
}
