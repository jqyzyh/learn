package jqyzyh.iee.schedulemanager;

import java.util.Calendar;

import static java.util.Calendar.YEAR;
import static java.util.Calendar.DAY_OF_YEAR;

/**
 * Created by yuhang on 2016/7/16.
 */
public class CalendarUtils {
    public static boolean isSameDay(Calendar calendar1, Calendar calendar2){
        if(calendar1 == calendar2){
            return true;
        }

        return calendar1.get(YEAR) == calendar2.get(YEAR) && calendar1.get(DAY_OF_YEAR) == calendar2.get(DAY_OF_YEAR);
    }

    public static int getWeekOffset(Calendar curWeek, Calendar calendar){
        int curYear = curWeek.get(YEAR);
        int year = calendar.get(YEAR);
        int offset = 0;
        if(curYear < year){/*如果当前年份小于被比较日期年份*/
            /*创建一个临时日期并且赋值被比较日期*/
            Calendar temp = Calendar.getInstance();
            temp.setTime(calendar.getTime());
            while (curYear < temp.get(YEAR)){/*如果当前年日期份小于临时日期年份*/
                /*偏移量增加临时日期已过了的周数*/
                offset += temp.get(Calendar.WEEK_OF_YEAR);
                /*临时日期周数设置为第一周*/
                temp.set(Calendar.WEEK_OF_YEAR, 1);
                /*临时日期周数-1变为上一年最后一周*/
                temp.add(Calendar.WEEK_OF_YEAR, -1);
            }
            offset += temp.get(Calendar.WEEK_OF_YEAR) - curWeek.get(Calendar.WEEK_OF_YEAR);
        }else if(year < curYear){/*如果被比较日期年份小于当前年份*/
            /*创建一个临时日期并且赋值当前日期*/
            Calendar temp = Calendar.getInstance();
            temp.setTime(curWeek.getTime());
            while (year < temp.get(YEAR)){/*如果被比较日期年份小于临时日期年份*/
                /*偏移量增加临时日期已过了的周数*/
                offset -= temp.get(Calendar.WEEK_OF_YEAR);
                /*临时日期周数设置为第一周*/
                temp.set(Calendar.WEEK_OF_YEAR, 1);
                /*临时日期周数-1变为上一年最后一周*/
                temp.add(Calendar.WEEK_OF_YEAR, -1);
            }
            offset += calendar.get(Calendar.WEEK_OF_YEAR) - temp.get(Calendar.WEEK_OF_YEAR);
        }else {
            offset = calendar.get(Calendar.WEEK_OF_YEAR) - curWeek.get(Calendar.WEEK_OF_YEAR);
        }

        return offset;
    }
}
