package com.bala.mobilesafe.activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.receiver.HeimaAdminReceiver;
import com.bala.mobilesafe.util.PreferencesUtils;
import com.bala.mobilesafe.util.ToastUtils;

/**
 * 设置向导页面1
 */
public class SetupActivity4 extends BaseSetupActivity implements
		OnClickListener {

	private static final int REQUEST_CODE_ENABLE_ADMIN = 100;
	private Button mBtnActiveAdmin;
	private ImageView mIvAdmin;
	private DevicePolicyManager mDpm;
	private ComponentName who;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);

		initView();
		initData();
		
		setListener();
	}

	private void initData() {
		mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		who = new ComponentName(this, HeimaAdminReceiver.class);
	}

	private void initView() {
		mBtnActiveAdmin = (Button) findViewById(R.id.btn_active_admin);
		mIvAdmin = (ImageView) findViewById(R.id.iv_admin_status);
		
		boolean active = PreferencesUtils.getDeviceAdmin(this, false);
		mIvAdmin.setImageResource(active ? R.drawable.admin_activated : R.drawable.admin_inactivated);
	}

	private void setListener() {
		mBtnActiveAdmin.setOnClickListener(this);
	}

	@Override
	protected void nextStep() {
		if(!mDpm.isAdminActive(who)){
			ToastUtils.makeText(this, "如果要开启防盗保护,必须激活设备管理员");
			return;
		}
		Intent intent = new Intent(this, SetupActivity5.class);
		startActivity(intent);
		finish();
	}

	@Override
	protected void preStep() {
		Intent intent = new Intent(this, SetupActivity3.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onClick(View v) {
		if (v == mBtnActiveAdmin) {
			clickActiveAdmin();// 点击激活设备管理器
		}
	}

	private void clickActiveAdmin() {
		
		boolean active = mDpm.isAdminActive(who);
		if(!active){
			// 如果没有激活设备管理器，激活
			  Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
              intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, who);
              intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"激活设备管理器");
              startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
		}else{
			// 否则，取消
			mDpm.removeActiveAdmin(who);
			mIvAdmin.setImageResource(R.drawable.admin_inactivated);
			//持久化数据
			PreferencesUtils.setDeviceAdmin(this, false);
		}
	

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_ENABLE_ADMIN){
			//激活成功回调
			// 切换图标
			mIvAdmin.setImageResource(R.drawable.admin_activated);
			PreferencesUtils.setDeviceAdmin(this, true);
		}
	}
}
