package com.bala.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.util.PreferencesUtils;
import com.bala.mobilesafe.util.ToastUtils;

/**
 * 设置向导页面1
 */
public class SetupActivity3 extends BaseSetupActivity implements OnClickListener {
	
	private static final int REQCODE_CONTACT = 300;
	private EditText mEtSafeNumber;
	private Button mBtnSelectContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		initView();
		setListener();
	}


	private void initView() {
		mEtSafeNumber = (EditText)findViewById(R.id.et_setup3);
		mBtnSelectContact = (Button)findViewById(R.id.btn_setup3);
		
		//回显数据
		String safeNumber = PreferencesUtils.getSafeNumber(this, null);
		mEtSafeNumber.setText(safeNumber);
	}
	

	private void setListener() {
		mBtnSelectContact.setOnClickListener(this);
	}

	@Override
	protected void nextStep() {
		String number = mEtSafeNumber.getText().toString().trim();
		if(TextUtils.isEmpty(number)){
			ToastUtils.makeText(this, "如果要开启防盗保护,必须设置安全号码");
			return;
		}
		//存储安全号码
		PreferencesUtils.setSafeNumber(this, number);
		Intent intent = new Intent(this, SetupActivity4.class);
		startActivity(intent );
		finish();
	}

	@Override
	protected void preStep() {
		Intent intent = new Intent(this, SetupActivity2.class);
		startActivity(intent );
		finish();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_setup3:
			clickSelectContact();//点击选择联系人
			break;

		default:
			break;
		}
	}


	private void clickSelectContact() {
		//跳转到联系人列表页面
		Intent intent = new Intent(this, ContactSListActivity.class);
		startActivityForResult(intent, REQCODE_CONTACT);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK){//成功回调
			switch (requestCode) {
			case REQCODE_CONTACT://联系人成功回到
				//获取手机号
				String phone = data.getStringExtra(ContactSListActivity.KEY_CONTACT_PHONE);
				mEtSafeNumber.setText(phone);
				PreferencesUtils.setSafeNumber(SetupActivity3.this, phone);
				break;

			default:
				break;
			}
		}
	}

}
