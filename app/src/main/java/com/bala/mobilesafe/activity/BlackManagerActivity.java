package com.bala.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.bean.BlackBean;
import com.bala.mobilesafe.dao.BlackDao;
import com.bala.mobilesafe.db.BlackOpenHelper;
import com.bala.mobilesafe.util.LogUtil;
import com.bala.mobilesafe.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class BlackManagerActivity extends Activity implements OnClickListener, OnItemClickListener, OnScrollListener {

	private static final int REQCODE_ADD_BLACK = 500;
	private static final int REQCODE_EDIT_BLACK = 501;
	protected static final int PAGE_SIZE = 20;//每页条数
	private static final String TAG = "BlackManagerActivity";
	private ImageView mIvAddBlack;
	private ListView mListView;
	private List<BlackBean> mListData;
	private Context mContext;
	private BlackAdapter mAdapter;
	private LinearLayout mLoading;
	private ImageView mIvEmpty;
	private boolean isLoading;
	private boolean canLoadMore = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_manager);

		mContext = this;

		initView();
		initData();
		setListener();
	}

	private void initView() {
		mIvAddBlack = (ImageView) findViewById(R.id.iv_add_black);
		mListView = (ListView) findViewById(R.id.listview_black);
		mLoading = (LinearLayout)findViewById(R.id.ll_loading);
		mIvEmpty = (ImageView)findViewById(R.id.iv_empty);
	}

	private void initData() {
		
		//子线程获取DB数据，大数据
		final BlackOpenHelper helper = new BlackOpenHelper(mContext);
		
		mLoading.setVisibility(View.VISIBLE);
		mIvEmpty.setVisibility(View.GONE);
		isLoading = true;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				mListData = new ArrayList<BlackBean>();
				// initTempData();
				mAdapter = new BlackAdapter();
				
				SystemClock.sleep(2000);
				BlackDao dao = new BlackDao(helper);
				mListData = dao.findPart(PAGE_SIZE, 0);
				//刷新UI，必须在主线程
				runOnUiThread(new Runnable() {
					public void run() {
						mLoading.setVisibility(View.GONE);
						mListView.setAdapter(mAdapter);
						isLoading = false;
					}
				});
			}
		}).start();

		// adapter -> 数据集List<Bean> -> ViewHolder -> getView
	}

	private void initTempData() {
		for (int i = 0; i < 30; i++) {
			BlackBean bean = new BlackBean();
			bean.phone = 18800000000L + i + "";

			mListData.add(bean);
		}
	}

	private void setListener() {
		mIvAddBlack.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this);//监听滚动事件
	}

	@Override
	public void onClick(View v) {
		if (v == mIvAddBlack) {
			clickAddBlack();// 点击添加黑名单
		}
	}

	private void clickAddBlack() {
		Intent intent = new Intent(this, AddBlackActivity.class);
		
		startActivityForResult(intent, REQCODE_ADD_BLACK);
	}
	
	

	class BlackAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mListData != null) {
				//处理空View
				mIvEmpty.setVisibility(mListData.size() == 0 ? View.VISIBLE : View.GONE);
				return mListData.size();
			}
			mIvEmpty.setVisibility(View.VISIBLE);
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return mListData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// 1.声明ViewHolder
			ViewHolder holder = null;
			if (convertView == null) {
				// 没有复用
				// 2.加载布局
				convertView = View.inflate(mContext, R.layout.item_black, null);// TODO
				// 3.创建ViewHolder
				holder = new ViewHolder();
				// 4.findViewById
				holder.tvPhone = (TextView) convertView
						.findViewById(R.id.tv_black_phone);
				holder.tvType = (TextView) convertView
						.findViewById(R.id.tv_black_type);
				holder.ivDelte = (ImageView) convertView
						.findViewById(R.id.iv_black_delete);
				// 5.设置标记
				convertView.setTag(holder);
			} else {
				// 有复用
				holder = (ViewHolder) convertView.getTag();
			}

			// 设置数据
			BlackBean bean = mListData.get(position);
			final String phone = bean.phone;
			holder.tvPhone.setText(bean.phone);
			String typeValue = "";
			switch (bean.type) {
			case BlackBean.TYPE_PHONE:
				typeValue = "电话";
				break;
			case BlackBean.TYPE_SMS:
				typeValue = "短信";
				break;
			case BlackBean.TYPE_ALL:
				typeValue = "电话+短信";
				break;
			}
			holder.tvType.setText(typeValue);
			
			//删除
			holder.ivDelte.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					BlackOpenHelper helper = new BlackOpenHelper(mContext);
					BlackDao dao = new BlackDao(helper);
					boolean delete = dao.delete(phone);
					if(delete){
						ToastUtils.makeText(mContext, "删除成功");
						//更新UI
						//1.删除数据集合数据
						mListData.remove(position);
						mAdapter.notifyDataSetChanged();
					}else{
						ToastUtils.makeText(mContext, "删除失败");
					}
				}
			});

			return convertView;
		}

	}

	static class ViewHolder {
		TextView tvPhone;
		TextView tvType;
		ImageView ivDelte;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//进入黑名单编辑页面 ，复用添加黑名单页面 TODO
		Intent intent = new Intent(this, AddBlackActivity.class);
		intent.putExtra(AddBlackActivity.KEY_PAGE_TYPE, 0);
		intent.putExtra(AddBlackActivity.KEY_POSITION, position);//为了回显更新数据，知道到底更新那一条数据
		intent.putExtra(AddBlackActivity.KEY_PHONE, mListData.get(position).phone);
		intent.putExtra(AddBlackActivity.KEY_TYPE, mListData.get(position).type);
		startActivityForResult(intent, REQCODE_EDIT_BLACK);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK){
			switch (requestCode) {
			case REQCODE_ADD_BLACK:
				String phone = data.getStringExtra(AddBlackActivity.KEY_PHONE);
				int type = data.getIntExtra(AddBlackActivity.KEY_TYPE, BlackBean.TYPE_PHONE);
				
				BlackBean bean = new BlackBean();
				bean.phone = phone;
				bean.type = type;
				mListData.add(bean);
				mAdapter.notifyDataSetChanged();
				break;
			case REQCODE_EDIT_BLACK:
				int updateType = data.getIntExtra(AddBlackActivity.KEY_TYPE, BlackBean.TYPE_PHONE);
				int postion = data.getIntExtra(AddBlackActivity.KEY_POSITION, 0);
				BlackBean updateBean = mListData.get(postion);//获取当前更新那一条
				updateBean.type = updateType;
				mAdapter.notifyDataSetChanged();
				break;

			}
		}
	}

	//滚动状态改变回调，暂时不使用
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
	}

	//滚动回调
	//firstVisibleItem-当前屏幕第一个可见item postion
	//visibleItemCount-当前屏幕可见条数
	//totalItemCount-总的条数
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
		//什么时候加载更多数据
		//滚动到底部
		//最后一条可见item positon = 数据总数-1
		if(mListData == null || mAdapter == null){
			return;
		}
		if(!isLoading2Bottom()){
			return;
		}
		
		//当正在加载中...,不让往下加载更多
		if(isLoading){
			return;
		}
		if(!canLoadMore){
			return;
		}
		
		isLoading = true;
		mLoading.setVisibility(view.VISIBLE);
		LogUtil.d(TAG, "滑动到底部");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				SystemClock.sleep(2000);
				BlackOpenHelper helper = new BlackOpenHelper(mContext);
				BlackDao dao = new BlackDao(helper );
				 List<BlackBean> findPart = dao.findPart(PAGE_SIZE, mAdapter.getCount());
				 
				 if(findPart.size() < PAGE_SIZE){
					 //最后一页
					canLoadMore = false;
				 }
				 
				 mListData.addAll(findPart);
				//刷新UI，必须在主线程
				runOnUiThread(new Runnable() {
					public void run() {
						mLoading.setVisibility(View.GONE);
						isLoading = false;
						mAdapter.notifyDataSetChanged();
					}
				});
			}
		}).start();
	}
	
	/**
	 * 判断是否滑动到底部
	 * @return
	 */
	private boolean isLoading2Bottom(){
		int lastVisiblePosition = mListView.getLastVisiblePosition();
		if(lastVisiblePosition == mListData.size() - 1){
			return true;
		}else{
			return false;
		}
	}

}
