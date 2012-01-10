/**
 * PushAgent.java
 * 
 * @Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.network;

import java.io.IOException;

import android.content.Intent;

import com.anheinno.pam.PamActivity;
import com.anheinno.pam.libs.application.Application;
import com.anheinno.pam.libs.message.util.IMessageListener;
import com.anheinno.pam.libs.util.Log;
import com.anheinno.pam.network.impl.IncomingMessageConnStateChangeListener;
import com.anheinno.pam.network.impl.OutgoingMessageConnStateChangeListener;
import com.anheinno.pam.receiver.PushController;
import com.anheinno.pam.receiver.PushController.Command.COMMAND;
import com.anheinno.pam.service.PamService;
import com.anheinno.pam.timer.ITimer.TYPE;
import com.anheinno.pam.timer.impl.TimerFactory;
import com.anheinno.pam.util.Util;

/**
 * @author wangxun
 * @date 2011-08-01
 * @description PushAgent for handle 
 */
public final class PushAgent {
	
	private final String TAG = PushAgent.class.getName();
//	private final String HOST = "58.68.150.170";
	private final String HOST = "58.68.150.170";
	private final int PORT = 12333;
	private ISocket _socket;
	
	private static PushAgent _instance;
	private static IMessageListener _msgListener;
	private static IConnStateChangeListener _incomingChangeListener;
	private static IConnStateChangeListener _outgoingChangeListener;
	private final int MAX_COUNT = 3;
	private static int _nCount;
	
	static{
		_instance = new PushAgent();
		_msgListener = new com.anheinno.pam.message.impl.MessageListener();
		_incomingChangeListener = new IncomingMessageConnStateChangeListener();
		_outgoingChangeListener = new OutgoingMessageConnStateChangeListener();
		_nCount = 1;
	}
	
	private PushAgent(){}
	
	public static PushAgent getInstance(){
		return _instance;
	}
	
	public synchronized void startAgent(){
		
		if(Log.DBG){
			Log.d(TAG, "Begin start PushAgent");
		}
		
		clear();
		
		IncomingMessageAgent.getInstance().startAgent();
		IncomingMessageAgent.getInstance().setMessageListener(_msgListener);
		IncomingMessageAgent.getInstance().setConnStateChangeListener(_incomingChangeListener);
		OutgoingMessageAgent.getInstance().startAgent();
		OutgoingMessageAgent.getInstance().setConnStateChangeListener(_outgoingChangeListener);
		
		connect();
		
		setIOAgent();
		
	}
	
	public synchronized void restart(){
		
		if(Log.DBG){
			Log.d(TAG, "Begin restart PushAgent");
		}
		
	
		IncomingMessageAgent.getInstance().setFlag(STATE.RESTART);
		OutgoingMessageAgent.getInstance().setFlag(STATE.RESTART);
		clear();
		IncomingMessageAgent.getInstance().restartAgent();
		OutgoingMessageAgent.getInstance().restartAgent();
		
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//		}
		
		IncomingMessageAgent.getInstance().startAgent();
		IncomingMessageAgent.getInstance().setMessageListener(_msgListener);
		IncomingMessageAgent.getInstance().setConnStateChangeListener(_incomingChangeListener);
		OutgoingMessageAgent.getInstance().startAgent();
		OutgoingMessageAgent.getInstance().setConnStateChangeListener(_outgoingChangeListener);
		
		connect();
		
		setIOAgent();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		
		if(Log.DBG){
			Log.d(TAG, "End restart PushAgent");
		}
		
	}
	
	public synchronized void stop(){
		
		if(Log.DBG){
			Log.d(TAG, "Begin stop PushAgent");
		}
		IncomingMessageAgent.getInstance().setFlag(STATE.STOP);
		OutgoingMessageAgent.getInstance().setFlag(STATE.STOP);
		clear();
		
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//		}
		
		IncomingMessageAgent.getInstance().stopAgent();
		OutgoingMessageAgent.getInstance().stopAgent();
		
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//		}
		
		if(Log.DBG){
			Log.d(TAG, "End stop PushAgent");
		}
		
	}
	
	public synchronized void pause(){
		
		if(Log.DBG){
			Log.d(TAG, "Begin pause PushAgent");
		}
		clear();
		
	}
	
	private void connect(){
		try {
			
//			// The first protect to avoid the network exception interrupt the timer
//			setHeartBeaterMoniter();

			beginMoniter();
			if(Log.DBG){
				Log.d(TAG, "Begin connect to host: " + Util.getHost() + " port: " + Util.getPort());
			}
			_socket = SocketFactory.create(ISocket.TYPE.ANDROID);
			_socket.Socket(Util.getHost(), Util.getPort());
			_socket.setKeepAlive(true);
			_socket.setSoTimeout(0);
			_nCount = 1;
			endMoniter();
			setHeartBeaterMoniter();
			
			if(Log.DBG){
				Log.d(TAG, "End connect");
			}
			
		}catch (Throwable t) {
			if(Log.DBG){
				Log.d(TAG, "During connect exception occur: " + t.getLocalizedMessage() + " count: " + _nCount);
			}
			clear();
		}
	}
	
	private void beginMoniter(){
		TimerFactory.create(TYPE.ANDROID).set(60 * _nCount, PushController.createCmd(COMMAND.RESTART));
		if(_nCount < MAX_COUNT){
			++_nCount;
		}
	}
	
	private void endMoniter(){
		TimerFactory.create(TYPE.ANDROID).cancel(PushController.createCmd(COMMAND.RESTART));
		_nCount = 1;
	}
	
	private void setHeartBeaterMoniter(){
		String str = Util.getInterval();
		TimerFactory.create(TYPE.ANDROID).set(Integer.parseInt(str), PushController.createCmd(COMMAND.CHOKE));
	}
	
	private void setIOAgent(){
		
		try {
			if(null != _socket){
				IncomingMessageAgent.getInstance().setInputStream(_socket.getInputStream());
			}
		} catch (IOException e) {
			if(Log.DBG){
				Log.d(TAG, "During setInputStream IOException occur: " + e.getLocalizedMessage());
			}
		}
		
		try {
			if(null != _socket){
				OutgoingMessageAgent.getInstance().setOutputStream(_socket.getOutputStream());
			}
		} catch (IOException e) {
			if(Log.DBG){
				Log.d(TAG, "During setOutputStream IOException occur: " + e.getLocalizedMessage());
			}
		}
		
	}
	
	private void clear(){
		
		if(null != _socket){
			try {
				_socket.shutdownInput();
			} catch (IOException e) {
			}
		}
		
		if(null != _socket){
			try {
				_socket.shutdownOutput();
			} catch (IOException e) {
			}
		}
		
		if(null != _socket){
			
			try {
				_socket.close();
			} catch (IOException e) {
			}
			
			_socket = null;
			
		}
	}
	
	public boolean isRunning(){
		return (PamService.isRunning); 
//				&& (STATE.RUNNING == IncomingMessageAgent.getInstance().getFlag()) && (STATE.RUNNING == OutgoingMessageAgent.getInstance().getFlag()));
	}
	
	private void checkService(){
		
		if(!PamService.isRunning){
			Intent i = new Intent();
			i.setClass(Application.CONTEXT, PamService.class);
			Application.CONTEXT.startService(i);
		}
		
	}
	
	public enum STATE{
		STOP{
			@Override
			public String toString(){
				return "STOP";
			}
		},
		RESTART{
			@Override
			public String toString(){
				return "RESTART";
			}
		},
		RUNNING{
			@Override
			public String toString(){
				return "RUNNING";
			}
		}
	}
	
}
