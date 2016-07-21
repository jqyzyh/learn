package jqyzyh.iee.commen;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author jqyzyh
 * 首选项工具类
 */
public class SPUtils {

    static SharedPreferences getSharedPreferences(Context context, String name){
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static boolean getBoolean(Context context, String name, String key, boolean defaultValue){
        SharedPreferences sp = getSharedPreferences(context, name);
        return sp.getBoolean(key, defaultValue);
    }

    public static void putBoolean(Context context, String name, String key, boolean value){
        SharedPreferences sp = getSharedPreferences(context, name);
        sp.edit().putBoolean(key, value).commit();
    }

    public static String getString(Context context, String name, String key, String defaultValue){
        SharedPreferences sp = getSharedPreferences(context, name);
        return sp.getString(key, defaultValue);
    }

    public static void putString(Context context, String name, String key, String value){
        SharedPreferences sp = getSharedPreferences(context, name);
        if(value == null){
            sp.edit().remove(key).commit();
        }else{
            sp.edit().putString(key, value).commit();
        }
    }
}
