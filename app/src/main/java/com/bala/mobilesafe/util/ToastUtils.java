package com.bala.mobilesafe.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
	
	public static void  makeText(Context context, String text){
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

}
