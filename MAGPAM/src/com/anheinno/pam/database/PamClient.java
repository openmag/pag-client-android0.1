/**
 *  PamClient.java
 * 
 *  @Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.database;

import com.anheinno.pam.database.table.PamClientColumns;
import com.anheinno.pam.libs.util.Log;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * @author wangxun
 * @date 2011-08-04
 * @description Utility for maintain PAM client
 */
public final class PamClient extends Contents {
	
	public final static String TAG = PamClient.class.getName();
	
	private PamClient(){};
	
	public enum STATUS{
		REGISTER{
			@Override
			public String toString(){
				return "REGISTER";
			}
		},
		UNREGISTER{
			@Override
			public String toString(){
				return "UNREGISTER";
			}
		}
	}
	
	public static void addClient(String appid, String category, STATUS status){
		
		long n = 0;
		ContentValues values = new ContentValues();
		values.put(PamClientColumns.APPID, appid);
		values.put(PamClientColumns.CATEGORY, category);
		values.put(PamClientColumns.STATUS, status.toString());
		
		n = update(PamDatabaseHelper.TABLE_CLIENT, values, PamClientColumns.APPID + "=?", new String[]{appid});
		if(0 == n){
			if(Log.DBG){
				Log.d(TAG, "Just insert one client with appid: " + appid + " category: " + category + " status: " + status);
			}
			insert(PamDatabaseHelper.TABLE_CLIENT, values);
		}
		else{
			if(Log.DBG){
				Log.d(TAG, "Client already exist just upate it with appid: " + appid + " category: " + category + " status: " + status);
			}
		}
		
	}
	
	public static void delClient(String appid){
		delete(PamDatabaseHelper.TABLE_CLIENT, PamClientColumns.APPID + "=?", new String[]{appid});
	}
	
	public static void delAllClient(){
		delete(PamDatabaseHelper.TABLE_CLIENT, null, null);
	}
	
	public static String getCategory(String appid){
		
		String category = null;
		Cursor c = null;
		
		c = query(PamDatabaseHelper.TABLE_CLIENT, null, PamClientColumns.APPID + "=?", new String[]{appid}, null);
		if(null != c && c.moveToNext()){
			category = c.getString(c.getColumnIndex(PamClientColumns.CATEGORY));
		}
		if(null != c){
			c.close();
		}
		
		if(Log.DBG){
			Log.d(TAG, "current appid: " + appid + " category: " + category);
		}
		
		return category;
		
	}
	
}
