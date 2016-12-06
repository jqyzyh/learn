package jqyzyh.iee.commen;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.UUID;

/**
 * @author jqyzyh
 *
 * 一些设备信息相关工具类
 */
public class DeviceUtils {
    static final String SP_NAME_DEVICE = "device_cache";
    static final String SP_KEY_DEVICE_ID = "device_id";



    static String deviceId = null;

    /**
     * 获取一个deviceId<br/>
     * 生成规则：<br/>
     * <li>获取本地缓存的deviceId，如果存在直接返回, 如果没有继续执行</li>
     * <li>获取AndroidId当做deviceId,如果获取不到继续执行</li>
     * <li>获取设备编号,如果获取不到继续执行</li>
     * <li>随机生成一个UUID当做deviceId,如果获取不到继续执行</li>
     * device获取后保存到本地，保证以后每次获取都可以获得同样的值
     * @param context {@link Context}
     * @return deviceId
     */
    public static String getDeviceId(Context context) {
        if(TextUtils.isEmpty(deviceId)){
            /*获取sp中缓存的deviceid*/
            if(TextUtils.isEmpty(deviceId = getCacheDeviceId(context))) {
                /*获取androidId*/
                if(TextUtils.isEmpty(deviceId = getAndroidId(context))){
                    /*获取设备号*/
                    if(TextUtils.isEmpty(deviceId = getTelDeviceId(context))){
                        /*生成一个uuid当做设备id*/
                        deviceId = UUID.randomUUID().toString();
                    }
                }
                SPUtils.putString(context, SP_NAME_DEVICE, SP_KEY_DEVICE_ID, deviceId);
            }
        }

        return deviceId;
    }

    /**
     * 获取缓存的设备id
     * @param context
     * @return
     */
    static String getCacheDeviceId(Context context){
        return SPUtils.getString(context, SP_NAME_DEVICE, SP_KEY_DEVICE_ID, null);
    }

    /**
     * 获取androidid
     * 通过{@linkplain android.provider.Settings.Secure#getString(ContentResolver, String) getString}({@linkplain Context#getContentResolver() ContentResolver}, {@link android.provider.Settings.Secure#ANDROID_ID}) 获得AndroidId
     * @param context {@link Context}
     * @return
     */
    static String getAndroidId(Context context){

        if(Build.VERSION.SDK_INT != Build.VERSION_CODES.FROYO){
            String ret = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if(!TextUtils.equals("9774d56d682e549c", ret)){
                return ret;
            }
        }
        return null;
    }

    /**
     * 获取设备id
     * 需要校验权限，不然6.0+系统可能会crash！！！
     * @see TelephonyManager#getDeviceId()
     * @param context {@link Context}
     * @return
     */
    static String getTelDeviceId(Context context){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            return null;
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /**
     * 获取是否有网络 只判断wfii跟移动网络</br>
     * 需要权限{@link android.Manifest.permission#ACCESS_NETWORK_STATE}
     * @param context
     * @return
     */
    public static boolean isNetworkConnection(Context context){
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if(networkInfo == null){
                return false;
            }

            return networkInfo.isAvailable() && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
}
