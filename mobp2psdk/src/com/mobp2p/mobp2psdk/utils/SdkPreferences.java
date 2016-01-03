package com.mobp2p.mobp2psdk.utils;

import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SdkPreferences {
	static SharedPreferences sdkPreferencesInstance;
	public static void initUZonePreferenceInstance(final Context context) {
		if (null == sdkPreferencesInstance)
			sdkPreferencesInstance = PreferenceManager
					.getDefaultSharedPreferences(context);
	}
	
	//清空配置
	public static void clear(){
		 SharedPreferences.Editor editor = sdkPreferencesInstance.edit();
		 editor.clear();
		 editor.apply();
	}
	// 设置活动页面
	public static void setActivity_page(String activity_page) {
		SharedPreferences.Editor editor = sdkPreferencesInstance.edit();
		editor.putString("activity_page", activity_page);
		editor.apply();
	}

	public static String getActivity_page() {
		return sdkPreferencesInstance.getString("activity_page", "");
	}
	// 设置配置信息项
	public static void setConfigInfo(Map<String, Object> rMap) {
		SharedPreferences.Editor editor = sdkPreferencesInstance.edit();
		Iterator it = rMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry element = (Map.Entry) it.next();
			editor.putString(element.getKey().toString(), element.getValue()
					.toString());
		}
		editor.apply();
	}
	public static String  getUpdate_sms_max() {
		return sdkPreferencesInstance.getString("update_sms_max", "50");
	}
	public static String getUpdate_contacts_max() {
		return sdkPreferencesInstance.getString("update_contacts_max", "50");
	}
}
