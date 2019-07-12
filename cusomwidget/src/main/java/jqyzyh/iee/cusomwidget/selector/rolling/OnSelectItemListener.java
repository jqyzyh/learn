package jqyzyh.iee.cusomwidget.selector.rolling;

/**
 * @author jqyzyh on 2019/7/12
 */
public interface OnSelectItemListener<Item> {
    void onSelectItem(RollingSelector selector, Item item);
    void onSelectChangeState(RollingSelector selector, int state);
}
