package com.bala.mobilesafe.adapter;


import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.bean.AppBean;
import com.bala.mobilesafe.dao.AppLockDao;

import java.util.List;

public class AppLockAdapter extends BaseAdapter {
	Context mContext;
	List<AppBean> mList;
	boolean mIsUnlock = true;

	public AppLockAdapter(Context context , List<AppBean> list) {
		super();
		mContext = context;
		mList = list;
	}
	
	/**
	 * 更改数据显示
	 * @param list
	 * @param isUnlock  true : 表明当前操作的是未加锁 ， false: 当前操作的是已加锁
	 */
	public void setData(List<AppBean> list , boolean isUnlock){
		mList = list;
		mIsUnlock = isUnlock;
	}


	//控制有多少个条目
	@Override
	public int getCount() {
		return mList.size();
	}
	

	//控制每个条目长什么样子，显示什么数据
	@Override
	public View getView(final int position,  View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = View.inflate(mContext, R.layout.item_applock, null);
			
			holder = new ViewHolder();
			
			holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
			holder.ivLock = (ImageView) convertView.findViewById(R.id.iv_lock);
			holder.tvLabel = (TextView) convertView.findViewById(R.id.tv_label);
			
			convertView.setTag(holder);
			
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		//变换图标
		if(!mIsUnlock){
			holder.ivLock.setImageResource(R.drawable.list_button_unlock_selector);
		}else{
			holder.ivLock.setImageResource(R.drawable.list_button_lock_selector);
		}
		
		
		final AppBean appBean = mList.get(position);
		
		holder.tvLabel.setText(appBean.name);
		holder.ivIcon.setImageDrawable(appBean.icon);
		
		final View itemView = convertView;
		final ViewHolder vHolder = holder;
		
		holder.ivLock .setOnClickListener(new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick(View v) {
				
				
				//2. 动画
				Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.push_right_out);
				itemView.startAnimation(animation);
				
				animation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					
						vHolder.ivLock.setEnabled(false);
					}
					
					@Override
					public void onAnimationRepeat(Animation animation) {
					}
					
					@Override
					public void onAnimationEnd(Animation animation) {
						
						//1. 移除
						mList.remove(position);
						//数据已经改变了，提醒listView 重新刷新界面，显示新的效果
						notifyDataSetChanged();
						
						vHolder.ivLock.setEnabled(true);
						
						//3. 持久化
						AppLockDao dao = new AppLockDao(mContext);
						
						if(mIsUnlock){
							dao.insert(appBean.packageName);
						}else{
							//显示的已加锁
							dao.delete(appBean.packageName);
						}
						
						
						//这里要通知activity，数据已经发生了改变
						if(mListener != null){
							mListener.onDataChange(appBean , mIsUnlock);
						}
						
					}
				});
				
				
			}
		});
		
		return convertView;
	}
	
	static class ViewHolder{
		ImageView ivIcon , ivLock ; 
		TextView tvLabel;
	}
	
	
	OnDataChangeListener mListener;
	public void setOnDataChangeListener(OnDataChangeListener listener){
		mListener = listener;
	}
	
	public interface OnDataChangeListener{
		
		
		//. 如果仅仅是想通知而已， 那么方法可以不要参数
		
		//. 如果想在通知的同时，还告诉对方，都有哪些数据发生了变化。 那么必须加参数
		
		/**
		 * 数据改变回调方法
		 * @param bean  改变的具体条目bean
		 * @param isUnLock  true : 表明当前是未加锁界面， 数据应该要加入到加锁集合中
		 * 			false : 表明当前是加锁界面，数据应该要接入到未加锁集合中
		 */
		void onDataChange(AppBean bean, boolean isUnLock);
		
	}
	
	
	
	
	
	
	
	
	//-----------------------------------------------

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}


}
