package com.bala.mobilesafe.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
	
	/**
	 * 将文件拷贝到指定目录下
	 * @param is
	 * @param os
	 */
	public static void copyFile(final InputStream is, final OutputStream os){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				byte[] buffer = new byte[1024];
				int len = -1;
				try {
					while((len = is.read(buffer)) != -1){
						os.write(buffer, 0, len);
						os.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally{
					IOUtils.close(is);
					IOUtils.close(os);
				}
			}
		}).start();
	}

}
