/**
 * IncomingMessageConnectionStateChangeListener.java
 * 
 * @Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.network.impl;

import com.anheinno.pam.libs.util.Log;
import com.anheinno.pam.network.IConnStateChangeListener;

/**
 * @author wangxun
 * @date 2011-08-03
 * @description Listener for incoming message's connection change
 */
public class IncomingMessageConnStateChangeListener implements
	IConnStateChangeListener {
	
	private final String TAG = IncomingMessageConnStateChangeListener.class.getName();
	
	/* (non-Javadoc)
	 * @see com.anheinno.pam.network.IConnectionStateChangeListener#onStateChange(com.anheinno.pam.network.IStateChangeListener.STATE)
	 */
	@Override
	public void onStateChange(STATE state) {
		
		switch(state){
			case CONNECTING:
				if(Log.DBG){
					Log.d(TAG, "Connecting state waiting for set InputStream");
				}
				break;
			case CONNECTED:
//				if(Log.DBG){
//					Log.d(TAG, "Connected state just get InputStream, begin send the register message");
//				}
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//				}
//				RegMessage reg = (RegMessage) MessageFactory.create(TYPE.REG);
//				OutgoingMessageAgent.getInstance().send(reg);
				break;
			case DISCONNECTED:
				if(Log.DBG){
					Log.d(TAG, "Disconnected state");
				}
				break;
		}
		
	}

}
