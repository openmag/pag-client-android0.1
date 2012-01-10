/**
 * 	MessageReceiver.java
 * 
 * 	@Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.receiver;

import com.anheinno.pam.database.PamClient;
import com.anheinno.pam.database.PamClient.STATUS;
import com.anheinno.pam.libs.application.Application;
import com.anheinno.pam.libs.message.Message;
import com.anheinno.pam.libs.message.impl.AckMessage;
import com.anheinno.pam.libs.message.impl.MappRegisterMessage;
import com.anheinno.pam.libs.message.impl.NotiMessage;
import com.anheinno.pam.libs.util.Log;
import com.anheinno.pam.network.OutgoingMessageAgent;
import com.anheinno.pam.service.PamService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author wangxun
 * @date 2011-07-27
 * @description Receive the incoming message
 */
public final class MessageReceiver extends BroadcastReceiver {
	
	private final String TAG = MessageReceiver.class.getName();
	
	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
	    if(Log.DBG){
	    	Log.d(TAG, intent.toString());
	    }
	    Message msg = (Message) intent.getSerializableExtra(Message.MSG);
	    
	    if(msg instanceof NotiMessage || msg instanceof AckMessage){
	    	if(Log.DBG){
	    		Log.d(TAG, "Just received outgoing message: " + msg);
	    	}
	    	OutgoingMessageAgent.getInstance().send(msg);
	    }
	    else if(msg instanceof MappRegisterMessage){
	    	if(Log.DBG){
	    		Log.d(TAG, "Just received mapp register message: " + msg);
	    	}
	    	MappRegisterMessage mapp = (MappRegisterMessage)msg;
	    	PamClient.addClient(mapp.getAppId(), mapp.getCategory(), STATUS.REGISTER);
	    	if(!PamService.isRunning){
	    		if(Log.DBG){
	    			Log.d(TAG, "Current service isn't running just start it");
	    			Intent i = new Intent();
	    			i.setClass(context, PamService.class);
	    			Application.CONTEXT.startService(i);
	    		}
	    	}
	    }
	}

}
