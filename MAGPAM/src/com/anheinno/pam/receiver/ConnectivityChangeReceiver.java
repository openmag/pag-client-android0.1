/**
 * ConnectivityChangeReceiver.java
 * 
 * @Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.receiver;

import com.anheinno.pam.libs.util.Log;
import com.anheinno.pam.receiver.PushController.Command;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author wangxun
 * @date 2011-07-28
 * @description Receive the system's connectivity change message.
 */
public final class ConnectivityChangeReceiver extends BroadcastReceiver {

	private final String TAG = ConnectivityChangeReceiver.class.getName();

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {

		if(Log.DBG){
			Log.d(TAG, "Received connectivity change message: " + intent);
		}
		
		  final NetworkInfo networkInfo = (NetworkInfo) intent
					.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		  
		  if(null == networkInfo){
			  if(Log.DBG){
				  Log.d(Log.TAG, "Can't get current NetworkInfo just ingore it");
			  }
			  
			  return;
		  }
		  
		  final int networkType = networkInfo.getType();
		  
		  if (ConnectivityManager.TYPE_MOBILE != networkType && ConnectivityManager.TYPE_WIFI != networkType) {
				if (Log.DBG)
					Log.d(TAG, "changed to other connections just ignore it, current network type is: " + networkType);
				return;
		  }
		  
		  if(NetworkInfo.State.CONNECTED == networkInfo.getState()){
			  if(Log.DBG){
				  Log.d(TAG, "Current network connected, just send RESTART command");
			  }
			  PushController.sendCmd(Command.COMMAND.RESTART);
		  }
		  else{
			  if(Log.DBG){
				  Log.d(TAG, "Current network disconnected");
			  }
		  }
		
	}
}
