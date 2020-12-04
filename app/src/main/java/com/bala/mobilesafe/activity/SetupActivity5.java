package com.bala.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.util.PreferencesUtils;
import com.bala.mobilesafe.util.ToastUtils;

/**
 * 设置向导页面1
 */
public class SetupActivity5 extends BaseSetupActivity {
	
	private CheckBox mCbSetupStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup5);
		
		initView();
	}
	

	private void initView() {
		mCbSetupStatus = (CheckBox)findViewById(R.id.cb_setup5);
		
		boolean isSetupSuccess = PreferencesUtils.getSetupSuccess(this, false);
		mCbSetupStatus.setChecked(isSetupSuccess);
	}


	@Override
	protected void nextStep() {
		//判断是否选中
		boolean checked = mCbSetupStatus.isChecked();
		if(!checked){
			ToastUtils.makeText(this, "勾选后才可以开启防盗保护");
			return;
		}
		//存储
		PreferencesUtils.setSetupSuccess(this, checked);
		//ToastUtils.makeText(this, "进入设置向导完成页面");// TODO
		Intent intent = new Intent(this, SetupSuccessActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	protected void preStep() {
		Intent intent = new Intent(this, SetupActivity4.class);
		startActivity(intent );
		finish();
	}

}
