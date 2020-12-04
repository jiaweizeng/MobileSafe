package com.bala.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.bean.BlackBean;
import com.bala.mobilesafe.dao.BlackDao;
import com.bala.mobilesafe.db.BlackOpenHelper;
import com.bala.mobilesafe.util.ToastUtils;

/**
 * 
 * @author developer
 * 
 */
public class AddBlackActivity extends Activity implements OnClickListener {

	public static final String KEY_PHONE = "phone";
	public static final String KEY_TYPE = "type";
	public static final String KEY_PAGE_TYPE = "page_type";
	
	/**编辑页面类型*/
	public static final int PAGE_EDIT = 0;
	/**添加页面类型*/
	public static final int PAGE_ADD = 1;
	public static final String KEY_POSITION = "position";
	private EditText mEtPhone;
	private RadioGroup mRadioGroup;
	private RadioButton mRbPhone;
	
	private RadioButton mRbSms;
	private RadioButton mRbAll;
	private Button mBtnOk;
	private Button mBtnCancel;
	private Context mContext;
	private TextView mTvTitle;
	private int pageType;
	private int mType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_black);

		mContext = this;
		
		initView();
		initData();
		setListener();
	}

	private void initData() {
		Intent intent = getIntent();
		pageType = intent.getIntExtra(KEY_PAGE_TYPE, 1);
		if(pageType == 0){
			mTvTitle.setText("更新黑名单");
			mBtnOk.setText("更新");
			mEtPhone.setEnabled(false);//不可用
			String phone = intent.getStringExtra(KEY_PHONE);
			mType = intent.getIntExtra(KEY_TYPE, BlackBean.TYPE_PHONE);
			
			mEtPhone.setText(phone);
			//回显拦截类型
			setRadioGroup(mType);
			//获取当前编辑条目
			
		}else{
			mTvTitle.setText("添加黑名单");
		}
	}

	private void setRadioGroup(int type) {
		switch (type) {
		case BlackBean.TYPE_PHONE:
			mRadioGroup.check(R.id.rb_type_phone);
			break;
		case BlackBean.TYPE_SMS:
			mRadioGroup.check(R.id.rb_type_sms);
			break;
		case BlackBean.TYPE_ALL:
			mRadioGroup.check(R.id.rb_type_all);
			break;
		}
	}

	private void initView() {
		mEtPhone = (EditText) findViewById(R.id.et_black_phone);
		mRadioGroup = (RadioGroup) findViewById(R.id.rg_black);
		mRbPhone = (RadioButton) findViewById(R.id.rb_type_phone);
		mRbSms = (RadioButton) findViewById(R.id.rb_type_sms);
		mRbAll = (RadioButton) findViewById(R.id.rb_type_all);
		mBtnOk = (Button) findViewById(R.id.btn_black_ok);
		mBtnCancel = (Button) findViewById(R.id.btn_black_cancel);
		
		
		mTvTitle = (TextView)findViewById(R.id.tv_add_black_title);
		
		//动态设置标题
	}

	private void setListener() {
		mBtnOk.setOnClickListener(AddBlackActivity.this);
		mBtnCancel.setOnClickListener(AddBlackActivity.this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_black_ok:
			if(pageType == PAGE_ADD){
				clickAddBlack();// 添加黑名单
			}else{
				editBlack();
			}
			break;
		case R.id.btn_black_cancel:
			finish();
			break;
		}
	}

	

	private void clickAddBlack() {
		// 判断
		String phone = mEtPhone.getText().toString().trim();
		int checkedId = mRadioGroup.getCheckedRadioButtonId();

		if (TextUtils.isEmpty(phone)) {
			ToastUtils.makeText(mContext, "号码不能为空");
			return;
		}
		if (checkedId == -1) {
			ToastUtils.makeText(mContext, "请选择拦截类型");
			return;
		}

		int type = BlackBean.TYPE_PHONE;
		switch (checkedId) {
		case R.id.rb_type_phone:
			type = BlackBean.TYPE_PHONE;
			break;
		case R.id.rb_type_sms:
			type = BlackBean.TYPE_SMS;
			break;
		case R.id.rb_type_all:
			type = BlackBean.TYPE_ALL;
			break;
		}

		// 插入数据库
		BlackOpenHelper helper = new BlackOpenHelper(mContext);
		BlackDao dao = new BlackDao(helper);
		boolean insert = dao.insert(phone, type);
		if(insert){
			ToastUtils.makeText(mContext, "插入成功");
		}
		
		Intent data = new Intent();
		data.putExtra(KEY_PHONE, phone);
		data.putExtra(KEY_TYPE, type);
		setResult(Activity.RESULT_OK, data );
		finish();
	}
	
	private void editBlack() {
		BlackOpenHelper helper = new BlackOpenHelper(mContext);
		BlackDao dao = new BlackDao(helper);
		String phone = mEtPhone.getText().toString().trim();
		
		
		int editPosition = getIntent().getIntExtra(KEY_POSITION, -1);//获取当前更新条目
		boolean update = dao.update(phone, getBlackType());
		if(update){
			ToastUtils.makeText(mContext, "更新成功");
		}else{
			ToastUtils.makeText(mContext, "更新失败");
		}
		
		Intent data = new Intent();
		data.putExtra(KEY_POSITION, editPosition);
		data.putExtra(KEY_TYPE, getBlackType());
		setResult(Activity.RESULT_OK, data );
		
		finish();
	}
	
	private int getBlackType(){
		int checkedId = mRadioGroup.getCheckedRadioButtonId();

		int type = BlackBean.TYPE_PHONE;
		switch (checkedId) {
		case R.id.rb_type_phone:
			type = BlackBean.TYPE_PHONE;
			break;
		case R.id.rb_type_sms:
			type = BlackBean.TYPE_SMS;
			break;
		case R.id.rb_type_all:
			type = BlackBean.TYPE_ALL;
			break;
		}
		return type;
	}

}
