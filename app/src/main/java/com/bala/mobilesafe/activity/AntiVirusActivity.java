package com.bala.mobilesafe.activity;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.adapter.AntiAdapter;
import com.bala.mobilesafe.bean.AntivirusBean;
import com.bala.mobilesafe.bean.AppBean;
import com.bala.mobilesafe.dao.AntivirusDao;
import com.bala.mobilesafe.util.Md5Util;
import com.bala.mobilesafe.util.PackageUtils;
import com.github.lzyzsd.circleprogress.ArcProgress;

import java.util.ArrayList;
import java.util.List;

/**
 * 手机杀毒
 */
public class AntiVirusActivity  extends Activity implements OnClickListener{

	private TextView mTvLabel;
	private ArcProgress mArcProgress;
	private ListView mListView;
	private List<AntivirusBean> mList;
	private AntiAdapter mAdapter;
	private View mContainerResult;
	private View mContainerScan;
	private ImageView mIvRight;
	private ImageView mIvLeft;
	private View mContainerDoor;
	private Button mBtnReScan;
	private boolean isCancel;
	private AntivirusDao mDao;
	private TextView mTvResult;
	private int mVirusCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_antivirus);
		
		initView();
		initData();
		initListener();
		
	}
	
	public void initView() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mIvRight = (ImageView) findViewById(R.id.iv_right);
		mBtnReScan = (Button) findViewById(R.id.btn_rescan);

		mTvResult = (TextView) findViewById(R.id.tv_result);
		mTvLabel = (TextView) findViewById(R.id.tv_label);
		mArcProgress = (ArcProgress) findViewById(R.id.arc_progress);
		mListView = (ListView) findViewById(R.id.listview);
		
		mContainerScan = findViewById(R.id.rl_scan);
		mContainerResult = findViewById(R.id.ll_result);
		mContainerDoor = findViewById(R.id.ll_door);
		
		
	}

	public void initData() {
		
		registerReceiver();
		
		mDao = new AntivirusDao();
		
		mList = new ArrayList<AntivirusBean>();
		mAdapter = new AntiAdapter(this , mList);
		mListView.setAdapter(mAdapter);
		

		//一进入这个界面，就马上执行扫描任务
		startScanTask();
		
		
	}

	private void registerReceiver() {
		mReceiver = new AppUninstallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		registerReceiver(mReceiver, filter);
	}

	public void initListener() {
		mBtnReScan.setOnClickListener(this);
	}
	
	private void startScanTask(){
		new ScanTask().execute();
	}
	
	
