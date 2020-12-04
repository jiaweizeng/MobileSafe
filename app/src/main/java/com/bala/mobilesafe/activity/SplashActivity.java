package com.bala.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.service.ProtectingService;
import com.bala.mobilesafe.util.FileUtils;
import com.bala.mobilesafe.util.GzipUtils;
import com.bala.mobilesafe.util.IOUtils;
import com.bala.mobilesafe.util.LogUtil;
import com.bala.mobilesafe.util.PackageUtils;
import com.bala.mobilesafe.util.PreferencesUtils;
import com.bala.mobilesafe.util.ServiceUtils;
import com.bala.mobilesafe.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class SplashActivity extends Activity {
	
	private static final String TAG = "SplashActivity";
	protected static final int MSG_LOAD_HOME = 1000;
	protected static final int MSG_UPDATE = 1001;//更新版本
	protected static final int MSG_INSTALL_APK = 1002;
	
	
	private static final int MSG_ERROR_NET = -100;//异常状态码
	
	protected static final int CONNECTION_TIMEOUT = 2000;//连接超时时间
	protected static final int READ_TIMEOUT = 2000;//响应超时时间
	private TextView mTvVersion;
	private Context mContext;
	private int mLocalVersion;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			//处理消息
			switch (msg.what) {
			case MSG_LOAD_HOME:
				loadHomePage();
				break;
			case MSG_UPDATE:
				String content = (String) msg.obj;
				showUpdateDialog(content);
				break;
			case MSG_INSTALL_APK:
				String apk = (String) msg.obj;
				installApk(apk);
				break;
			case MSG_ERROR_NET:
				ToastUtils.makeText(mContext, (String)msg.obj);
				//进入主页
				loadHomePage();
				break;
			default:
				break;
			}
		};
	};
	protected String downLoadUrl;

	//显示View
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//绑定布局文件
		setContentView(R.layout.activity_splash);
		initView();
		initData();
		
		
	}

	private void initView() {
		mTvVersion = (TextView)findViewById(R.id.tv_splash_version);
	}

	
	private void initData() {
		
		performInstallShortCut();
		
		mContext = this;
		
		mTvVersion.setText("版本：" + PackageUtils.getVersionName(mContext));
		int netVersion = 2;//访问网络接口，获取服务器返回json参数
		mLocalVersion = PackageUtils.getVersionCode(mContext);
		
		boolean updateStatus = PreferencesUtils.getBoolean(mContext, PreferencesUtils.KEY_UPDATE_STATUS, true);
		if(updateStatus){
			getNetVersion();
		}else{
			Message msg = mHandler.obtainMessage();
			msg.what = MSG_LOAD_HOME;
			mHandler.sendMessageDelayed(msg , 2000);
		}
		
		//copyAddressDB();
		unzipAddressDB();
		copyCommonNumberDB();
		copyAntivirusDB();
		
		//开始前台进程服务
		if(!ServiceUtils.isServiceRunning(mContext, ProtectingService.class)){
			//没有开启，再开启
			Intent service = new Intent(mContext, ProtectingService.class);
			startService(service);
		}
	}

	


	/**
	 * 处理安装快捷图标
	 * 
	 * 要显示一个快捷图标，光发广播是不够的，还需要给定 icon + label ＋　intent对象 (点击图标的跳转意图)
	 */
	private void performInstallShortCut() {
		
	/*	 Intent intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
	        String name = data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
	        Parcelable bitmap = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);*/
		boolean flag = PreferencesUtils.getBoolean(this,  PreferencesUtils.KEY_SHORT_CUT, false);
		if(flag){
			return;
		}
		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Bala手机卫士");
		
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.heima));
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(this,SplashActivity.class));
		
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		sendBroadcast(intent);
		
		PreferencesUtils.setBoolean(this, PreferencesUtils.KEY_SHORT_CUT, true);
	}

	private void copyAddressDB() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//asserts目录下的数据库拷贝到data/data/package/files/address.db
				//2.输出：
				File datafile = new File(getFilesDir(), "address.db");
				if(datafile.exists()){
					LogUtil.d(TAG, "已经存在，不需要拷贝数据库");
					return;
				}
				//1.获取源文件
				InputStream is = null;
				FileOutputStream fos = null;
				try {
					is = getAssets().open("address.db");
					fos = new FileOutputStream(datafile);
					
					//3.缓冲区
					byte[] buffer = new byte[1024];
					int len = 0;
					while((len = is.read(buffer)) != -1){
						//写入文件
						fos.write(buffer, 0, len);
					}
					
					
					LogUtil.d(TAG, "拷贝数据库");
					
				} catch (IOException e) {
					e.printStackTrace();
				} finally{
					IOUtils.close(is);
					IOUtils.close(fos);
				}
			}
		}).start();
	}

	private void getNetVersion() {
		new Thread(new Runnable() {
			

			@Override
			public void run() {
				try {
					URL url = new URL("http://10.0.2.2:8080/versionUpdate.json");
					URLConnection connection = url.openConnection();
					//设置连接超时时间
					connection.setConnectTimeout(CONNECTION_TIMEOUT);
					//服务响应超时时间
					connection.setReadTimeout(READ_TIMEOUT);
					//获取服务器返回流
					InputStream is = connection.getInputStream();
					//转成字符串
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int len = -1;
					while((len = is.read(buffer)) != -1){
						bos.write(buffer, 0, len);
					}
					String result = bos.toString();
					LogUtil.d(TAG, "服务器返回数据="+result);
					
					//解析json
					JSONObject json = new JSONObject(result);
					//int version = json.getInt("version");//正式开放不建议使用，当key不存在，空指针
					int version = json.optInt("version");//正式开放建议使用，当key不存在，返回默认值：0, "",0.0
					final String content = json.optString("content");
					downLoadUrl = json.optString("downloadUrl");
					
					if(version > mLocalVersion){
						LogUtil.d(TAG, "更新版本");
						//showUpdateDialog(content);
						Message msg = mHandler.obtainMessage();
						msg.what = MSG_UPDATE;
						msg.obj = content;
						mHandler.sendMessageDelayed(msg, 200);
					}else{
						LogUtil.d(TAG, "不更新版本");
						Message msg = mHandler.obtainMessage();
						msg.what = MSG_LOAD_HOME;
						mHandler.sendMessageDelayed(msg, 2000);
					}
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
					//网络连接超时，可以进入首页
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_ERROR_NET;
					msg.obj = "网络异常";
					mHandler.sendMessageDelayed(msg, 2000);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/**
	 * 加载主页
	 */
	private void loadHomePage() {
		
		Intent intent = new Intent(mContext, HomeActivity.class);
		startActivity(intent );
		finish();
	}

	private void showUpdateDialog(final String content) {
		//提示用户更新：对话框
//		runOnUiThread(new Runnable() {
//			public void run() {
//				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//				//设置标题、信息、按钮
//				builder.setTitle("版本更新提醒");
//				builder.setMessage(content);//通过服务器获取更新信息
//				builder.setCancelable(false);//外围点击
//				AlertDialog dialog = builder.create();
//				builder.setPositiveButton("立刻更新", new OnClickListener() {
//					
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						// TODO Auto-generated method stub
//					}
//				});
//				
//				builder.setNegativeButton("以后再说", new OnClickListener() {
//					
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						diaLogUtil.dismiss();
//					}
//				});
//				builder.show();
//			}
//		});
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		//设置标题、信息、按钮
		builder.setTitle("版本更新提醒");
		builder.setMessage(content);//通过服务器获取更新信息
		builder.setCancelable(false);//外围点击
		AlertDialog dialog = builder.create();
		builder.setPositiveButton("立刻更新", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//下载最新版本apk
				//访问网络获取服务器apk数据
				ProgressDialog dlg = new ProgressDialog(mContext);
				dlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				dlg.show();
				downLoadApk(dlg);
			}
		});
		
		builder.setNegativeButton("以后再说", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//进入主页
				loadHomePage();
			}
		});
		builder.show();
	}

	private void downLoadApk(final ProgressDialog dlg) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				InputStream io = null;
				FileOutputStream fos = null;
				try {
					URL url = new URL(downLoadUrl);
					URLConnection connection = url.openConnection();
					//设置超时时间
					connection.setConnectTimeout(CONNECTION_TIMEOUT);
					connection.setReadTimeout(READ_TIMEOUT);
					io = connection.getInputStream();
					//设置最大进度
					dlg.setMax(connection.getContentLength());
					//输入成apk文件
					//获取SD卡路径
					File file = new File(Environment.getExternalStorageDirectory(), "MobileSafe.apk");
					fos = new FileOutputStream(file);
					byte[] buffer = new byte[1024];
					int len = -1;
					int progress = 0;
					while((len = io.read(buffer)) != -1){
						//输出到文件
						fos.write(buffer, 0, len);
						//设置进度
						progress += len;
						dlg.setProgress(progress);
					}
					dlg.dismiss();
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_INSTALL_APK;
					msg.obj = file.getAbsolutePath();
					mHandler.sendMessage(msg);
					//安装新版本apk
					
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally{
//					if(io != null){
//						try {
//							io.close();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
					IOUtils.close(io);
					IOUtils.close(fos);
				}
			}
		}).start();
	}

	private void installApk(String apk) {
		//打开app安装器，一个应用，隐式
		Intent intent = new Intent();
		/* <intent-filter>
         <action android:name="android.intent.action.VIEW" />
         <category android:name="android.intent.category.DEFAULT" />
         <data android:scheme="content" />
         <data android:scheme="file" />
         <data android:mimeType="application/vnd.android.package-archive" />
     </intent-filter>*/
		//设置动作
		intent.setAction("android.intent.action.VIEW");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//安装完成打开
		//intent.setAction(Intent.ACTION_VIEW);
		intent.addCategory("android.intent.category.DEFAULT");
		  intent.setDataAndType(Uri.parse("file:" + apk),
                  "application/vnd.android.package-archive");
		
		startActivity(intent );
	}
	
	/**
	 * 将数据库压缩包 -> 解压到data/data/package/files目录，address.db
	 */
	private void unzipAddressDB() {
		final File destFile = new File(getFilesDir(), "address.db");
		if(destFile.exists()){
			LogUtil.d(TAG, "已经存在数据库，不需要解压");
			return;
		}
		LogUtil.d(TAG, "解压数据库");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//1.输入文件address.zip
				
				try {
					InputStream is = getAssets().open("address.zip");
					//File destFile = new File(getFilesDir(), "address.db");
					FileOutputStream fos = new FileOutputStream(destFile );
					GzipUtils.unzip(is, fos);
				} catch (IOException e) {
					e.printStackTrace();
				}
				//2.输出address.db
			}
		}).start();
	}
	

	private void copyCommonNumberDB() {
		File destFile = new File(getFilesDir(), "commonnum.db");
		if(destFile.exists()){
			LogUtil.d(TAG, "常用号码数据库存在，不需要拷贝");
			return;
		}
		LogUtil.d(TAG, "拷贝常用号码数据库");
		try {
			InputStream is = getAssets().open("commonnum.db");
			OutputStream os = new FileOutputStream(destFile );
			FileUtils.copyFile(is , os);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private void copyAntivirusDB() {
		File destFile = new File(getFilesDir(), "antivirus.db");
		if(destFile.exists()){
			LogUtil.d(TAG, "病毒数据库存在，不需要拷贝");
			return;
		}
		LogUtil.d(TAG, "拷贝常用病毒数据库");
		try {
			InputStream is = getAssets().open("antivirus.db");
			OutputStream os = new FileOutputStream(destFile );
			FileUtils.copyFile(is , os);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
