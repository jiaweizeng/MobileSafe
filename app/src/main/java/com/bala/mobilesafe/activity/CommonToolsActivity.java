package com.bala.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.business.SmsProvider;
import com.bala.mobilesafe.service.WatchDogService01;
import com.bala.mobilesafe.service.WatchDogService02;
import com.bala.mobilesafe.util.ServiceUtils;
import com.bala.mobilesafe.view.SettingItemView;

/**
 * 
 * @author xiaomi
 */
public class CommonToolsActivity extends Activity implements OnClickListener {
	
	private SettingItemView mSivCommonLoc;
	private SettingItemView mSivCommonNumber;
	private SettingItemView mSivSmsRestore;
	private SettingItemView mSivSmsBackup;
	private SettingItemView mSivAppLock;
	private SettingItemView mSivWatchDog01;
	private SettingItemView mSivWatchDog02;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_tools);
		
		
		initView();
		setListener();
		
		
	}
	@Override
	protected void onStart() {
		super.onStart();
		mSivWatchDog02.setToggleStatus(ServiceUtils.isServiceRunning(this, WatchDogService02.class));
	}

	private void initView() {
		mSivCommonLoc = (SettingItemView)findViewById(R.id.siv_number_loc);
		mSivCommonNumber = (SettingItemView)findViewById(R.id.siv_common_number);
		mSivSmsBackup = (SettingItemView)findViewById(R.id.siv_sms_backup);
		mSivSmsRestore = (SettingItemView)findViewById(R.id.siv_sms_restore);
		mSivAppLock = (SettingItemView)findViewById(R.id.siv_app_lock);
		
		mSivWatchDog01 = (SettingItemView)findViewById(R.id.siv_watch_dog01);
		mSivWatchDog02 = (SettingItemView)findViewById(R.id.siv_watch_dog02);
		
		
		//回显状态
		mSivWatchDog01.setToggleStatus(ServiceUtils.isServiceRunning(this, WatchDogService01.class));
		
	}

	
	private void setListener() {
		mSivCommonLoc.setOnClickListener(this);
		mSivCommonNumber.setOnClickListener(this);
		mSivSmsBackup.setOnClickListener(this);
		mSivSmsRestore.setOnClickListener(this);
		mSivAppLock.setOnClickListener(this);
		
		mSivWatchDog01.setOnClickListener(this);
		mSivWatchDog02.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.siv_watch_dog02://电子狗02
			performWatchDog02();
			break;
		case R.id.siv_watch_dog01://电子狗01
			performWatchDog01();
			break;
		case R.id.siv_app_lock://程序锁
			startActivity(new Intent(this, AppLockActivity.class));
			break;
		case R.id.siv_number_loc://跳转号码归属地查询
			Intent intent = new Intent(this, NumberLocationActivity.class);
			startActivity(intent);
			break;
		case R.id.siv_common_number:
			clickCommonNumber();//点击了常用工具
			break;

		case R.id.siv_sms_backup://备份
//			SmsProvider.backup(this);
			
			
			
			new SmsProvider().backup02(this , new SmsProvider.ResultListener() {
				
				@Override
				public void onSuccess() {
					System.out.println("---->smsprovider已经成功了，我们收到了通知...");
				}
				
				@Override
				public void onFailed() {
					System.out.println("---->smsprovider已经失败了，我们收到了通知...");
					
				}
			});
			
			break; 
		case R.id.siv_sms_restore://还原
			/*try {
				SmsProvider.restore(this);
				ToastUtils.makeText(this, "还原成功");
			} catch (Exception e) {
				e.printStackTrace();
				ToastUtils.makeText(this, "还原失败");
			}*/
			
			new SmsProvider().restore02(this , new SmsProvider.ResultListener() {
				
				@Override
				public void onSuccess() {
					System.out.println("---->smsprovider已经成功了2222，我们收到了通知...");
				}
				
				@Override
				public void onFailed() {
					System.out.println("---->smsprovider已经失败了22222，我们收到了通知...");
				}
			});
			break; 
			
		default:
			break;
		}
	}

	private void performWatchDog02() {
		
		
	/*	 <intent-filter>
	         <action android:name="android.intent.action.MAIN" />
	         <action android:name="android.settings.ACCESSIBILITY_SETTINGS" />
	         <!-- Wtf...  this action is bogus!  Can we remove it? -->
	         <action android:name="ACCESSIBILITY_FEEDBACK_SETTINGS" />
	         <category android:name="android.intent.category.DEFAULT" />
	         <category android:name="android.intent.category.VOICE_LAUNCH" />
	     </intent-filter>*/
		
		Intent intent = new Intent();
		intent.setAction("android.settings.ACCESSIBILITY_SETTINGS");
		startActivity(intent);
		
		
	}

	/**
	 * 执行电子狗服务01 
	 */
	private void performWatchDog01() {
		//如果服务在运行
		if(ServiceUtils.isServiceRunning(this, WatchDogService01.class)){
			
			//1. 关闭服务
			stopService(new Intent(this ,WatchDogService01.class));
			//2. 关闭开关
			mSivWatchDog01.setToggleStatus(false);
		}else{
			//服务没有运行
			//1. 开启服务
			startService(new Intent(this ,WatchDogService01.class));
			//2. 打开开关
			mSivWatchDog01.setToggleStatus(true);
		}
	}

	private void clickCommonNumber() {
		Intent intent = new Intent(this, CommonNumberActivity.class);
		startActivity(intent);
	}
}
