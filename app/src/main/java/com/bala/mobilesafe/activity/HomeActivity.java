package com.bala.mobilesafe.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.bean.HomeBean;
import com.bala.mobilesafe.util.LogUtil;
import com.bala.mobilesafe.util.PreferencesUtils;
import com.bala.mobilesafe.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Activity implements OnClickListener, OnItemClickListener {

	private static final String TAG = "HomeActivity";
	private ImageButton mIbSetting;
	private ImageView mIvLogo;
	private GridView mGridView;
	private List<HomeBean> mListDatas;
	private int[] ICONS = new int[] { R.drawable.sjfd, R.drawable.srlj,
			R.drawable.rjgj, R.drawable.jcgl, R.drawable.lltj, R.drawable.sjsd,
			R.drawable.hcql, R.drawable.cygj };
	private String[] TITLES = new String[] { "手机防盗", "骚扰拦截", "软件管家", "进程管理",
			"流量统计", "手机杀毒", "缓存清理", "常用工具" };
	private String[] DESCS = new String[] { "远程定位手机", "全面拦截骚扰", "管理您的软件",
			"管理运行进程", "流量一目了然", "病毒无处藏身", "系统快如火箭", "工具大全" };
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		initView();
		initData();
		setListener();
	}

	private void initView() {
		mIbSetting = (ImageButton) findViewById(R.id.ib_setting);
		mIvLogo = (ImageView) findViewById(R.id.iv_home_logo);
		mGridView = (GridView) findViewById(R.id.gridview_home);

		loadLogoAnimation();
	}

	private void initData() {
		mContext = this;
		mListDatas = new ArrayList<HomeBean>();
		//添加到数据集
		for (int i = 0; i < TITLES.length; i++) {
			HomeBean bean = new HomeBean();
			bean.icon = ICONS[i];
			bean.title = TITLES[i];
			bean.desc = DESCS[i];
			mListDatas.add(bean );
		}
		//adapter -> 数据集 List<Bean> -> 继承BaseAdapter -> getView返回item样子 -> getView动态设置数据
		HomeGridViewAdapter adapter = new HomeGridViewAdapter();
		mGridView.setAdapter(adapter);
	}

	private void loadLogoAnimation() {
		// 设置View属性
		// mIvLogo.setAlpha(alpha)
		// mIvLogo.setRotationX(rotationX);
		// mIvLogo.setRotationY(rotationY)

		// 设置属性动画:target-对哪个对象执行动画，propertyName-属性,
		// ObjectAnimator.ofFloat(target, propertyName, values);
		ObjectAnimator animator = ObjectAnimator.ofFloat(mIvLogo, "rotationY",
				0, 90, 360, 180, 270);
		animator.setDuration(1000);
		animator.setRepeatCount(ObjectAnimator.INFINITE);// 设置无限循环
		animator.setRepeatMode(ObjectAnimator.REVERSE);// 循环模式
		animator.start();
	}

	/**
	 * 统一管理所有监听事件
	 */
	private void setListener() {
		mIbSetting.setOnClickListener(this);
		mGridView.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ib_setting) {
			// 点击头部设置
			clickSetting();
		}
	}

	private void clickSetting() {
		Intent intent = new Intent(this, SettingActivity.class);
		startActivity(intent );
	}

	private class HomeGridViewAdapter extends BaseAdapter {

		// item个数
		@Override
		public int getCount() {
			if (mListDatas != null) {
				return mListDatas.size();
			}
			return 0;
		}

		// 返回每一个Bean对象
		@Override
		public Object getItem(int position) {
			return mListDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		// 设置item样子
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(HomeActivity.this, R.layout.item_home,
					null);
			//设置数据
			//1.findViewById
			ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_home_item_icon);
			TextView tvTitle = (TextView) view.findViewById(R.id.tv_home_item_title);
			TextView tvDesc = (TextView) view.findViewById(R.id.tv_home_item_desc);
			
			//2.获取数据
			//HomeBean bean = mListDatas.get(position);
			HomeBean bean = (HomeBean) getItem(position);
			
			ivIcon.setImageResource(bean.icon);
			tvTitle.setText(bean.title);
			tvDesc.setText(bean.desc);
			
			return view;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		switch (position) {
		case 0:
			clickSjfd();//点击手机防盗
			
			break;
		case 1:
			clickBlackManager();//点击黑名单管理
			break;
		case 2:
			clickAppManager();
			break;
		case 3:
			clickProcessManager();
			break;
		case 4:
			clickTraffic();
			break;
		case 5:
			clickAntivirus();
			break;
		case 6:
			clickClearCache();//点击缓存清理
			break;
		case 7:
			clickCommonTools();//点击常用工具
			break;

		default:
			break;
		}
		
	}

	



	private void clickTraffic() {
		startActivity(new Intent(this , TrafficActivity.class));
	}

	private void clickAntivirus() {
		startActivity(new Intent(this , AntiVirusActivity.class));
		
	}

	private void clickClearCache() {

		startActivity(new Intent(this , ClearCacheActivity.class));
	}

	private void clickSjfd() {
		String savePwd = PreferencesUtils.getString(this, PreferencesUtils.KEY_SJFD_PWD, null);
		if(TextUtils.isEmpty(savePwd)){
			//弹出设置密码dialog
			showSetPwdDialog();
			
		}else{
			showIntputPwdDialog();
		}
	}

	private void showIntputPwdDialog() {
		LogUtil.d(TAG, "进入输入密码对话框");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.sjfd_input_pwd, null);
		final EditText etPwd = (EditText) view.findViewById(R.id.et_pwd);
		Button btnOk = (Button) view.findViewById(R.id.btn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
		//自定义Dialog
		builder.setView(view);
		final AlertDialog dialog = builder.show();
		
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//判断密码
				String pwd = etPwd.getText().toString().trim();
				if(TextUtils.isEmpty(pwd)){
					ToastUtils.makeText(mContext, "请输入密码");
					return;
				}
				//判断存储密码和输入密码是否一致
				String savedPwd = PreferencesUtils.getString(mContext, PreferencesUtils.KEY_SJFD_PWD, null);
				if(!savedPwd.equals(pwd)){
					ToastUtils.makeText(mContext, "密码错误");
					return;
				}
//				dialog.dismiss();
				dialog.dismiss();
				boolean setupSuccess = PreferencesUtils.getSetupSuccess(mContext, false);
				if(setupSuccess){//设置向导完成
					load2SetupSuccessPage();
				}else{
					load2SetupPage();
				}
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	private void showSetPwdDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.sjfd_setting_pwd, null);
		final EditText etPwd = (EditText) view.findViewById(R.id.et_pwd);
		final EditText etComfirmPwd = (EditText) view.findViewById(R.id.et_comfirm_pwd);
		Button btnOk = (Button) view.findViewById(R.id.btn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
		//自定义Dialog
		builder.setView(view);
		
		final AlertDialog dialog = builder.show();
		
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//点击确定按钮
				String pwd = etPwd.getText().toString().trim();
				String comfirmPwd = etComfirmPwd.getText().toString().trim();
				
				if(TextUtils.isEmpty(pwd)){
					ToastUtils.makeText(mContext, "请输入密码");
					return;
				}
				if(TextUtils.isEmpty(comfirmPwd)){
					ToastUtils.makeText(mContext, "请确认密码");
					return;
				}
				
				if(!pwd.equals(comfirmPwd)){
					ToastUtils.makeText(mContext, "两次密码不对");
					return;
				}
				
				//持久化密码
				PreferencesUtils.setString(mContext, PreferencesUtils.KEY_SJFD_PWD, pwd);
				dialog.dismiss();
				load2SetupPage();
				
			}

		});
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//点击取消按钮
				dialog.dismiss();
			}
		});
	}
	

	private void load2SetupSuccessPage() {
		Intent intent = new Intent(mContext, SetupSuccessActivity.class);
		startActivity(intent);
	}

	protected void load2SetupPage() {
		Intent intent = new Intent(mContext, SetupActivity1.class);
		startActivity(intent );
	}
	
	private void clickBlackManager() {
		Intent intent = new Intent(mContext, BlackManagerActivity.class);
		startActivity(intent );
	}
	

	private void clickCommonTools() {
		Intent intent = new Intent(mContext, CommonToolsActivity.class);
		startActivity(intent );
	}
	

	private void clickAppManager() {
		Intent intent = new Intent(mContext, AppManagerActivity.class);
		startActivity(intent );
	}
	


	/**
	 * 打开进程管理员
	 */
	private void clickProcessManager() {
		Intent intent = new Intent(mContext, ProcessManagerActivity.class);
		startActivity(intent );
	}

}
