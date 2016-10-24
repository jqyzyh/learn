package jqyzyh.iee.cusomwidget.pullrefreshlistview;

import android.view.View;

/**
 * Created by jqyzyh on 2016/10/24.
 */

public interface ILoadingLayout {

    /**
     * 下拉了多少 0 - 1   => 0% - 100%
     * @param offsetY
     */
    void offsetY(float offsetY);

    /**
     * 改变状态
     */
    void setState(int state);

    View getLoadingLayout();
}
