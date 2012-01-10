/**
 * 	PamClientActivity.java
 * 
 * 	@Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.client;


import java.io.UnsupportedEncodingException;

import com.anheinno.pam.libs.message.MessageFactory;
import com.anheinno.pam.libs.message.Message.TYPE;
import com.anheinno.pam.libs.message.impl.NotiMessage;
import com.anheinno.pam.libs.util.Log;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * @author wangxun
 * @date 2011-07-27
 * @description Pam client activity simulate the push client
 */
public class PamClientActivity extends Activity {
	
	private Button _btnSend;
	private EditText _edtContent;
	private EditText _edtId;
	private static TextView _tvContent;
	private static StringBuilder _sb;
	public static Handler _Handler;
	private static ScrollView _scroll;
	
	static{
		_sb = new StringBuilder(); 
		_Handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				Bundle b = msg.getData();
				_sb.append(">");
				_sb.append(b.getString("content"));
				_sb.append("\n");
				if(null != _tvContent){
					_tvContent.setText(_sb.toString());
				}
				if(null != _scroll){
					_scroll.fullScroll(View.FOCUS_DOWN);
				}
			}
			
		};
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        _edtContent = (EditText) findViewById(R.id.content);
        
        _edtId = (EditText) findViewById(R.id.id);
        
        _btnSend = (Button) findViewById(R.id.send);
        _btnSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent();
////				intent.setClassName("com.hanvon.ocr.bizcard", "MainActivity");
//				intent.setComponent(new ComponentName("com.hanvon.ocr.bizcard", "MainActivity"));
				
//				final String strPackage = "com.intsig.BizCardReader";
				
//				final String strPackage = "com.hanvon.ocr.bizcard";
//				PackageManager pm = null;
//				PackageInfo pi = null;
//				ActivityInfo ai = null;
//				Intent intent = null;
//				try {
//					pm = Application.CONTEXT.getPackageManager();
//					pi = pm.getPackageInfo(strPackage, PackageManager.GET_ACTIVITIES);
//					ai = pi.activities[0];
//					intent = new Intent();
//					intent.setComponent(new ComponentName(strPackage, ai.name));
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////					Application.CONTEXT.startActivity(intent);
//					PamClientActivity.this.startActivityForResult(intent, 110);
//				} catch (NameNotFoundException e) {
//				}
				
				send(_edtContent.getText().toString());
				_edtContent.setText("");
				_scroll.fullScroll(View.FOCUS_DOWN);
				Context context = null;
				try {
					context = createPackageContext("com.anheinno.pam", Context.CONTEXT_IGNORE_SECURITY);
				} catch (NameNotFoundException e) {
				}
				SharedPreferences _MSEQ_PREFERENCES = context.getSharedPreferences("com.anheinno.pam.mseq", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
				
				if(Log.DBG){
					long l = _MSEQ_PREFERENCES.getLong("com.anheinno.pam.mseq", -1);
					Log.d(Log.TAG, "PamClient _MSEQ_PREFERENCES: " + Long.toHexString(l));
					_MSEQ_PREFERENCES.edit().putLong("com.anheinno.pam.mseq", ++l).commit();
				}
				
			}
		});
        
        _tvContent = (TextView) findViewById(R.id.converstation);
        _tvContent.setText(_sb.toString());
        
        _scroll = (ScrollView) findViewById(R.id.scroll);
        _scroll.fullScroll(View.FOCUS_DOWN);
        
        if(Log.DBG){
        	Log.d(Log.TAG, "PamClientActivity onCreate");
        }
        
//        Plugin.register(MessageUtil.getID(), "com.anheinno.pam.client");
        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	if(Log.DBG){
    		Log.d(Log.TAG, "onActivityResult");
    	}
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void send(String msg){
    	Intent i = new Intent();
		i.setAction("com.anheinno.intent.action.SEND");
		NotiMessage noti = (NotiMessage) MessageFactory.create(TYPE.NOTI);
		noti.setAppId(_edtId.getText().toString());
		try {
			noti.setContent(msg.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
		}
		i.putExtra(com.anheinno.pam.libs.message.Message.MSG, noti);
		_sb.append("<");
		_sb.append(msg);
		_sb.append("\n");
		_tvContent.setText(_sb.toString());
		 _scroll.fullScroll(View.FOCUS_DOWN);
		PamClientActivity.this.sendBroadcast(i);
    }
    
}