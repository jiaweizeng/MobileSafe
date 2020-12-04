package com.bala.mobilesafe.bean;

import android.graphics.drawable.Drawable;

public class AntivirusBean {

	public String label;
	public Drawable icon;
	public boolean isVirus; //是否是病毒， true : 病毒 ， false: 不是病毒
	public String packageName;
	public AntivirusBean(String label, Drawable icon, boolean isVirus,
			String packageName) {
		super();
		this.label = label;
		this.icon = icon;
		this.isVirus = isVirus;
		this.packageName = packageName;
	}
	
	
}
