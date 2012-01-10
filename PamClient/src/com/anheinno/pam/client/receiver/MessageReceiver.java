/**
 * 	MessageReceiver.java
 * 
 * 	@Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.client.receiver;


import com.anheinno.pam.client.PamClientActivity;
import com.anheinno.pam.libs.plugin.Plugin;
import com.anheinno.pam.libs.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

/**
 * @author wangxun
 * @date 2011-07-27
 * @description Receive the push content
 */
public class MessageReceiver extends BroadcastReceiver {

	private final String TAG = MessageReceiver.class.getName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
			if(Log.DBG){
				Log.d(TAG, "Just received intent: " + intent);
			}
	    	byte[] content = null;
	    	content = Plugin.onReceive(intent);
	    	if(null != content){
	    		String strContent = null;
	    		strContent = new String(content);
	    		if(Log.DBG){
	    			Log.d(TAG, "Just received message: " + strContent);
	    		}
	    		Message msg = new Message();
		    	Bundle data = new Bundle();
		    	data.putString("content", strContent);
		    	msg.setData(data);
		    	PamClientActivity._Handler.sendMessage(msg);
	    	}
	    	
	}

}
