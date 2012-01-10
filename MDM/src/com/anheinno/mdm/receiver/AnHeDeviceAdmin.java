/**
 * AnHeDeviceAdmin.java
 * 
 * @Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.mdm.receiver;

import com.anheinno.mdm.R;
import com.anheinno.pam.libs.application.Application;
import android.app.admin.DeviceAdminReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * @author wangxun
 * @date 2011-08-22
 * @description Anhe-inno device administrator
 */
public final class AnHeDeviceAdmin extends DeviceAdminReceiver {

	/**
	 * Component for anhe device administrator
	 */
	public static final ComponentName ANHEDEVICEADMIN = new ComponentName(Application.CONTEXT, AnHeDeviceAdmin.class);

	void showToast(Context context, CharSequence msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onEnabled(Context context, Intent intent) {
		showToast(context, context.getString(R.string.enable_notify));
	}

	@Override
	public CharSequence onDisableRequested(Context context, Intent intent) {
		return context.getString(R.string.warning);
	}

	@Override
	public void onDisabled(Context context, Intent intent) {
		showToast(context, context.getString(R.string.disable_notify));
	}

	@Override
	public void onPasswordChanged(Context context, Intent intent) {
		showToast(context, context.getString(R.string.pwd_change_notify));
	}

	@Override
	public void onPasswordFailed(Context context, Intent intent) {
		showToast(context, context.getString(R.string.pwd_failed_notify));
	}

	@Override
	public void onPasswordSucceeded(Context context, Intent intent) {
		showToast(context, context.getString(R.string.pwd_succ_notify));
	}
	
}
