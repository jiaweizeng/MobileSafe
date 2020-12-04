package com.bala.mobilesafe.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bala.mobilesafe.bean.ChildBean;
import com.bala.mobilesafe.bean.GroupBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class CommonNumberDao {
	
	public static List<GroupBean> getAllCommonNumber(Context context){
		List<GroupBean> datas = new ArrayList<GroupBean>();
		
		File file = new File(context.getFilesDir(), "commonnum.db");
		
		//读取常用号码数据
		SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
		if(db != null){
			String sql = "select name, idx from classlist";
			Cursor groupCursor = db.rawQuery(sql, null);
			if(groupCursor != null){
				while(groupCursor.moveToNext()){

					String title = groupCursor.getString(0);
					int idx = groupCursor.getInt(1);
					GroupBean groupBean = new GroupBean();
					groupBean.title = title;
					datas.add(groupBean);
					
					List<ChildBean> chiList = new ArrayList<ChildBean>();
					
					String childSql = "select name, number from table" + idx;
					Cursor childCursor = db.rawQuery(childSql , null);
					
					if(childCursor != null){
						while(childCursor.moveToNext()){
							String name = childCursor.getString(0);
							String number = childCursor.getString(1);
							ChildBean childBean = new ChildBean();
							childBean.name = name;
							childBean.number = number;
							chiList.add(childBean);
						}
						groupBean.childDatas = chiList;
						childCursor.close();
					}
					
				}
				groupCursor.close();
			}
			db.close();
		}
		
		return datas;
	}

}
