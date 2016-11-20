package jqyzyh.iee.cusomwidget.iospupopmenu;

import android.view.View;

/**
 * Created by yuhang on 2016/8/17.
 */
public class MenuItem {
    CharSequence text;
    int color;
    View.OnClickListener onClickListener;

    public MenuItem(CharSequence text, int color, View.OnClickListener onClickListener) {
        this.text = text;
        this.color = color;
        this.onClickListener = onClickListener;
    }
}
