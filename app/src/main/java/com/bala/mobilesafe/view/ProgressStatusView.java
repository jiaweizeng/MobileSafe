package com.bala.mobilesafe.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bala.mobilesafe.R;

public class ProgressStatusView extends RelativeLayout {

	private TextView mTvLeft;
	private TextView mTvRight;
	private ProgressBar mProgress;

	public ProgressStatusView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//管理布局文件
		View view = View.inflate(context, R.layout.progress_status_view, this);
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ProgressStatusView);
		//读取属性
		String text = ta.getString(R.styleable.ProgressStatusView_psvText);
		
		ta.recycle();
		
		//设置属性值
		TextView tvInstall = (TextView) view.findViewById(R.id.tv_app_install);
		mTvLeft = (TextView) view.findViewById(R.id.tv_left);
		mTvRight = (TextView) view.findViewById(R.id.tv_right);
		mProgress = (ProgressBar)view.findViewById(R.id.progress);
		
		tvInstall.setText(text);
	}

	public ProgressStatusView(Context context) {
		this(context, null);//保证只调用2个参数构造方法
	}
	
	public void setRightText(String text){
		mTvRight.setText(text);
	}
	
	public void setLeftText(String text){
		mTvLeft.setText(text);
	}
	
	public void setProgress(int progress){
		mProgress.setProgress(progress);
	}
	
	

}
