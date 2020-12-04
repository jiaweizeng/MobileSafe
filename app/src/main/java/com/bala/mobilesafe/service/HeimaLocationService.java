package com.bala.mobilesafe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

import com.bala.mobilesafe.util.LogUtil;
import com.bala.mobilesafe.util.PreferencesUtils;


public class HeimaLocationService extends Service {

	private static final String TAG = "HeimaLocationService";
	private LocationManager mLocMgr;
	private MeituanLocationListener mListener;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.d(TAG, "定位服务开启");
		mLocMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//请求定位信息
		mListener = new MeituanLocationListener();
		mLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mListener);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.d(TAG, "定位服务关闭");
		mLocMgr.removeUpdates(mListener);
	}
	
	/**
	 * 定位回调监听
	 * @author youliang.ji
	 *
	 */
	private class MeituanLocationListener implements LocationListener{

		//定位成功回调
		@Override
		public void onLocationChanged(Location location) {
			double lng = location.getLongitude();//经度
			double lat = location.getLatitude();//维度
			
			//发送短信到安全号码
			String safeNumber = PreferencesUtils.getSafeNumber(HeimaLocationService.this, null);
			
			SmsManager sm = SmsManager.getDefault();
			LogUtil.d(TAG, "安全号码="+safeNumber);
			sm.sendTextMessage(safeNumber, null, "lost mobile location "+"lng="+lng+",lat="+lat, null, null);
			stopSelf();//停在自己
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}
		
	}

}
