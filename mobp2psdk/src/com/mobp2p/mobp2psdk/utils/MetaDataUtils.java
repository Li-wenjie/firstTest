package com.mobp2p.mobp2psdk.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

public class MetaDataUtils {
	public static String getMetaData(Context context, String key) {
		try {
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			Object vObject = info.metaData.get(key);
			if (vObject != null) {
				return vObject.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 获取设备信息
	public static Map<String, Object> getInfos(Context context) {
		Map<String, Object> mapInfo = new HashMap<String, Object>();
		// 渠道号
		String appVersion = "";
		// 版本号
		String sdkVersion = "";
		// device_id
		String device_id = "";
/*		// phone_model
		String phone_model = "";
		// phone_os
		String phone_os = "";
		// phone_number
		String phone_number = "";*/
		PackageManager manager = context.getPackageManager();
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		try {
			PackageInfo pi = manager
					.getPackageInfo(context.getPackageName(), 0);
			appVersion = getMetaData(context, "SDK_CHANNEL");
			sdkVersion = String.valueOf(pi.versionName);
			device_id = info.getMacAddress() == null ? "" : info
					.getMacAddress()
					+ "|"
					+ ""
					+ "|"
					+ telephonyManager.getDeviceId();
			int i = info.getIpAddress();
/*			phone_model = Build.MANUFACTURER;
			phone_os = Build.VERSION.RELEASE;
			phone_number = telephonyManager.getLine1Number();*/
			mapInfo.put("device_id", device_id);
			//精度
			mapInfo.put("longitude", "");
			//纬度
			mapInfo.put("latitude", "");
			//坐标地址
			mapInfo.put("coordAddr", "");
			//ip
			mapInfo.put("ip", int2ip(i));
			mapInfo.put("appVersion", appVersion);
			mapInfo.put("sdkVersion", sdkVersion);
			/*mapInfo.put("phone_model", phone_model);
			mapInfo.put("phone_os", phone_os);
			mapInfo.put("phone_number", phone_number);*/
			if (mapInfo != null) {
				return mapInfo;
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return mapInfo;
	}

	public static String formatPhontNumber(String phone) {
		return phone.replaceAll("-", "").replaceAll(" ", "")
				.replaceAll("17951", "").replaceAll("\\+86", "")
				.replaceAll("12593", "").replaceAll("\\+", "")
				.replaceAll("\\(", "").replaceAll("\\)", "")
				.replaceAll("\\<", "").replaceAll("\\>", "")
				.replaceAll("\\*", "").replaceAll("\\#", "");
	}

	public static String StringFilter(String str) {
		// 清除掉所有特殊字符
		String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？_-]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
    public static boolean isEmptyString(final String str) {
        return
                TextUtils.isEmpty(str) ||
                        str.equalsIgnoreCase("null");

    }
    /** 
     * 将ip的整数形式转换成ip形式 
     *  
     * @param ipInt 
     * @return 
     */  
    public static String int2ip(int ipInt) {  
        StringBuilder sb = new StringBuilder();  
        sb.append(ipInt & 0xFF).append(".");  
        sb.append((ipInt >> 8) & 0xFF).append(".");  
        sb.append((ipInt >> 16) & 0xFF).append(".");  
        sb.append((ipInt >> 24) & 0xFF);  
        return sb.toString();  
    }  
    
}
