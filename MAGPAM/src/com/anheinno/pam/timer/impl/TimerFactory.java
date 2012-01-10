/**
 * TimerFactory.java
 * 
 * @Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.timer.impl;

import com.anheinno.pam.timer.ITimer;

/**
 * @author wangxun
 * @date 2011-08-03
 * @description Timer factory
 */
public final class TimerFactory {
	
	private TimerFactory(){}
	
	public static ITimer create(ITimer.TYPE type){
		
		ITimer timer = null;
		switch(type){
			case ANDROID:
				timer = new AndroidTimer();
				break;
		}
		return timer;
		
	}
	
}
