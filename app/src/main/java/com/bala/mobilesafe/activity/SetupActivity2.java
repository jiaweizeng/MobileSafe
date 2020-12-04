package com.bala.mobilesafe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.util.LogUtil;
import com.bala.mobilesafe.util.PreferencesUtils;
import com.bala.mobilesafe.util.ToastUtils;

/**
 * 设置向导页面1
 */
public class SetupActivity2 extends BaseSetupActivity implements OnClickListener {

	private Button mBtnBindSim;
	private ImageView mIvBindStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		
		initView();
		setListener();
		initData();
	}


	private void initView() {
		mBtnBindSim = (Button)findViewById(R.id.btn_bind_sim);
		mIvBindStatus = (ImageView)findViewById(R.id.iv_bind_status);
	}
	

	private void initData() {
		String sim = PreferencesUtils.getString(this, PreferencesUtils.KEY_SJFD_SIM, null);
		if(TextUtils.isEmpty(sim)){
			mIvBindStatus.setImageResource(R.drawable.unlock);
		}else{
			mIvBindStatus.setImageResource(R.drawable.lock);
		}
	}
	
	private void setListener() {
		mBtnBindSim.setOnClickListener(this);
	}

	@Override
	protected void nextStep() {
		if(!hasBindSim()){//没有绑定sim卡
			ToastUtils.makeText(this, "如果要开启防盗保护,必须绑定SIM卡");
			return;
		}
		Intent intent = new Intent(this, SetupActivity3.class);
		startActivity(intent );
		finish();
	}

	@Override
	protected void preStep() {
	
		Intent intent = new Intent(this, SetupActivity1.class);
		startActivity(intent );
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_bind_sim:
			clickBindSim();//点击绑定sim卡
			break;

		default:
			break;
		}
	}

	private void clickBindSim() {
		String saveSim = PreferencesUtils.getString(this, PreferencesUtils.KEY_SJFD_SIM, null);
		if(!TextUtils.isEmpty(saveSim)){
			//解绑
			PreferencesUtils.setString(this, PreferencesUtils.KEY_SJFD_SIM, null);
			mIvBindStatus.setImageResource(R.drawable.unlock);
		}else{
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String phoneNumber = tm.getLine1Number();//手机号:一般运营商会将手机号烧制到手机卡，不一定存在
			String sim = tm.getSimSerialNumber();//一般都会有
			
			LogUtil.d(TAG, "sim="+sim+",phoneNumber="+phoneNumber);
			//存储sim卡信息
			PreferencesUtils.setString(this, PreferencesUtils.KEY_SJFD_SIM, sim);
			mIvBindStatus.setImageResource(R.drawable.lock);
		}
		
	}
	
	private boolean hasBindSim(){
		String saveSim = PreferencesUtils.getString(this, PreferencesUtils.KEY_SJFD_SIM, null);
		return !TextUtils.isEmpty(saveSim);
	}

}
