/**
 * 
 */
package com.anheinno.pam.receiver;

import com.anheinno.pam.libs.util.Log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author wangxun
 *
 */
public class BatteryReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if(Log.DBG){
			Log.d(Log.TAG, "Intent: " + intent);
		}
		
	}

}
