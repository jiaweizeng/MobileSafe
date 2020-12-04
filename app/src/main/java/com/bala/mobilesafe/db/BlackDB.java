package com.bala.mobilesafe.db;

/**
 * 黑名单数据库
 * 
 * @author youliang.ji
 * 
 */
public interface BlackDB {
	/** 数据库名字 */
	String NAME = "black.db";

	int VERSION = 1;

	/**
	 * 数据表
	 */
	public interface BlackTable {
		String TABLE_NAME = "black";

		// 列
		String COLUMN_ID = "id";
		String COLUMN_PHONE = "phone";
		String COLUMN_TYPE = "type";

		String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_PHONE
				+ " TEXT UNIQUE, " + COLUMN_TYPE + " INTEGER)";
	}

}
