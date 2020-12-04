package com.bala.mobilesafe.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.bean.StyleBean;
import com.bala.mobilesafe.util.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

public class NumberStyleDialog extends Dialog implements OnItemClickListener {

	private static String[] TITLES = new String[] { "半透明", "活力橙", "卫士蓝", "金属灰",
			"苹果绿" };
	private static int[] RESIDS = new int[] {
			R.drawable.shape_half_transparent, R.drawable.shape_orange,
			R.drawable.shape_blue, R.drawable.shape_gray,
			R.drawable.shape_green };

	private ListView mListView;
	private NumberLocationAdapter mAdapter;
	private List<StyleBean> mListData;
	private Context mContext;

	public NumberStyleDialog(Context context, int theme) {
		super(context, R.style.dlg_style);// 使用自定义样式

		this.mContext = context;
		// 处理样式

		Window window = getWindow();
		LayoutParams params = window.getAttributes();
		params.gravity = Gravity.BOTTOM;// 显示在底部

	}

	// 和Activity一样
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_number_style);

		mListView = (ListView) findViewById(R.id.lv_number_style);
		mListView.setOnItemClickListener(this);
		mAdapter = new NumberLocationAdapter();
		initData();
		// adapter -> 数据集List<Bean>
		mListView.setAdapter(mAdapter);
	}

	private void initData() {
		mListData = new ArrayList<StyleBean>();
		for (int i = 0; i < TITLES.length; i++) {
			StyleBean bean = new StyleBean();
			bean.title = TITLES[i];
			bean.resId = RESIDS[i];
			mListData.add(bean );
		}
	}

	public NumberStyleDialog(Context context) {
		this(context, 0);// 调用2个参数构造方法
	}

	private class NumberLocationAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mListData.size();
		}

		@Override
		public Object getItem(int position) {
			return mListData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//1.声明ViewHolder
			ViewHolder holder = null;
			if(convertView == null){
				//没有复用
				//1.加载布局
				convertView = View.inflate(mContext, R.layout.item_number_style, null);
				//2.创建ViewHolder
				holder = new ViewHolder();
				//3.findViewById
				holder.ivStyle = (ImageView) convertView.findViewById(R.id.iv_number_style);
				holder.ivSelected = (ImageView) convertView.findViewById(R.id.iv_number_style_selected);
				holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_number_style_title);
				//4.设置标记
				convertView.setTag(holder);
			}else{
				//有复用
				holder = (ViewHolder) convertView.getTag();
			}
			
			//设置数据
			StyleBean bean = (StyleBean) getItem(position);
			holder.tvTitle.setText(bean.title);
			holder.ivStyle.setImageResource(bean.resId);
			
			int numberStyle = PreferencesUtils.getNumberStyle(mContext, 0);
			
			if(numberStyle == position){
				holder.ivSelected.setVisibility(View.VISIBLE);
			}else{
				holder.ivSelected.setVisibility(View.GONE);
			}
			
			return convertView;
		}

	}
	
	static class ViewHolder{
		ImageView ivStyle;
		ImageView ivSelected;
		TextView tvTitle;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//持久化存储
		PreferencesUtils.setNumberStyle(mContext, position);
		//显示选中图片
		mAdapter.notifyDataSetChanged();
	}

}
