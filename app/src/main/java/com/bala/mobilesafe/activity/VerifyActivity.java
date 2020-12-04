package com.bala.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.util.ToastUtils;

public class VerifyActivity  extends Activity implements OnClickListener{

	private Button mBtnEnsure;
	private EditText mEtPassWord;
	private String mPackageName;
	private TextView mTvLabel;
	private ImageView mIvIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verify);

		initView();
		initData();
		initListener();
		
	}
	
	
	
	
	//来了一个新的意图。 把这个界面给调到前面去显示了。
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		String pName = intent.getStringExtra("packageName");
		
		initIconLabel(pName);
		
	}

	/**
	 * 初始化icon&label
	 * @param pName
	 */
	private void initIconLabel(String pName) {
		try {
			PackageManager pm = getPackageManager();
			
//			PackageInfo --- applicationInfo  (icon & label)
			PackageInfo info = pm.getPackageInfo(pName, 0);
			
			String label = info.applicationInfo.loadLabel(pm).toString();
			mTvLabel.setText(label);
			
			Drawable icon = info.applicationInfo.loadIcon(pm);
			mIvIcon.setImageDrawable(icon);
			
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void initView() {

		mIvIcon = (ImageView) findViewById(R.id.iv_icon);
		mTvLabel = (TextView) findViewById(R.id.tv_label);
		mEtPassWord = (EditText) findViewById(R.id.et_password);
		mBtnEnsure = (Button) findViewById(R.id.btn_ensure);
	}

	public void initData() {

		
//		getIntent() 返回启动这个界面的意图对象。 startActivity 得到的就是创建这个界面实例的那个意图对象。
		
		mPackageName = getIntent().getStringExtra("packageName");
		
		initIconLabel(mPackageName);
		
	}

	public void initListener() {

		mBtnEnsure.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		
		String password= mEtPassWord.getText().toString();
		
		if(TextUtils.isEmpty(password)){
			ToastUtils.makeText(this, "密码不能为空!");
			return ;
		}
		if("123".equals(password)){
			
			//密码一致。
			finish();
			//发送广播，通知服务。
			Intent intent = new Intent();
			
			//顺便带一点数据给服务，通知它，该程序验证通过了。
			intent.putExtra("packageName", mPackageName);
			
			intent.setAction("com.itheima.verify");
			sendBroadcast(intent);
		}
	}
	
	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		
		/*<intent-filter>
	        <action android:name="android.intent.action.MAIN" />
	        <category android:name="android.intent.category.HOME" />
	        <category android:name="android.intent.category.DEFAULT" />
	        <category android:name="android.intent.category.MONKEY"/>
	    </intent-filter>*/
		
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		startActivity(intent);
	}
}
