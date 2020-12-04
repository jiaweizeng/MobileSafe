package com.bala.mobilesafe.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.util.Log;

public class ServiceUtils {
	
	private static final String TAG = "ServiceUtils";

	/**
	 * 判断服务是否允许
	 * @param context
	 * @param clazz
	 * @return
	 */
	public static boolean isServiceRunning(Context context, Class<? extends Service> clazz){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//获取当前运行的服务列表
		List<RunningServiceInfo> services = am.getRunningServices(Integer.MAX_VALUE);
		for(RunningServiceInfo serviceInfo : services){
			String className = serviceInfo.service.getClassName();
			if(className.equals(clazz.getName())){
				LogUtil.d(TAG, "服务开启,className="+className+", clazz="+clazz.getName());
				return true;
			}
		}
		return false;
	}

}
