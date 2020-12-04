package com.bala.mobilesafe.bean;

public class SmsBean {

	public String address;
	public String body ;
	public String type ;
	public String date ;
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	
	
	public SmsBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	public SmsBean(String address, String body, String type, String date) {
		super();
		this.address = address;
		this.body = body;
		this.type = type;
		this.date = date;
	}
	
	
	
	
}
