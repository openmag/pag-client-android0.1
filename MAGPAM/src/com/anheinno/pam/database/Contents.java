/**
 * 	Contents.java
 * 
 * 	@Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.database;

import com.anheinno.pam.libs.application.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;

/**
 * @author wangxun
 * @date 2011-07-28
 * @description Helper class for access database
 */
public abstract class Contents {

	public static final String AUTH = "com.anhe_inno.pam";

	public static Context getContext() {
		return Application.CONTEXT;
	}

	public static Uri getTableUri(String tableName) {
		return Uri.parse("content://" + AUTH + "/" + tableName + "/");
	}

	public static Uri getRowUri(String tableName, long rowId) {
		return Uri.parse("content://" + AUTH + "/" + tableName + "/" + rowId);
	}

	public static Uri getSetUri(String tableName, long[] ids) {
		String s = null;
		for (long id : ids) {
			if (s == null) {
				s = String.valueOf(id);
			} else {
				s += "," + id;
			}
		}
		return Uri.parse("content://" + AUTH + "/" + tableName
				+ "/" + s);
	}

	public static Cursor query(String table, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy) {
		Cursor cursor = PamDatabaseHelper
				.getInstance()
				.getReadableDatabase()
				.query(table, columns, selection, selectionArgs, groupBy,
						having, orderBy);

		cursor.setNotificationUri(getContext().getContentResolver(),
				getTableUri(table));
		return cursor;
	}

	public static Cursor query(String table, String[] columns,
			String selection, String[] selectionArgs, String orderBy) {
		return query(table, columns, selection, selectionArgs, null, null,
				orderBy);
	}

	public static long insert(String table, ContentValues values) {
		long rowId = PamDatabaseHelper.getInstance().getWritableDatabase()
				.insert(table, "", values);

		getContext().getContentResolver()
				.notifyChange(getTableUri(table), null);
		return rowId;
	}

	public static long update(String table, ContentValues values,
			String whereClause, String[] whereArgs) {
		int affectedRows = PamDatabaseHelper.getInstance().getWritableDatabase()
				.update(table, values, whereClause, whereArgs);
		getContext().getContentResolver()
				.notifyChange(getTableUri(table), null);

		return affectedRows;
	}

	public static long delete(String table, String whereClause,
			String[] whereArgs) {
		int affectedRows = PamDatabaseHelper.getInstance().getWritableDatabase()
				.delete(table, whereClause, whereArgs);
		getContext().getContentResolver()
				.notifyChange(getTableUri(table), null);
		return affectedRows;
	}

	public static Cursor rawQuery(String sql, String[] selectionArgs) {
		return PamDatabaseHelper.getInstance().getReadableDatabase()
				.rawQuery(sql, selectionArgs);
	}

	public static void notifyChange(Uri uri, ContentObserver observer) {
		getContext().getContentResolver().notifyChange(uri, observer);
	}

	public static void execSQL(String sql, Object[] bindArgs) {
		PamDatabaseHelper.getInstance().getWritableDatabase()
				.execSQL(sql, bindArgs);
	}

	public static void execSQL(String sql) {
		PamDatabaseHelper.getInstance().getWritableDatabase().execSQL(sql);
	}

}
