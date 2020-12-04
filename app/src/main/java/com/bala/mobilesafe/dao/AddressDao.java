package com.bala.mobilesafe.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bala.mobilesafe.util.LogUtil;

import java.io.File;


/**
 * 号码归属地查询
 * @author developer
 *
 */
public class AddressDao {
	
	private static final String TAG = "AddressDao";

	public static String getNumberLocation(Context context, String number){
		String location = "未知地区";
		//查询address.db
		//1.获取数据库文件
		File file = new File(context.getFilesDir(), "address.db");
		
		boolean isPhone = number.matches("^1[3578]\\d{9}$");
		
		if(isPhone){//是手机号
			LogUtil.d(TAG, "手机号码");
			//截取字符串
			String prefix = number.substring(0, 7);//前7位
			//2.db对象
			SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
			if(db != null){
				String sql = "select cardtype from info where mobileprefix = ?";
				String[] selectionArgs = new String[]{prefix};//查询条件
				Cursor cursor = db.rawQuery(sql, selectionArgs);
				if(cursor != null){
					if(cursor.moveToNext()){
						location = cursor.getString(0);
					}
					cursor.close();
				}
				db.close();
			}
		}else{
			LogUtil.d(TAG, "不是手机号码");
			
			switch (number.length()) {
			case 3://110,120,119
				location = "报警电话";
				break;
			case 4://5554，模拟器&情亲号码
				location = "模拟器&亲亲号码";
				break;
			case 5://95555,银行卡
				location = "银行客服";
				break;
				
			case 7:
			case 8: //8123456,81234567
				location = "本地电话";
				break;
			case 10: //0108123456, 0989-81234567,0755-81234567
			case 11:
			case 12:
				//查询区号3、4位
				String prefix = number.substring(0, 3);
				SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
				if(db != null){
					String sql = "select city from info where area = ?";
					String[] selectionArgs = new String[]{prefix};//查询条件
					Cursor cursor = db.rawQuery(sql, selectionArgs);
					if(cursor != null){
						if(cursor.moveToNext()){
							location = cursor.getString(0);
						}
						cursor.close();
					}
					db.close();
					
				}
				
				//查询4位
				if("未知地区".equals(location)){//继续查询4位区号
					prefix = number.substring(0, 4);
					db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
					if(db != null){
						String sql = "select city from info where area = ?";
						String[] selectionArgs = new String[]{prefix};//查询条件
						Cursor cursor = db.rawQuery(sql, selectionArgs);
						if(cursor != null){
							if(cursor.moveToNext()){
								location = cursor.getString(0);
							}
							cursor.close();
						}
						db.close();
						
					}
				}
				break;
			}
		}
		
		
		return location;
	}

}
