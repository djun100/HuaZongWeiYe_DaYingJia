package com.hzwydyj.finace.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

public class DateUtils {

	/* 时间戳转换成字符窜 */
	@SuppressLint("SimpleDateFormat")
	public static String getDateToString(String time) {

		String strTimeString = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		long longTime = Long.valueOf(time);
		strTimeString = simpleDateFormat.format(new Date(longTime * 1000));
		return strTimeString;
	}
	
	public static String getDateString(String time) {
		
		String strTimeString = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long longTime = Long.valueOf(time);
		strTimeString = simpleDateFormat.format(new Date(longTime * 1000));
		return strTimeString;
	}
}
