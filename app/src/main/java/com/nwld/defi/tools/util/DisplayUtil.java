package com.nwld.defi.tools.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;

public class DisplayUtil {
	/**
	 * 通过dip计算px
	 * 
	 * @param context
	 *            应用上下文
	 * @param dipValue
	 *            dip值
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	@SuppressLint("NewApi")
	public static Point getScreenPoint(Activity activity) {
		// 已经获取到屏幕宽度、高度了，就无须获取，只需一个Activity获取即可
		Display screen = activity.getWindowManager().getDefaultDisplay();
		Point screenPoint = new Point();
		screen.getSize(screenPoint);
		return screenPoint;
	}
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
