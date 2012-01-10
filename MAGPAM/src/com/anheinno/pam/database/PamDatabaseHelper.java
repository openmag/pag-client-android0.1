/**
 *  PamDatabaseHelper.java
 * 
 *  @Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.database;

import com.anheinno.pam.database.table.PamClientColumns;
import com.anheinno.pam.libs.application.Application;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author wangxun
 * @date 2011-07-28
 * @description Database helper
 */
public class PamDatabaseHelper extends SQLiteOpenHelper {

	/**
	 * Database name
	 */
	private static String DATABASE_NAME = "pam.db";

	/**
	 * Database version
	 */
	private static final int DATABASE_VERSION = 1;

	/**
	 * client table which contains client relevant information, for example:
	 * appid, category, etc...
	 */
	public static final String TABLE_CLIENT = "client";

	private static final String SQL_CREATE_CLIENT_TABLE = "CREATE TABLE "
			+ TABLE_CLIENT + " (" + PamClientColumns._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + PamClientColumns.APPID
			+ " TEXT," + PamClientColumns.CATEGORY + " TEXT,"
			+ PamClientColumns.STATUS + " TEXT);";

	private static PamDatabaseHelper _instance;

	static {
		_instance = new PamDatabaseHelper(Application.CONTEXT);
	}

	public PamDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	private void createTables(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_CLIENT_TABLE);
	}

	public static SQLiteOpenHelper getInstance() {
		return _instance;
	}

}
