/**
 * IncomingMessageAgent.java
 * 
 * @Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.network;

import java.io.IOException;
import java.io.InputStream;

import com.anheinno.pam.libs.message.Message;
import com.anheinno.pam.libs.message.Message.MessageHeader;
import com.anheinno.pam.libs.message.util.IMessageListener;
import com.anheinno.pam.libs.message.util.MessageUtil;
import com.anheinno.pam.libs.util.Log;
import com.anheinno.pam.network.IConnStateChangeListener.STATE;
import com.anheinno.pam.receiver.PushController;
import com.anheinno.pam.receiver.PushController.Command;

/**
 * @author wangxun
 * @date 2011-08-01
 * @description IncomingMessageAgent handle the incoming message
 */
public final class IncomingMessageAgent extends Thread {

	private final static String TAG = IncomingMessageAgent.class.getName();

	private static IncomingMessageAgent _instance;

	private InputStream _is;

	private static StringBuilder _header;

	private static int _data;
	
	private static int _len;
	
	private static int _actualReadLen;
	
	private static String _contentLen;
	
	private static byte[] _content;
	
	private IMessageListener _messageHandler;
	
	private IConnStateChangeListener _changeListener;
	
	private volatile PushAgent.STATE _state;

	static {
		_instance = new IncomingMessageAgent();
		_header = new StringBuilder();
	}
	
	private IncomingMessageAgent() {
		setName("IncomingMessageAgent");
	}

	public static IncomingMessageAgent getInstance() {
		
		if(null == _instance){
			synchronized(IncomingMessageAgent.class){
				if(null == _instance){
					_instance = new IncomingMessageAgent();
				}
			}
		}
		
		return _instance;
		
	}

	public void startAgent() {
		_state = PushAgent.STATE.RUNNING;
		if (!_instance.isAlive()) {
			_instance.start();
			if (Log.DBG) {
				Log.d(TAG, "Instance IncomingMessageAgent & start agent thread");
			}
		} else {
			if (Log.DBG) {
				Log.d(TAG, "IncomingMessageAgent is already running");
			}
		}
	}
	
	public void setFlag(PushAgent.STATE state){
		_state = state;
	}
	
	public PushAgent.STATE getFlag(){
		if(null == _state){
			return _state;
		}
		return _state;
	}
	
	public void stopAgent(){
		
		if(Log.DBG){
			Log.d(TAG, "Begin stop agent");
		}
		
		_state = PushAgent.STATE.STOP;
		interrupt();
		try {
			if(null != _is){
				_is.close();
			}
		} catch (IOException e) {
		}
		synchronized(_instance){
			_instance.notify();
		}
		_is = null;
		_instance = null;
		
		if(Log.DBG){
			Log.d(TAG, "After stop agent");
		}
		
	}
	
	public void restartAgent(){
		
		if(Log.DBG){
			Log.d(TAG, "Begin restart agent");
		}
		
		_state = PushAgent.STATE.RESTART;
//		interrupt();
		try {
			if(null != _is){
				_is.close();
			}
		} catch (IOException e) {
		}
//		synchronized(_instance){
//			_instance.notify();
//		}
		_is = null;
		
		if(Log.DBG){
			Log.d(TAG, "After restart agent");
		}
		
	}
	
	public void setMessageListener(IMessageListener listener){
		
		_messageHandler = listener;
		
	}
	
	public void setConnStateChangeListener(IConnStateChangeListener listener){
		
		_changeListener = listener;
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_FOREGROUND);
		
		if (Log.DBG) {
			Log.d(TAG, "IncomingMessageAgent is running now");
		}
		
