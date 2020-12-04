package com.bala.mobilesafe.util;

import java.io.FileInputStream;
import java.security.MessageDigest;

public class Md5Util {

	//9a0f7b8812...
	public static String  encodeFile(String path){
		
		try {
			FileInputStream in = new FileInputStream(path);
			MessageDigest digester = MessageDigest.getInstance("MD5");
			byte[] bytes = new byte[8192];
			int byteCount;
			while ((byteCount = in.read(bytes)) > 0) {
				digester.update(bytes, 0, byteCount);
			}
			byte[] digest = digester.digest();
			
			StringBuffer stringBuffer = new StringBuffer();
			
			for (byte b : digest) {
				String str = Integer.toHexString(b&0xff);
				//只有一位
				if(str.length() == 1){
					str = "0"+str;
				}
				stringBuffer.append(str);
			}
			in.close();
			return stringBuffer.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		 
	}
	/**
	 * 加密字符串
	 * @param s
	 * @return
	 */
	public static String  encodeStr(String s){
		
		try {
			MessageDigest digester = MessageDigest.getInstance("MD5");
			
			digester.update(s.getBytes());
			byte[] digest = digester.digest();
			
			StringBuffer stringBuffer = new StringBuffer();
			
			for (byte b : digest) {
				String str = Integer.toHexString(b&0xff);
				//只有一位
				if(str.length() == 1){
					str = "0"+str;
				}
				stringBuffer.append(str);
			}
			return stringBuffer.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
}
