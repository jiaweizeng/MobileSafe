package com.bala.mobilesafe.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

public class MarqueeTextView extends TextView {

	public MarqueeTextView(Context context) {
		this(context, null);//调用2个参数构造方法
	}
	
	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//动态设置跑马灯效果
		/* android:focusable="true"
                 android:focusableInTouchMode="true"
                 android:marqueeRepeatLimit="marquee_forever"
                 android:singleLine="true"*/
		setEllipsize(TextUtils.TruncateAt.MARQUEE);
		setSingleLine(true);
		setMarqueeRepeatLimit(-1);
		setFocusable(true);
		setFocusableInTouchMode(true);
		
	}
	
	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		return true;//代码动态获取焦点
	}

	
	//当焦点变化回到
	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		if(focused){//有焦点，调用父类，没有不调用
			super.onFocusChanged(focused, direction, previouslyFocusedRect);
		}
	}
	
	
	//当窗体焦点变化回调
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if(hasWindowFocus){//有焦点，走父类方法
			super.onWindowFocusChanged(hasWindowFocus);
		}
	}
	
	

}
