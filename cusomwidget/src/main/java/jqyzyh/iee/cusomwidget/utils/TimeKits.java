package jqyzyh.iee.cusomwidget.utils;

/**
 * @author yuhang
 */

public class TimeKits {
    public static final int LEVEL_SECOND = 1;
    public static final int LEVEL_MINUTES = 2;
    public static final int LEVEL_HOUR = 3;

    public static final long SECOND = 1000L;
    public static final long MINUTES = SECOND * 60;
    public static final long HOUR = MINUTES * 60;
    public static final long DAY = HOUR * 24;

    static final String DURATION_FORMAT_1 = "%02d";
    static final String DURATION_FORMAT_2 = "%02d:%02d";
    static final String DURATION_FORMAT_3 = "%02d:%02d:%02d";

    /**
     * 格式化时长
     *
     * @param duration 时长毫秒数
     * @param minLevel 最少显示级别 3-00:00:00 2-00:00 1-00
     */
    public static String formatDurationMs(long duration, int minLevel) {
        return formatDuration(duration / SECOND, minLevel);
    }

    public static String formatDuration(long duration, int minLevel) {
        switch (minLevel) {
            case LEVEL_SECOND:
                if (duration < 60){
                    return formatDurationSecond(duration);
                }
            case LEVEL_MINUTES:
                if (duration < 3600){
                    return formatDurationMinute(duration);
                }
            default:
                return formatDurationHour(duration);
        }
    }

    static String formatDurationSecond(long duration) {
        if (duration <= 0) {
            return "00";
        }
        int second = (int) (duration % 60);
        return String.format(DURATION_FORMAT_1, second);
    }

    static String formatDurationMinute(long duration) {
        if (duration <= 0) {
            return "00";
        }
        int minute = (int) ((duration / 60) % 60);
        int second = (int) (duration % 60);
        return String.format(DURATION_FORMAT_2, minute, second);
    }

    static String formatDurationHour(long duration) {
        if (duration <= 0) {
            return "00";
        }
        int second = (int) (duration % 60);
        duration /= 60;
        int minute = (int) (duration % 60);
        duration /= 60;
        int hour = (int) (duration % 60);
        return String.format(DURATION_FORMAT_3, hour, minute, second);
    }

    static String checkZero(int num) {
        if (num > 9) {
            return String.valueOf(num);
        } else {
            return "0" + num;
        }
    }
}
