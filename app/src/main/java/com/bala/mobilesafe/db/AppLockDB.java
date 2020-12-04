package com.bala.mobilesafe.db;

public interface AppLockDB {
	/** 数据库名字 */
	String NAME = "applock.db";

	int VERSION = 1;
	
	public interface AppLockTable {
		String TABLE_NAME = "applock";
		// 列
		String COLUMN_ID = "id";
		
		String COLUMN_PACKAGENAME = "packagename";
		
		
		String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_PACKAGENAME
				+ " VARCHAR UNIQUE)";
	}
}
