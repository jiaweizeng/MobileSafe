package com.bala.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.service.NumberLocationService;
import com.bala.mobilesafe.service.ScreenOffService;
import com.bala.mobilesafe.service.SmsCallService;
import com.bala.mobilesafe.util.ServiceUtils;
import com.bala.mobilesafe.view.NumberStyleDialog;
import com.bala.mobilesafe.view.SettingItemView;

public class SettingActivity extends Activity implements OnClickListener {

	private SettingItemView mSivUpdate;
	private SettingItemView mSivSmsCall;
	private SettingItemView mSivNumberLoc;
	private SettingItemView mSivLocationStyle;
	private SettingItemView mSivAutoClean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 绑定布局文件
		setContentView(R.layout.activity_setting);

		initView();
		setListener();
	}

	private void initView() {
		mSivUpdate = (SettingItemView) findViewById(R.id.siv_update);
		mSivSmsCall = (SettingItemView) findViewById(R.id.siv_sms_call);
		mSivNumberLoc = (SettingItemView) findViewById(R.id.siv_number_loc);
		mSivLocationStyle = (SettingItemView) findViewById(R.id.siv_number_style);
		mSivAutoClean = (SettingItemView) findViewById(R.id.siv_auto_clean);

		mSivUpdate
				.setToggleStatus(this, mSivUpdate.getToggleStatus(this, true));

		boolean serviceRunning = ServiceUtils.isServiceRunning(this,
				SmsCallService.class);
		mSivSmsCall.setToggleStatus(serviceRunning);
		
		initNumberLocationStatus();
		
		mSivAutoClean.setToggleStatus(ServiceUtils.isServiceRunning(this, ScreenOffService.class));

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		// 回显骚扰拦截服务状态
		boolean serviceRunning = ServiceUtils.isServiceRunning(this,
				SmsCallService.class);
		mSivSmsCall.setToggleStatus(this, serviceRunning);
	}

	private void setListener() {
		mSivUpdate.setOnClickListener(this);
		mSivSmsCall.setOnClickListener(this);
		mSivNumberLoc.setOnClickListener(this);
		mSivLocationStyle.setOnClickListener(this);
		mSivAutoClean.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.siv_update:
			// 获取之前状态
			boolean toggleStatus = mSivUpdate.getToggleStatus(this, true);
			mSivUpdate.setToggleStatus(this, !toggleStatus);
			break;

		case R.id.siv_sms_call:
			// 点击了骚扰拦截设置

			// 判断服务开启/关闭状态
			if (ServiceUtils.isServiceRunning(this, SmsCallService.class)) {
				// 运行中，关闭
				Intent intent = new Intent(this, SmsCallService.class);
				stopService(intent);
				mSivSmsCall.setToggleStatus(false);
			} else {
				// 没有运行，开启
				Intent intent = new Intent(this, SmsCallService.class);
				startService(intent);
				mSivSmsCall.setToggleStatus(true);
			}

			break;
		case R.id.siv_number_loc:
			clickNumberLocationService();//点击了归属地服务
			break;
		case R.id.siv_number_style:
			clickNumberStyle();//点击归属地样式
			break;
		case R.id.siv_auto_clean:
			clickAutoClean();
			break;
		}
	}



	private void clickNumberLocationService() {
		//开启或者关闭服务
		Intent service = new Intent(this, NumberLocationService.class);
		if(ServiceUtils.isServiceRunning(this, NumberLocationService.class)){
			stopService(service);
			mSivNumberLoc.setToggleStatus(false);
		}else{
			startService(service );
			mSivNumberLoc.setToggleStatus(true);
		}
	}
	
	private void initNumberLocationStatus(){
		boolean running = ServiceUtils.isServiceRunning(this, NumberLocationService.class);
		mSivNumberLoc.setToggleStatus(running);
	}
	

	private void clickNumberStyle() {
		//底部弹出对话框
		NumberStyleDialog dialog = new NumberStyleDialog(this);
		dialog.show();
	}
	

	private void clickAutoClean() {
		Intent service = new Intent(this, ScreenOffService.class);
		if(ServiceUtils.isServiceRunning(this, ScreenOffService.class)){
			stopService(service);
			mSivAutoClean.setToggleStatus(false);
		}else{
			startService(service);
			mSivAutoClean.setToggleStatus(true);
		}
	}

}
