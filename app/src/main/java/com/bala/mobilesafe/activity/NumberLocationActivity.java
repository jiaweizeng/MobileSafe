package com.bala.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.dao.AddressDao;
import com.bala.mobilesafe.util.LogUtil;
import com.bala.mobilesafe.util.ToastUtils;

public class NumberLocationActivity extends Activity implements OnClickListener, TextWatcher {

	private static final String TAG = "NumberLocationActivity";
	private Button mBtnSearch;
	private EditText mEtNumber;
	private Context mContext;
	private TextView mTvLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_location);

		mContext = this;

		initView();
		setListener();
	}

	private void initView() {
		mBtnSearch = (Button) findViewById(R.id.btn_search);
		mEtNumber = (EditText) findViewById(R.id.et_number_loc);
		mTvLocation = (TextView)findViewById(R.id.tv_number_loc);
	}

	private void setListener() {
		mBtnSearch.setOnClickListener(this);
		//监听输入框数据变化
		mEtNumber.addTextChangedListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_search:
			searchLocation();// 查询归属地
			break;

		default:
			break;
		}
	}

	private void searchLocation() {
		// 非空判断
		String number = mEtNumber.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			ToastUtils.makeText(mContext, "号码不能为空");
			// 执行动画1
		/*	Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			mEtNumber.startAnimation(shake);*/
			
			//代码2.
			TranslateAnimation anim = new TranslateAnimation(0, 10, 0, 0);
			anim.setDuration(1000);
			CycleInterpolator interpolator = new CycleInterpolator(7);
			anim.setInterpolator(interpolator);
			mEtNumber.startAnimation(anim);
			return;
		}
		
		//查询归属地：归属地数据库
		String location = AddressDao.getNumberLocation(mContext, number);
		mTvLocation.setText("归属地：" + location);
	}

	
	

	//EdiText内容变化回调方法
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if(TextUtils.isEmpty(s.toString())){
			return;
		}
		LogUtil.d(TAG, s.toString());
		//动态查询归属地数据库
		String location = AddressDao.getNumberLocation(mContext, s.toString());
		mTvLocation.setText("归属地："+location);
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

}
