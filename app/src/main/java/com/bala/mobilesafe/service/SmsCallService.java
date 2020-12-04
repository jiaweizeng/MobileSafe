package com.bala.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.bala.mobilesafe.bean.BlackBean;
import com.bala.mobilesafe.dao.BlackDao;
import com.bala.mobilesafe.db.BlackOpenHelper;
import com.bala.mobilesafe.telephony.ITelephony;
import com.bala.mobilesafe.util.LogUtil;

import java.lang.reflect.Method;


/**
 * 骚扰拦截：电话和短信，服务
 * @author developer
 *
 */
public class SmsCallService extends Service {

	private static final String TAG = "SmsCallService";
	private SmsReceiver mReceiver;
	private TelephonyManager mTm;
	private PhoneListener mListener;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.d(TAG, "开启骚扰拦截服务");
		
		//拦截电话和短信
		//1.广播监听短信状态
		mReceiver = new SmsReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);//设置优先级
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");//动作：监听短信
		registerReceiver(mReceiver, filter);
		
		//2.监听电话状态
		mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		//参数2：events-状态类型（来电、接听）
		mListener = new PhoneListener();
		mTm.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.d(TAG, "关闭骚扰拦截服务");
		unregisterReceiver(mReceiver);//取消注册
		//取消监听电话
		mTm.listen(mListener, PhoneStateListener.LISTEN_NONE);
	}
	
	private class SmsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//读取短信内容
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for(Object obj : objs){
				SmsMessage sm = SmsMessage.createFromPdu((byte[])obj);
				String sender = sm.getOriginatingAddress();//发送者
				
				//发送短信者为黑名单，短信类型、全部，拦截
				//1.查询拦截类型
				BlackOpenHelper helper = new BlackOpenHelper(context);
				BlackDao dao = new BlackDao(helper );
				int type = dao.getType(sender);
				if(type == BlackBean.TYPE_SMS || type == BlackBean.TYPE_ALL){
					//拦截掉
					abortBroadcast();
					LogUtil.d(TAG, "拦截短信");
				}
				
			}
		}
		
	}
	
	private class PhoneListener extends PhoneStateListener{
		//电话状态回调方法
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			/* * @see TelephonyManager#CALL_STATE_IDLE 空闲状态
		     * @see TelephonyManager#CALL_STATE_RINGING 响铃状态
		     * @see TelephonyManager#CALL_STATE_OFFHOOK 接听电话状态
		     * */
			//获取拦截类型
			BlackOpenHelper helper = new BlackOpenHelper(SmsCallService.this);
			BlackDao dao = new BlackDao(helper);
			int type = dao.getType(incomingNumber);
			if(type == BlackBean.TYPE_PHONE || type == BlackBean.TYPE_ALL){
				if(state == TelephonyManager.CALL_STATE_RINGING){//响铃，挂掉电话
					LogUtil.d(TAG, "拦截电话");
					
					ITelephony iTelephony = null;
					
					//iTelephony = mTm.getITelephony();
					 //private ITelephony getITelephony() ;
				/*	try {
						//1.会出现空指针问题，因为内部通过异步方式调用
						Method method = mTm.getClass().getDeclaredMethod("getITelephony");
						iTelephony = (ITelephony) method.invoke(mTm);
					} catch (Exception e) {
						e.printStackTrace();
					}*/
					
					//IBinder iBinder = ServiceManager.getService(Context.TELEPHONY_SERVICE);
					
					try{
						Class<?> clazz = Class.forName("android.os.ServiceManager");
						Method method = clazz.getMethod("getService", String.class);
						IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
						iTelephony = ITelephony.Stub.asInterface(iBinder);
					}catch(Exception e){
						e.printStackTrace();
					}
					
					 //挂掉电话
					try {
						iTelephony.endCall();
						//情况通话记录
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
			
		}
	}

}
