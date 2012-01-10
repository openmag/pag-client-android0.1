package com.anheinno.mdm;

import com.anheinno.android.libs.ui.UiApplication;
import com.anheinno.mdm.receiver.AnHeDeviceAdmin;
import com.anheinno.pam.libs.plugin.Plugin;
import com.anheinno.pam.libs.util.Log;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MDMActivity extends UiApplication {
	
	private static final String TAG = MDMActivity.class.getName();
	private static final int RESULT_ENABLE = 1;
	private Button _btn;
	private DevicePolicyManager _DPM;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        MAGDocumentConfig.setDefaultLoginURL(this, "http://119.161.134.220:20180/MAGLIBv0.3/magtest/services.php");
//        
//        MAGDocumentScreen screen = new MAGDocumentScreen(this);
//        MAGLoginScreenConfiguration login_conf = new MAGLoginScreenConfiguration(this, getString(R.string.login), 0.7, -1, -1, Color.BLUE,
//				Color.WHITE, Color.WHITE, "login.jpg", "login.jpg", true, true);
//        screen.setLoginConfig(login_conf);
//        screen.show();
        
        setContentView(R.layout.main);
        
        Plugin.register("mdm", "com.anheinno.mdm");
        
        _DPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        
        _btn = (Button) findViewById(R.id.admin);
        if(_DPM.isAdminActive(AnHeDeviceAdmin.ANHEDEVICEADMIN)){
        	_btn.setText(getString(R.string.disable));
        }
        else{
        	_btn.setText(getString(R.string.enable));
        }
        _btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(!_DPM.isAdminActive(AnHeDeviceAdmin.ANHEDEVICEADMIN)){
					// Launch the activity to have the user enable our admin.
	                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
	                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
	                		AnHeDeviceAdmin.ANHEDEVICEADMIN);
	                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
	                        getString(R.string.warning_info));
	                startActivityForResult(intent, RESULT_ENABLE);
	                _btn.setText(getString(R.string.disable));
				}
				else{
					_DPM.removeActiveAdmin(AnHeDeviceAdmin.ANHEDEVICEADMIN);
					_btn.setText(getString(R.string.enable));
				}
			}
        	
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	 switch (requestCode) {
         case RESULT_ENABLE:
             if (resultCode == Activity.RESULT_OK) {
            	 if(Log.DBG){
            		 Log.d(TAG, "Admin enabled!");
            	 }
            	 _btn.setText(getString(R.string.disable));
             } else {
            	 if(Log.DBG){
            		 Log.d(TAG, "Admin enable FAILED!");
            	 }
            	 _btn.setText(getString(R.string.enable));
             }
             return;
    	}	
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
}