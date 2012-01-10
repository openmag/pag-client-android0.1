/**
 * AndroidTimer.java
 * 
 * @Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.timer.impl;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import com.anheinno.pam.libs.application.Application;
import com.anheinno.pam.timer.ITimer;

/**
 * @author wangxun
 * @date 2011-08-03
 * @description Timer implementation on android platform
 */
public final class AndroidTimer implements ITimer {
	
	private static AlarmManager _alarm;
	
	static{
		_alarm = (AlarmManager) Application.CONTEXT.getSystemService(Context.ALARM_SERVICE);
	}
	
	/* (non-Javadoc)
	 * @see com.anheinno.pam.timer.ITimer#set(long, java.lang.Object[])
	 */
	@Override
	public void set(long interval, Object... params) {
		
		if(null != _alarm){
			PendingIntent i = null;
			if(params[0] instanceof PendingIntent){
				i = (PendingIntent) params[0];
			}
//			_alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, (SystemClock.elapsedRealtime() + (interval + 10) * 1000), i);
			// Due to exist bug -- when use ELAPSED_REALTIME_WAKEUP, the alarm can't be waked up on time so here have to use RTC_WAKEUP
			_alarm.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() +  (interval + 10) * 1000), i);
		}
		
	}

	/* (non-Javadoc)
	 * @see com.anheinno.pam.timer.ITimer#cancel(java.lang.Object)
	 */
	@Override
	public void cancel(Object... params) {
		
		if(null != _alarm){
			PendingIntent i = null;
			if(params[0] instanceof PendingIntent){
				i = (PendingIntent) params[0];
			}
			_alarm.cancel(i);
		}
		
	}

}
