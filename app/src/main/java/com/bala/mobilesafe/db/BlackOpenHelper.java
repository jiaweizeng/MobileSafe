package com.bala.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackOpenHelper extends SQLiteOpenHelper {

	public BlackOpenHelper(Context context) {
		super(context, BlackDB.NAME, null, BlackDB.VERSION);
	}

	// 创建
	@Override
	public void onCreate(SQLiteDatabase db) {
		/*String sql = "CREATE TABLE " + BlackDB.BlackTable.TABLE_NAME + "("
				+ BlackDB.BlackTable.COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ BlackDB.BlackTable.COLUMN_PHONE
				+ " TEXT UNIQUE, "+BlackDB.BlackTable.COLUMN_TYPE+" INTEGER)";*/
		
		db.execSQL(BlackDB.BlackTable.SQL_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
