package com.bala.mobilesafe.bean;

/**
 * 黑名单数据模型
 * @author youliang.ji
 *
 */
public class BlackBean {
	
	public String phone;
	public int type;
	
	/**类型常量*/
	public final static int TYPE_PHONE = 0;
	public final static int TYPE_SMS = 1;
	public final static int TYPE_ALL = 2;

}
