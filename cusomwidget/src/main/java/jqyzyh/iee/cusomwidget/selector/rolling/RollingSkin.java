package jqyzyh.iee.cusomwidget.selector.rolling;

import android.graphics.Canvas;

import jqyzyh.iee.cusomwidget.selector.CanSelectItem;

/**
 *
 * @author jqyzyh on 2019/7/12
 */
public interface RollingSkin {
    /**
     * 测量高度
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    int measureHeight(int widthMeasureSpec, int heightMeasureSpec);

    void onLayout(boolean changed, int left, int top, int right, int bottom);

    /**
     * 绘制
     * @param canvas
     */
    void draw(Canvas canvas, CanSelectItem item, float paperOffsetY);

    /**
     * 每一行的高度
     * @return
     */
    int getLineHeight();
}
