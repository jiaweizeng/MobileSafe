package com.bala.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.util.PreferencesUtils;

/**
 * 设置向导完成页面
 * @author youliang.ji
 *
 */
public class SetupSuccessActivity extends Activity implements OnClickListener {
	
	private RelativeLayout mRlSjfdStatus;
	private TextView mTvReenterSetup;
	private ImageView mIvStatus;
	private TextView mTvSafeNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_setup_success);
		
		initView();
		setListener();
	}


	private void initView() {
		mRlSjfdStatus = (RelativeLayout)findViewById(R.id.rl_setup_success);
		mTvReenterSetup = (TextView)findViewById(R.id.tv_reenter_setup);
		mIvStatus = (ImageView)findViewById(R.id.iv_sjfd_status);
		mTvSafeNumber = (TextView)findViewById(R.id.tv_safe_number);
		
		boolean status = PreferencesUtils.getSjfdProtection(this, false);
		mIvStatus.setImageResource(status ? R.drawable.lock : R.drawable.unlock);
		
		String safeNumber = PreferencesUtils.getSafeNumber(this, null);
		mTvSafeNumber.setText(safeNumber);
	}
	

	private void setListener() {
		mRlSjfdStatus.setOnClickListener(this);
		mTvReenterSetup.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.rl_setup_success){
			//点击开启手机防盗
			clickOpenProtection();
		}else if(v.getId() == R.id.tv_reenter_setup){
			//重新进入手机防盗
			Intent intent = new Intent(this, SetupActivity1.class);
			startActivity(intent);
			finish();
		}
	}


	private void clickOpenProtection() {
		//获取之前存储的状态
		boolean status = PreferencesUtils.getSjfdProtection(this, false);
		if(!status){
			mIvStatus.setImageResource(R.drawable.lock);
		}else{
			mIvStatus.setImageResource(R.drawable.unlock);
		}
		
		//存储状态
		PreferencesUtils.setSjfdProtection(this, !status);
	}

}
