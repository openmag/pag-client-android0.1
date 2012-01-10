/**
 * OutgoingMessageAgent.java
 * 
 * @Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.network;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.PriorityBlockingQueue;

import com.anheinno.pam.libs.message.Message;
import com.anheinno.pam.libs.message.impl.ExitMessage;
import com.anheinno.pam.libs.message.impl.RegMessage;
import com.anheinno.pam.libs.message.util.MessageUtil;
import com.anheinno.pam.libs.util.Log;
import com.anheinno.pam.network.IConnStateChangeListener.STATE;
import com.anheinno.pam.receiver.PushController;
import com.anheinno.pam.receiver.PushController.Command;

/**
 * @author wangxun
 * @date 2011-08-01
 * @description OutgoingMessageAgent handle the outgoing message
 */
public class OutgoingMessageAgent extends Thread {

	private final static String TAG = OutgoingMessageAgent.class.getName();
	
	private static OutgoingMessageAgent _instance;
	
	private static PriorityBlockingQueue<Message> _queue;
	
	private OutputStream _os;
	
	private volatile PushAgent.STATE _state;
	
	private Message _msg;
	
	private final static int _default_timeout = 30000;
	
	private long _interval;
	
	private IConnStateChangeListener _changeListener;
	
	static{
		_instance = new OutgoingMessageAgent();
		_queue = new PriorityBlockingQueue<Message>();
	}
	
	private OutgoingMessageAgent(){
		setName("OutgoingMessageAgent");
	}
	
	public static OutgoingMessageAgent getInstance(){
		
		if(null == _instance){
			synchronized(OutgoingMessageAgent.class){
				if(null == _instance){
					_instance = new OutgoingMessageAgent();
				}
			}
		}
		
		return _instance;
		
	}
	
	public void startAgent() {
		
		if (!_instance.isAlive()) {
			_instance.start();
			if (Log.DBG) {
				Log.d(TAG, "Instance OutgoingMessageAgent & start agent thread");
			}
		}
		else{
			if(Log.DBG){
				Log.d(TAG, "OutgoingMessageAgent is already running");
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
		synchronized(_instance){
			_instance.notify();
		}
		send(new ExitMessage());
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e1) {
//		}
		accept();
//		interrupt();
		try {
			if(null != _os){
				_os.close();
			}
		} catch (IOException e) {
		}
//		synchronized(_instance){
//			_instance.notify();
//			accept();
//		}
		_os = null;
		_instance = null;
		
		if(Log.DBG){
			Log.d(TAG, "After stop agent");
		}
		
	}
	
	public void restartAgent(){
		
		if(Log.DBG){
			Log.d(TAG, "Before restart agent");
		}
		
		_state = PushAgent.STATE.RESTART;
		synchronized(_instance){
			_instance.notify();
		}
		send(new ExitMessage());
		accept();
//		interrupt();
		try {
			if(null != _os){
				_os.close();
			}
		} catch (IOException e) {
		}
//		synchronized(_instance){
//			_instance.notify();
//		}
		_os = null;
		
		if(Log.DBG){
			Log.d(TAG, "After restart agent");
		}
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);
		
		if (Log.DBG) {
			Log.d(TAG, "OutgoingMessageAgent is running now");
		}
		
		while(!(PushAgent.STATE.STOP == _state)){
			
			// Waiting for set OutputStream
			if(null != _changeListener){
				_changeListener.onStateChange(STATE.CONNECTING);
			}
			
			if(Log.DBG){
				Log.i(TAG, "Begin waiting for OutputStream");
			}
			
			synchronized(_instance){
				try {
					_instance.wait();
				} catch (InterruptedException e) {
				}
			}
			
			if (Log.DBG) {
				Log.w(TAG, "Just get the OutputStream");
			}
			
			if(null != _changeListener){
				_changeListener.onStateChange(STATE.CONNECTED);
			}
			
			_state = PushAgent.STATE.RUNNING;
			
			while(!(PushAgent.STATE.STOP == _state)){
				
				try {
					if(Log.DBG){
						Log.d(TAG, "Begin waiting for message");
					}
					_msg = _queue.take();
					if(Log.DBG){
						Log.d(TAG, "After waiting for message");
					}
				} catch (InterruptedException e1) {
				}
				
				if(null != _msg){
					
					if(_msg instanceof ExitMessage){
						if(PushAgent.STATE.RUNNING != _state){
							if(Log.DBG){
								Log.d(TAG, "Just received exit message begin exit");
							}
							break;
						}
						else{
							continue;
						}
					}
					
					try {
						if(Log.DBG){
							Log.d(TAG, "Begin send message:\n" + _msg);
						}
						if(null != _os){
							_os.write(_msg.getBytes());
						}
						else{
							_queue.put(_msg);
							break;
						}
					} catch (IOException e) {
						
						if(Log.DBG){
							Log.d(TAG, "During send IOException occur: " + e);
						}
						
						// Just put current unsent message into the queue
						_queue.put(_msg);
						_msg = null;
						
						if(null != _changeListener){
							_changeListener.onStateChange(STATE.DISCONNECTED);
						}
						
						restart();
						break;
						
					}
				}
				
				synchronized(_queue){
					try {
						if(Log.DBG){
							Log.i(TAG, "Begin waiting for response");
						}
						_interval = System.currentTimeMillis();
						_queue.wait(_default_timeout);
						_interval = System.currentTimeMillis() - _interval;
						if(_interval >= _default_timeout){
							if(Log.DBG){
								Log.d(TAG, "During 30 seconds can't receive response from server just restart");
							}
							// Just put current unsent message into the queue
							_queue.put(_msg);
							restart();
						}
						
						// In case missing message just add this protector on in running state received the accepted signal is valid
						if(PushAgent.STATE.RUNNING != _state){
							_queue.put(_msg);
						}
						
						if(Log.DBG){
							Log.i(TAG, "After waiting for response, interval: " + _interval);
						}
					} catch (InterruptedException e) {
					}
				}
				
				_msg = null;
			}
			
		}
		
		if(Log.DBG){
			Log.w(TAG, "OutgoingMessageAgent stopped");
		}
		
	}
	
	private void restart(){
		
		if((PushAgent.STATE.RUNNING == _state)){
			if(Log.DBG){
				Log.d(TAG, "Just send RESTART command");
			}
			
			MessageUtil.setStatus(MessageUtil.STATUS.EXCEPTION.toString());
			PushController.sendCmd(Command.COMMAND.RESTART);
		}
		else{
			if(Log.DBG){
				Log.d(TAG, "OutgoingMessageAgent is stop running now");
			}
		}
		
	}
	
	public synchronized void send(Message msg){
		if(isExistsReg(msg)){
			return;
		}
		_queue.put(msg);
	}
	
	private boolean isExistsReg(Message msg){
		
		boolean isExists = false;
		if(msg instanceof RegMessage){
			if(_queue.contains(msg)){
				isExists = true;
			}
		}
		return isExists;
		
	}
	
	public void setOutputStream(OutputStream os){
		
		synchronized(_instance){
			if(Log.DBG){
				Log.d(TAG, "Before set OutputStream");
			}
			_os = os;
			_instance.notify();
			if(Log.DBG){
				Log.d(TAG, "After set OutputStream");
			}
		}
		
	}
	
	public void accept(){
		synchronized(_queue){
			_queue.notify();
		}
	}
	
	public void setConnStateChangeListener(IConnStateChangeListener listener){
		
		_changeListener = listener;
		
	}

}
