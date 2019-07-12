package jqyzyh.iee.cusomwidget.selector;

/**
 * @author jqyzyh on 2019/7/12
 */
public interface CanSelectItem<Item> {
    /**
     * 当前代表的元素
     * @return
     */
    Item getItem();

    /**
     * 当前元素的文本
     * @return 元素显示的文本
     */
    String getText();

    /**
     * 上一个
     * @return 如果有上一个返回上一个，如果没有返回null
     */
    CanSelectItem<Item> last();

    /**
     * 下一个
     * @return 如果有下一个返回下一个，如果没有返回null
     */
    CanSelectItem<Item> next();
}
