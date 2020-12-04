package com.bala.mobilesafe.util;

import android.util.Log;

public class LogUtil {

//	LogUtil.d("aa",":aksdfjasdjf");
	
//	LogUtil.d("aa","akalkjlksdf");
	
	private static final boolean isLog = false;
	
	public static void d(String tag , String msg){
		if(!isLog){
			return;
		}
		Log.d(tag, msg);
	}
}
