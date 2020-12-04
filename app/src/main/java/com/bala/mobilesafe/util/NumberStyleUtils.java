package com.bala.mobilesafe.util;


import com.bala.mobilesafe.R;

public class NumberStyleUtils {
	
	public static int getNumberStyleResId(int postion){
		int[] RESIDS = new int[] {
			R.drawable.shape_half_transparent, R.drawable.shape_orange,
			R.drawable.shape_blue, R.drawable.shape_gray,
			R.drawable.shape_green };
		
		return RESIDS[postion];
	}

}
