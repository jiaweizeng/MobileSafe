package com.bala.mobilesafe.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.util.PreferencesUtils;

public class SettingItemView extends RelativeLayout {


	private static final int FIRST = 0;
	private static final int MIDDLE = 1;
	private static final int LAST = 2;
	private TextView mTvTitle;
	private RelativeLayout mRlItem;
	private ImageView mIvToggle;

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//设置布局
		View.inflate(context, R.layout.setting_item_view, this);
		initView();
		//相当于属性集合
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView);
		//读取属性值
		String text = ta.getString(R.styleable.SettingItemView_sivText);
		//背景
		int bgIndex = ta.getInt(R.styleable.SettingItemView_sivBackground, FIRST);
		//开关状态
		boolean toggleStatus = ta.getBoolean(R.styleable.SettingItemView_sivToggleStatus, true);
		boolean toggleVisiable = ta.getBoolean(R.styleable.SettingItemView_sivToggleVisiable, true);
		
		ta.recycle();//必须调用该方法，避免性能消耗
		
		//设置内容
		mTvTitle.setText(text);
		switch(bgIndex){
		case FIRST:
			mRlItem.setBackgroundResource(R.drawable.setting_item_first_selector);
			break;
		case MIDDLE:
			mRlItem.setBackgroundResource(R.drawable.setting_item_middle_selector);
			break;
		case LAST:
			mRlItem.setBackgroundResource(R.drawable.setting_item_last_selector);
			break;
		}
		
		if(toggleVisiable){
			mIvToggle.setImageResource(toggleStatus ? R.drawable.on : R.drawable.off);
			mIvToggle.setVisibility(View.VISIBLE);
		}else{
			mIvToggle.setVisibility(View.GONE);
		}
	}

	private void initView() {
		mTvTitle = (TextView)findViewById(R.id.tv_setting_item_name);
		mRlItem = (RelativeLayout)findViewById(R.id.rl_setting_item);
		mIvToggle = (ImageView)findViewById(R.id.iv_setting_item_toggle);
	}

	public SettingItemView(Context context) {
		this(context, null);//保证只调用2个参数构造方法
	}
	
	public void setToggleStatus(Context context, boolean status){
		mIvToggle.setImageResource(status ? R.drawable.on : R.drawable.off);
		//持久化存储：sp
		PreferencesUtils.setBoolean(context, PreferencesUtils.KEY_UPDATE_STATUS, status);
	}
	
	public void setToggleStatus(boolean status){
		mIvToggle.setImageResource(status ? R.drawable.on : R.drawable.off);
	}
	
	public boolean getToggleStatus(Context context, boolean defValue){
		return PreferencesUtils.getBoolean(context, PreferencesUtils.KEY_UPDATE_STATUS, defValue);
	}
	
	

}
