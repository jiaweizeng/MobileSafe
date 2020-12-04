package com.bala.mobilesafe.util;

import android.R.bool;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesUtils {
	
	private static final String NAME = "heima_preferences";
	public static final String KEY_UPDATE_STATUS = "update_status";//自动更新状态
	public static final String KEY_SJFD_PWD = "sjfd_pwd";//手机防盗密码
	public static final String KEY_SJFD_SIM = "sjfd_sim";//sim卡
	public static final String KEY_SJFD_SAFE_NUMBER = "sjfd_safe_number";//安全号码
	public static final String KEY_SJFD_SETUP_SUCCESS = "sjfd_setup_success";//手机防盗设置完成
	public static final String KEY_SJFD_SETUP_PROTECTION = "sjfd_setup_protection";//设置向导完成页面开启手机防盗
	public static final String KEY_SJFD_ADMIN_ACTIVE = "sjfd_admin_active";//激活设备管理器
	public static final String KEY_NUMBER_STYLE = "number_style";//归属地样式
	public static final String KEY_SHORT_CUT = "short_cut";//快捷图标

	private static SharedPreferences getSharedPreferences(Context context){
		SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		return preferences;
	}
	
	/**
	 * 统一管理所有存储boolean
	 */
	public static void setBoolean(Context context, String key, boolean value){
		SharedPreferences preferences = getSharedPreferences(context);
		Editor edit = preferences.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}
	
	public static boolean getBoolean(Context context, String key, boolean defValue){
		SharedPreferences sharedPreferences = getSharedPreferences(context);
		return sharedPreferences.getBoolean(key, defValue);
	}
	
	public static void setString(Context context, String key, String value){
		SharedPreferences preferences = getSharedPreferences(context);
		Editor edit = preferences.edit();
		edit.putString(key, value);
		edit.commit();
	}
	
	public static String getString(Context context, String key, String defValue){
		SharedPreferences sharedPreferences = getSharedPreferences(context);
		return sharedPreferences.getString(key, defValue);
	}
	
	public static void setSafeNumber(Context context, String number) {
		setString(context, KEY_SJFD_SAFE_NUMBER, number);
	}
	
	public static String getSafeNumber(Context context, String defValue) {
		return getString(context, KEY_SJFD_SAFE_NUMBER, defValue);
	}
	
	/**
	 * 设置向导页面5，设置完成
	 * @param context
	 * @param status
	 */
	public static void setSetupSuccess(Context context, boolean status) {
		setBoolean(context, KEY_SJFD_SETUP_SUCCESS, status);
	}
	
	public static boolean getSetupSuccess(Context context, boolean defValue) {
		return getBoolean(context, KEY_SJFD_SETUP_SUCCESS, defValue);
	}
	
	
	public static void setSjfdProtection(Context context, boolean status) {
		setBoolean(context, KEY_SJFD_SETUP_PROTECTION, status);
	}
	
	public static boolean getSjfdProtection(Context context, boolean defValue) {
		return getBoolean(context, KEY_SJFD_SETUP_PROTECTION, defValue);
	}
	
	public static void setSim(Context context, String number) {
		setString(context, KEY_SJFD_SIM, number);
	}
	
	/**
	 * 获取sim卡
	 * @param context
	 * @param defValue
	 * @return
	 */
	public static String getSim(Context context, String defValue) {
		return getString(context, KEY_SJFD_SIM, defValue);
	}
	
	public static void setDeviceAdmin(Context context, boolean isActive) {
		setBoolean(context, KEY_SJFD_ADMIN_ACTIVE, isActive);
	}
	
	public static boolean getDeviceAdmin(Context context, boolean defValue) {
		return getBoolean(context, KEY_SJFD_ADMIN_ACTIVE, defValue);
	}
	
	public static void setNumberStyle(Context context, int value){
		SharedPreferences preferences = getSharedPreferences(context);
		Editor edit = preferences.edit();
		edit.putInt(KEY_NUMBER_STYLE, value);
		edit.commit();
	}
	
	public static int getNumberStyle(Context context, int defValue){
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getInt(KEY_NUMBER_STYLE, defValue);
	}

}
