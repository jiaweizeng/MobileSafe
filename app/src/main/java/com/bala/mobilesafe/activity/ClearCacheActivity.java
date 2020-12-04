package com.bala.mobilesafe.activity;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.adapter.ClearCacheAdapter;
import com.bala.mobilesafe.bean.AppBean;
import com.bala.mobilesafe.bean.CacheBean;
import com.bala.mobilesafe.pm.IPackageDataObserver;
import com.bala.mobilesafe.pm.IPackageStatsObserver;
import com.bala.mobilesafe.util.PackageUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 这是缓存清理
 */
public class ClearCacheActivity extends Activity implements OnClickListener, OnScrollListener {

	private ImageView mIvIcon;
	private ImageView mIvLine;
	private ProgressBar mProgressBar;
	private TextView mIvLabel;
	private static  ListView mListView ;
	private List<CacheBean> mList;
	private ClearCacheAdapter mAdapter;
	private TextView mIvCacheSize;

	int mCacheCount = 0 ;
	long mCacheSize = 0 ;
	private TextView mTvResult;
	private Button mBtnQuickScan;
	private Button mBtnOneKeyClear;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clear_cache);
		
		initView();
		initData();
		initListener();
		
	}
	
	public void initView() {

		mIvIcon = (ImageView) findViewById(R.id.iv_icon);
		mIvLine = (ImageView) findViewById(R.id.iv_line);
		mIvLabel = (TextView) findViewById(R.id.tv_label);
		mIvCacheSize = (TextView) findViewById(R.id.tv_cache_size);
		mBtnQuickScan = (Button) findViewById(R.id.btn_quick_scan);
		mBtnOneKeyClear = (Button) findViewById(R.id.btn_one_key_clear);
		mTvResult = (TextView) findViewById(R.id.tv_result);
		mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
		
		mListView = (ListView) findViewById(R.id.listview);
		
		mRlResult = findViewById(R.id.rl_result);
		mLlScan = findViewById(R.id.ll_scan);
		
		
	
		
	}

	public void initData() {
		mList = new ArrayList<CacheBean>();
		mAdapter = new ClearCacheAdapter(this , mList);
		mListView.setAdapter(mAdapter);
		
		
		startScanTask();
		
		
		
/*
	startScanAnimation();
		
		new Thread(){
			@Override
			public void run() {
				//1. 获取所有的应用程序
				List<AppBean> list = PackageUtils.getAppData(ClearCacheActivity.this);
				
				
				//2. 遍历所有程序，然后让每一个程序都到上面去走一回。
				for (final AppBean appBean : list) {
					runOnUiThread(new Runnable() {
						public void run() {
							mIvIcon.setImageDrawable(appBean.icon);
						}
					});
					SystemClock.sleep(80);
				}
				
			}
		}.start();
		*/
	
		
	}

	public void initListener() {
		mBtnQuickScan.setOnClickListener(this);
		mBtnOneKeyClear.setOnClickListener(this);
		mListView.setOnScrollListener(this);
	}
	
	/**
	 * 开始扫描动画
	 */
	private void startScanAnimation(){
		
		//1. 定义一个平移动画
		/*
			Animation.ABSOLUTE 用于定义后面的value 的值是 绝对值的像素点。
			Animation.RELATIVE_TO_SELF,  用于定义后面的value 是当前这个控件的宽度或者高度的 倍数值
			Animation.RELATIVE_TO_PARENT  用于定义后面的value 是当前这个控件的父容器宽度或者高度的 倍数值
		*/
		TranslateAnimation anim = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0, 
				Animation.RELATIVE_TO_PARENT, 0, 
				Animation.RELATIVE_TO_PARENT, 0, //从这个线条的目前位置开始 
				Animation.RELATIVE_TO_PARENT, 1); //相对父容器的高度 1倍
		
		//设置无限循环
		anim.setRepeatCount(Animation.INFINITE);
		
		//设置重复模式 ： 反向循环
		anim.setRepeatMode(Animation.REVERSE);
		
		//设置动画时长
		anim.setDuration(1000);
		
		//开始动画
		mIvLine.startAnimation(anim);
	}
	
	/**
	 * 停止扫描动画
	 */
	private void stopScanAnimation() {
		
		//清除动画
		mIvLine.clearAnimation();
		
		
	}
	
	private void startScanTask(){
		
		mTask = new ScanTask();
		mTask.execute();
	}
	
	class ScanTask extends AsyncTask <Void , CacheBean ,Void>{
		
		List<AppBean> appList ;
		int progress;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			mBtnOneKeyClear.setEnabled(false);
			
			mRlResult.setVisibility(View.GONE);
			mLlScan.setVisibility(View.VISIBLE);
			
			//重置 缓存的总数 总大小 ， listView的条目数据 
			mCacheCount=0;
			mCacheSize = 0 ;
			mList.clear();
			
			startScanAnimation();
		}

		@Override
		protected Void doInBackground(Void... params) {
			//1. 获取所有的应用程序
			appList = PackageUtils.getAppData(ClearCacheActivity.this);
			//2. 遍历所有程序，然后让每一个程序都到上面去走一回。
			for (final AppBean appBean : appList) {
				//循环一次， 构建一个cachebean对象
				//CacheBean bean = new CacheBean(appBean.icon, appBean.name , mSize); //size
				
				//每次循环，都先看一看，现在任务是否已经取消了，取消的话，就直接renturn . 任务退出
				if(isCancel){
					return null;
				}
				
				
				System.out.println("将要去获取缓存，并且更新条目数据...."+appBean.packageName);
				
				//让进度叠加
				progress++;
				//马上去读取这个程序的缓存。
				readCache(appBean.packageName);
				//马上更新一UI
				SystemClock.sleep(50);
			}
			return null;
		}
		
		public void updateProgress(CacheBean bean){
			publishProgress(bean);
		}
		
		@Override
		protected void onProgressUpdate(CacheBean... values ) {
			CacheBean bean  =  values[0];
			mIvIcon.setImageDrawable(bean.icon);
			mIvLabel.setText(bean.label);
			mIvCacheSize.setText("缓存大小："+Formatter.formatFileSize(ClearCacheActivity.this, bean.cacheSize));
			
			mProgressBar.setMax(appList.size());
			mProgressBar.setProgress(progress);
			
			
			if(bean.cacheSize > 0 ){
				mList.add(0 , bean);
				mCacheCount++;
				mCacheSize+=bean.cacheSize;
			}else{
				mList.add( bean);
			}
			mAdapter.notifyDataSetChanged();
			mListView.smoothScrollToPosition(mList.size());
		}
		
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			stopScanAnimation();
			
			
			mBtnOneKeyClear.setEnabled(true);
			//显示结果视图
			mRlResult.setVisibility(View.VISIBLE);
			mLlScan.setVisibility(View.INVISIBLE);
			
			mTvResult.setText("共有"+mCacheCount+"个程序有缓存，大小为:"+Formatter.formatFileSize(ClearCacheActivity.this, mCacheSize));
			
			mListView.smoothScrollToPosition(0);
		}
	}
	
	private void readCache(String packageName){
		//1. 得到包管理器
				PackageManager pm = getPackageManager();
				//pm.getpack
				try {
					//2. 反射得到方法的引用
					Method method = pm.getClass().getMethod("getPackageSizeInfo", String.class , IPackageStatsObserver.class);
					
					//3. 调用该方法 ， 参数一： 调用的实例对象， 参数二： 上面说明要用的第一个参数String , 参数三 IPackageStatsObserver
					method.invoke(pm, packageName ,mStatsObserver );
				} catch (Exception e) {
					e.printStackTrace();
				}
	}
	
	long mSize;
	
	IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
        public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
        	
        	if(isCancel){
				return ;
			}
        	
