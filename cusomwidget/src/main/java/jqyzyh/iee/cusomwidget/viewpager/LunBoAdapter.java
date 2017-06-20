package jqyzyh.iee.cusomwidget.viewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * 支持轮播的Adapter
 * @author jqyzyh
 */

public abstract class LunBoAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener{
    protected Context context;
    protected ViewPager viewPager;
    protected int currentPosition;

    public LunBoAdapter(Context context){
        this.context = context;
    }

    public void attachPagerView(ViewPager viewPager){
        if(this.viewPager != null){
            this.viewPager.removeOnPageChangeListener(this);
        }
        this.viewPager = viewPager;
        if(viewPager == null){
            return;
        }
        viewPager.addOnPageChangeListener(this);
        this.viewPager.setAdapter(this);
        if(getCount() > 0){
            this.viewPager.setCurrentItem(1, false);
        }
    }

    protected abstract int getRealCount();

    public int getRealPosition(int postion){
        if(postion == 0){//第一项是最后一项
            return getRealCount() - 1;
        }else if(postion == getCount() - 1){//最后一项是第一项
            return 0;
        }
        return postion - 1;//第二项才是数据的开始
    }

    protected abstract Object getRealItem(int postion);

    public Object getItem(int postion){
        return getRealItem(getRealPosition(postion));
    }

    @Override
    public int getCount() {
        int count = getRealCount();
        return count == 0 ? 0 : (count + 2);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(ViewPager.SCROLL_STATE_IDLE == state){//静止了
            if(this.viewPager.getCurrentItem() == 0){//停在第一项显示的是数据最后一项， 需要移动到实际的最后一项
                this.viewPager.setCurrentItem(getRealCount(), false);
            }else if(this.viewPager.getCurrentItem() == getCount() - 1){//停在最后一项显示数据第一项， 需要移动到实际的第一项
                this.viewPager.setCurrentItem(1, false);
            }
        }
    }

    public int getCurrentPosition(){
        return this.viewPager == null ? 0 : this.viewPager.getCurrentItem();
    }

    public ViewPager getViewPager(){
        return viewPager;
    }
}
