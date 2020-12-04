package com.bala.mobilesafe.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.bean.TrafficBean;

import java.util.List;

public class TrafficAdapter extends BaseAdapter {
	
	Context mContext;
	List<TrafficBean> mList;

	public TrafficAdapter(Context context, List<TrafficBean> trafficList) {
		super();
		mContext = context;
		this.mList = trafficList;
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
			convertView = View.inflate(mContext, R.layout.item_traffic, null);
			
			holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
			holder.tvLabel = (TextView) convertView.findViewById(R.id.tv_label);
			holder.tvSend = (TextView) convertView.findViewById(R.id.tv_send);
			holder.tvRecv = (TextView) convertView.findViewById(R.id.tv_recv);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		TrafficBean bean = mList.get(position);
		holder.ivIcon.setImageDrawable(bean.icon);
		holder.tvLabel.setText(bean.label);
		holder.tvSend.setText("发送:"+Formatter.formatFileSize(mContext, bean.send));
		holder.tvRecv.setText("接收:"+Formatter.formatFileSize(mContext, bean.recv));
		
		return convertView;
	}
	
	static class ViewHolder{
		ImageView ivIcon;
		TextView tvLabel , tvSend,tvRecv;
	}

	
	
	//-================================
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
