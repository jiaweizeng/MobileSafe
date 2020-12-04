package com.bala.mobilesafe.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.util.LogUtil;
import com.bala.mobilesafe.util.NumberStyleUtils;
import com.bala.mobilesafe.util.PreferencesUtils;

public class NumberLocationToast implements OnTouchListener {
	private static final String TAG = "NumberLocationToast";
	private View mView;
	private WindowManager mWM;
	private WindowManager.LayoutParams mParams;
	
	private TextView mTvLocation;
	private float mDownX;
	private float mDownY;
	private Context mContext;

	public NumberLocationToast(Context context) {
		
		this.mContext = context;
		
		mParams = new WindowManager.LayoutParams();
		// 初始化view、mWM、mWM参数
		LayoutInflater inflate = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = inflate.inflate(R.layout.toast_number_location, null);
		
		setListener();
		
		mTvLocation = (TextView)mView.findViewById(R.id.tv_toast_location);
		
		mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		
		 WindowManager.LayoutParams params = mParams;
         params.height = WindowManager.LayoutParams.WRAP_CONTENT;
         params.width = WindowManager.LayoutParams.WRAP_CONTENT;
         params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                 |/* WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE 不可触摸
                 | */WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
         params.format = PixelFormat.TRANSLUCENT;
         //params.windowAnimations = com.android.internal.R.style.Animation_Toast;
         params.type = WindowManager.LayoutParams.TYPE_TOAST;//默认不可触摸
         params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;//屏幕最前面
         //params.setTitle("Toast");
	}

	private void setListener() {
		mView.setOnTouchListener(this);
	}

	public void show(String location) {
		if (mView.getParent() != null) {
			mWM.removeView(mView);
		}
		//设置归属地样式
		int numberStyle = PreferencesUtils.getNumberStyle(mContext, 0);
		int resId = NumberStyleUtils.getNumberStyleResId(numberStyle);
		mView.setBackgroundResource(resId);
		mTvLocation.setText(location);
		mWM.addView(mView, mParams);
	}

	public void hide() {
		if (mView != null) {
			if (mView.getParent() != null) {
				mWM.removeView(mView);
			}
		}
	}

	/**
	 * 触摸回调
	 * event:事件类型、移动位置
	 */
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		LogUtil.d(TAG, "触摸了自定义toast");
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN://按下
			
			//获取按下坐标
			mDownX = event.getRawX();
			mDownY = event.getRawY();
			
			break;
		case MotionEvent.ACTION_MOVE://移动
			//移动Toast
			float moveX = event.getRawX();
			float moveY = event.getRawY();
			
			//计算移动偏移量
			float x = moveX - mDownX;
			float y = moveY - mDownY;
			
			//更新Toast页面
			mParams.x += x;
			mParams.y += y;
			mWM.updateViewLayout(mView, mParams);
			
			//重新计算
			mDownX = moveX;
			mDownY = moveY;
			break;
		case MotionEvent.ACTION_UP://抬起
			break;
		}
		return true;//自己处理
	}

}