//	---什么时候切割?---
	/**
	 * 负责切割图像
	 */
	private void splitImage(){
		
		//切割图像  ---切割谁 ?  --- 扫描的容器
		//设置这个容器在绘制的时候，保留缓存 。 并且保留高清图像
		mContainerScan.setDrawingCacheEnabled(true);
		mContainerScan.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
		
		//原型图
		Bitmap bitmap = mContainerScan.getDrawingCache();
		
		
		Bitmap leftBitmap = getLeftImage(bitmap);
		Bitmap rightBitmap = getRightImage(bitmap);
		
		mIvLeft.setImageBitmap(leftBitmap);
		mIvRight.setImageBitmap(rightBitmap);
	}
	
	/**
	 * 获取左边图像  但是我们不要整个，只要左边
	 * @param bitmap  原型图 整个画面。 
	 * @return
	 */
	private Bitmap getLeftImage(Bitmap bitmap){
		
		
		//如何从一个完整的图像中取出左边的一部分呢?  需要自己绘制，然后只绘制左边一半就可以了。
		
		//1. 创建空白纸张
		Bitmap copytBitmap = Bitmap.createBitmap(bitmap.getWidth() /2 , bitmap.getHeight(), bitmap.getConfig());
		
		//2. 创建画板 把空白的纸张丢上去。
		Canvas canvas = new Canvas(copytBitmap);
		
		//3. 创建画笔
		Paint paint = new Paint();
		
		//4. 创建矩阵 绘制出来1:1的比例
		Matrix matrix = new Matrix();
		
		//5. 开始作画  参数一： 参照谁去画
		canvas.drawBitmap(bitmap, matrix, paint);
		
		
		return copytBitmap;
	}
	/**
	 * 获取左边图像  但是我们不要整个，只要左边
	 * @param bitmap  原型图 整个画面。 
	 * @return
	 */
	private Bitmap getRightImage(Bitmap bitmap){
		
		
		//如何从一个完整的图像中取出左边的一部分呢?  需要自己绘制，然后只绘制左边一半就可以了。
		
		//1. 创建空白纸张
		Bitmap copytBitmap = Bitmap.createBitmap(bitmap.getWidth() /2 , bitmap.getHeight(), bitmap.getConfig());
		
		//2. 创建画板 把空白的纸张丢上去。
		Canvas canvas = new Canvas(copytBitmap);
		
		//3. 创建画笔
		Paint paint = new Paint();
		
		//4. 创建矩阵 绘制出来1:1的比例
		Matrix matrix = new Matrix();
		
		matrix.setTranslate(-bitmap.getWidth() /2, 0);
		
		//5. 开始作画  参数一： 参照谁去画
		canvas.drawBitmap(bitmap, matrix, paint);
		
		
		return copytBitmap;
	}
	
	/**
	 * 执行开门动画
	 * 
	 * 平移的距离 ：
	 * 
	 * 		1. 移动屏幕的一半
	 * 		2. 空间的宽度  mIvLeft  
	 * 				mIvRight.getWidth() --- 0 ; 因为 获取到的值是 0 。  控件从开始到显示  经过三个阶段  测量阶段  布局阶段  绘制阶段
	 * 
	 * 		3. 图片的宽度  bitmap
	 */
	private void startOpenDoorAnim(){
		AnimatorSet set = new AnimatorSet();
		
		//我们打算测量一下这个容器的宽度高度， 打算给定它的宽高是 0 ， 0  。
		mContainerDoor.measure(0, 0);
		
		
		set.playTogether(
				ObjectAnimator.ofFloat(mIvLeft, "translationX", 0 , -mIvLeft.getMeasuredWidth()),
				ObjectAnimator.ofFloat(mIvLeft, "alpha", 1 , 0),
				
				ObjectAnimator.ofFloat(mIvRight, "translationX", 0 , mIvRight.getMeasuredWidth()),
				ObjectAnimator.ofFloat(mIvRight, "alpha",1 , 0),
				
				
				ObjectAnimator.ofFloat(mContainerResult, "alpha",0 , 1)
				
				);
		
		set.setDuration(3000);
		
		set.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				mBtnReScan.setEnabled(false);
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				mBtnReScan.setEnabled(true);
				
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				
			}
		});
		
		set.start();
	}
	
	/**
	 * 关门动画
	 */
	private void startCloseDoorAnim(){
		AnimatorSet set = new AnimatorSet();
		
		//我们打算测量一下这个容器的宽度高度， 打算给定它的宽高是 0 ， 0  。
		mContainerDoor.measure(0, 0);
		
		
		set.playTogether(
				ObjectAnimator.ofFloat(mIvLeft, "translationX",-mIvLeft.getMeasuredWidth() , 0 ),
				ObjectAnimator.ofFloat(mIvLeft, "alpha",  0 , 1 ),
				
				ObjectAnimator.ofFloat(mIvRight, "translationX", mIvRight.getMeasuredWidth() ,0  ),
				ObjectAnimator.ofFloat(mIvRight, "alpha",0 , 1),
				
				
				ObjectAnimator.ofFloat(mContainerResult, "alpha",1 , 0)
				
				);
		
		set.setDuration(3000);
		
		
		//执行动画监听
		set.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				mBtnReScan.setEnabled(false);
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				mBtnReScan.setEnabled(true);
				//2. 执行扫描
				startScanTask();
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				
			}
		});
		set.start();
	}
	
	
	class ScanTask extends AsyncTask<Void ,AntivirusBean ,Void>{

		List<AppBean> appList  ;
		int progress;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			mTvResult.setText("您的手机很安全");
			mTvResult.setTextColor(Color.WHITE);
			
			mVirusCount = 0 ;
			
			mContainerScan.setVisibility(View.VISIBLE);
			mContainerDoor.setVisibility(View.GONE);
			mContainerResult.setVisibility(View.GONE);
			
			mList.clear();
			
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			
			//1. 获取所有程序，并且挨个遍历一下。
			appList = PackageUtils.getAppData(AntiVirusActivity.this);
			for (AppBean appBean : appList) {
				
				if(isCancel){
					return null;
				}
				
				progress++;
				
				//遍历每一个程序，就提取这个程序的MD5值  程序的APK 的MD5值
				String md5 = Md5Util.encodeFile(appBean.apkPath);
				boolean isVirus = mDao.isVirus(AntiVirusActivity.this, md5);
				
				System.out.println(appBean.name+"=="+md5);
				
				//更新进度 
				publishProgress(new AntivirusBean(appBean.name, appBean.icon, isVirus , appBean.packageName));
				SystemClock.sleep(50);
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(AntivirusBean... values) {
			super.onProgressUpdate(values);
			
			//告诉人家 最大值  +  进度值 。  progressbar  100  , 50 
			//mArcProgress.setMax(appList.size());
			//mArcProgress.setProgress(progress);

			
			/*进度值    最大值 
			50      100
			50 
			进度值 * 100 / 最大值*/
			mArcProgress.setProgress(progress*100/appList.size());
			
			AntivirusBean bean = values[0];
			mTvLabel.setText(bean.label);
			if(bean.isVirus){
				mList.add(0, bean);
				mVirusCount++;
			}else{
				mList.add(bean);
			}
			
			mAdapter.notifyDataSetChanged();
			mListView.smoothScrollToPosition(mList.size());
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mListView.smoothScrollToPosition(0);
			
			mContainerScan.setVisibility(View.GONE);
			mContainerResult.setVisibility(View.VISIBLE);
			
			//切割图像 切割完毕后，马上执行动画
			mContainerDoor.setVisibility(View.VISIBLE);
			
			if(mVirusCount>0){
				mTvResult.setText("您的手机很危险");
				mTvResult.setTextColor(Color.RED);
			}
			
			splitImage();
			startOpenDoorAnim();
			
		}
	}


	@Override
	public void onClick(View v) {
		clickReScan();
	}

	/**
	 * 重新扫描
	 */
	private void clickReScan() {
		//1. 执行关门动画
		startCloseDoorAnim();
	}
	
	
/*	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}*/
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		isCancel = true;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		if(isUnstall){
			startScanTask();
			isUnstall = false;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
	
	
	private boolean isUnstall;
	private AppUninstallReceiver mReceiver;
	class AppUninstallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//假设收到了已经有程序卸载的广播
			System.out.println("-----有程序已经被卸载了..需要重新扫描...");
			isUnstall = true;
		}
	}
	
}
