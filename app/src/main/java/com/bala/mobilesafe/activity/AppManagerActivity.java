package com.bala.mobilesafe.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bala.mobilesafe.R;
import com.bala.mobilesafe.bean.AppBean;
import com.bala.mobilesafe.util.LogUtil;
import com.bala.mobilesafe.util.PackageUtils;
import com.bala.mobilesafe.view.ProgressStatusView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


public class AppManagerActivity extends Activity implements OnItemClickListener {

    public static final String TAG = "AppManagerActivity";
    private ProgressStatusView mPsvRom;
    private ProgressStatusView mPsvSD;
    private StickyListHeadersListView mListView;
    private AppAdapter mAdapter;
    private List<AppBean> mListDatas;
    private UninstallReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        mContext = this;

        initView();
        initData();
        setListener();
    }

    private void initView() {
        mPsvRom = (ProgressStatusView) findViewById(R.id.psv_rom);
        mPsvSD = (ProgressStatusView) findViewById(R.id.psv_sd);
        mListView = (StickyListHeadersListView) findViewById(R.id.lv_app_manager);
    }

    private void setListener() {
        mListView.setOnItemClickListener(this);
    }

    private void initData() {
        // 获取内存大小
        initRom();
        // 获取SD卡大小
        initSD();

        mListDatas = new ArrayList<AppBean>();
        // initTempData();
        mListDatas = PackageUtils.getAppData(this);
        // 统计应用数量
        getAppCount();
        // 排序:用户应用在前，系统应用在后
        Collections.sort(mListDatas, new Comparator<AppBean>() {

            @Override
            public int compare(AppBean lhs, AppBean rhs) {
                int lhsInt = lhs.isSystem ? 1 : 0;
                int rhsInt = rhs.isSystem ? 1 : 0;
                return lhsInt - rhsInt;
            }
        });
        mAdapter = new AppAdapter();

        mListView.setAdapter(mAdapter);

        mReceiver = new UninstallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);//优先级
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);//卸载动作
        filter.addDataScheme("package");
        registerReceiver(mReceiver, filter);
    }

    private void initTempData() {
        for (int i = 0; i < 30; i++) {
            AppBean bean = new AppBean();
            bean.name = "name " + i;
            mListDatas.add(bean);
        }
    }

    private void initSD() {
        File sdFile = Environment.getExternalStorageDirectory();
        long totalSD = sdFile.getTotalSpace();
        long freeSD = sdFile.getFreeSpace();
        long usedSD = totalSD - freeSD;// 使用

        mPsvSD.setLeftText(Formatter.formatFileSize(this, usedSD) + "已用");
        mPsvSD.setRightText(Formatter.formatFileSize(this, freeSD) + "可用");
        long sdProgress = usedSD * 100 / totalSD;
        mPsvSD.setProgress((int) (sdProgress));
    }

    private void initRom() {
        File dataFile = Environment.getDataDirectory();
        long totalRom = dataFile.getTotalSpace();// 中大小
        long freeRom = dataFile.getFreeSpace();// 可用空间
        long usedRom = totalRom - freeRom; // 已用大小

        mPsvRom.setLeftText(Formatter.formatFileSize(this, usedRom) + "已用");
        mPsvRom.setRightText(Formatter.formatFileSize(this, freeRom) + "可用");
        long progress = usedRom * 100 / totalRom;// 40 * 100 / 100

        mPsvRom.setProgress((int) progress);
    }

    private class AppAdapter extends BaseAdapter implements
            StickyListHeadersAdapter {

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
            // 1.声明ViewHolder
            ViewHolder holder = null;
            if (convertView == null) {
                // 没有复用
                // 1.加载布局
                convertView = View.inflate(AppManagerActivity.this,
                        R.layout.item_app_managerr, null);
                // 2.创建ViewHolder
                holder = new ViewHolder();
                // 3. findViewById
                holder.ivIcon = (ImageView) convertView
                        .findViewById(R.id.iv_app_icon);
                holder.tvName = (TextView) convertView
                        .findViewById(R.id.tv_app_name);
                holder.tvInstall = (TextView) convertView
                        .findViewById(R.id.tv_app_install);
                holder.tvSpace = (TextView) convertView
                        .findViewById(R.id.tv_app_space);

                // 4.设置标记
                convertView.setTag(holder);
            } else {
                // 复用
                holder = (ViewHolder) convertView.getTag();
            }

            // 设置数据
            AppBean bean = (AppBean) getItem(position);

            holder.ivIcon.setImageDrawable(bean.icon);
            holder.tvName.setText(bean.name);
            holder.tvSpace.setText(bean.space);
            holder.tvInstall.setText(bean.isInstallSD ? "SD卡安装" : "内存安装");

            return convertView;
        }

        // Header长相,和adapter的getView一样
        @Override
        public View getHeaderView(int position, View convertView,
                                  ViewGroup parent) {
            // 1.声明ViewHolder
            HeaderViewHolder holder = null;
            if (convertView == null) {
                // 没有复用
                // 1.加载布局
                convertView = View.inflate(mContext,
                        R.layout.app_manager_header, null);
                // 2.创建ViewHolder
                holder = new HeaderViewHolder();
                // 3.findViewById
                holder.tvTitle = (TextView) convertView
                        .findViewById(R.id.tv_header_title);
                // 4.设置标记
                convertView.setTag(holder);
            } else {
                // 有复用
                holder = (HeaderViewHolder) convertView.getTag();
            }

            // 设置数据
            AppBean bean = (AppBean) getItem(position);
            if (bean.isSystem) {
                holder.tvTitle.setText("系统程序(" + mSystemCount + "个)");
            } else {
                holder.tvTitle.setText("用户程序(" + mUserCount + "个)");
            }

            return convertView;
        }

        // 到底有几个header
        @Override
        public long getHeaderId(int position) {
            // return position;//默认每个item都添加header
            // 当前只有2个头，系统、用户
            AppBean bean = (AppBean) getItem(position);
            return bean.isSystem ? 0 : 1;
        }

    }

    static class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvInstall;
        TextView tvSpace;
    }

    static class HeaderViewHolder {
        TextView tvTitle;
    }

    private int mSystemCount = 0;
    private int mUserCount = 0;
    private Context mContext;

    /**
     * 统计系统、用户程序数量
     */
    private void getAppCount() {
        for (int i = 0; i < mListDatas.size(); i++) {
            AppBean bean = mListDatas.get(i);
            if (bean.isSystem) {
                mSystemCount++;
            } else {
                mUserCount++;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // 弹窗

        // contentView-显示的View, width, height-宽高
	/*	TextView contentView = new TextView(mContext);
		contentView.setText("弹窗");
		contentView.setTextSize(20);
		contentView.setBackgroundColor(Color.RED);*/

        View contentView = View.inflate(mContext, R.layout.pop_window_layout, null);

        final PopupWindow window = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        //设置默认只显示一个弹窗
        window.setBackgroundDrawable(new ColorDrawable());
        window.setFocusable(true);
        window.setOutsideTouchable(true);//设置外围点击

        //设置动画样式
        window.setAnimationStyle(R.style.pop_anim);

        //显示
        //window.showAsDropDown(view);//在哪个控件下面显示
        //window.showAsDropDown(view, 80, -60);

        AppBean bean = mListDatas.get(position);
        final String packageName = bean.packageName;


        window.showAtLocation(parent, Gravity.CENTER, 0, 0);//显示在指定位置

        TextView tvUninstall = (TextView) contentView.findViewById(R.id.tv_uninstall);
        TextView tvOpen = (TextView) contentView.findViewById(R.id.tv_open);
        TextView tvShare = (TextView) contentView.findViewById(R.id.tv_share);
        TextView tvInfo = (TextView) contentView.findViewById(R.id.tv_info);


        //判断显示或者隐藏
        tvUninstall.setVisibility(bean.isSystem ? View.GONE : View.VISIBLE);

        PackageManager pkgMgr = getPackageManager();
        final Intent intent = pkgMgr.getLaunchIntentForPackage(packageName);
        tvOpen.setVisibility(intent == null ? View.GONE : View.VISIBLE);


        tvUninstall.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //实现卸载功能
				  /* <intent-filter>
	                <action android:name="android.intent.action.VIEW" />
	                <action android:name="android.intent.action.DELETE" />
	                <category android:name="android.intent.category.DEFAULT" />
	                <data android:scheme="package" />
	            </intent-filter>*/

                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setAction("android.intent.action.DELETE");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
                //startActivityForResult(intent, requestCode)不能实现监听卸载完成

                window.dismiss();
            }
        });

        tvOpen.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                window.dismiss();
                //打开
                //获取应用程序启动项
                startActivity(intent);
            }
        });

        tvInfo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO
			/*	 <intent-filter>
	                <action android:name="android.settings.APPLICATION_DETAILS_SETTINGS" />
	                <category android:name="android.intent.category.DEFAULT" />
	                <data android:scheme="package" />
	            </intent-filter>*/

                window.dismiss();
                Intent intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        });

        tvShare.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                window.dismiss();
                //share
                showShare(packageName);
            }
        });

    }

    private class UninstallReceiver extends BroadcastReceiver {

        //卸载完成回调
        @Override
        public void onReceive(Context context, Intent intent) {
            String dataString = intent.getDataString();
            LogUtil.d(TAG, "卸载onReceive=" + dataString);
            //获取删除回调包名
            String packageName = dataString.replace("package:", "");
            //删除数据，刷新页面
            Iterator<AppBean> iterator = mListDatas.iterator();
            while (iterator.hasNext()) {
                AppBean bean = iterator.next();
                if (bean.packageName.equals(packageName)) {
                    iterator.remove();
                    break;
                }
            }

            //刷新UI
            mSystemCount = 0;
            mUserCount = 0;
            getAppCount();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void finish() {
        super.finish();
        unregisterReceiver(mReceiver);//取消注册
    }

    private void showShare(String content) {
        /*OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
        oks.setTitle("标题");
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("ShareSDK");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);*/
    }

}
