package com.bala.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.bala.mobilesafe.bean.PkgBean;
import com.bala.mobilesafe.bean.ProcessBean;
import com.bala.mobilesafe.business.ProcessProvider;
import com.bala.mobilesafe.util.LogUtil;

import java.util.List;

public class ScreenOffService extends Service {

	private static final String TAG = "ScreenOffService";
	private ScreenOffReceiver mReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.d(TAG, "开启锁屏清理服务");
		mReceiver = new ScreenOffReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.d(TAG, "关闭锁屏清理服务");
		unregisterReceiver(mReceiver);
	}
	
	private class ScreenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			List<ProcessBean> list = ProcessProvider.getProcessList(context);
			for (ProcessBean bean : list) {
				PkgBean pkg = bean.pkg;
				if(pkg.packageName.equals(context.getPackageName())){
					continue;
				}
				//清理进程
				ProcessProvider.killProcess(context, pkg.packageName);
				LogUtil.d(TAG, "清理进程="+bean.processName);
			}
		}
		
	}

}
