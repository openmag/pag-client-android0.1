/**
 * IConnStateChangeListener.java
 * 
 * @Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.network;

/**
 * @author wangxun
 * @date 2011-08-03
 * @description Interface for state change
 */
public interface IConnStateChangeListener {
	
	public void onStateChange(STATE state);
	
	public enum STATE{
		CONNECTING{
			@Override
			public String toString(){
				return "CONNECTING";
			}
		},
		CONNECTED{
			@Override
			public String toString(){
				return "CONNECTED";
			}
		},
		DISCONNECTED{
			@Override
			public String toString(){
				return "CONNECTING";
			}
		}
	}
	
}
