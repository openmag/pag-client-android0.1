/**
 * SocketFactory.java
 * 
 * @Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.network;

import com.anheinno.pam.network.impl.AndroidSocket;

/**
 * @author wangxun
 * @date 2011-08-02
 * @description Factory to create new socket according the socket type
 */
public final class SocketFactory {
	
	private SocketFactory(){}
	
	public static ISocket create(ISocket.TYPE type){
		
		ISocket socket = null;
		
		switch(type){
			case ANDROID:
				socket = new AndroidSocket();
				break;
		}
		
		return socket;
		
	}
	
}
