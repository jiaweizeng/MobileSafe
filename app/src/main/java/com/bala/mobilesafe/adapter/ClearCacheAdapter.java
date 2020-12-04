package com.bala.mobilesafe.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.bean.CacheBean;

import java.util.List;

public class ClearCacheAdapter extends BaseAdapter {
	
	Context mContext;
	List<CacheBean> mList;

	public ClearCacheAdapter(Context context ,  List<CacheBean> list) {
		super();
		mContext = context;
		mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if(convertView == null){
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.item_clear_cache, null);
			
			holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
			holder.ivClean = (ImageView) convertView.findViewById(R.id.iv_clean);
			holder.tvLabel = (TextView) convertView.findViewById(R.id.tv_label);
			holder.tvCacheSize = (TextView) convertView.findViewById(R.id.tv_cache_size);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		final CacheBean cacheBean = mList.get(position);
		holder.ivIcon.setImageDrawable(cacheBean.icon);
		holder.tvLabel.setText(cacheBean.label);
		holder.tvCacheSize.setText("缓存大小:"+Formatter.formatFileSize(mContext, cacheBean.cacheSize));
		
		
		//只有有缓存的条目才有 清除的入口
	/*	if(cacheBean.cacheSize>0){
			holder.ivClean.setVisibility(View.VISIBLE);
		}else{
			holder.ivClean.setVisibility(View.GONE);
		}*/
		
		holder.ivClean.setVisibility(cacheBean.cacheSize>0?View.VISIBLE:View.GONE);
		holder.ivClean.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//跳转到详情页面。
				Intent intent = new Intent();
				intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setData(Uri.parse("package:" + cacheBean.packageName));
				mContext.startActivity(intent);
			}
		});
		
		
		return convertView;
	}
	
	static class ViewHolder{
		 ImageView ivIcon , ivClean;
		 TextView tvLabel , tvCacheSize;
	}


	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	
}
