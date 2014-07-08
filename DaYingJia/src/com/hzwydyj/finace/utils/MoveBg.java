package com.hzwydyj.finace.utils;

import android.view.View;
import android.view.animation.TranslateAnimation;

public class MoveBg {
	/**
	 * 
	 * @param v
	 * @param startX
	 * @param toX
	 * @param startY
	 * @param toY
	 */
	public static int moveFrontBg(View v, int startX, int toX, int startY, int toY) {
		TranslateAnimation anim = new TranslateAnimation(startX, toX, startY, toY);
		anim.setDuration(200);
		anim.setFillAfter(true);
		v.startAnimation(anim);
		return toX;
	}
}
