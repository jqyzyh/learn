package jqyzyh.iee.schedulemanager;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by yuhang on 2016/7/15.
 */
public class ScheduleDetailView extends RelativeLayout {

    private Calendar _calender;

    public ScheduleDetailView(Context context) {
        super(context);
        init(context, null);
    }

    public ScheduleDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ScheduleDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScheduleDetailView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.layout_jqyzyh_schedule_detail, this);
    }

    public void setDate(Calendar calendar){
        _calender = calendar;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        TextView tv = (TextView) findViewById(R.id.tv);
        tv.setText(simpleDateFormat.format(calendar.getTime()));
    }

    public Calendar getDate(){
        return _calender;
    }

}
