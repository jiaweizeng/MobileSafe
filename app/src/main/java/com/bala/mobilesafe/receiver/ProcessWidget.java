package com.bala.mobilesafe.receiver;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.bala.mobilesafe.service.ProcessWidgetService;

/**
 * 用于控制窗口小部件的类
 */
public class ProcessWidget extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		
		//为了能够长久性的存在更新的逻辑，所以此处选择使用服务去完成
		//在这个方法里面更新桌面小部件的内容。 
		context.startService(new Intent(context , ProcessWidgetService.class));
		
	}
	
	//小部件不可用了。
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		context.stopService(new Intent(context , ProcessWidgetService.class));
	}
}
