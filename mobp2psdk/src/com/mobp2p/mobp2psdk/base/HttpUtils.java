package com.mobp2p.mobp2psdk.base;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.mobp2p.mobp2psdk.Api.Api;
import com.mobp2p.mobp2psdk.callback.CallBack;

public class HttpUtils {
	// 超时时间
	private static final int TIMEOUT_IN_MILLIONS = 5000;

	/*
	 * ͬ无参get方法
	 */
	public static String getHttpResult(String url) {
		ByteArrayOutputStream os = null;
		InputStream is = null;
		String result;
		try {
			URL urls = new URL(url);
			HttpURLConnection urlConnection = (HttpURLConnection) urls
					.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setReadTimeout(TIMEOUT_IN_MILLIONS);
			urlConnection.setConnectTimeout(TIMEOUT_IN_MILLIONS);
			if (urlConnection.getResponseCode() == 200) {
				is = urlConnection.getInputStream();
				os = new ByteArrayOutputStream();
				int len = 0;
				byte buffer[] = new byte[1024];
				while ((len = is.read(buffer)) != -1) {
					os.write(buffer, 0, len);
				}
			}
			result = new String(os.toByteArray());
			return result;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	/*
	 * 有参get方法
	 */
	public static String getHttpResult(String url,
			Map<String, Object> requestParamsMap) {
		ByteArrayOutputStream os = null;
		InputStream is = null;
		String result;
		try {
			StringBuffer params = new StringBuffer();
			StringBuffer urlParams = new StringBuffer();
			Iterator it = requestParamsMap.entrySet().iterator();
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
			urlParams.append(url).append("?").append(params);
			Log.d("URL", urlParams.toString());
			URL urls = new URL(urlParams.toString());
			HttpURLConnection urlConnection = (HttpURLConnection) urls
					.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setReadTimeout(TIMEOUT_IN_MILLIONS);
			urlConnection.setConnectTimeout(TIMEOUT_IN_MILLIONS);
			if (urlConnection.getResponseCode() == 200) {
				is = urlConnection.getInputStream();
				os = new ByteArrayOutputStream();
				int len = 0;
				byte buffer[] = new byte[1024];
				while ((len = is.read(buffer)) != -1) {
					os.write(buffer, 0, len);
				}
				result = new String(os.toByteArray());
				return result;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	/*
	 * 
	 * 有参异步调用
	 */
	public static void doGetAsyn(Context context, final String urlStr,
			final Map<String, Object> requestParamsMap, final CallBack callBack) {
		if (isNetAvailable(context)) {
			new Thread() {
				public void run() {
					try {
						String result = getHttpResult(urlStr, requestParamsMap);
						if (callBack != null) {
							callBack.onRequestComplete(result);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				};
			}.start();
		} else {
			Toast.makeText(context, "网络连接错误，请检查网络", Toast.LENGTH_LONG).show();
		}
	}

	/*
	 * 启用异步调用
	 */
	public static void doGetAsyn(Context context, final String urlStr,
			final CallBack callBack) {
		if (isNetAvailable(context)) {
			new Thread() {
				public void run() {
					try {
						String result = getHttpResult(urlStr);
						if (callBack != null) {
							callBack.onRequestComplete(result);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				};
			}.start();
		} else {
			Toast.makeText(context, "网络连接错误，请检查网络", Toast.LENGTH_LONG).show();
		}
	}

	/*
	 * post请求
	 */
	public static String postHttpResult(String url,
			Map<String, Object> requestParamsMap) {
		URL realUrl = null;
		InputStream in = null;
		HttpURLConnection connection = null;
		PrintWriter printWriter = null;
		DataOutputStream out = null;
		BufferedReader bufferedReader = null;
		try {
			StringBuffer responseResult = new StringBuffer();
			StringBuffer params = new StringBuffer();
			Iterator it = requestParamsMap.entrySet().iterator();
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
			realUrl = new URL(url);
			connection = (HttpURLConnection) realUrl.openConnection();
			connection.setReadTimeout(TIMEOUT_IN_MILLIONS);
			connection.setConnectTimeout(TIMEOUT_IN_MILLIONS);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty("Content-Type",
					"application/form-data");
			connection.setRequestProperty("Charset","utf-8");
			connection.setRequestProperty("contentType","utf-8");
			connection.setRequestMethod("POST");
			out = new DataOutputStream(connection.getOutputStream());
			out.writeBytes(params.toString());
			out.flush();
			int responseCode = connection.getResponseCode();
			if (responseCode != 200) {
				Log.d("send post request error!", "请求失败");
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					responseResult.append(line);
				}
				return responseResult.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("send post request error!", e + "");
		} finally {
			connection.disconnect();
			try {
				if (out != null) {
					out.close();
				}
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
	/*
	 * 异步post请求
	 */
	public static void doPostAsyn(Context context, final String urlStr,
			final Map<String, Object> requestParamsMap, final CallBack callBack) {
		if (isNetAvailable(context)) {
			new Thread() {
				public void run() {
					try {
						String result = postHttpResult(urlStr, requestParamsMap);
						if (callBack != null) {
							callBack.onRequestComplete(result);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				};
			}.start();
		} else {
			Toast.makeText(context, "网络连接错误，请检查网络", Toast.LENGTH_LONG).show();
		}
	}

	public static boolean isNetAvailable(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		return (info != null && info.isAvailable());
	}

}
