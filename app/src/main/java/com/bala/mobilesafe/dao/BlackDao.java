package com.bala.mobilesafe.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bala.mobilesafe.bean.BlackBean;
import com.bala.mobilesafe.db.BlackDB;
import com.bala.mobilesafe.db.BlackOpenHelper;
import com.bala.mobilesafe.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class BlackDao {
	private static final String TAG = "BlackDao";
	private BlackOpenHelper helper;

	public BlackDao(BlackOpenHelper helper) {
		this.helper = helper;
	}

	/**
	 * 插入
	 * 
	 * @param phone
	 * @param type
	 * @return
	 */
	public boolean insert(String phone, int type) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("phone", phone);
		values.put("type", type);
		long insert = db.insert("black", null, values);
		db.close();
		return insert != -1;
	}

	public boolean update(String phone, int type) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		// values.put(BlackDB.BlackTable.COLUMN_PHONE, phone);
		values.put(BlackDB.BlackTable.COLUMN_TYPE, type);

		String whereClause = BlackDB.BlackTable.COLUMN_PHONE + "=?";// 条件
		String[] whereArgs = new String[] { phone };// 条件对应参数

		int update = db.update(BlackDB.BlackTable.TABLE_NAME, values,
				whereClause, whereArgs);
		db.close();
		return update > 0;
	}

	public List<BlackBean> getAllBlack() {
		List<BlackBean> list = new ArrayList<BlackBean>();
		// 遍历查询
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql = "select " + BlackDB.BlackTable.COLUMN_PHONE + ","
				+ BlackDB.BlackTable.COLUMN_TYPE + " from "
				+ BlackDB.BlackTable.TABLE_NAME;

		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				/*
				 * String phone =
				 * cursor.getString(cursor.getColumnIndex(BlackDB.
				 * BlackTable.COLUMN_PHONE)); int type =
				 * cursor.getInt(cursor.getColumnIndex
				 * (BlackDB.BlackTable.COLUMN_TYPE));
				 */

				String phone = cursor.getString(0);
				int type = cursor.getInt(1);

				BlackBean bean = new BlackBean();
				bean.phone = phone;
				bean.type = type;
				list.add(bean);

			}
			cursor.close();
		}
		db.close();
		return list;
	}

	public boolean delete(String phone) {
		SQLiteDatabase db = helper.getWritableDatabase();
		String whereClause = BlackDB.BlackTable.COLUMN_PHONE + "=?";
		String[] whereArgs = new String[] { phone };
		int delete = db.delete(BlackDB.BlackTable.TABLE_NAME, whereClause,
				whereArgs);
		db.close();
		return delete > 0;
	}

	public int getType(String phone) {
		int type = -1;
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql = "select " + BlackDB.BlackTable.COLUMN_TYPE + " from "
				+ BlackDB.BlackTable.TABLE_NAME + " where "
				+ BlackDB.BlackTable.COLUMN_PHONE + "=?";
		Cursor cursor = db.rawQuery(sql, new String[] { phone });
		if (cursor != null) {
			if (cursor.moveToNext()) {
				type = cursor.getInt(0);
			}
			cursor.close();
		}
		db.close();
		return type;
	}

	public List<BlackBean> findPart(int pageSize, int offset) {
		List<BlackBean> datas = new ArrayList<BlackBean>();
		// 分页查询
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql = "select " + BlackDB.BlackTable.COLUMN_PHONE + ", "
				+ BlackDB.BlackTable.COLUMN_TYPE + " from "
				+ BlackDB.BlackTable.TABLE_NAME + " limit " + pageSize
				+ " offset " + offset;
		LogUtil.d(TAG, "pageSize="+pageSize+",offset="+offset);
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String phone = cursor.getString(0);
				int type = cursor.getInt(1);
				
				BlackBean bean = new BlackBean();
				bean.phone =phone;
				bean.type = type;
				datas.add(bean);
			}
			cursor.close();
		}
		db.close();
		return datas;
	}

}
