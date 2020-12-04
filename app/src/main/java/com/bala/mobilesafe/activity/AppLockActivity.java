package com.bala.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.adapter.AppLockAdapter;
import com.bala.mobilesafe.bean.AppBean;
import com.bala.mobilesafe.dao.AppLockDao;
import com.bala.mobilesafe.util.PackageUtils;
import com.bala.mobilesafe.view.SectionView;

import java.util.ArrayList;
import java.util.List;

public class AppLockActivity extends Activity implements SectionView.OnSelectedListener, AppLockAdapter.OnDataChangeListener {

	private TextView mTvLabel;
	private SectionView mSectionView;
	private ListView mListView;
	List<AppBean> mLockList = new ArrayList<AppBean>();
	List<AppBean> mUnLockList = new ArrayList<AppBean>();
	private AppLockAdapter mAdapter;
	private TextView mTvLabel2;
	private View mLoadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);
		
		initView();
		initData();
		initListener();
	}
	
	public void initView() {
		mSectionView = (SectionView) findViewById(R.id.sectionview);
		mTvLabel = (TextView) findViewById(R.id.tv_label);
		mListView = (ListView) findViewById(R.id.listview);
		
		mLoadingView = findViewById(R.id.ll_loading);
	}

	public void initData() {
		
		mLoadingView.setVisibility(View.VISIBLE);
		mTvLabel.setVisibility(View.GONE);

		mSectionView.setEnabled(false);
		
		new Thread(){
			@Override
			public void run() {
				
				//1， 获取所有的应用程序  100 
				List<AppBean> list = PackageUtils.getAppData(AppLockActivity.this);
				
				//2. 得到所有的已加锁的程序 ， 包名  3个被加锁
				AppLockDao dao = new AppLockDao(AppLockActivity.this);
				List<String> lockList = dao.query();
				
				
				//3. 区分加锁和未加锁程序
				for (AppBean AppBean : list) {
					String packagename = AppBean.packageName;
					if(lockList.contains(packagename)){
						mLockList.add(AppBean); //3个
					}else{
						mUnLockList.add(AppBean); //97 
					}
				}
				
				SystemClock.sleep(1200);
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						mLoadingView.setVisibility(View.GONE);
						mTvLabel.setVisibility(View.VISIBLE);
						mTvLabel.setText("未加锁("+mUnLockList.size()+")个");
						
						mAdapter = new AppLockAdapter(AppLockActivity.this , mUnLockList);
						mAdapter.setOnDataChangeListener(AppLockActivity.this);
						mListView.setAdapter(mAdapter);
						
						mSectionView.setEnabled(true);
					}
				});
			}
		}.start();
		
		
	}

	public void initListener() {
		mSectionView.setOnSelectedListener(this);
	}

	@Override
	public void onLeftSelected() {
		mTvLabel.setText("未加锁("+mUnLockList.size()+")个");
		//2.设置数据显示
		/*AppLockAdapter adapter =new AppLockAdapter(this , mUnLockList);
		mListView.setAdapter(adapter);*/
		mAdapter.setData(mUnLockList , true);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onRightSelected() {
		mTvLabel.setText("已加锁("+mLockList.size()+")个");
		/*AppLockAdapter adapter =new AppLockAdapter(this , mLockList);
		mListView.setAdapter(adapter);*/
		
		mAdapter.setData(mLockList , false);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDataChange(AppBean bean , boolean isUnlock) {
		//未加锁界面
		if(isUnlock){
			mLockList.add(bean);
			
			mTvLabel.setText("未加锁("+mUnLockList.size()+")个");
		}else{
			//已加锁界面
			
			mUnLockList.add(bean);
			mTvLabel.setText("已加锁("+mLockList.size()+")个");
		}
	}
	
	
}
