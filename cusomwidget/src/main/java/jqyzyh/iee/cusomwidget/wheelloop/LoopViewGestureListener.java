package jqyzyh.iee.cusomwidget.wheelloop;

import android.view.MotionEvent;

/**
 * @author jqyzyh
 * 捕获滑动手指抬起后惯性滚动
 */
final class LoopViewGestureListener extends android.view.GestureDetector.SimpleOnGestureListener {

    final LoopView loopView;

    LoopViewGestureListener(LoopView loopview) {
        loopView = loopview;
    }

    @Override
    public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        loopView.scrollBy(velocityY);
        return true;
    }
}
