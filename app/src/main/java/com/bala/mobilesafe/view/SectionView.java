package com.bala.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bala.mobilesafe.R;


public class SectionView extends RelativeLayout implements OnClickListener{

	private TextView mTvLeft;
	private TextView mTvRight;

	//一般用于在代码里面使用  new 关键字创建对象使用的。
	public SectionView(Context context) {
		super(context);
	}
	
	// 如果在布局中使用了该控件，那么系统会将执行这个构造方法
	public SectionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//只是把一个布局变成view
		/*View view = View.inflate(context, R.layout.view_section, null);
		addView(view);*/
		
		//把这个layout编程一个布局，并且放置到 当前的这个容器里面去。 
		View.inflate(context, R.layout.view_section, this);
		
//		
		
		initView();
		initData();
		initListener();
		
	}

	

	private void initView() {
		
		mTvLeft = (TextView) findViewById(R.id.tv_left);
		mTvRight = (TextView) findViewById(R.id.tv_right);
	}
	
	private void initData() {
		mTvLeft.setSelected(true);
	}
	

	private void initListener() {
		mTvLeft.setOnClickListener(this);
		mTvRight.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_left: //点击左边
			mTvLeft.setSelected(true);
			mTvRight.setSelected(false);
			
			//AppLockActivity.tv.setText("未加锁");
			if(mListener!= null){
				mListener.onLeftSelected();
			}
			break;
		case R.id.tv_right: //点击右边
			mTvRight.setSelected(true);
			mTvLeft.setSelected(false);
			
			//AppLockActivity.tv.setText("已加锁");
			if(mListener != null){
				mListener.onRightSelected();
			}
			break;
		}
	}
	
	

	OnSelectedListener mListener;
	public void setOnSelectedListener(OnSelectedListener listener){
		mListener = listener;
	}
	
	
	public interface OnSelectedListener{
		
		/**
		 * 左边点击，那么调用该方法
		 */
		void  onLeftSelected();
		
		/**
		 * 右边点击，那么调用这个方法
		 */
		void  onRightSelected();
		
		//void onSelected(boolean isLeft);
	}
	
	
	/**
	 * 设置右边的加锁是否可以点击
	 * @param enabled  true :大家都可以使用 ，false : 大家都不允许使用
	 */
	public void setEnabled(boolean enabled){
		mTvLeft.setEnabled(enabled);
		mTvRight.setEnabled(enabled);
	}
	
	


}
