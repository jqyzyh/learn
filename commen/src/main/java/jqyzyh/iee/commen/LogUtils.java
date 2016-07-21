package jqyzyh.iee.commen;

import android.os.Build;
import android.util.Log;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by yuhang on 2016/6/17.
 * log封装
 */
public class LogUtils {
    static int log_level;

    public static int i(String tag, String msg){
        if(log_level <= Log.INFO){
            return Log.i(tag, msg);
        }
        return 0;
    }

    public static int w(String tag, String msg){
        if(log_level <= Log.WARN){
            return Log.w(tag, msg);
        }
        return 0;
    }

    public static int e(String tag, String msg){
        if(log_level <= Log.ERROR){
            return Log.e(tag, msg);
        }
        return 0;
    }

    public static int d(String tag, String msg){
        if(log_level <= Log.DEBUG){
            if(msg.length() > 2000){
                int len = 0;
                while(len < msg.length()){
                    int end = Math.min(msg.length(), len + 2000);
                    Log.d(tag, msg.substring(len, end));
                    len = end;
                }
            }else{
                Log.d(tag, msg);
            }
        }
        return 0;
    }

    public static int d(String tag, String msg, Map<String, String> data){
        if(log_level <= Log.DEBUG){
            if(data == null){
                return d(tag, msg + " data null");
            }
            d(tag, msg + "======>");
            for(Iterator<String> it = data.keySet().iterator(); it.hasNext();){
                String key = it.next();
                String value = data.get(key);
                d(tag, key + "=" + value);
            }
        }
        return 0;
    }

    public static int v(String tag, String msg){
        if(log_level <= Log.VERBOSE){
            return Log.v(tag, msg);
        }
        return 0;
    }

    static void e(String tag, Throwable throwable){
        if(log_level > Log.ERROR){
            return;
        }

        if(throwable != null){
            e(tag, throwable.toString());

            StackTraceElement[] stack = throwable.getStackTrace();
            if (stack != null) {
                for (int i = 0; i < stack.length; i++) {
                    e(tag, "\tat " + stack[i].toString());
                }
            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                Throwable[] throwables = throwable.getSuppressed();
                if (throwables != null) {
                    for (Throwable t : throwables) {
                        e(tag, "\tSuppressed: ");
                        e(tag, t);
                    }
                }
            }


            Throwable cause = throwable.getCause();
            if (cause != null) {
                e(tag, "\tSuppressed: ");
                e(tag, cause);
            }
        }
    }
}
