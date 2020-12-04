package com.bala.mobilesafe.business;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Xml;

import com.bala.mobilesafe.bean.SmsBean;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 操作短信备份与还原功能
 * @author xiaomi
 *
 */
public class SmsProvider {
	
	/*
	 * 还原短信： 把xml的文件内容，放置到数据库里面。
	 */
	public static void restore(Context context) throws Exception {
		
		//1. 解析xml  Xml
		List<SmsBean> list = parseXml();
		
		//2. 添加到数据库  ContentResolver ： 专门用于获取其他应用使用ContentProvider暴露出来的那些数据库数据
		ContentResolver resolver =  context.getContentResolver();
		Uri uri  = Uri.parse("content://sms");
		
		ContentValues values =new ContentValues();
		
		for (SmsBean smsBean : list) {
			
			//参数一： 对应的列名 ， 参数二： 列要放的值
			values.put("address", smsBean.address);
			values.put("body", smsBean.body);
			values.put("date", smsBean.date);
			values.put("type", smsBean.type);
			
			resolver.insert(uri, values);
		}
		
	}
	
	//解析xml
	public static List<SmsBean> parseXml() throws Exception{
		
		
		List<SmsBean> list = new ArrayList<SmsBean>();
				
		
		//1. 获取pull解析器
		XmlPullParser parser = Xml.newPullParser();
		
		//2. 指定解析的数据源
		parser.setInput(new FileInputStream("/mnt/sdcard/smss.xml"), "utf-8");
		
		//3. 开始解析
		//得到当前指针指向的事件类型 ： 指向了哪一个元素节点。
		int type = parser.getEventType();
		
		
		SmsBean bean = null;
		//只要指的不是文档的结束
		while(type != XmlPullParser.END_DOCUMENT){
			//假设现在是开始的节点
			if(type == XmlPullParser.START_TAG){
				
				//获取现在这个节点的名称
				String name = parser.getName();
				//如果现在开始的节点是sms ，那么创建对象
				if("sms".equals(name)){
					bean = new SmsBean();
					
				}else if("address".equals(name)){
					String address = parser.nextText();
					bean.setAddress(address);
				}else if("body".equals(name)){
					String body = parser.nextText();
					bean.setBody(body);
				}else if("date".equals(name)){
					String date = parser.nextText();
					bean.setDate(date);
				}else if("type".equals(name)){
					String typeName = parser.nextText();
					bean.setType(typeName);
				}
				
			}
			
			
			if(type == XmlPullParser.END_TAG){
				//获取现在这个节点的名称
				String name = parser.getName();
				if("sms".equals(name)){
					list.add(bean);
				}
			}
			
			//让指针，往下移动。
			type = parser.next();
		}
		
		return list;
	}

	
	public void restore02(Context context , ResultListener listener){
		
		new RestoreTask(context , listener).execute();
	}
	
	
	class RestoreTask extends AsyncTask<Void , Integer , Boolean>{

		Context context;
		ProgressDialog dialog ;
		 ResultListener listener;
		
		public RestoreTask(Context context , ResultListener listener) {
			super();
			this.context = context;
			this.listener = listener;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			dialog = new ProgressDialog(context);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.show();
			
		}

		
		//还原短信：  解析xml ，添加数据库
		@Override
		protected Boolean doInBackground(Void... params) {
			
			try {
				List<SmsBean> list = parseXml();
				
				//2. 添加到数据库  ContentResolver ： 专门用于获取其他应用使用ContentProvider暴露出来的那些数据库数据
				ContentResolver resolver =  context.getContentResolver();
				Uri uri  = Uri.parse("content://sms");
				
				ContentValues values =new ContentValues();
				
				for (int i = 0; i < list.size(); i++) {
					SmsBean smsBean = list.get(i);
					
					
					//参数一： 对应的列名 ， 参数二： 列要放的值
					values.put("address", smsBean.address);
					values.put("body", smsBean.body);
					values.put("date", smsBean.date);
					values.put("type", smsBean.type);
					
					resolver.insert(uri, values);
					
					publishProgress(list.size() , i+1);
					
					SystemClock.sleep(150);
				}
				
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			
			dialog.setMax(values[0]);
			dialog.setProgress(values[1]);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			if(result){
				//ToastUtils.makeText(context, "还原成功");
				listener.onSuccess();
			}else{
//				ToastUtils.makeText(context, "还原失败");
				listener.onFailed();
			}
			
			dialog.dismiss();
		}
		
	}
	
	
	