//        	icon  + label   -- packagename
        	//由于这里才有缓存数据，所以对bean的封装，统一放到这里来。
			try {
				 PackageManager pm = getPackageManager() ; 
				 ApplicationInfo appInfo = pm.getApplicationInfo(stats.packageName, 0);
				 
				 Drawable icon = appInfo.loadIcon(pm);
				 String label = appInfo.loadLabel(pm).toString();
				 long cacheSize = stats.cacheSize ; 
				 
				 //借助中间的方法去调用。
				 CacheBean bean = new CacheBean(icon,label , stats.packageName,  cacheSize);
				 
				 //调用任务里面自定义的更新进度方法
				 mTask.updateProgress(bean);
				 
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
	};
	private ScanTask mTask;
	private View mLlScan;
	private View mRlResult;
	private Object mClearCacheObserver;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_quick_scan:
			clickQuickScan();
			break;
		case R.id.btn_one_key_clear:
			clickOneKeyClear();
			break;
		}
	}

	/**
	 * 一键清理
	 */
	private void clickOneKeyClear() {
		try {
			
			if (mClearCacheObserver == null) {
                mClearCacheObserver = new ClearCacheObserver();
            }
			
			
			PackageManager pm = getPackageManager();
			Method method = pm.getClass().getMethod("freeStorageAndNotify", long.class , IPackageDataObserver.class );
			
			//Long.MAX_VALUE 代表我们想要释放这么大的缓存空间
			method.invoke(pm,  Long.MAX_VALUE, mClearCacheObserver );
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 快速扫描
	 */
	private void clickQuickScan() {
		//重新再扫描一次。
		
		startScanTask();
	}
	
	
	

	//如果清除成功，会回调下面的方法
	class ClearCacheObserver extends IPackageDataObserver.Stub {

		//这个方法是子线程执行
		public void onRemoveCompleted(final String packageName, final boolean succeeded) {
			System.out.println("threadname=="+Thread.currentThread().getName());
			
			//重新再扫描一次。
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					startScanTask();
				}
			});
		 }
	 }
	
	boolean isCancel;
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		isCancel = true;
		
		//mTask.cancel(true);
	}

	//只要滑动的状态发生了改变， 那么就调用
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
//		如果当前不是滑动状态，就可以点击
		//当前是空闲状态。
		if(scrollState == SCROLL_STATE_IDLE){
			mBtnOneKeyClear.setEnabled(true);
			mBtnQuickScan.setEnabled(true);
		}else{
			mBtnQuickScan.setEnabled(false);
			mBtnOneKeyClear.setEnabled(false);
		}
	}

	//只要有滑动， 那么就调用
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
	}

}
