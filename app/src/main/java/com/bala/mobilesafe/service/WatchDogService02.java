package com.bala.mobilesafe.service;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;

import com.bala.mobilesafe.activity.VerifyActivity;
import com.bala.mobilesafe.dao.AppLockDao;

import java.util.ArrayList;
import java.util.List;

public class WatchDogService02 extends AccessibilityService {
	
	
	List<String> mVerifyList = new ArrayList<String>();
	private AppLockDao mDao;
	private List<String> mLockList;
	private ScreenLockReceiver mLockReceiver;
	private VerifyReceiver mReceiver;
	private DataContentObserver mObserver;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		

		mLockReceiver = new ScreenLockReceiver();
		IntentFilter lockFilter = new IntentFilter();
		lockFilter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mLockReceiver, lockFilter);
		
		mReceiver = new VerifyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.itheima.verify");
		registerReceiver(mReceiver, filter);
		
		//注册内容观察者
//		itheima://com.itheima.db.applock/234/234/124/3465/346/346
		Uri uri = Uri.parse("itheima://com.itheima.db.applock");
		mObserver = new DataContentObserver(new Handler());
		
		//参数一： 注册的观察者一直观察这个地址路径，注意要与发通知的路径保持一致
		//参数二： true : 监听的地址与发布通知的地址 ，只要前半段匹配，也能收到通知， false : 两者必须一模一样
		//参数三： 如果收到了通知，那么调用哪个观察者。
		getContentResolver().registerContentObserver(uri, true, mObserver);
		
		mDao = new AppLockDao(this);
		mLockList = mDao.query();
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		
		//获取正在运行的这个程序包名
		String packageName = event.getPackageName().toString();
		
		//问一问这个程序是否在验证的集合中
		if(mVerifyList.contains(packageName)){
			return;
		}
		
		
		if(mLockList.contains(packageName)){
			System.out.println(packageName+ "这个程序是加锁程序");
			
			//改程序已经验证过了呀。
			Intent intent = new Intent(this , VerifyActivity.class) ;
			intent.putExtra("packageName", packageName);
			//这里要添加这个标记，因为也许这个服务是咱们应用最后残留的一个组件，所有的activity都已经被干掉了，
			//那么这个时候是没有任何任务栈存在的，所以必须加上这个。系统会为这个activity开启新的任务栈，并且
			//把这个activity的实例放置到这个栈中
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}else{
			System.out.println(packageName+ "这个程序未加锁");
		}
	}

	@Override
	public void onInterrupt() {

	}
	
	class ScreenLockReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			
			mVerifyList.clear();
		}
	}
	
	
	class VerifyReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			
			//除了收到验证通过的通知之外， 服务还应该知道，到底现在是谁已经验证通过了。
			String packageName = intent.getStringExtra("packageName");
			
			//验证过的程序，都装这个集合中
			mVerifyList.add(packageName);
			
		}
		
	}
	
	class DataContentObserver extends ContentObserver{
		public DataContentObserver(Handler handler) {
			super(handler);
		}
		
		//如果收到了通知，该方法将会调用
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			
			mLockList = mDao.query();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("电子狗服务01已经停止");
		
		unregisterReceiver(mReceiver);
		unregisterReceiver(mLockReceiver);
		getContentResolver().unregisterContentObserver(mObserver);
		
		if(mVerifyList != null){
			mVerifyList.clear();
			mVerifyList = null;
		}
		
	}

}
