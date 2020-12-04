package com.bala.mobilesafe.util;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class GzipUtils {
	
	public static void gzip(String srcPath, String destPath) throws FileNotFoundException{
		gzip(new File(srcPath), new File(destPath));
	}
	
	public static void gzip(File srcFile, File destFile) throws FileNotFoundException{
		gzip(new FileInputStream(srcFile), new FileOutputStream(destFile));
	}

	public static void gzip(FileInputStream fis, OutputStream fos) {
		GZIPOutputStream gos = null;
		try {
			gos = new GZIPOutputStream(fos);
			
			//写文件
			byte[] buffer = new byte[1024];
			int len = -1;
			while((len = fis.read(buffer)) != -1){
				gos.write(buffer, 0, len);
				gos.flush();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			close(gos);
			close(fis);
		}
	}
	
	public static void unzip(String srcPath, String destPath) throws FileNotFoundException{
		unzip(new File(srcPath), new File(destPath));
	}
	
	public static void unzip(File srcFile, File destFile) throws FileNotFoundException{
		unzip(new FileInputStream(srcFile), new FileOutputStream(destFile));
	}
	
	public static void unzip(InputStream is, OutputStream os){
		//解压：address.zip -> address.db
		//1.输入zip包
		GZIPInputStream gis = null;
		try {
			gis = new GZIPInputStream(is);
			
			//2.输出address.db
			byte[] buffer = new byte[1024];
			int len = -1;
			while((len = gis.read(buffer)) != -1){
				os.write(buffer, 0, len);
				os.flush();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			close(gis);
			close(os);
		}
	}
	
	private static void close(Closeable is){
		if(is != null){
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
