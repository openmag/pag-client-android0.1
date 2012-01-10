/**
 * 	MessageReceiver.java
 * 
 * 	@Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.mdm.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import com.anheinno.pam.libs.application.Application;
import com.anheinno.pam.libs.plugin.Plugin;
import com.anheinno.pam.libs.util.Log;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author wangxun
 * @date 2011-08-19
 * @description MDM's message receiver -- receive the message from MAGPE
 */
public class MessageReceiver extends BroadcastReceiver {

	private final static String TAG = MessageReceiver.class.getName();
	private final static String PWD = "pwd";
	private final static String ENCRYPTION = "encryption";
	private final static String LOCK = "lock";
	private final static String WIPE = "wipe";
	private final static String ENABLE = "enable";
	private final static String DISABLE = "disable";
	private final static String MAXFAILEDPWDSFORWIPE = "maxfailedpwdsforwipe";
	private final static String MAXTIMETOLOCK = "maxtimetolock";
	private final static String PWDMINLEN = "pwdminlen";
	private final static String PWDQUALITY = "pwdquality";

	@Override
	public void onReceive(Context context, Intent intent) {
		
		DevicePolicyManager dm = (DevicePolicyManager) Application.CONTEXT.getSystemService(Context.DEVICE_POLICY_SERVICE);
		
		// Retrieve the push content
		byte[] content = null;
		content = Plugin.onReceive(intent);
		
		if (null != content) {
			if(Log.DBG){
				Log.d(TAG, "Just received cmd: " + new String(content));
			}
			JSONObject cmd = null;
			try {
				cmd = new JSONObject(new String(content));
			} catch (JSONException e) {
				if(Log.DBG){
					Log.d(TAG, "JSONException: " + e);
				}
			}
			if (null != cmd) {
			
				if (!cmd.isNull(PWD)) {
					try {
						dm.resetPassword(cmd.getString(PWD).trim(), 0);
					} catch (JSONException e) {
					}
				}
				
				if (!cmd.isNull(ENCRYPTION)) {
					
				}
				
				if (!cmd.isNull(LOCK)) {
					dm.lockNow();
				}
				
				if (!cmd.isNull(WIPE)) {
					dm.wipeData(0);
				}
				
				if (!cmd.isNull(MAXFAILEDPWDSFORWIPE)) {
					try {
						dm.setMaximumFailedPasswordsForWipe(AnHeDeviceAdmin.ANHEDEVICEADMIN, Integer.parseInt(cmd.getString(MAXFAILEDPWDSFORWIPE)));
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if (!cmd.isNull(MAXTIMETOLOCK)) {
					try {
						dm.setMaximumTimeToLock(AnHeDeviceAdmin.ANHEDEVICEADMIN, Integer.parseInt(cmd.getString(MAXTIMETOLOCK)));
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if (!cmd.isNull(PWDMINLEN)) {
					try {
						dm.setPasswordMinimumLength(AnHeDeviceAdmin.ANHEDEVICEADMIN, Integer.parseInt(cmd.getString(PWDMINLEN)));
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if (!cmd.isNull(PWDQUALITY)) {
					try {
						dm.setPasswordQuality(AnHeDeviceAdmin.ANHEDEVICEADMIN, Integer.parseInt(cmd.getString(PWDQUALITY)));
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			
		}
	}

}
