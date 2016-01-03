package com.mobp2p.mobp2psdk;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobp2p.mobp2psdk.Api.Api;
import com.mobp2p.mobp2psdk.base.HttpUtils;
import com.mobp2p.mobp2psdk.callback.CallBack;
import com.mobp2p.mobp2psdk.utils.MResource;
import com.mobp2p.mobp2psdk.utils.MetaDataUtils;
import com.mobp2p.mobp2psdk.utils.SdkPreferences;

public class LoadingActivity extends Activity {
	JSONObject object;
	JSONObject data;
	String status = "", activity_page = "", mobileloan_page = "", msgs = "";
	int update_sms_max = 50;
	int update_contacts_max = 50;
	RelativeLayout navigation;
	WebView view;
	TextView tv_title, left_text_string;
	ProgressBar query_pro;
	RelativeLayout left_finish;
	static final int HANDERL_MESSAGEWEB_ACTIVITY = 1;
	static final int HANDERL_MESSAGEERRO = 2;
	static final int HANDERL_MESSAGEWEB_LOAN = 3;
	static final int HANDERL_MSG = 4;
	// 通过handler发消息更新ui
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDERL_MESSAGEWEB_ACTIVITY:
				view.loadUrl(activity_page);
				break;
			case HANDERL_MESSAGEERRO:
				Toast.makeText(LoadingActivity.this, "暂无数据", Toast.LENGTH_LONG)
						.show();
				break;
			case HANDERL_MESSAGEWEB_LOAN:
				view.loadUrl(mobileloan_page);
				break;
			case HANDERL_MSG:
				Toast.makeText(LoadingActivity.this, msgs, Toast.LENGTH_LONG)
						.show();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(MResource.getIdByName(getApplication(), "layout",
				"mobp2psdk_main"));
		SdkPreferences.initUZonePreferenceInstance(getApplicationContext());
		initWebView();
		initDate();
	}

	void initDate() {
		Map<String, Object> requestParamsMap = new HashMap<String, Object>();
		requestParamsMap.put("latlng", "40.714224,-73.96145");
		requestParamsMap.put("sensor", "false");
		HttpUtils.doGetAsyn(LoadingActivity.this, "http://maps.googleapis.com/maps/api/json", requestParamsMap, new CallBack() {
			@Override
			public void onRequestComplete(String result) {
				// TODO Auto-generated method stub
				Log.d("result", result);
			}
		});
		// 拿到设备信息
		HttpUtils.doGetAsyn(LoadingActivity.this, Api.API_GETLOANINFO,
				new CallBack() {
					@Override
					public void onRequestComplete(String result) {
						// TODO Auto-generated method stub
						try {
							Log.d("MainActivity", result);
							if (result != null) {
								object = new JSONObject(result);
								if (object != null) {
									status = object.optString("status");
									msgs = object.optString("msg");
									if ("1".equals(status)) {
										data = object.optJSONObject("data");
										mobileloan_page = data
												.optString("mobileloan_page");
										activity_page = data
												.optString("activity_page");
										update_sms_max = data
												.optInt("update_sms_max");
										update_contacts_max = data
												.optInt("update_contacts_max");
										Map<String, Object> infoMap = new HashMap<String, Object>();
										infoMap.put("update_sms_max",
												update_sms_max);
										infoMap.put("update_contacts_max",
												update_contacts_max);
										SdkPreferences.setConfigInfo(infoMap);
										if ("".equals(activity_page)
												|| activity_page.length() == 0)
											// 证明没有活动页加载借款页
											handler.sendEmptyMessage(HANDERL_MESSAGEWEB_LOAN);
										else
											handler.sendEmptyMessage(HANDERL_MESSAGEWEB_ACTIVITY);
									} else {
										handler.sendEmptyMessage(HANDERL_MSG);
									}
								} else {
									handler.sendEmptyMessage(HANDERL_MESSAGEERRO);
								}
							} else {
								handler.sendEmptyMessage(HANDERL_MESSAGEERRO);
							}
						} catch (Exception e) {

						}
					}
				});
	}

	void initWebView() {
		navigation = (RelativeLayout) findViewById(MResource.getIdByName(
				getApplication(), "id", "navigation"));
		view = (WebView) findViewById(MResource.getIdByName(getApplication(),
				"id", "wv_main"));
		query_pro = (ProgressBar) findViewById(MResource.getIdByName(
				getApplication(), "id", "query_pro"));
		left_finish = (RelativeLayout) findViewById(MResource.getIdByName(
				getApplication(), "id", "left_finish"));

		tv_title = (TextView) findViewById(MResource.getIdByName(
				getApplication(), "id", "center_text_string"));
		tv_title.setMovementMethod(ScrollingMovementMethod.getInstance());
		tv_title.setSelected(true);
		left_text_string = (TextView) findViewById(MResource.getIdByName(
				getApplication(), "id", "left_text_string"));
		left_finish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (view.canGoBack()) {
					view.goBack();
				} else {
					SdkPreferences.clear();
					finish();
				}
			}
		});
		WebSettings wSettings = view.getSettings();
		wSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		wSettings.setDomStorageEnabled(true);
		wSettings.setDisplayZoomControls(true);
		wSettings.setSupportZoom(true);
		wSettings.setBuiltInZoomControls(true);
		wSettings.setJavaScriptEnabled(true);
		wSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		view.requestFocusFromTouch();
		view.addJavascriptInterface(new JavaScriptinterface(this), "android");
		view.setWebViewClient(mWebViewClient);
		view.setWebChromeClient(mChromeClient);
		// view.loadUrl("file:///android_asset/index.html");
	}

	private WebViewClient mWebViewClient = new WebViewClient() {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	};
	private WebChromeClient mChromeClient = new WebChromeClient() {
		// 设置标题 根据网页标题决定
		public void onReceivedTitle(WebView view, String title) {
			tv_title.setText(title);
			super.onReceivedTitle(view, title);
		};

		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress != 100) {
				query_pro.setVisibility(View.VISIBLE);
				query_pro.setProgress(newProgress);
			} else {
				query_pro.setVisibility(View.GONE);
			}

			super.onProgressChanged(view, newProgress);
		};
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && view.canGoBack()) {
			view.goBack();
			return true;
		} else {
			SdkPreferences.clear();
		}
		return super.onKeyDown(keyCode, event);
	}

	// 获取联系人
	protected void onActivityResult(int requestCode, int resultCode,
			android.content.Intent data) {
		if (RESULT_OK == resultCode && null != data) {
			Uri uri = data.getData();
			Cursor cursor = null;
			try {
				cursor = getContentResolver()
						.query(uri, null, null, null, null);
				if (null != cursor) {
					if (cursor.moveToFirst()) {
						String contactID = cursor.getString(cursor
								.getColumnIndex(ContactsContract.Contacts._ID));
						String contactnameString = cursor
								.getString(cursor
										.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
						// Added
						String phonenumber = "";
						Cursor phoneCursor = getContentResolver()
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
												+ "=" + contactID, null, null);
						while (phoneCursor.moveToNext()) {
							phonenumber = phoneCursor
									.getString(phoneCursor
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							phonenumber.trim();
							phonenumber = phonenumber.replaceAll(" ", "");
							phonenumber = phonenumber.replace("+86", "");
						}
						String phoneinfo = (contactnameString + "," + phonenumber)
								.trim().replaceAll(" ", "");
						view.addJavascriptInterface(new JavaScriptinterface(
								this, phoneinfo), "android");
						view.loadUrl("javascript:androidGetMLinfo()");
						phoneCursor.close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (null != cursor) {
					cursor.close();
				}
			}
		}
	};

}
