package com.bala.mobilesafe.business;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;

import com.bala.mobilesafe.bean.PkgBean;
import com.bala.mobilesafe.bean.ProcessBean;
import com.bala.mobilesafe.util.ChineseToEnglishUtils;
import com.bala.mobilesafe.util.PackageUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 进程管理工具类
 * 
 * @author developer
 * 
 */
public class ProcessProvider {

	/**
	 * 获取当前运行进程个数
	 * 
	 * @return
	 */
	public static int getRunningProcessCount(Context context) {
		// 任务管理器
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
		return processes.size();
	}

	/**
	 * 获取总的进程个数
	 * 
	 * @param context
	 * @return
	 */
	public static int getTotalProcessCount(Context context) {
		// 任务管理器
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		Set<String> set = new HashSet<String>();// 存储进程名字
		// 获取包管理器
		PackageManager pm = context.getPackageManager();
		// List<ApplicationInfo> applications = pm.getInstalledApplications(0);
		List<PackageInfo> packages = pm.getInstalledPackages(0);
		// 遍历所有应用程序清单文件
		for (PackageInfo packageInfo : packages) {
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			// 1.application节点
			String processName = applicationInfo.processName;// 获取进程名字
			set.add(processName);

			// 2.activity节点
			ActivityInfo[] activities = packageInfo.activities;
			if (activities != null) {
				for (ActivityInfo activityInfo : activities) {
					set.add(activityInfo.processName);
				}
			}

			// 3.service
			ServiceInfo[] services = packageInfo.services;
			if (services != null) {
				for (ServiceInfo serviceInfo : services) {
					set.add(serviceInfo.processName);
				}
			}

			// 4.receiver
			ActivityInfo[] receivers = packageInfo.receivers;
			if (receivers != null) {
				for (ActivityInfo activityInfo : receivers) {
					set.add(activityInfo.processName);
				}
			}

			// 5.provider
			ProviderInfo[] providers = packageInfo.providers;
			if (providers != null) {
				for (ProviderInfo providerInfo : providers) {
					set.add(providerInfo.processName);
				}
			}
		}

		return set.size();
	}

	/**
	 * 获取使用内存大小：和进程有关
	 * 
	 * @param context
	 * @return
	 */
	public static long getUsedMemory(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		PackageManager pm = context.getPackageManager();
		long memory = 0;

		List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
		for (RunningAppProcessInfo process : processes) {

			int pid = process.pid;

			/*
			 * MemoryInfo outInfo = new MemoryInfo(); am.getMemoryInfo(outInfo
			 * ); long totalMemory = outInfo.totalMem;//api 16以上才可以使用
			 */
			android.os.Debug.MemoryInfo memoryInfo = am
					.getProcessMemoryInfo(new int[] { pid })[0];// 获取单个进程占用内存清空
			memory += memoryInfo.getTotalPss() * 1024;// 单位kb-> byte

		}
		return memory;
	}

	/**
	 * 获取所有内存
	 * 
	 * @param context
	 * @return
	 */
	public static long getTotalMemory(Context context) {
		// 获取总的内存，借助文件 /proc/meminfo

		String readLine = null;

		try {
			File file = new File("/proc/meminfo");// 手机设备内存描述文件
			BufferedReader reader = new BufferedReader(new FileReader(file));
			readLine = reader.readLine();// 读取一行 MemTotal: 511056 kB
			readLine = readLine.replace("MemTotal:", "");
			readLine = readLine.replace("kB", "");
			readLine = readLine.trim();// 剩下内存总量 KB

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Long.parseLong(readLine) * 1024;
	}

	public static List<ProcessBean> getProcessList(Context context) {
		List<ProcessBean> datas = new ArrayList<ProcessBean>();
		
		// 描述多个进程对应应用程序：key-包名，value-进程列表
		Map<String, List<ProcessBean>> map = new HashMap<String, List<ProcessBean>>();
		//获取任务管理器
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//获取当前运行进程信息
		List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
		for (RunningAppProcessInfo process : processes) {
			//获取每一个进程信息
			String processName = process.processName;//进程名字
			int pid = process.pid;
			android.os.Debug.MemoryInfo memoryInfo = am.getProcessMemoryInfo(new int[] { pid })[0];// 获取
			long memory = memoryInfo.getTotalPss() * 1024;//内存
			//process.
			
		
			//bean.pkg = null;// TODO
			
			String[] pkgList = process.pkgList;//进程对应应用程序包名
			for (String pkgName : pkgList) {//变量所有包名
				
				ProcessBean bean = new ProcessBean();
				bean.processMemory = memory;
				bean.processName = processName;
				bean.pid = pid;
				
				List<ProcessBean> list = map.get(pkgName);
				if(list == null){//没有存储过
					list = new ArrayList<ProcessBean>();
					map.put(pkgName, list);
				}
				
				list.add(bean);
			}
		}
		
		//循环变量map集合，构造pkgBean对象
		for(Map.Entry<String, List<ProcessBean>> entry : map.entrySet()){
			String packageName = entry.getKey();
			List<ProcessBean> list = entry.getValue();
			
			String appName = PackageUtils.getAppName(context, packageName);
			Drawable icon = PackageUtils.getAppIcon(context, packageName);
			
			PkgBean pkg = new PkgBean();
			pkg.packageName = packageName;
			pkg.name = appName;
			pkg.icon = icon;
			pkg.firstLetter = ChineseToEnglishUtils.getPinYinHeadChar(appName).substring(0, 1);
			
			for (ProcessBean bean : list) {
				bean.pkg = pkg;
			}
			datas.addAll(list);
		}
		
		return datas;
	}
	
	/**
	 * 简单方法
	 * @param context
	 * @return
	 */
	public static List<ProcessBean> getProcessSimpleList(Context context) {
		List<ProcessBean> datas = new ArrayList<ProcessBean>();
		
		//获取任务管理器
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//获取当前运行进程信息
		List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
		for (RunningAppProcessInfo process : processes) {
			//获取每一个进程信息
			String processName = process.processName;//进程名字
			int pid = process.pid;
			android.os.Debug.MemoryInfo memoryInfo = am.getProcessMemoryInfo(new int[] { pid })[0];// 获取
			long memory = memoryInfo.getTotalPss() * 1024;//内存
			
			ProcessBean bean = new ProcessBean();
			bean.processMemory = memory;
			bean.processName = processName;
			bean.pid = pid;
			
			
			PkgBean pkg = new PkgBean();//进程对应的应用程序
			pkg.name = processName;
			pkg.packageName = processName;
			
			bean.pkg = pkg;
			
			datas.add(bean);
		}
		
		return datas;
	}
	
	/**
	 * 杀死进程
	 * @param context
	 * @param packageName 进程对应的应用程序包名
	 */
	public static void killProcess(Context context, String packageName){
		//获取任务管理器
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		am.killBackgroundProcesses(packageName);
	}

}
