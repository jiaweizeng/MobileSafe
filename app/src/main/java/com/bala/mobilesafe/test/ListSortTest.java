package com.bala.mobilesafe.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//import android.test.AndroidTestCase;

public class ListSortTest /*extends AndroidTestCase */{
	
	public void testSort(){
		List<Integer> list = new ArrayList<Integer>();
		list.add(3);
		list.add(1);
		list.add(8);
		list.add(5);
		
		Collections.sort(list, new Comparator<Integer>() {

			@Override
			public int compare(Integer lhs, Integer rhs) {
				return lhs - rhs;
			}
		});
		
		System.out.println(list.toString());
	}

}
