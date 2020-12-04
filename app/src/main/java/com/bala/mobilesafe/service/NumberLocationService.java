package com.bala.mobilesafe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.bala.mobilesafe.dao.AddressDao;
import com.bala.mobilesafe.util.LogUtil;
import com.bala.mobilesafe.view.NumberLocationToast;


public class NumberLocationService extends Service {

	private static final String TAG = "NumberLocationService";
	private TelephonyManager mTm;
	private LocationPhoneListener mListener;
	private NumberLocationToast mToast;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.d(TAG, "开启归属地服务");
		
		mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		//监听电话状态
		mListener = new LocationPhoneListener();
		mTm.listen(mListener, LocationPhoneListener.LISTEN_CALL_STATE);
		
		mToast = new NumberLocationToast(this);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.d(TAG, "关闭归属地服务");
		//取消接听
		mTm.listen(mListener, PhoneStateListener.LISTEN_NONE);
	}
	
	private class LocationPhoneListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
		  /*  * @see TelephonyManager#CALL_STATE_IDLE 空闲
		     * @see TelephonyManager#CALL_STATE_RINGING 响铃
		     * @see TelephonyManager#CALL_STATE_OFFHOOK 接听 */
			
			if(state == TelephonyManager.CALL_STATE_RINGING){//响铃
				//查询归属地
				String location = AddressDao.getNumberLocation(NumberLocationService.this, incomingNumber);
				//ToastUtils.makeText(NumberLocationService.this, "来电号码：" + location);
				mToast.show(location);
			}else if(state == TelephonyManager.CALL_STATE_IDLE){
				//空闲
				mToast.hide();
			}
			
			
		}
	}

}
