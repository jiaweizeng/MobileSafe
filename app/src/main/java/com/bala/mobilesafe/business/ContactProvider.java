package com.bala.mobilesafe.business;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

import com.bala.mobilesafe.bean.ContactBean;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class ContactProvider {

	public static List<ContactBean> getAllContacts(Context context) {
		List<ContactBean> list = new ArrayList<ContactBean>();
		// 查询联系人数据
		ContentResolver cr = context.getContentResolver();
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		//姓名、电话、联系人ID（唯一标识）
		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID };// 查询的列
		
		String selection = null;//查询条件
		String[] selectionArgs = null;//条件对应参数
		String sortOrder = null;//排序规则
		//数据集合
		Cursor cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		if(cursor != null){
			while(cursor.moveToNext()){
				String name = cursor.getString(0);
				String phone = cursor.getString(1);
				long contactId = cursor.getLong(2);
				
				ContactBean bean = new ContactBean();
				bean.name = name;
				bean.phone = phone;
				bean.contactId = contactId;
				list.add(bean );
			}
			
			cursor.close();
		}

		return list;
	}
	
	public static Bitmap getContactPhoto(Context context, long contactId){
		ContentResolver cr = context.getContentResolver();
		//content:contact/id
		Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactId));
		InputStream is = ContactsContract.Contacts.openContactPhotoInputStream(cr, contactUri);
		//流->Bitmap
		return BitmapFactory.decodeStream(is);
	}

}
