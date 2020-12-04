package com.bala.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.bean.ProcessBean;
import com.bala.mobilesafe.business.ProcessProvider;
import com.bala.mobilesafe.receiver.ProcessWidget;
import com.bala.mobilesafe.util.ToastUtils;

import java.util.List;

public class ProcessWidgetService extends Service {

	private ProcessClearReceiver mReceiver;
	boolean isCancel;


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		//先注册 
		regiseterReceiver();
		
		//再发送
		
		new Thread(){
			public void run() {
				while(!isCancel){
					//更新窗口小部件内容。
					
					//1. 查询目前运行的进程有几个  占用的内存是多少
					int count = ProcessProvider.getRunningProcessCount(ProcessWidgetService.this);
					long useMemory = ProcessProvider.getUsedMemory(ProcessWidgetService.this);
					long totalMemory = ProcessProvider.getTotalMemory(ProcessWidgetService.this);
					
					//2.  更新到小部件上。 
					/*
					 * 获取一个类的对象
					 * 	1. 直接new
					 * 2. 单例模式 、 这个类身上有一个静态方法
					 * 3. 工厂模式 、 构建者模式 。  alertdialog.builder   BitmapFactory
					 * 
					 */
					AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(ProcessWidgetService.this);
					
					
					//使用小部件的管理者去更新小部件上的内容 , 系统会找到这个类，然后找到所有与这个类有关的那些窗口小部件，更新上面的内容。
					//参数一： Component name 组件名   组件名其实就是对某一个组件的包装。 里面包含两个成员， 包名 、 类名。
					ComponentName provider = new ComponentName(ProcessWidgetService.this, ProcessWidget.class);
					
					//参数二： RemoteView
					RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
					
					//把这个布局身上的这两个控件的值编程 括号的第二个参数
					views.setTextViewText(R.id.process_count, "正在运行的进程:"+count+"个");
					views.setTextViewText(R.id.process_memory, "可用的内存:"+Formatter.formatFileSize(ProcessWidgetService.this, totalMemory - useMemory ));
					

					
					//点击桌面小部件按钮，打印log 点击这个按钮，就发送一个广播出来。
					/*
					 * 参数一： 上下文
					 * 参数二： 请求码 requestcode
					 * 参数三： 意图对象 用于配置谁才能收这个广播
					 * 参数四： 就直接写0  ， 不使用系统规定的那些配置
					 */
					
					//发送一个广播， 请问 intent在这怎么写。 代表意思。 意图。。。
					
					Intent intent =new Intent();
					intent.setAction("com.itheima.widget.clear");
					
					
					//点击一键清理之后，发送一个广播出来
					PendingIntent pendingIntent = PendingIntent.getBroadcast(ProcessWidgetService.this, 1, intent, 0);
					views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
					
					
					//让桌面小部件管理者去 ，更新 ProcessWidget 相关的那些所有小部件， 更新他们的UI ，变成process_widget布局
					appWidgetManager.updateAppWidget(provider, views);
					
					SystemClock.sleep(1000);
				}
			};
		}.start();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//服务销毁，取消注册广播接收者
		unregisterReceiver(mReceiver);
		
		//取消后续的动作
		isCancel = true; 
	}

	
	/**
	 * 注册接收那个一键清理的广播接收者
	 */
	private void regiseterReceiver() {
		
		mReceiver = new ProcessClearReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.itheima.widget.clear");
		this.registerReceiver(mReceiver, filter);
		
	}


	class ProcessClearReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("有人点击了一键清理...");
			
				//1. 清理进程 + 内存
				int beforeCount = ProcessProvider.getRunningProcessCount(context);
				long beforeMemory = ProcessProvider.getUsedMemory(context);
			
				//a. 先获取所有的进程
				
				//b. 清理进程。
				
				//c. 比较前后，就知道清理了多少个进程。
				List<ProcessBean> runList = ProcessProvider.getProcessList(context);
				for (ProcessBean processBean : runList) {
					if(processBean.pkg.packageName.equals(getPackageName())){
						//跳过当前循环，执行下一次循环
						continue;
					}
					ProcessProvider.killProcess(context, processBean.pkg.packageName);
				}
				
				int afterCount = ProcessProvider.getRunningProcessCount(context);
				long afterMemory = ProcessProvider.getUsedMemory(context);
		
				//有进程被干掉了
				if(beforeCount > afterCount){
					
					int count = beforeCount - afterCount;
					long memory = beforeMemory - afterMemory;
					
					String text = "清理了"+count+"个进程，释放了"+Formatter.formatFileSize(context, memory)+"内存";
					
					ToastUtils.makeText(context, text);
				}else{
					ToastUtils.makeText(context, "没有可清理的进程");
				}
			
			
			//2. 清理了多少个进程，释放了多少内存。   没有可清理的进程。
			
			
		}
	}

}
