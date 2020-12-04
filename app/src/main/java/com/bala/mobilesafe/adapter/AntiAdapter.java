package com.bala.mobilesafe.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.bean.AntivirusBean;

import java.util.List;

public class AntiAdapter extends BaseAdapter {

	Context mContext;
	List<AntivirusBean>  mList;
	
	public AntiAdapter(Context context , List<AntivirusBean>  list) {
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
			convertView = View.inflate(mContext, R.layout.item_antivirus, null);
			holder.ivIcon  =  (ImageView) convertView.findViewById(R.id.iv_icon);
			holder.ivClean  =  (ImageView) convertView.findViewById(R.id.iv_clean);
			holder.tvLabel = (TextView)convertView.findViewById(R.id.tv_label);
			holder.tvIsVirus = (TextView)convertView.findViewById(R.id.tv_isvirus);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		final AntivirusBean bean = mList.get(position);
		
		holder.tvLabel.setText(bean.label);
		holder.ivIcon.setImageDrawable(bean.icon);
		
		holder.tvIsVirus.setText(bean.isVirus?"危险":"安全");
		holder.tvIsVirus.setTextColor(bean.isVirus?Color.RED:Color.GREEN);
		holder.ivClean.setVisibility(bean.isVirus?View.VISIBLE:View.GONE);
	
		holder.ivClean.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					
				//卸载程序 ----> packageName
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				intent.setAction("android.intent.action.DELETE");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setData(Uri.parse("package:" + bean.packageName));
				mContext.startActivity(intent);
				
			}
		});
		return convertView;
		
	}
	
	static class ViewHolder{
		ImageView  ivIcon , ivClean;
		TextView tvLabel , tvIsVirus;
	}
	
	
	//========================
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	

}
