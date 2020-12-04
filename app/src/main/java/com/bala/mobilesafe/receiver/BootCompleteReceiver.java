package com.bala.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.text.TextUtils;

import com.bala.mobilesafe.util.LogUtil;
import com.bala.mobilesafe.util.PreferencesUtils;


public class BootCompleteReceiver extends BroadcastReceiver {

	private static final String TAG = "BootCompleteReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtil.d(TAG, "重启手机");
		
		SmsManager sm = SmsManager.getDefault();//获取短信管理器
		String phone = PreferencesUtils.getSafeNumber(context, null);//安全号码
		if(TextUtils.isEmpty(phone)){
			LogUtil.d(TAG, "安全号码为空");
			return;
		}
		//发送文本短信
		
		//sim卡发生改变，发送短信
		String sim = PreferencesUtils.getSim(context, null);
		
		String newSim = sim + "123";
		
		if(sim.equals(newSim)){
			return;
		}
		LogUtil.d(TAG, "sim卡改变，发送报警短信");
		sm.sendTextMessage(phone, null, "SOS,mobile is lost.", null, null);
	}

}
