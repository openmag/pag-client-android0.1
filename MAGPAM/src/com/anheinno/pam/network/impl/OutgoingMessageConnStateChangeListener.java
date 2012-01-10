/**
 * OutgoingMessageConnStateChangeListener.java
 * 
 * @Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.network.impl;

import com.anheinno.pam.libs.message.Message.TYPE;
import com.anheinno.pam.libs.message.MessageFactory;
import com.anheinno.pam.libs.message.impl.RegMessage;
import com.anheinno.pam.libs.util.Log;
import com.anheinno.pam.network.IConnStateChangeListener;
import com.anheinno.pam.network.OutgoingMessageAgent;
import com.anheinno.pam.util.Util;

/**
 * @author wangxun
 * @date 2011-08-10
 * @description Outgoing message connection state change listener
 */
public class OutgoingMessageConnStateChangeListener implements
	IConnStateChangeListener {

	private final String TAG = OutgoingMessageConnStateChangeListener.class.getName();
	
	/* (non-Javadoc)
	 * @see com.anheinno.pam.network.IConnectionStateChangeListener#onStateChange(com.anheinno.pam.network.IStateChangeListener.STATE)
	 */
	@Override
	public void onStateChange(STATE state) {
		
		switch(state){
			case CONNECTING:
				if(Log.DBG){
					Log.d(TAG, "Connecting state waiting for set OutputStream");
				}
				break;
			case CONNECTED:
				if(Log.DBG){
					Log.d(TAG, "Connected state just get OutputStream, begin send the register message");
				}
				RegMessage reg = (RegMessage) MessageFactory.create(TYPE.REG);
				reg.setInterval(Util.getInterval());
				OutgoingMessageAgent.getInstance().send(reg);
				break;
			case DISCONNECTED:
				if(Log.DBG){
					Log.d(TAG, "Disconnected state");
				}
				break;
		}
		
	}

}
