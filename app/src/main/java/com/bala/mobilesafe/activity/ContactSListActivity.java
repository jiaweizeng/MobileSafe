package com.bala.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.bean.ContactBean;
import com.bala.mobilesafe.business.ContactProvider;

import java.util.ArrayList;
import java.util.List;

public class ContactSListActivity extends Activity implements OnItemClickListener {

	
	public static final String KEY_CONTACT_PHONE = "contact_phone";
	private ListView mListView;
	private ContactAdapter mAdapter;
	private List<ContactBean> mListDatas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_list);
		
		initView();
		initData();
		setListener();
	}

	

	private void setListener() {
		mListView.setOnItemClickListener(this);
	}



	private void initView() {
		mListView = (ListView)findViewById(R.id.lv_contacts);
	}
	
	private void initData() {
		mListDatas = new ArrayList<ContactBean>();
		//initContactTempData();
		mListDatas = ContactProvider.getAllContacts(this);
		mAdapter = new ContactAdapter();
		mListView.setAdapter(mAdapter);
	}
	
	private void initContactTempData() {
		for (int i = 0; i < 200; i++) {
			ContactBean bean = new ContactBean();
			bean.icon = R.drawable.ic_contact;
			bean.name = "name " + i;
			bean.phone = 18800000000L + i + "";
			mListDatas.add(bean );
		}
	}

	private class ContactAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mListDatas.size();
		}

		@Override
		public Object getItem(int position) {
			return mListDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			//1.The Slow Way：
		/*	//加载布局
			View view = View.inflate(ContactSListActivity.this, R.layout.item_contact, null);
			
			//findViewById
			ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_contact_icon);
			TextView tvName = (TextView) view.findViewById(R.id.tv_contact_name);
			TextView tvPhone = (TextView) view.findViewById(R.id.tv_contact_phone);
			
			//设置数据
			ContactBean bean = (ContactBean) getItem(position);
			ivIcon.setImageResource(R.drawable.ic_contact);
			tvName.setText(bean.name);
			tvPhone.setText(bean.phone);
			//查询联系人头像
			Bitmap photo = ContactProvider.getContactPhoto(ContactSListActivity.this, bean.contactId);
			
			ivIcon.setImageBitmap(photo);
			return view;*/
			
			
			//2.The Right Way
			/*if(convertView == null){
				//1.没有复用
				convertView = View.inflate(ContactSListActivity.this, R.layout.item_contact, null);
			}
			
			//findViewById
			ImageView ivIcon = (ImageView) convertView.findViewById(R.id.iv_contact_icon);
			TextView tvName = (TextView) convertView.findViewById(R.id.tv_contact_name);
			TextView tvPhone = (TextView) convertView.findViewById(R.id.tv_contact_phone);
			
			//设置数据
			ContactBean bean = (ContactBean) getItem(position);
			ivIcon.setImageResource(R.drawable.ic_contact);
			tvName.setText(bean.name);
			tvPhone.setText(bean.phone);
			//查询联系人头像
			Bitmap photo = ContactProvider.getContactPhoto(ContactSListActivity.this, bean.contactId);
			
			ivIcon.setImageBitmap(photo);
			return convertView;
			*/
			
			// 3.The Fast Way
			
			//3.1 声明ViewHolder
			ViewHolder holder = null;
			if(convertView == null){
				//没有复用
				//3.2加载布局
				convertView = View.inflate(ContactSListActivity.this, R.layout.item_contact, null);
				//3.3 创建ViewHolder
				holder = new ViewHolder();
				//3.4 通过findViewById初始化ViewHolder里的控件
				holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_contact_icon);
				holder.tvName = (TextView) convertView.findViewById(R.id.tv_contact_name);
				holder.tvPhone = (TextView) convertView.findViewById(R.id.tv_contact_phone);
				//3.5设置标记
				convertView.setTag(holder);
			}else{
				//有复用
				holder = (ViewHolder) convertView.getTag();
			}
			
			//设置数据
			ContactBean bean = (ContactBean) getItem(position);
			holder.ivIcon.setImageResource(R.drawable.ic_contact);
			holder.tvName.setText(bean.name);
			holder.tvPhone.setText(bean.phone);
			//查询联系人头像
			Bitmap photo = ContactProvider.getContactPhoto(ContactSListActivity.this, bean.contactId);
			
			holder.ivIcon.setImageBitmap(photo);
			
			return convertView;
		}
		
	}
	
	/**
	 * 缓存View对象，具体看item有几个控件
	 * @author youliang.ji
	 *
	 */
	static class ViewHolder{
		ImageView ivIcon;
		TextView tvName;
		TextView tvPhone;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ContactBean bean = mListDatas.get(position);
		//数据回传上一个页面
		Intent data = new Intent();
		data.putExtra(KEY_CONTACT_PHONE, bean.phone);
		setResult(Activity.RESULT_OK, data);
		finish();
	}
}
