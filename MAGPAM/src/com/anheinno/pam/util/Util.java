/**
 * Util.java
 * 
 * @Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.util;

import com.anheinno.pam.libs.application.Application;
import com.anheinno.pam.libs.util.Log;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author wangxun
 * @date 2011-07-29
 * @description Utility class contains various tools 
 */

public final class Util {
	
	private final static String TAG = Util.class.getName();
	
	private static SharedPreferences _UTIL_PREFERENCES;
	
	private final static String INTERVAL = "com.anheinno.pam.interval";
	
	private final static String UTIL_SHAREDPREFERENCES = "com.anheinno.pam.util";
	
	private final static String DEFAULT_INTERVAL = "400";
	
	private final static String HOST = "com.anheinno.pam.host";
	
	private final static String PORT = "com.anheinno.pam.port";
	
	private final static String DEFAULT_HOST = "58.68.150.170";
	
	private final static int DEFAULT_PORT = 12333;
	
	static{
		_UTIL_PREFERENCES = Application.CONTEXT.getSharedPreferences(UTIL_SHAREDPREFERENCES, Context.MODE_PRIVATE);
	}
	
	private Util(){}
	
	/**
	 * Retrieve the heart beat interval
	 * @return String interval
	 */
	public static synchronized String getInterval(){
		return _UTIL_PREFERENCES.getString(INTERVAL, DEFAULT_INTERVAL);
	}
	
	/**
	 * Set the heart beat interval
	 * @param interval
	 */
	public static synchronized void setInterval(String interval){
		_UTIL_PREFERENCES.edit().putString(INTERVAL, interval.trim()).commit();
	}
	
	/**
	 * Retrieve the host's ip address
	 * @return String host
	 */
	public static synchronized String getHost(){
		return _UTIL_PREFERENCES.getString(HOST, DEFAULT_HOST);
	}
	
	/**
	 * Set the host's ip address
	 * @param host
	 */
	public static synchronized void setHost(String host){
		_UTIL_PREFERENCES.edit().putString(HOST, host).commit();
	}
	
	/**
	 * Retrieve the host's port
	 * @return String port
	 */
	public static synchronized int getPort(){
		return _UTIL_PREFERENCES.getInt(PORT, DEFAULT_PORT);
	}
	
	/**
	 * Set the host's port
	 * @param host
	 */
	public static synchronized void setPort(int port){
		_UTIL_PREFERENCES.edit().putInt(PORT, port).commit();
	}
	
	/**
	 * Check if current network is ready
	 * 
	 * @return
	 */
	public static boolean checkNetworkState() {
		NetworkInfo info = getNetworkInfo();

		return (info != null && info.isConnected());
	}
  
  	/**
	 * Get current network information
	 * 
	 * @return
	 */
	public static NetworkInfo getNetworkInfo() {
		NetworkInfo info = null;
		try {
			ConnectivityManager connMan = (ConnectivityManager) Application.CONTEXT
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			info = connMan.getActiveNetworkInfo();
		} catch (Throwable t) {
			if (Log.DBG) {
				Log.e(TAG, "During getNetworkInfo exception occur: "
						+ t.getLocalizedMessage());
			}
		}

		return info;
	}
	
}
