package com.bala.mobilesafe.view;


import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.TextView;

import com.bala.mobilesafe.R;

/**
 * 进程管理-首字母-弹出toast
 * 
 * @author developer
 * 
 */
public class LetterToast {

	public WindowManager mWM;
	public WindowManager.LayoutParams mParams;
	public TextView mView;

	public LetterToast(Context context) {
		// 设置参数

		mParams = new WindowManager.LayoutParams();
		// 初始化view、mWM、mWM参数
		LayoutInflater inflate = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = (TextView) inflate.inflate(R.layout.toast_letter, null);

		mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		mParams.gravity = Gravity.CENTER;
		WindowManager.LayoutParams params = mParams;

		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		// params.windowAnimations =
		// com.android.internal.R.style.Animation_Toast;
		params.type = WindowManager.LayoutParams.TYPE_TOAST;// 默认不可触摸
	}

	private String mLetter = "";
	public void show(String letter) {
		if(mLetter.equals(letter)){//上次已经显示
			return;
		}
		mLetter = letter; 
		if (mView.getParent() == null) {// 从来没有显示过
			mWM.addView(mView, mParams);
		}
		
		mView.setText(letter);
		//避免闪屏，移除之前的任务,显示的同事，做移除操作
		mHandler.removeCallbacks(mHideTask);
		//延时隐藏
		mHandler.postDelayed(mHideTask, 500);
	}

	public void hide() {
		if (mView.getParent() != null) {
			mWM.removeView(mView);
		}
	}
	
	private Handler mHandler = new Handler();
	private Runnable mHideTask  = new Runnable() {
		
		@Override
		public void run() {
			hide();
		}
	};

}
