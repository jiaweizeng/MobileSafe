package com.bala.mobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.bala.mobilesafe.db.AppLockDB;
import com.bala.mobilesafe.db.AppLockOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class AppLockDao {
	AppLockOpenHelper helper = null;
	Context mContext;
	public AppLockDao(Context context){
		mContext = context;
		helper =new AppLockOpenHelper(context);
	}
	
	/**
	 * 添加一个加锁程序
	 * @param packageName
	 * @return  增加的行号， 如果是-1表明添加失败，否则成功
	 */
	public long insert(  String packageName){
		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(AppLockDB.AppLockTable.COLUMN_PACKAGENAME, packageName);
		long result = database.insert(AppLockDB.AppLockTable.TABLE_NAME, null, values);
		database.close();
		
		
		//发出通知。
		Uri uri = Uri.parse("itheima://com.itheima.db.applock");
		
		//参数一：　指定发生数据改变的具体路径　，　参数二：　是否要告诉具体的某一个观察者，如果为null, 表明没有具体通知谁。
		mContext.getContentResolver().notifyChange(uri, null);
		
		
		return result;
	}
	
	/**
	 * 删除一个加锁程序
	 * @param packageName
	 * @return 影响的行数， 如果 》0 表明删除成功，否则失败
	 */
	public int  delete( String packageName){
		SQLiteDatabase database = helper.getWritableDatabase();
		
		int result = database.delete(AppLockDB.AppLockTable.TABLE_NAME, AppLockDB.AppLockTable.COLUMN_PACKAGENAME+"=?", new String[]{packageName});
		database.close();
		
		
		
		//发出通知。
		Uri uri = Uri.parse("itheima://com.itheima.db.applock");
		
		//参数一：　指定发生数据改变的具体路径　，　参数二：　是否要告诉具体的某一个观察者，如果为null, 表明没有具体通知谁。
		mContext.getContentResolver().notifyChange(uri, null);
		
		return result;
	}
	
	/**
	 * 查询所有已加锁的程序
	 * @return List<String>
	 */
	public List<String>  query(){
		List<String> list = new ArrayList<String>();
				
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.query(AppLockDB.AppLockTable.TABLE_NAME, new String[]{AppLockDB.AppLockTable.COLUMN_PACKAGENAME}, null, null, null, null, null);
		while(cursor.moveToNext()){
			String pacakgeName = cursor.getString(0);
			list.add(pacakgeName);
		}
		
		cursor.close();
		database.close();
		return list;
	}
}
