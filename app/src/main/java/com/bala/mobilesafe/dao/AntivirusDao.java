package com.bala.mobilesafe.dao;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AntivirusDao {

	/**
	 * 判断某一个程序是否是病毒程序
	 * @param context
	 * @param md5
	 * @return true : 病毒程序 ，false:不是病毒程序
	 */
	public boolean isVirus(Context context,String md5){
		//打开一个已有的数据库.
		File file = new File( context.getFilesDir(), "antivirus.db");
		SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.query("datable", null, "md5=?", new String[]{md5}, null, null, null);
		boolean result = cursor.moveToNext();
		cursor.close();
		db.close();
		return result;
	}
	
	
	/**
	 * 添加记录
	 * @param context
	 * @param md5
	 * @return
	 */
	public long insert(Context context , String md5){
		File file = new File( context.getFilesDir(), "antivirus.db");
		SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
		ContentValues values = new ContentValues();
		values.put("md5", md5);
		values.put("type", "6");
		values.put("name", "Android.Adware.AirAD.a");
		values.put("desc", "恶意后台扣费,病毒木马程序");
		long result = db.insert("datable", null, values);
		db.close();
		return result;
	}
}
