package com.bala.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * 软件管家数据模型
 * @author developer
 *
 */
public class AppBean {
	public Drawable icon;//图标 
	public String name;
	public boolean isInstallSD;//是否安装在SD卡
	public String space;//应用大小
	public boolean isSystem;//是否为系统应用
	public String packageName;
	public String apkPath; //应用程序的APK路径
	public int uid;
}
