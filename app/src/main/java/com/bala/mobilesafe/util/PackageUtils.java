package com.bala.mobilesafe.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.text.format.Formatter;
import com.bala.mobilesafe.bean.AppBean;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author youliang.ji
 * 
 */
public class PackageUtils {

	/**
	 * 获取versionName
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		String versionName = null;
		PackageManager pm = context.getPackageManager();
		try {
			// 相当于清单文件对象
			PackageInfo packInfo = pm.getPackageInfo(context.getPackageName(),
					0);
			versionName = packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	public static int getVersionCode(Context context) {
		int versionCode = 1;
		PackageManager pm = context.getPackageManager();
		try {
			// 相当于清单文件对象
			PackageInfo packInfo = pm.getPackageInfo(context.getPackageName(),
					0);
			versionCode = packInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	public static List<AppBean> getAppData(Context context){
		List<AppBean> datas = new ArrayList<AppBean>();
		PackageManager pkgMrg = context.getPackageManager();
		List<PackageInfo> packageInfos = pkgMrg.getInstalledPackages(0);//获取安装应用程序信息
		for(PackageInfo packageInfo : packageInfos){//遍历所有应用程序信息
			//相当于清单文件对象
			ApplicationInfo info = packageInfo.applicationInfo;//每个应用程序的清单文件对象
			String name = info.loadLabel(pkgMrg).toString();//获取应用程序名称
			Drawable icon = info.loadIcon(pkgMrg);//图标
			int flags = info.flags;//可以通过flag判断安装路径、系统应用、用户应用
			boolean isSystem = isSystemApp(info);
			String packageName = info.packageName;
			
			boolean installSD = isInstallSD(info);
			
			String dataDir = info.dataDir;//data/data/package/目录
			String sourceDir = info.sourceDir;//apk文件所在目录
			File apkFile = new File(sourceDir);
			long length = apkFile.length();//apk文件大小
			String space = Formatter.formatFileSize(context, length);
			
			// TODO 暂时不获取安装路径
			
			AppBean bean = new AppBean();
			//设置数据
			bean.uid = info.uid;
			bean.apkPath = sourceDir;
			bean.name = name;
			bean.icon = icon;
			bean.space = space;
			bean.isInstallSD = installSD;
			bean.isSystem = isSystem;
			bean.packageName = packageName;
			datas.add(bean);
		}
		return datas;
	}

	/**
	 * 判断当前应用程序是否安装在SD卡
	 * 
	 * @param info
	 * @return
	 */
	public static boolean isInstallSD(ApplicationInfo info) {
		int flags = info.flags;// 所有标记
		return (ApplicationInfo.FLAG_EXTERNAL_STORAGE & flags) == ApplicationInfo.FLAG_EXTERNAL_STORAGE;
	}
	
	public static boolean isSystemApp(ApplicationInfo info) {
		int flags = info.flags;// 所有标记
		return (ApplicationInfo.FLAG_SYSTEM & flags) == ApplicationInfo.FLAG_SYSTEM;
	}
	
	/**
	 * 获取应用程序图标
	 * @param context
	 * @return
	 */
	public static Drawable getAppIcon(Context context, String packageName){
		PackageManager pm = context.getPackageManager();
		PackageInfo packageInfo = null;
		try {
			packageInfo = pm.getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return packageInfo.applicationInfo.loadIcon(pm);
	}
	
	public static String getAppName(Context context, String packageName){
		PackageManager pm = context.getPackageManager();
		String name = null;
		try {
			PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
			name = packageInfo.applicationInfo.loadLabel(pm).toString();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return name;
	}

}
