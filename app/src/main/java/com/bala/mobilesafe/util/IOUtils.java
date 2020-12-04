package com.bala.mobilesafe.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {
	
	public static void close(Closeable io){
		if(io != null){
			try {
				io.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String convert2String(InputStream is){
		String result = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = -1;
			while((len = is.read(buffer)) != -1){
				bos.write(buffer, 0, len);
			}
			result = bos.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			close(is);
		}
		return result;
	}

}
