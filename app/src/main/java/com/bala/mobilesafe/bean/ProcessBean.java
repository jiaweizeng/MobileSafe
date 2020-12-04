package com.bala.mobilesafe.bean;

/**
 * 进程数据模型
 */
public class ProcessBean {
	public String processName;//进程名字
	public long processMemory;//进程占用内存大小
	
	//进程对应的应用
	public PkgBean pkg;
	public int pid;//进程id
}
