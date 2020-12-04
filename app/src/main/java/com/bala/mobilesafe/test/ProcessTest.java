package com.bala.mobilesafe.test;

import com.bala.mobilesafe.bean.PkgBean;
import com.bala.mobilesafe.bean.ProcessBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class ProcessTest {
	
	/**
	 * 生成进程管理临时数据
	 * @return
	 */
	public static List<ProcessBean> getProcessList(){
		List<ProcessBean> datas = new ArrayList<ProcessBean>();
		
		List<PkgBean> pkgs = new ArrayList<PkgBean>();
		for (int i = 0; i < 20; i++) {//构造应用程序
			PkgBean pkg = new PkgBean();
			pkg.name = "应用程序名称-"+i;
			pkg.packageName = "应用程序包名-"+i;
			pkgs.add(pkg);
		}
		
		Random rm = new Random();
		for (PkgBean pkg : pkgs) {
			//构造一个应用程序对应多个进程
			for (int i = 0; i < rm.nextInt(6); i++) {
				ProcessBean bean = new ProcessBean();
				bean.processMemory = 242588 * (rm.nextInt(100) + 1);
				bean.processName = "进程名-"+i;
				bean.pkg = pkg;
				
				datas.add(bean);
			}
		}
		
		return datas;
	}

}
