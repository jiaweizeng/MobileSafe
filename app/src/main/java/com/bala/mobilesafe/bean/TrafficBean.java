package com.bala.mobilesafe.bean;

import android.graphics.drawable.Drawable;

public class TrafficBean {

	public Drawable icon ;
	public String label;
	public long send;
	public long recv;
	public TrafficBean(Drawable icon, String label, long send, long recv) {
		super();
		this.icon = icon;
		this.label = label;
		this.send = send;
		this.recv = recv;
	}
	
	
}
