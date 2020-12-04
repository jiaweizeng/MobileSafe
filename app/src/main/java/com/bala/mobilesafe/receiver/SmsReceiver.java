package com.bala.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.service.HeimaLocationService;
import com.bala.mobilesafe.util.LogUtil;
import com.bala.mobilesafe.util.PreferencesUtils;

/**
 * 发送短信广播
 * @author youliang.ji
 *
 */
public class SmsReceiver extends BroadcastReceiver {

	private static final String TAG = "SmsReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		//读取短信内容
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		for(Object obj : objs){
			SmsMessage sm = SmsMessage.createFromPdu((byte[])obj);
			String sender = sm.getOriginatingAddress();//发送者
			String content = sm.getMessageBody();//短信内容
			
			String safeNumber = PreferencesUtils.getSafeNumber(context, null);
			if(sender.equals(safeNumber)){//如果发送者是安全号码
				if("#*location*#".equals(content)){
					LogUtil.d(TAG, "GPS追踪");
					//开启定位
					Intent service = new Intent(context, HeimaLocationService.class);
					context.startService(service);
					abortBroadcast();
				}else if("#*wipedata*#".equals(content)){
					LogUtil.d(TAG, "远程销毁数据");
					DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
					ComponentName who = new ComponentName(context, HeimaAdminReceiver.class);
					abortBroadcast();
					dpm.wipeData(0);
				}else if("#*alarm*#".equals(content)){
					LogUtil.d(TAG, "播放报警音乐");
					MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
					player.setLooping(true);//循环播放
					player.setVolume(1f, 1f);//音量最大
					player.start();
					abortBroadcast();
				}else if("#*lockscreen*#".equals(content)){
					LogUtil.d(TAG, "远程锁屏");
					DevicePolicyManager mDpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
					ComponentName who = new ComponentName(context, HeimaAdminReceiver.class);
					mDpm.lockNow();
					abortBroadcast();
				}
			}
		}
	}

}
