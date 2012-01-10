/**
 * MessageListener.java
 * 
 * @Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.message.impl;

import android.app.PendingIntent;
import android.content.Intent;

import com.anheinno.pam.database.PamClient;
import com.anheinno.pam.libs.application.Application;
import com.anheinno.pam.libs.message.Message;
import com.anheinno.pam.libs.message.impl.ActMessage;
import com.anheinno.pam.libs.message.impl.NotiMessage;
import com.anheinno.pam.libs.message.impl.RspMessage;
import com.anheinno.pam.libs.message.util.IMessageListener;
import com.anheinno.pam.libs.util.Log;
import com.anheinno.pam.network.OutgoingMessageAgent;
import com.anheinno.pam.receiver.PushController;
import com.anheinno.pam.receiver.PushController.Command.COMMAND;
import com.anheinno.pam.timer.ITimer;
import com.anheinno.pam.timer.ITimer.TYPE;
import com.anheinno.pam.timer.impl.TimerFactory;
import com.anheinno.pam.util.Util;

/**
 * @author wangxun
 * @date 2011-08-02
 * @description Message listener listen to new message
 */
public final class MessageListener implements IMessageListener {

	private final String TAG = MessageListener.class.getName();
	
	private final static ITimer _timer;
	
	private final static PendingIntent _intent;
	
	static{
		_timer = TimerFactory.create(TYPE.ANDROID);
		_intent = PushController.createCmd(COMMAND.CHOKE);
	}
	
	public MessageListener(){
	}
	
	/* (non-Javadoc)
	 * @see com.anheinno.pam.message.util.IMessageListener#onReceive(com.anheinno.pam.message.Message)
	 */
	@Override
	public void onReceive(Message msg) {
		
		if(msg instanceof ActMessage){
			onActMessageHandler((ActMessage) msg);
		}
		else if(msg instanceof RspMessage){
			onRspMessageHandler((RspMessage) msg);
		}
		else if(msg instanceof NotiMessage){
			onNotiMessageHandler((NotiMessage) msg);
		}
		
	}

	private void onActMessageHandler(ActMessage msg) {

		if (Log.DBG) {
			Log.d(TAG,
					"Received active message just cancel old timer and set new timer, current time interval: "
							+ msg.getTimeInterval());
		}
		String interval = msg.getTimeInterval();
		Util.setInterval(interval);

		TimerFactory.create(TYPE.ANDROID).cancel(PushController.createCmd(COMMAND.CHOKE));
		TimerFactory.create(TYPE.ANDROID).set(Integer.parseInt(interval), PushController.createCmd(COMMAND.CHOKE));

	}
	
	private void onRspMessageHandler(RspMessage msg){
		
		if(Log.DBG){
			Log.d(TAG, "Received response message: " + msg);
		}
		OutgoingMessageAgent.getInstance().accept();
		
	}
	
	private void onNotiMessageHandler(NotiMessage msg) {
		
		if (Log.DBG) {
			Log.d(TAG, "Received notify message:\n" + msg);
		}
		Intent i = new Intent();
		i.setAction("com.anheinno.intent.action.PUSHMSG");
		i.addCategory(PamClient.getCategory(msg.getParam()));
		i.putExtra(Message.MSG, msg);
		Application.CONTEXT.sendBroadcast(i);
		
	}
	
}
