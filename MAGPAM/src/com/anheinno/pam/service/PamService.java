/**
 * PamService.java
 * 
 * @Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.service;

import com.anheinno.pam.libs.message.util.MessageUtil;
import com.anheinno.pam.libs.util.Log;
import com.anheinno.pam.receiver.PushController;
import com.anheinno.pam.receiver.PushController.Command.COMMAND;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author wangxun
 * @date 2011-07-29
 * @description Service for handle long time running background task 
 */
public final class PamService extends Service {

	private final String TAG = PamService.class.getName();
	
	public static volatile boolean isRunning;
	
	static{
		isRunning = false;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		if(Log.DBG){
			Log.d(TAG, "PamService created");
		}
		MessageUtil.setStatus(MessageUtil.STATUS.INITIAL.toString());
		isRunning = true;
		
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(Log.DBG){
			Log.d(TAG, "onStartCommand, intent: " + intent + " flags: " + flags + " startId: " + startId);
		}
		if(Log.DBG){
			Log.d(TAG, "PamService startted, begin send START command");
		}
		
		isRunning = true;
		
		PushController.sendCmd(COMMAND.START);
		return START_STICKY;
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		if(Log.DBG){
			Log.d(TAG, "PamService onDestroy");
		}
		isRunning = false;
		PushController.sendCmd(COMMAND.STOP);
		super.onDestroy();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		if(Log.DBG){
			Log.d(TAG, "PamService finalize");
		}
		isRunning = false;
		super.finalize();
	}
	
}
