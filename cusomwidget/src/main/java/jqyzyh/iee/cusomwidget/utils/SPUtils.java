package jqyzyh.iee.cusomwidget.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 首选项工具类
 * @author yuhang
 */
public class SPUtils {

    public static SharedPreferences getSharedPreferences(Context context, String name){
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

    public static int getInt(Context context, String name, String key, int defaultValue){
        SharedPreferences sp = getSharedPreferences(context, name);
        return sp.getInt(key, defaultValue);
    }

    public static void putInt(Context context, String name, String key, int value){
        SharedPreferences sp = getSharedPreferences(context, name);
        sp.edit().putInt(key, value).commit();
    }

    public static long getLong(Context context, String name, String key, long defaultValue){
        SharedPreferences sp = getSharedPreferences(context, name);
        return sp.getLong(key, defaultValue);
    }

    public static void putLong(Context context, String name, String key, long value){
        SharedPreferences sp = getSharedPreferences(context, name);
        sp.edit().putLong(key, value).commit();
    }
}
