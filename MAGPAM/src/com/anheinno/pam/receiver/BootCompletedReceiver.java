/**
 * BootCompletedReceiver.java
 * 
 * @Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.receiver;

import com.anheinno.pam.libs.application.Application;
import com.anheinno.pam.libs.message.util.MessageUtil;
import com.anheinno.pam.libs.util.Log;
import com.anheinno.pam.receiver.PushController.Command.COMMAND;
import com.anheinno.pam.service.PamService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author wangxun
 * @date 2011-07-28
 * @description Receive the system's boot complete message.
 */
public final class BootCompletedReceiver extends BroadcastReceiver {

	private final String TAG = BootCompletedReceiver.class.getName();
	
	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if(Log.DBG){
			Log.d(TAG, "Received message: " + intent);
		}
		
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			if(Log.DBG){
				Log.d(TAG, "Received the boot completed message");
			}
			MessageUtil.setStatus(MessageUtil.STATUS.INITIAL.toString());
			PushController.sendCmd(COMMAND.START);
			Intent i = new Intent();
			i.setClass(context, PamService.class);
			Application.CONTEXT.startService(i);
			if(Log.DBG){
				Log.d(TAG, "Just start the PamService");
			}
		}
	}

}
