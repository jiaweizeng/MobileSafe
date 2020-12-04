package com.bala.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.util.LogUtil;

public abstract class BaseSetupActivity extends Activity {
	
	protected static final String TAG = "BaseSetupActivity";
	private GestureDetector mDetector;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//1.初始化手势识别器
		mDetector = new GestureDetector(this, new SimpleOnGestureListener(){
			//滑动回调 e1-按下坐标, e2-抬起坐标, velocityX-X轴方向速率, velocityY-Y轴方向速率
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				
				//按下坐标
				float x1 = e1.getRawX();
				float y1 = e1.getRawY();
				//抬起坐标
				float x2 = e2.getRawX();
				float y2 = e2.getRawY();
				
				//速率判断
				LogUtil.d(TAG, "velocityX="+velocityX+", velocityY="+velocityY);
				if(Math.abs(velocityX) < 100){
					LogUtil.d(TAG, "速率过低");
					return true;
				}
				//方向判断：Y轴位移大于X轴，不处理
				if(Math.abs(y1-y2) > Math.abs(x1-x2)){
					LogUtil.d(TAG, "y轴方向移动");
					return true;
				}
				if(x1 > x2){
					LogUtil.d(TAG, "下一步");
					nextStep();
					overridePendingTransition(R.anim.next_step_enter, R.anim.next_step_exit);
				}else{
					LogUtil.d(TAG, "上一步");
					preStep();
					overridePendingTransition(R.anim.pre_step_enter, R.anim.pre_step_exit);
				}
				
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}
	
	/**
	 * 点击监听
	 * @param v
	 */
	public void preStep(View v){
		preStep();
		overridePendingTransition(R.anim.pre_step_enter, R.anim.pre_step_exit);
	}
	
	/**
	 * 点击监听
	 * @param v
	 */
	public void nextStep(View v){
		nextStep();
		overridePendingTransition(R.anim.next_step_enter, R.anim.next_step_exit);
	}
	
	/**
	 * 下一步操作
	 */
	protected abstract void nextStep();
	
	
	/**
	 * 上一步
	 */
	protected abstract void preStep();
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

}
