/**
 *  PamClientColumns.java
 * 
 *  @Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.database.table;

import android.provider.BaseColumns;

/**
 * @author wangxun
 * @date 2011-08-04
 * @description 
 */
public interface PamClientColumns extends BaseColumns {
	
	/**
	 * PAM client's application identify
	 * <P>
	 * Type: TEXT
	 * </P>
	 */
	public static final String APPID = "appid";
	
	/**
	 * PAM client's category
	 * <P>
	 * Type: TEXT
	 * </P>
	 */
	public static final String CATEGORY = "category";
	
	/**
	 * PAM client's status
	 * <P>
	 * Type: TEXT
	 * </P>
	 */
	public static final String STATUS = "status";
	
}
