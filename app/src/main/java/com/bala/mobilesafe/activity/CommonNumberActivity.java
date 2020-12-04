package com.bala.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.bean.ChildBean;
import com.bala.mobilesafe.bean.GroupBean;
import com.bala.mobilesafe.dao.CommonNumberDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommonNumberActivity extends Activity implements OnGroupClickListener, OnChildClickListener {
	
	private ExpandableListView mListView;
	private CommonNumberAdapter mAdapter;
	private List<GroupBean> mListDatas;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_number);
		
		mContext = this;
		
		initView();
		initData();
		setListener();
	}




	private void initView() {
		mListView = (ExpandableListView) findViewById(R.id.lv_common_number);
	}
	

	private void setListener() {
		mListView.setOnGroupClickListener(this);
		mListView.setOnChildClickListener(this);
	}

	private void initData() {
		
		mListDatas = new ArrayList<GroupBean>();
		
		//initTempData();
		mListDatas = CommonNumberDao.getAllCommonNumber(mContext);
		mAdapter = new CommonNumberAdapter();
		mListView.setAdapter(mAdapter);
	}
	

	private class CommonNumberAdapter extends BaseExpandableListAdapter{

		//返回一级列表条数
		@Override
		public int getGroupCount() {
			if(mListDatas.size() > 0){
				return mListDatas.size();
			}
			return 0;
		}

		//返回二级列表条数
		@Override
		public int getChildrenCount(int groupPosition) {
			//获取对应二级列表
			GroupBean groupBean = mListDatas.get(groupPosition);
			if(groupBean != null){
				List<ChildBean> childDatas = groupBean.childDatas;
				if(childDatas != null){
					return childDatas.size();
				}
			}
			return 0;
		}

		//返回一级列表Bean
		@Override
		public Object getGroup(int groupPosition) {
			GroupBean groupBean = mListDatas.get(groupPosition);
			return groupBean;
		}

		//返回二级列表Bean
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			GroupBean groupBean = mListDatas.get(groupPosition);
			List<ChildBean> childDatas = groupBean.childDatas;
			if(childDatas != null){
				return childDatas.get(childPosition);
			}
			return null;
		}

		//暂时不使用
		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		//暂时不使用
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		//不使用
		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		//返回一级列表View
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			
			//1.声明ViewHolder
			GroupViewHolder holder = null;
			if(convertView == null){
				//没有复用
				//2.加载布局
				convertView = View.inflate(mContext, R.layout.item_group, null);
				//3.创建ViewHolder
				holder = new GroupViewHolder();
				//4.findViewById
				holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_group_title);
				//5.设置标记
				convertView.setTag(holder);
			}else{
				//有复用
				holder = (GroupViewHolder) convertView.getTag();
			}
			
			GroupBean bean = (GroupBean) getGroup(groupPosition);
			
			//设置数据
			holder.tvTitle.setText(bean.title);
			
			return convertView;
		}

		//返回二级列表View
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			
			//1.声明child ViewHolder
			ChildViewHolder holder = null;
			if(convertView == null){
				//没有复用
				convertView = View.inflate(mContext, R.layout.item_child, null);
				holder = new ChildViewHolder();
				holder.tvName = (TextView) convertView.findViewById(R.id.tv_child_name);
				holder.tvNumber = (TextView) convertView.findViewById(R.id.tv_child_number);
				
				convertView.setTag(holder);
			}else{
				//复用
				holder = (ChildViewHolder) convertView.getTag();
			}
			
			//设置数据
			ChildBean bean = (ChildBean) getChild(groupPosition, childPosition);
			holder.tvName.setText(bean.name);
			holder.tvNumber.setText(bean.number);
			
			return convertView;
		}

		//二级列表item是否可选中，默认不可选中
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;//true-二级列表item可点击，否则不可点击
		}
		
	}
	
	static class GroupViewHolder{
		TextView tvTitle;
	}
	
	static class ChildViewHolder{
		TextView tvName;
		TextView tvNumber;
	}
	

	private void initTempData() {
		for (int i = 0; i < 8; i++) {
			GroupBean groupBean = new GroupBean();
			groupBean.title = "Group title " + i;
			List<ChildBean> childDatas = new ArrayList<ChildBean>();
			
			int nextInt = new Random().nextInt(5) + 1;
			for (int j = 0; j < nextInt; j++) {
				ChildBean childBean = new ChildBean();
				childBean.name = "child name " + j;
				childBean.number = "child number " +j;
				childDatas.add(childBean );
			}
			groupBean.childDatas = childDatas;
			mListDatas.add(groupBean);
		}
	}


	private int mCurGroupPosition = -1;//标识当前有几个Group打开

	//点击一级列表回调
	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		//1.只能存在一个打开的Group
		//2.如果点击了打开，关闭
		//3.如果点击关闭，打开，置顶
		if(groupPosition != mCurGroupPosition){
			//打开当前点击position
			mListView.expandGroup(groupPosition);
			//关闭已经打开的
			mListView.collapseGroup(mCurGroupPosition);
			//置顶
			mListView.setSelectedGroup(groupPosition);
			//记录当前打开位置
			mCurGroupPosition = groupPosition;
		}else{
			//当前打开，关闭
			mListView.collapseGroup(groupPosition);
			//当前没有一个打开
			mCurGroupPosition = -1;
		}
		
		return true;//true处理该事件，false-系统处理
	}



	//child item点击回调
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		//打开拨号界面
		
	/*	 <intent-filter>
         <action android:name="android.intent.action.VIEW" />
         <action android:name="android.intent.action.DIAL" />
         <category android:name="android.intent.category.DEFAULT" />
         <category android:name="android.intent.category.BROWSABLE" />
         <data android:scheme="tel" />
     </intent-filter>*/
		
		ChildBean childBean = mListDatas.get(groupPosition).childDatas.get(childPosition);
		
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.setAction("android.intent.action.DIAL");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.BROWSABLE");
		
		intent.setData(Uri.parse("tel:" + childBean.number));
		
		startActivity(intent );
		
		return true;
	}
}
