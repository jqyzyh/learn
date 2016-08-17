package jqyzyh.iee.cusomwidget.wheelview;

/**
 * Created by jqyzyh on 2016/8/17.
 */
public interface DataWraper {

    boolean haveNext();
    boolean haveLast();

    Object getData(int offset);

    boolean moveNext();
    boolean moveLast();
}
