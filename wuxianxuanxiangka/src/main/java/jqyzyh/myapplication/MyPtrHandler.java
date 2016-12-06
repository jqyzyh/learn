package jqyzyh.myapplication;

import android.view.View;

import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;

import jqyzyh.iee.cusomwidget.repetpager.AutoScrollNextViewPager;

/**
 * Created by jqyzyh on 2016/12/6.
 */

public abstract class MyPtrHandler extends PtrDefaultHandler implements AutoScrollNextViewPager.OnMyPagerTouchListener{

    boolean lock = false;

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
//        Log.i("", "" + content.requestFocusFromTouch())
        return  !lock && super.checkCanDoRefresh(frame, content, header);
    }

    @Override
    public void setLockParent(boolean lock) {
        this.lock = lock;
    }
}
