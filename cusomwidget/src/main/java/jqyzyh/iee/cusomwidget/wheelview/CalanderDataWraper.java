package jqyzyh.iee.cusomwidget.wheelview;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by jqyzyh on 2016/8/17.
 */
public class CalanderDataWraper implements DataWraper{

    private Calendar mCalender;

    private SimpleDateFormat mFormat;

    public CalanderDataWraper(){
        mCalender = Calendar.getInstance();
        mFormat = new SimpleDateFormat("yyyy年MM月dd日");
    }

    @Override
    public boolean haveNext() {
        return true;
    }

    @Override
    public boolean haveLast() {
        return true;
    }

    @Override
    public String getData(int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mCalender.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_YEAR, offset);
        return mFormat.format(calendar.getTime());
    }

    @Override
    public boolean moveNext() {
        mCalender.add(Calendar.DAY_OF_YEAR, 1);
        return true;
    }

    @Override
    public boolean moveLast() {
        mCalender.add(Calendar.DAY_OF_YEAR, -1);
        return true;
    }
}
