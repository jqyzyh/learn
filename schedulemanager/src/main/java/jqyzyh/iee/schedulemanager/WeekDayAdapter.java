package jqyzyh.iee.schedulemanager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuhang on 2016/7/15.
 */
public class WeekDayAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener{
    static final String LOG_TAG = "WeekDayAdapter";
    public interface WeekDelegate{
        void onSelectedDay(Calendar calendar, boolean isClick);
    }

    public static final int CURRENT_WEEK_POSTION = 100000000;

    private Context _context;
    private LayoutInflater _inflater;

    private Calendar _today;

    private Map<Integer, WeekView> viewMap = new HashMap<>();

    private int _currentPagePosition;

    private WeekDelegate _delegate;

    private boolean _notJumpLastDay;

    public WeekDayAdapter(Context context, Calendar today, WeekDelegate delegate){
        this._context = context;
        this._inflater = LayoutInflater.from(context);
        _today = today;
        _delegate = delegate;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        WeekView weekView = new WeekView(_context);
        container.addView(weekView);
        weekView.setWeek(getWeek(position), _today);
        viewMap.put(position, weekView);
        weekView.setDaySelecter(new WeekView.DaySelecter() {
            @Override
            public void onSelect(WeekView weekView, Calendar selectedCalender) {
                selectDay(selectedCalender, true);
            }
        });
        return weekView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        viewMap.remove(position);
        container.removeView((View) object);
    }

    public void setNotJumpLastDay(boolean notJumpLastDay){
        _notJumpLastDay = notJumpLastDay;
    }

    public void selectDay(Calendar calendar, boolean isClick){
        for(WeekView wv : viewMap.values()){
            wv.selectDay(calendar);
        }

        if(_delegate != null){
            _delegate.onSelectedDay(calendar, isClick);
        }
    }

    public int getPosition(Calendar calendar){
        Log.d(LOG_TAG, "getPosition 1==>" + _today.get(Calendar.WEEK_OF_YEAR));
        Log.d(LOG_TAG, "getPosition 2==>" + calendar.get(Calendar.WEEK_OF_YEAR));
        return CURRENT_WEEK_POSTION + CalendarUtils.getWeekOffset(_today, calendar);
    }

    /**
     * 获取position对应便宜的周
     * @param position
     * @return
     */
    public Calendar getWeek(int position){
        //创造一个新的日历
        Calendar week =  Calendar.getInstance();
        //把当前时间复制给他
        week.setTime(_today.getTime());
        week.setFirstDayOfWeek(Calendar.SUNDAY);
        //计算周数的偏移量
        int delta = position - CURRENT_WEEK_POSTION;
        //便宜周数
        week.add(Calendar.WEEK_OF_YEAR, delta);
        return week;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        _currentPagePosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(state == ViewPager.SCROLL_STATE_IDLE){
            if(_notJumpLastDay){
                _notJumpLastDay = false;
                return;
            }
            WeekView weekView = viewMap.get(_currentPagePosition);
            if(weekView != null){
                weekView.selectedDay(6, true);
            }
        }
    }
}
