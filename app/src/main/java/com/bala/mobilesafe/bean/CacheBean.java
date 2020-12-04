package com.bala.mobilesafe.bean;

import android.graphics.drawable.Drawable;

public class CacheBean {

	public Drawable icon;
	public String label;
	public String packageName;
	public long cacheSize;
	public CacheBean(Drawable icon, String label, String packageName,
			long cacheSize) {
		super();
		this.icon = icon;
		this.label = label;
		this.packageName = packageName;
		this.cacheSize = cacheSize;
	}
	
	
	
	
	
}
