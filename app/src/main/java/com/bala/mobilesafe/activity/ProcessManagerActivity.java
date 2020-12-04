package com.bala.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.bean.PkgBean;
import com.bala.mobilesafe.bean.ProcessBean;
import com.bala.mobilesafe.business.ProcessProvider;
import com.bala.mobilesafe.util.ToastUtils;
import com.bala.mobilesafe.view.LetterToast;
import com.bala.mobilesafe.view.ProgressStatusView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView.OnHeaderClickListener;

public class ProcessManagerActivity extends Activity implements
		OnScrollListener, OnClickListener, OnHeaderClickListener {

	private ProgressStatusView mPsvCount;
	private ProgressStatusView mPsvMemory;
	private Context mContext;
	private StickyListHeadersListView mListView;
	private ProcessAdapter mAdapter;
	private List<ProcessBean> mListDatas;
	private View mLoadingView;
	private LetterToast mLetterToast;
	private Button mBtnAll;
	private Button mBtnReverse;
	private Set<PkgBean> mPkgSet;
	private ImageView mIvClean;
	private int mRunningProcessCount;
	private int mTotalProcessCount;
	private long mUsedMemory;
	private long mTotalMemory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_manager);
		mContext = this;

		initView();
		initData();
		setListener();
	}

	private void initView() {
		mPsvCount = (ProgressStatusView) findViewById(R.id.psv_process_count);
		mPsvMemory = (ProgressStatusView) findViewById(R.id.psv_process_memory);
		mListView = (StickyListHeadersListView) findViewById(R.id.lv_process);
		mLoadingView = findViewById(R.id.ll_loading);
		mLetterToast = new LetterToast(mContext);
		mBtnAll = (Button) findViewById(R.id.btn_all);
		mBtnReverse = (Button) findViewById(R.id.btn_reverse);
		mIvClean = (ImageView)findViewById(R.id.iv_clean_process);
	}

	private void setListener() {
		mListView.setOnScrollListener(this);
		mBtnAll.setOnClickListener(this);
		mBtnReverse.setOnClickListener(this);
		mListView.setOnHeaderClickListener(this);
		mIvClean.setOnClickListener(this);
	}

	private void initData() {
		// 获取当前运行进程个数，总的进程个数
		mRunningProcessCount = ProcessProvider
				.getRunningProcessCount(mContext);
		mTotalProcessCount = ProcessProvider.getTotalProcessCount(mContext);
		mUsedMemory = ProcessProvider.getUsedMemory(mContext);
		mTotalMemory = ProcessProvider.getTotalMemory(mContext);
		
		setProcessCount();
		setProcessMemory();

		mListDatas = new ArrayList<ProcessBean>();
		mPkgSet = new HashSet<PkgBean>();

		mLoadingView.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				SystemClock.sleep(300);
				mListDatas = ProcessProvider.getProcessList(mContext);
				// 根据首字母排序
				Collections.sort(mListDatas, new Comparator<ProcessBean>() {

					@Override
					public int compare(ProcessBean lhs, ProcessBean rhs) {
						String lhsFirstLetter = lhs.pkg.firstLetter;
						String rhsFirstLetter = rhs.pkg.firstLetter;

						return lhsFirstLetter
								.compareToIgnoreCase(rhsFirstLetter);
					}
				});

				for (ProcessBean bean : mListDatas) {
					mPkgSet.add(bean.pkg);
				}

				runOnUiThread(new Runnable() {
					public void run() {
						mLoadingView.setVisibility(View.GONE);
						mAdapter = new ProcessAdapter();
						mListView.setAdapter(mAdapter);
					}
				});
			}
		}).start();
	}

	private void setProcessMemory() {
		
		int memoryProgress = (int) (mUsedMemory * 100f / mTotalMemory + 0.5f);

		mPsvMemory.setLeftText("占用内存："
				+ Formatter.formatFileSize(mContext, mUsedMemory));
		mPsvMemory.setRightText("可用内存："
				+ Formatter.formatFileSize(mContext, mTotalMemory - mUsedMemory));
		mPsvMemory.setProgress(memoryProgress);
	}

	private class ProcessAdapter extends BaseAdapter implements
			StickyListHeadersAdapter {

		@Override
		public int getCount() {
			return mListDatas.size();
		}

		@Override
		public Object getItem(int position) {
			return mListDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 1.声明Holder
			ItemViewHolder holder = null;
			if (convertView == null) {
				// 没有复用
				// 1.加载布局文件
				convertView = View.inflate(mContext, R.layout.item_process,
						null);
				// 2.创建Holder
				holder = new ItemViewHolder();
				// 3.findViewById
				holder.tvProcessName = (TextView) convertView
						.findViewById(R.id.tv_process_name);
				holder.tvProcessMemory = (TextView) convertView
						.findViewById(R.id.tv_process_memory);
				// 4.设置标记
				convertView.setTag(holder);
			} else {
				// 没有复用
				holder = (ItemViewHolder) convertView.getTag();
			}
			// 设置数据
			ProcessBean bean = mListDatas.get(position);
			holder.tvProcessName.setText(bean.processName);
			holder.tvProcessMemory.setText(Formatter.formatFileSize(mContext,
					bean.processMemory));

			return convertView;
		}

		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {
			// 1.声明Holder
			HeaderViewHolder holder = null;
			if (convertView == null) {
				// 没有复用
				// 1.加载布局文件
				convertView = View.inflate(mContext,
						R.layout.item_process_header, null);
				// 2.创建Holder
				holder = new HeaderViewHolder();
				// 3.findViewById
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.iv_process_header_icon);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.tv_process_header_name);
				holder.cbChecked = (CheckBox) convertView
						.findViewById(R.id.cb_process_header_checked);
				// 4.设置标记
				convertView.setTag(holder);
			} else {
				// 有复用
				holder = (HeaderViewHolder) convertView.getTag();
			}

			// 设置数据
			ProcessBean bean = mListDatas.get(position);
			PkgBean pkg = bean.pkg;
			
			if(pkg.packageName.equals(getPackageName())){
				holder.cbChecked.setVisibility(View.GONE);
			}else{
				holder.cbChecked.setVisibility(View.VISIBLE);
			}

			holder.cbChecked.setChecked(pkg.isChecked);

			if (pkg.icon == null) {
				holder.ivIcon.setImageResource(R.drawable.ic_default);
			} else {
				holder.ivIcon.setImageDrawable(pkg.icon);
			}

			holder.tvName.setText(pkg.name);

			return convertView;
		}

		@Override
		public long getHeaderId(int position) {
			ProcessBean bean = mListDatas.get(position);
			return bean.pkg.hashCode();
		}

	}

	static class ItemViewHolder {
		TextView tvProcessName;
		TextView tvProcessMemory;
	}

	static class HeaderViewHolder {
		ImageView ivIcon;// 应用图标
		TextView tvName;// 应用名字
		CheckBox cbChecked;
	}

	private void setProcessCount() {
		mPsvCount.setLeftText("正在运行" + mRunningProcessCount + "个");

		mPsvCount.setRightText("可有进程" + mTotalProcessCount + "个");

		int processProgress = (int) (mRunningProcessCount * 100f
				/ mTotalProcessCount + 0.5f);
		mPsvCount.setProgress(processProgress);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
	}

	// 滚动回调方法
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mListDatas == null || mListDatas.size() == 0) {
			return;
		}
		// 显示首字母
		ProcessBean bean = mListDatas.get(firstVisibleItem);
		String firstLetter = bean.pkg.firstLetter;
		mLetterToast.show(firstLetter.toUpperCase());
	}

	@Override
	public void onClick(View v) {
		if (v == mBtnAll) {
			clickAll();// 全选
		} else if (v == mBtnReverse) {
			clickReverse();// 反选
		} else if(v == mIvClean){
			killProcess();//杀死进程
		}
	}


	private void clickReverse() {
		// 1.修改数据
		for (PkgBean pkg : mPkgSet) {
			if(pkg.packageName.equals(getPackageName())){
				continue;//屏蔽自己的应用
			}
			pkg.isChecked = !pkg.isChecked;//取反
		}
		// 2.刷新UI
		mAdapter.notifyDataSetChanged();
	}

	private void clickAll() {
		// 1.修改数据
		for (PkgBean pkg : mPkgSet) {
			if(pkg.packageName.equals(getPackageName())){
				continue;//屏蔽自己的应用
			}
			pkg.isChecked = true;
		}
		// 2.刷新UI
		mAdapter.notifyDataSetChanged();
	}
	
	//点击ListView header事件
	@Override
	public void onHeaderClick(StickyListHeadersListView l, View header,
			int itemPosition, long headerId, boolean currentlySticky) {
		ProcessBean bean = mListDatas.get(itemPosition);//点击的item
		PkgBean pkg = bean.pkg;
		
		if(pkg.packageName.equals(getPackageName())){
			return;//不让选中当前
		}
		
		pkg.isChecked = !pkg.isChecked;
		
		//刷新UI
		mAdapter.notifyDataSetChanged();
	}
	
	//key:pid，value：进程占用的内存
	private Map<Integer, Long> killMap;
	private void killProcess() {
		killMap = new HashMap<Integer, Long>();
		int killCount = 0;
		long killMemory = 0;
		//获取任务管理器
		//遍历数据，true
		for (PkgBean pkg : mPkgSet) {
			String packageName = pkg.packageName;
			if(packageName.equals(getPackageName())){
				continue;//不杀死自己
			}
			
			if(pkg.isChecked){
				ProcessProvider.killProcess(mContext, packageName);//杀死进程
				
				ListIterator<ProcessBean> listIterator = mListDatas.listIterator();
				while(listIterator.hasNext()){
					ProcessBean bean = listIterator.next();
					
					if(bean.pkg.packageName.equals(packageName)){//移除杀死的包名
						killMap.put(bean.pid, bean.processMemory);
						listIterator.remove();
						//killCount ++; 重复进程问题
					}
				}
			}
			
		}
		
		mAdapter.notifyDataSetChanged();
		//更新头部UI
		for(Map.Entry<Integer, Long> entry : killMap.entrySet()){
			killMemory += entry.getValue();
		}
		killCount = killMap.size();//正确计算方式
		mRunningProcessCount -= killCount;//
		setProcessCount();
		
		mUsedMemory -= killMemory;
		setProcessMemory();
		
		String text = "杀死"+ killCount +"个进程,释放"+Formatter.formatFileSize(mContext, killMemory);
		ToastUtils.makeText(mContext, text);
	}

}
