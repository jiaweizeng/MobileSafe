package com.bala.mobilesafe.activity;


import android.content.Intent;
import android.os.Bundle;

import com.bala.mobilesafe.R;

/**
 * 设置向导页面1
 */
public class SetupActivity1 extends BaseSetupActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup_one);
	}
	
	@Override
	protected void nextStep() {
		Intent intent = new Intent(this, SetupActivity2.class);
		startActivity(intent );
		finish();
	}

	@Override
	protected void preStep() {
		// TODO Auto-generated method stub
		
	}

	

}
