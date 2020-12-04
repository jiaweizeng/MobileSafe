package com.bala.mobilesafe.activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ListView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.adapter.TrafficAdapter;
import com.bala.mobilesafe.bean.AppBean;
import com.bala.mobilesafe.bean.TrafficBean;
import com.bala.mobilesafe.util.PackageUtils;

import java.util.ArrayList;
import java.util.List;

public class TrafficActivity extends Activity {

	private ListView mListView;
	private View mLoadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic);

		initView();
		initData();
		initListener();

	}

	public void initView() {

		mListView = (ListView) findViewById(R.id.listview);
		mLoadingView = findViewById(R.id.ll_loading);
	}

	public void initData() {

		new Thread() {
			@Override
			public void run() {
				super.run();
			}
		};
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
			}
		}).start();

		// 获取数据
		// 使用API
		// 读文件方式

		// 根据uid 获取流量信息
		/*
		 * uid : 用户ID 。 android手机
		 * 
		 * android --- linux --- 多用户操作系统。 sudo 一个应用程序就是一个用户。 ----------- uid :
		 * 当前的程序
		 */
		mLoadingView.setVisibility(View.VISIBLE);

		new Thread() {
			@Override
			public void run() {
				final List<TrafficBean> trafficList = new ArrayList<TrafficBean>();
				List<AppBean> list = PackageUtils
						.getAppData(TrafficActivity.this);
				for (AppBean appBean : list) {

					// 接收数据花费的流量
					long recv = TrafficStats.getUidRxBytes(appBean.uid);
					// 发送数据花费的流量
					long send = TrafficStats.getUidTxBytes(appBean.uid);
					Drawable icon = appBean.icon;
					String label = appBean.name;
					trafficList.add(new TrafficBean(icon, label, send, recv));
				}

				SystemClock.sleep(1200);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						mLoadingView.setVisibility(View.GONE);

						// 关联数据
						TrafficAdapter adapter = new TrafficAdapter(
								TrafficActivity.this, trafficList);
						mListView.setAdapter(adapter);
					}
				});
			}
		}.start();

	}

	public void initListener() {

	}
}
