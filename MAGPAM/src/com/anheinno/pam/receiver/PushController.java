/**
 * PushController.java
 * 
 * @Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.receiver;

import java.io.Serializable;

import com.anheinno.pam.libs.application.Application;
import com.anheinno.pam.libs.message.util.MessageUtil;
import com.anheinno.pam.libs.util.Log;
import com.anheinno.pam.network.PushAgent;
import com.anheinno.pam.receiver.PushController.Command.COMMAND;
import com.anheinno.pam.service.PamService;
import com.anheinno.pam.util.Util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author wangxun
 * @date 2011-08-01
 * @description PushAgent's controller
 */
public final class PushController extends BroadcastReceiver {
	
	private final String TAG = PushController.class.getName();
	
	private static COMMAND _cmd;
	
	public synchronized static void setCmd(COMMAND _cmd2){
		_cmd = _cmd2;
	}
	
	public synchronized static COMMAND getCmd(){
		return _cmd;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Command cmd = (Command) intent.getSerializableExtra(Command.CMD);
		if(Log.DBG){
			Log.d(TAG, "Just received cmd: " + cmd);
		}
		
		COMMAND previousCmd = null;
		previousCmd = getCmd();
		if(null != previousCmd && previousCmd == cmd.getCmd()){
			if(Log.DBG){
				Log.d(TAG, "The previous command equal current command and is execute now just ignone current command");
			}
			return;
		}
		
		cmd.execute();
		
	}
	
	public static void sendCmd(COMMAND cmd){
		Intent i = new Intent();
		i.putExtra(Command.CMD, new Command(cmd));
		i.setClass(Application.CONTEXT, PushController.class);
		Application.CONTEXT.sendBroadcast(i);
	}
	
	public static PendingIntent createCmd(COMMAND cmd){
		
		Intent i = new Intent();
		i.putExtra(Command.CMD, new Command(cmd));
		i.setClass(Application.CONTEXT, PushController.class);
		return PendingIntent.getBroadcast(Application.CONTEXT, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
		
	}
	
	public static class Command extends Thread implements Serializable {
		
		private final String TAG = Command.class.getName();
		/**
		 * Serial id for Command
		 */
		private static final long serialVersionUID = -3976885105079859425L;
		
		private COMMAND _cmd;
		
		public static final String CMD = "com.anheinno.pam.receiver.cmd";
		
		public static final String STATUS = "com.anheinno.pam.receiver.status";
		
		public enum COMMAND{
			START{
				@Override
				public String toString(){
					return "START";
				}
			},
			STOP{
				@Override
				public String toString(){
					return "STOP";
				}
			},
			PAUSE{
				@Override
				public String toString(){
					return "PAUSE";
				}
			},
			RESTART{
				@Override
				public String toString(){
					return "RESTART";
				}
			},
			CHOKE{
				@Override
				public String toString(){
					return "CHOKE";
				}
			}
		}

		public Command(COMMAND cmd) {
			_cmd = cmd;
		}

		public COMMAND getCmd(){
			return _cmd;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			
			if(!Util.checkNetworkState()){
				if(_cmd == COMMAND.START || _cmd == COMMAND.RESTART || _cmd == COMMAND.CHOKE){
					if(Log.DBG){
						Log.d(TAG, "Current network isn't connected, just ignore the command");
					}
					return;
				}
			}
			
//			if(COMMAND.STOP != _cmd && !PamService.isRunning){
//				if(Log.DBG){
//					Log.d(TAG, "Current service isn't running, just start service");
//				}
//				Intent i = new Intent();
//				i.setClass(Application.CONTEXT, PamService.class);
//				Application.CONTEXT.startService(i);
//				return;
//			}
			
			setCmd(_cmd);
			
			switch (_cmd) {
			case START:
				PushAgent.getInstance().startAgent();
				break;
			case STOP:
				PushAgent.getInstance().stop();
				break;
			case PAUSE:
				PushAgent.getInstance().pause();
				break;
			case CHOKE:
				MessageUtil.setStatus(MessageUtil.STATUS.CHOKE.toString());
			case RESTART:
				PushAgent.getInstance().restart();
				break;
			}
			
			setCmd(null);
			
		}
		
		public void execute(){
			start();
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#toString()
		 */
		@Override
		public String toString() {
			return _cmd.toString();
		}
		
	}
	
}
