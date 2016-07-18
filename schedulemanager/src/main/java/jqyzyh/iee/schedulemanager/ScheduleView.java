package jqyzyh.iee.schedulemanager;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 2016/7/13.
 */
public class ScheduleView extends LinearLayout implements WeekDayAdapter.WeekDelegate, ViewPager.OnPageChangeListener{
    private ViewPager _vpWeek;
    private ViewPager _vpSchedule;
    private WeekDayAdapter _weekAdapter;
    private ScheduleAdapter _scheduleAdapter;
    private TextView _tvDate;
    private SimpleDateFormat _dateFormat = new SimpleDateFormat("dd,yyyy");
    private String[] _weekDays;
    private String[] _months;

    private Calendar _today;

    public ScheduleView(Context context) {
        super(context);
        initView(context, null);
    }

    public ScheduleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ScheduleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScheduleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs){
        _weekDays = context.getResources().getStringArray(R.array.jqyzyh_schedule_week);
        _months = context.getResources().getStringArray(R.array.jqyzyh_schedule_months);
        _today = Calendar.getInstance();
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.layout_jqyzyh_schedule_root, this);
        _vpWeek = (ViewPager) findViewById(R.id.vp_jqyzyh_schedule_day);
        _tvDate = (TextView) findViewById(R.id.tv_jqyzyh_schedule_date);
        _vpSchedule = (ViewPager) findViewById(R.id.vp_jqyzyh_schedule_date);

        /*初始日期选择*/
        _weekAdapter = new WeekDayAdapter(context, _today, this);
        _vpWeek.setAdapter(_weekAdapter);
        _vpWeek.setCurrentItem(WeekDayAdapter.CURRENT_WEEK_POSTION);
        _vpWeek.addOnPageChangeListener(_weekAdapter);

        _scheduleAdapter = new ScheduleAdapter(getContext(), _today);
        _scheduleAdapter.setWeek(_today);
        _vpSchedule.setAdapter(_scheduleAdapter);
        _vpSchedule.addOnPageChangeListener(this);

        post(new Runnable() {
            @Override
            public void run() {
                _weekAdapter.selectDay(Calendar.getInstance(), true);
            }
        });
    }

    @Override
    public void onSelectedDay(Calendar calendar, boolean isClick) {
        _tvDate.setText(_weekDays[calendar.get(Calendar.DAY_OF_WEEK) - 1] + " " + _months[calendar.get(Calendar.MONTH)] + " " + _dateFormat.format(calendar.getTime()));
        int weekPosition = _weekAdapter.getPosition(calendar);
        if(_vpWeek.getCurrentItem() != weekPosition){
            _vpWeek.setCurrentItem(weekPosition);
        }
        int position = _scheduleAdapter.getCaladerPosition(calendar);
        if(position == -1){
            _scheduleAdapter.setWeek(calendar);
            position = _scheduleAdapter.getCaladerPosition(calendar);
        }
        _vpSchedule.setCurrentItem(position, isClick);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        _vpSchedule.requestDisallowInterceptTouchEvent(ViewPager.SCROLL_STATE_IDLE != state);

        if(state == ViewPager.SCROLL_STATE_IDLE){
            /*滚动停止如果position是开头或者结尾跳转上一页或者下一页*/
            int position = _vpSchedule.getCurrentItem();
            Calendar calendar = _scheduleAdapter.getCalendar(position);
            if(position == 0){
                _scheduleAdapter.offsetWeek(-1);
                _weekAdapter.setNotJumpLastDay(true);
            }else if(position == ScheduleAdapter.COUNT - 1){
                _scheduleAdapter.offsetWeek(1);
                _weekAdapter.setNotJumpLastDay(true);
                _weekAdapter.selectDay(calendar, false);
            }
            _weekAdapter.selectDay(calendar, false);
        }
    }
}
