/**
 * PamActivity.java
 * 
 * @Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.PriorityBlockingQueue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anheinno.pam.libs.application.Application;
import com.anheinno.pam.libs.message.Message;
import com.anheinno.pam.libs.message.Message.TYPE;
import com.anheinno.pam.libs.message.MessageFactory;
import com.anheinno.pam.libs.message.impl.NotiMessage;
import com.anheinno.pam.libs.message.util.MessageUtil;
import com.anheinno.pam.libs.util.Log;
import com.anheinno.pam.network.OutgoingMessageAgent;
import com.anheinno.pam.network.PushAgent;
import com.anheinno.pam.receiver.PushController;
import com.anheinno.pam.receiver.PushController.Command.COMMAND;
import com.anheinno.pam.service.PamService;
import com.anheinno.pam.util.Util;

/**
 * @author wangxun
 * @date 2011-07-27
 * @description test stub PAM doesn't need any activity when release need remove this class in AndroidManifest.xml
 */
public class PamActivity extends Activity {
	
	@SuppressWarnings("unused")
	private final String TAG = PamActivity.class.getName();
	@SuppressWarnings("unused")
	private EditText _edit;
	private Button _btnSend;
	private Button _btnStrat;
	private Button _btnStop;
	private Button _btnRestart;
	private Button _btnSet;
	private EditText _editHost;
	private EditText _editPort;
	private TextView _txtUUID;
	private Button _btnStatus;
	
	private static PriorityBlockingQueue<Message> _queue = new PriorityBlockingQueue<Message>();
	
	private static int i = 10;
	
	/** Called when the activity is first created. */
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
	    super.onCreate(savedInstanceState);
	    this.setContentView(R.layout.pam);
	    
//	    Intent intent = new Intent();
//	    intent.setClass(PamActivity.this, PamService.class);
//		PamActivity.this.startService(intent);
	    
	    Intent intent = getIntent();
	    
	    _txtUUID = (TextView) findViewById(R.id.uuid);
	    _txtUUID.setText("UUID: " + MessageUtil.getID());
	    
	    _edit = (EditText) this.findViewById(R.id.content);
	    
	    _btnSend = (Button) this.findViewById(R.id.send);
	    _btnSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				NotiMessage noti = (NotiMessage) MessageFactory.create(TYPE.NOTI);
				noti.setAppId("12345");//new DeviceUuidFactory(Application.CONTEXT).getDeviceUuid().toString());
				String msg = _edit.getText().toString();
				try {
					noti.setContent(msg.getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
				}
				_edit.setText("");
				OutgoingMessageAgent.getInstance().send(noti);
				for(int i=0; i<1000; ++i){
					Log.d(TAG, "id: " + MessageUtil.getID());
				}
			}
		});
	    
	    _btnStrat = (Button) findViewById(R.id.start);
	    _btnStrat.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PushController.sendCmd(COMMAND.START);
				AnimationSet animationSet = new AnimationSet(true);   
                TranslateAnimation translateAnimation = new TranslateAnimation(   
                        Animation.RELATIVE_TO_SELF, 1,   
                        Animation.RELATIVE_TO_SELF,0,   
                        Animation.RELATIVE_TO_SELF,1,   
                        Animation.RELATIVE_TO_SELF,0);   
                translateAnimation.setDuration(1000);   
                animationSet.addAnimation(translateAnimation);   


	                _btnStrat.startAnimation(animationSet);
				Intent i = new Intent();
				i.setClass(PamActivity.this, PamService.class);
				PamActivity.this.startService(i);
			}
		});
	    
	    _btnStop = (Button) findViewById(R.id.stop);
	    _btnStop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(PamActivity.this, PamService.class);
				PamActivity.this.stopService(i);
			}
		});
	    
	    _btnRestart = (Button) findViewById(R.id.restart);
	    _btnRestart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				for(int i=0; i<10; ++i)
				PushController.sendCmd(COMMAND.RESTART);
			}
		});
	    
	    _editHost = (EditText) findViewById(R.id.host);
	    
	    _editPort = (EditText) findViewById(R.id.port);
	    
	    _btnSet = (Button) findViewById(R.id.set);
	    _btnSet.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String host = null;
				String port = null;
				host = _editHost.getText().toString();
				port = _editPort.getText().toString();
				if(!TextUtils.isEmpty(host) && !TextUtils.isEmpty(port)){
					Util.setHost(host);
					Util.setPort(Integer.parseInt(port));
				}
			}
		});
	    
	    _btnStatus = (Button) findViewById(R.id.status);
	    _btnStatus.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				if(PushAgent.getInstance().isRunning()){
					Toast.makeText(Application.CONTEXT, "Push is running now!", Toast.LENGTH_SHORT).show();
				}
				else{
					Toast.makeText(Application.CONTEXT, "Push isn't running now!", Toast.LENGTH_SHORT).show();
				}
			}
	    	
	    });
	}

}
