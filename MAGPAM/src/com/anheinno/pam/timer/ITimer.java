/**
 * ITimer.java
 * 
 * @Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.timer;

/**
 * @author wangxun
 * @date 2011-08-03
 * @description Timer interface
 */
public interface ITimer {
	
	public void set(long interval, Object... params);
	
	public void cancel(Object... params);
	
	public enum TYPE{
		ANDROID{
			@Override
			public String toString(){
				return "ANDROID";
			}
		}
	};
	
}
