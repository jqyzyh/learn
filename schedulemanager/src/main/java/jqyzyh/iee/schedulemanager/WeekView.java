package jqyzyh.iee.schedulemanager;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by yuhang on 2016/7/15.
 */
public class WeekView extends LinearLayout implements View.OnClickListener{

    public interface DaySelecter{
        void onSelect(WeekView weekView, Calendar selectedCalender);
    }
    static final int[] IDS = {R.id.tv_day_1, R.id.tv_day_2, R.id.tv_day_3, R.id.tv_day_4, R.id.tv_day_5, R.id.tv_day_6, R.id.tv_day_7};
    private TextView[] _tvDays = new TextView[7];

    private int _curIndex = -1;

    private Calendar _weekCalendar;

    private DaySelecter _daySelecter;

    public WeekView(Context context) {
        super(context);
        init(context, null);
    }

    public WeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public WeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WeekView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.layout_jqyzyh_schedule_week, this);
        setOrientation(HORIZONTAL);
        for(int i = 0; i < 7; i ++){
            _tvDays[i] = (TextView) findViewById(IDS[i]);
            _tvDays[i].setOnClickListener(this);
        }
    }

    /**
     * 设置当前周数
     * @param week 当前周
     * @param today 今天的日期
     */
    public void setWeek(Calendar week, Calendar today){
        _weekCalendar = Calendar.getInstance();
        _weekCalendar.setTime(week.getTime());
        _weekCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
        for(int i = 0; i < 7; i ++){
            _weekCalendar.set(Calendar.DAY_OF_WEEK, i + 1);
            if(today.get(Calendar.YEAR) == _weekCalendar.get(Calendar.YEAR) && today.get(Calendar.DAY_OF_YEAR) == _weekCalendar.get(Calendar.DAY_OF_YEAR)){
                /*今天的日期配色不同*/
                _tvDays[i].setTextColor(ContextCompat.getColorStateList(getContext(), R.drawable.jqyzyh_schedule_colors_today));
                _tvDays[i].setBackgroundResource(R.drawable.jqyzyh_schedule_selector_day_today);
            }
            _tvDays[i].setText(String.valueOf(_weekCalendar.get(Calendar.DAY_OF_MONTH)));
        }
        _weekCalendar.set(Calendar.DAY_OF_WEEK, 1);
    }

    public void setDaySelecter(DaySelecter selecter){
        _daySelecter = selecter;
    }

    @Override
    public void onClick(View v) {

        for(int i = 0; i < 7; i ++){
            if(v == _tvDays[i]){
                selectedDay(i, true);
            }
        }

    }

    /**
     * 选择这个星期中的某一天
     * @param index 日期的index 0-6 -> 周日-周一
     * @param callback 是否调用回调函数
     */
    public void selectedDay(int index, boolean callback){
        if(_curIndex == index || (index < 0 || _curIndex >= 7)){
            return;
        }

        if(_curIndex >= 0 && _curIndex < 7){
            /*重置上一次选择的日期*/
            _tvDays[_curIndex].setSelected(false);
        }

        _curIndex = index;
        _tvDays[_curIndex].setSelected(true);

        if(callback && _daySelecter != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(_weekCalendar.getTime());
            calendar.set(Calendar.DAY_OF_WEEK, index + 1);
            _daySelecter.onSelect(this, calendar);
        }
    }

    /**
     * 选择一个日期，如果这个日期不是这周的日期就清除这周的选中状态
     * @param calendar 选中的日期
     */
    public void selectDay(Calendar calendar){
        if(calendar.get(Calendar.YEAR) == _weekCalendar.get(Calendar.YEAR)
                && calendar.get(Calendar.WEEK_OF_YEAR) == _weekCalendar.get(Calendar.WEEK_OF_YEAR)){
            /*同年同星期操作当前view*/
            selectedDay(calendar.get(Calendar.DAY_OF_WEEK) - 1, false);
        }else{
            /*不是当前周清除选中日期*/
            clearSelect();
        }
    }

    /**
     * 清除选择状态
     */
    public void clearSelect(){
        for(View view : _tvDays){
            view.setSelected(false);
        }
        _curIndex = -1;
    }
}
