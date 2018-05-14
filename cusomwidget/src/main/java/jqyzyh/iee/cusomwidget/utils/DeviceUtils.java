package jqyzyh.iee.cusomwidget.utils;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;

import static android.content.Context.WIFI_SERVICE;

/**
 * @author jqyzyh
 *         <p>
 *         一些设备信息相关工具类
 */
public class DeviceUtils {
    static final String SP_NAME_DEVICE = "device_cache";
    static final String SP_KEY_DEVICE_ID = "device_id";

    /**
     * 设备id缓存
     */
    static String deviceId = null;

    /**
     * 获取一个deviceId<br/>
     * 生成规则：<br/>
     * <li>获取本地缓存的deviceId，如果存在直接返回, 如果没有继续执行</li>
     * <li>获取AndroidId当做deviceId,如果获取不到继续执行</li>
     * <li>获取设备编号,如果获取不到继续执行</li>
     * <li>随机生成一个UUID当做deviceId,如果获取不到继续执行</li>
     * device获取后保存到本地，保证以后每次获取都可以获得同样的值
     *
     * @param context {@link Context}
     * @return deviceId
     */
    public static String getDeviceId(Context context) {
        if (TextUtils.isEmpty(deviceId)) {
            /*获取sp中缓存的deviceid*/
            if (TextUtils.isEmpty(deviceId = getCacheDeviceId(context))) {
                /*获取androidId*/
                if (TextUtils.isEmpty(deviceId = getAndroidId(context))) {
                    /*获取设备号*/
                    if (TextUtils.isEmpty(deviceId = getTelDeviceId(context))) {
//                        /*生成一个uuid当做设备id*/
                        deviceId = UUID.randomUUID().toString();
                    }
                }
                SPUtils.putString(context, SP_NAME_DEVICE, SP_KEY_DEVICE_ID, deviceId);
            }
        }

        return deviceId;
    }

    public static void clearDevId(Context context){
        deviceId = null;
        SPUtils.putString(context, SP_NAME_DEVICE, SP_KEY_DEVICE_ID, null);
    }

    public static void setDevId(Context context, String devId){
        deviceId = devId;
        SPUtils.putString(context, SP_NAME_DEVICE, SP_KEY_DEVICE_ID, deviceId);
    }

    /**
     * 获取缓存的设备id
     *
     * @param context
     * @return
     */
    static String getCacheDeviceId(Context context) {
        return SPUtils.getString(context, SP_NAME_DEVICE, SP_KEY_DEVICE_ID, null);
    }

    /**
     * 获取androidid
     * 通过{@linkplain Settings.Secure#getString(ContentResolver, String) getString}({@linkplain Context#getContentResolver() ContentResolver}, {@link Settings.Secure#ANDROID_ID}) 获得AndroidId
     *
     * @param context {@link Context}
     * @return
     */
    static String getAndroidId(Context context) {

        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.FROYO) {
            String ret = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (!TextUtils.equals("9774d56d682e549c", ret)) {
                return ret;
            }
        }
        return null;
    }

    /**
     * 获取设备id
     * 需要校验权限，不然6.0+系统可能会crash！！！
     *
     * @param context {@link Context}
     * @return
     * @see TelephonyManager#getDeviceId()
     */
    static String getTelDeviceId(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /**
     * 获取是否有网络 只判断wfii跟移动网络</br>
     * 需要权限{@link Manifest.permission#ACCESS_NETWORK_STATE}
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnection(Context context) {
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo == null) {
                return false;
            }

            return networkInfo.isAvailable() && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 获取是否是wifi</br>
     * 需要权限{@link Manifest.permission#ACCESS_NETWORK_STATE}
     *
     * @param context
     * @return
     */
    public static boolean isConnectionWifi(Context context) {
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo == null) {
                return false;
            }

            return networkInfo.isAvailable() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取ip
     * @param context
     * @return
     */
    public static String getIP(Context context){

        if(isNetworkConnection(context)){
            if(isConnectionWifi(context)){
                WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                // 获取32位整型IP地址
                int ipAddress = wifiInfo.getIpAddress();

                //返回整型地址转换成“*.*.*.*”地址
                return String.format("%d.%d.%d.%d",
                        (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                        (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
            }else{
                try {
                    for(Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ){
                        NetworkInterface intf = e.nextElement();
                        for (Enumeration<InetAddress> enumIPAddr = intf
                                .getInetAddresses(); enumIPAddr.hasMoreElements();) {
                            InetAddress inetAddress = enumIPAddr.nextElement();
                            // 如果不是回环地址
                            if (!inetAddress.isLoopbackAddress()) {
                                // 直接返回本地IP地址
                                return inetAddress.getHostAddress().toString();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static String getHostname() {
        try {
            Method method = Build.class.getDeclaredMethod("getString", new Class[]{String.class});
            method.setAccessible(true);
            Object ret = method.invoke(null, new Object[]{"net.hostname"});
            return (String) ret;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