	//----------------------------------------------
	//需求： 把短息存储到SD卡上的xml文件
	public static void backup(Context context){
		
		
		//1. 读取短信
		//ContentResolver : 内容解析者
		List<SmsBean> list = readSms(context);
		
		//2. 使用xml保存短信。
		/*<smss>
			<sms>
				<address>110</address>
				<body>1aaaaaa</body>
				<type>1</type>
				<date>1372937472937</date>
			</sms>
			<sms>
				<address>110</address>
				<body>1aaaaaa</body>
				<type>1</type>
				<date>1372937472937</date>
			</sms>
			....
		</smss>*/
		
		try {
			//1. 得到序列化器
			XmlSerializer xml = Xml.newSerializer();
			//2. 指定这个xml存放的位置
			OutputStream os = new FileOutputStream("/mnt/sdcard/smss.xml");
			xml.setOutput(os, "UTF-8");
			
			//3. 拼接xml
			
			xml.startDocument("utf-8", true);
			xml.startTag(null, "smss");
			
			
			for (SmsBean smsBean : list) {
				xml.startTag(null, "sms");
				
					xml.startTag(null, "address");
					xml.text(smsBean.address);
					xml.endTag(null, "address");
					
					xml.startTag(null, "body");
					xml.text(smsBean.body);
					xml.endTag(null, "body");
					
					xml.startTag(null, "date");
					xml.text(smsBean.date);
					xml.endTag(null, "date");
					
					xml.startTag(null, "type");
					xml.text(smsBean.type);
					xml.endTag(null, "type");
					
					
				xml.endTag(null, "sms");
			}
			
			xml.endTag(null, "smss");
			xml.endDocument();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 查询数据库中的所有短信，并且返回一个集合 
	 * @param context
	 * @return
	 */
	public static List<SmsBean> readSms(Context context){
		
		List<SmsBean> list =new ArrayList<SmsBean>();
		
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms");
		Cursor cursor = resolver.query(uri, new String[]{"address","body","type","date"}, null, null, null);
		while(cursor.moveToNext()){
			String address = cursor.getString(0);
			String body = cursor.getString(1);
			String type = cursor.getString(2);
			String date = cursor.getString(3);
			
			list.add(new SmsBean(address, body, type, date));
		}
		cursor.close();
		return list;
	}
	
	
	
	
	public  void backup02(Context context , ResultListener listener){
		
		new BackupTask(context , listener).execute();
	}
	
	//备份短信 ： 查询所有短信 + 存储到xml中。
	class BackupTask extends AsyncTask<Void, Integer, Boolean>{
		Context context;
		ProgressDialog dialog ;
		ResultListener listener ; 
		
		public BackupTask(Context context , ResultListener listener) {
			super();
			this.context = context;
			this.listener = listener;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			dialog = new ProgressDialog(context);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.show();
			
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			
			try {
				//3. 拼接xml
				//读取到了所有短信
				List<SmsBean> list = SmsProvider.readSms(context);
				//1. 得到序列化器
				XmlSerializer xml = Xml.newSerializer();
				//2. 指定这个xml存放的位置
				OutputStream os = new FileOutputStream("/mnt/sdcard/smss.xml");
				xml.setOutput(os, "UTF-8");
				xml.startDocument("utf-8", true);
				xml.startTag(null, "smss");
				//for (SmsBean smsBean : list) {
				for (int i = 0; i < list.size(); i++) {
					SmsBean smsBean = list.get(i);
					
					xml.startTag(null, "sms");

					xml.startTag(null, "address");
					xml.text(smsBean.address);
					xml.endTag(null, "address");

					xml.startTag(null, "body");
					xml.text(smsBean.body);
					xml.endTag(null, "body");

					xml.startTag(null, "date");
					xml.text(smsBean.date);
					xml.endTag(null, "date");

					xml.startTag(null, "type");
					xml.text(smsBean.type);
					xml.endTag(null, "type");

					xml.endTag(null, "sms");
					
					publishProgress(list.size() , i+1);
					
					SystemClock.sleep(300);
				}
				xml.endTag(null, "smss");
				xml.endDocument();
				
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			
			dialog.setMax(values[0]);
			dialog.setProgress(values[1]);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			if(result){
				//ToastUtils.makeText(context, "备份成功");
				listener.onSuccess();
			}else{
//				ToastUtils.makeText(context, "备份失败");
				listener.onFailed();
			}
			
			dialog.dismiss();
		}
		
	}
	
	//1. 先定义接口。 谁负责通知，谁就负责定义接口
	public interface ResultListener{
		
		//2. 接口里面有什么方法呢？ 要通知什么事情，就写什么方法。
		void onSuccess();
		
		void onFailed();
	}
	
	
	
	
	
	
	
}
