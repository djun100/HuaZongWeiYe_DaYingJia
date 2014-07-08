package com.hzwydyj.finace.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.SparseArray;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 通用的工具类
 */
public class CommonUtil {

	/**
	 * 隐藏软键盘的方法
	 * @param context  要传入的上下文
	 */
	public static void hiddenSoft(Context context) {
		// 取得输入方法的服务类
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		View view = ((Activity) context).getCurrentFocus();
		if (view != null) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);// 隐藏软键盘

		}
	}

	/**
	 * 快速显示键盘的方法，引用回复的时候回复按钮一点弹出键盘就用这个方法。
	 * @param context
	 */
	public static void ShowSoftFast(Context context) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		View view = ((Activity) context).getCurrentFocus();
		if (view != null) {
			imm.showSoftInput(view, 0); // 显示软键盘
		}
	}

	/**
	 * 判断手机格式
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNum(String mobiles) {
		// String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";
		String regExp = "^((13[0-9])|(15[^4,\\D])|(18[0,0-9]))\\d{8}$";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(mobiles);
		return m.find();
	}

	/**
	 * 检测网络
	 * @param context
	 * @return
	 */
	public static boolean isConnectingToInternet(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
		}
		return false;
	}

	/**
	 * ViewHolder
	 * 只提供一个静态方法，其实可以加一个私有构造函数防止外部实例化
	 * @param view
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends View> T get(View view, int id) {
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}
}
