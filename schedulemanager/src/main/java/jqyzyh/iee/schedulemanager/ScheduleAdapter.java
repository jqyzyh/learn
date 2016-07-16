package jqyzyh.iee.schedulemanager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by yuhang on 2016/7/15.
 */
public class ScheduleAdapter extends PagerAdapter {

    static final String LOG_TAG = "ScheduleAdapter";

    /**
     * view数量7（一周天数）+ 2（这周左右个一天）一共9项
     */
    public static final int COUNT = 7 + 2;

    private Context _context;

    private Calendar _today;

    private Calendar _week;

    private List<ScheduleDetailView> _viewList = new ArrayList<>();

    public ScheduleAdapter(Context context, Calendar today){
        _context = context;
        _today = today;
    }

    public void setWeek(Calendar calendar){
        _week = Calendar.getInstance();
        _week.setTime(calendar.getTime());
        _week.set(Calendar.DAY_OF_WEEK, 1);
    }

    public void offsetWeek(int offset){
        _week.add(Calendar.WEEK_OF_YEAR, offset);
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.getTag() == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ScheduleDetailView view = null;
        Calendar calendar = getCalendar(position);
        if(_viewList.isEmpty()){
            view = new ScheduleDetailView(_context);
        }else{
            for(ScheduleDetailView sdv : _viewList){
                if(CalendarUtils.isSameDay(sdv.getDate(), calendar)){
                    view = sdv;
                    break;
                }
            }
            if(view == null){
                view = _viewList.get(0);
            }
        }
        container.addView(view);
        view.setDate(calendar);
        view.setTag(calendar);
        _viewList.remove(view);
        return view.getDate();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = container.findViewWithTag(object);
        container.removeView(view);
        if(view instanceof ScheduleDetailView){
            _viewList.add((ScheduleDetailView) view);
        }
    }

    public Calendar getCalendar(int position){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(_week.getTime());
        calendar.add(Calendar.DAY_OF_WEEK, position - 1);
        return calendar;
    }

    /**
     * 返回日期对应的position 如果不在范围内返回-1
     * @param calendar
     * @return
     */
    public int getCaladerPosition(Calendar calendar){
        //构造一个跟本周相同的日期
        Calendar temp = Calendar.getInstance();
        temp.setTime(_week.getTime());
        temp.add(Calendar.DAY_OF_YEAR, -1);

        if(calendar.get(Calendar.YEAR) < temp.get(Calendar.YEAR)){
            /*如果目标年份小于当前年份说明不在范围内返回-1*/
            return -1;
        }else if(calendar.get(Calendar.YEAR) == temp.get(Calendar.YEAR)){
            if(calendar.get(Calendar.DAY_OF_YEAR) < temp.get(Calendar.DAY_OF_YEAR)){
            /*如果目标年份相同天数小于当前年份说明不在范围内返回-1*/
                return -1;
            }
        }

        int ret = 0;
        //回到postion0
        while (!CalendarUtils.isSameDay(temp, calendar)){
            /*从postion0开始自增直到日期相等返回结果 如果超出范围返回-1*/
            temp.add(Calendar.DAY_OF_YEAR, 1);
            ret ++;
            if(ret >= getCount()){
                ret = -1;
                break;
            }
        }
        return ret;
    }
}