		while (!(PushAgent.STATE.STOP == _state)) {

			// Waiting for set InputStream
			if(null != _changeListener)
			{
				_changeListener.onStateChange(STATE.CONNECTING);
			}
			
			if(Log.DBG){
				Log.i(TAG, "Begin waiting for InputStream");
			}
			
			try {
				synchronized (_instance) {
					_instance.wait();
				}
			} catch (InterruptedException e) {
			}

			if (Log.DBG) {
				Log.d(TAG, "Just get the inputstream");
			}
			
			if(null != _changeListener)
			{
				_changeListener.onStateChange(STATE.CONNECTED);
			}
			
			// Clear the header's previous content
			_header.delete(0, _header.length());
			
			while (!(PushAgent.STATE.STOP == _state)) {

				try {
					
					if(null != _is){
						_data = _is.read();
					}
					else{
						break;
					}
					
					if (-1 == _data) {
						if(Log.DBG){
							Log.d(TAG, "read == -1, just disconnected, current agent state: " + _state);
						}
						if(null != _changeListener)
						{
							_changeListener.onStateChange(STATE.DISCONNECTED);
						}
						if(PushAgent.STATE.RUNNING == _state){
							MessageUtil.setStatus(MessageUtil.STATUS.EXCEPTION.toString());
							restart();
						}
						break;
					}
					
					_header.append((char)_data);
					
					// Optimize the speed only check it when the header's length larger than the least header's length
					if (_header.length() >= Message.MINHEADERLEN && _header.toString().indexOf(Message.TERMINAL) > 0) {
						
						if(Log.DBG){
							Log.d(TAG, "Message header's length is: " + _header.length() + " msg:\n" + _header.toString());
						}
						
						Message msg = Message.decode(_header.toString());
						
						// Clear the header's previous content
						_header.delete(0, _header.length());
						
						if(null == msg){
							if(Log.DBG){
								Log.e(TAG, "Something is wrong can't generate the message just ignore it");
							}
							continue;
						}
						
						_contentLen = msg.getHeader().getFirstHeader(MessageHeader.HEADER_CONTENT_LENGTH);
						if (null != _contentLen) {
							
							_len = Integer.parseInt(_contentLen);
							_content = new byte[_len];
							_actualReadLen = _is.read(_content);
							
							if(-1 == _actualReadLen){
								if(Log.DBG){
									Log.e(TAG, "During read content read -1, the connection is reset by peer, just begin restart");
								}
								if((PushAgent.STATE.RUNNING == _state)){
									MessageUtil.setStatus(MessageUtil.STATUS.EXCEPTION.toString());
									restart();
								}
								break;
							}
							
							if(_actualReadLen == _len){
								msg.setContent(_content);
							}
							else{
								if(Log.DBG){
									Log.e(TAG, "The content len isn't equal, except: " + _len + " actual: " + _actualReadLen + " just ignore it");
								}
							}
							
							_len = 0;
							_actualReadLen = 0;
							_contentLen = null;
							_content = null;
							
							// Skip the end \r\n\r\n
//							_is.read();
//							_is.read();
							
						}
						
						if (Log.DBG) {
							Log.d(TAG, "Just get new message:\n" + msg);
						}
						
						if(null != _messageHandler){
							_messageHandler.onReceive(msg);
						}
						
						if(Log.DBG){
							Log.d(TAG, "Just handled one message");
						}
						
					}
				} catch (IOException e) {
					
					if(Log.DBG){
						Log.d(TAG, "During read incoming message IOException occur: " + e.getLocalizedMessage());
					}
					
					_changeListener.onStateChange(STATE.DISCONNECTED);
					
					if((PushAgent.STATE.RUNNING == _state)){
						MessageUtil.setStatus(MessageUtil.STATUS.EXCEPTION.toString());
						restart();
					}
					
					break;
					
				}

			}
		}
		
		if(Log.DBG){
			Log.w(TAG, "IncomingMessageAgent stopped");
		}
		
	}
	
	private void restart(){
		
		if(Log.DBG){
			Log.d(TAG, "Just send RESTART command");
		}
		PushController.sendCmd(Command.COMMAND.RESTART);
		
	}
	
	public void setInputStream(InputStream is) {
		synchronized (_instance) {
			_is = is;
			_instance.notify();
			if (Log.DBG) {
				Log.d(TAG, "After setInputStream");
			}
		}
	}

}
