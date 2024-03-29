package com.hzwydyj.finace.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Environment;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ListView;
import android.widget.ScrollView;

import com.hzwydyj.finace.R;

public class ScreenShot {

	private static MyLogger log = MyLogger.yLog();

	// 获取指定Activity的截屏，保存到png文件
	public static Bitmap takeScreenShot(Activity activity) {
		// View是你需要截图的View
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();

		// 获取状态栏高度
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		//log.i("获取状态栏高度->" + statusBarHeight);

		// 获取屏幕长和高
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		int height = activity.getWindowManager().getDefaultDisplay().getHeight();
		// 去掉标题栏
		// Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
		view.destroyDrawingCache();
		savePic(b, "/sdcard/screen_test.png");
		return b;
	}

	// 保存到sdcard
	public static void savePic(Bitmap b, String strFileName) {
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(strFileName);
			if (null != fos) {
				b.compress(Bitmap.CompressFormat.PNG, 90, fos);
				//log.i("savePic-b.compress");
				fos.flush();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			//log.i("异常");
			e.printStackTrace();
		} catch (IOException e) {
			//log.i("异常");
			e.printStackTrace();
		}
	}

	/**
	 * 把View对象转换成bitmap
	 * */
	public static Bitmap convertViewToBitmap(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		if (bitmap != null) {
			//log.i("这不是nullde1");
			log.d("nullde1");
		} else {
			//log.i("这nullnulllnulnlul");
		}
		return bitmap;
	}

	// 程序入口1
	public static void shoot(Activity a) {
		ScreenShot.savePic(ScreenShot.takeScreenShot(a), "/sdcard/screen_test.png");
		//log.i("shoot(Activity a)");
	}

	// 程序入口2
	public static void shootView(View view) {
		ScreenShot.savePic(ScreenShot.convertViewToBitmap(view), "sdcard/xx.png");
		//log.i("shootView(View view)");
	}

	public static Bitmap getViewBitmap(View v) {
		v.clearFocus();
		v.setPressed(false);

		boolean willNotCache = v.willNotCacheDrawing();
		v.setWillNotCacheDrawing(false);

		// Reset the drawing cache background color to fully transparent
		// for the duration of this operation
		int color = v.getDrawingCacheBackgroundColor();
		v.setDrawingCacheBackgroundColor(0);

		if (color != 0) {
			v.destroyDrawingCache();
		}
		v.buildDrawingCache();
		Bitmap cacheBitmap = v.getDrawingCache();
		if (cacheBitmap == null) {
			//log.i("failed getViewBitmap(" + v + ")");
			return null;
		}

		Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

		// Restore the view
		v.destroyDrawingCache();
		v.setWillNotCacheDrawing(willNotCache);
		v.setDrawingCacheBackgroundColor(color);

		return bitmap;
	}

	/**
	 * 截取scrollview的屏幕
	 * **/
	public static Bitmap getBitmapByView(ScrollView scrollView) {
		int h = 0;
		Bitmap bitmap = null;
		// 获取listView实际高度
		for (int i = 0; i < scrollView.getChildCount(); i++) {
			h += scrollView.getChildAt(i).getHeight();
			scrollView.getChildAt(i).setBackgroundResource(R.drawable.card_background_training_flat);
		}
		//log.i("实际高度:" + h);
		//log.i(" 高度:" + scrollView.getHeight());
		// 创建对应大小的bitmap
		bitmap = Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.ARGB_8888);
		final Canvas canvas = new Canvas(bitmap);
		scrollView.draw(canvas);
		// 测试输出
		FileOutputStream out = null;
		try {
			out = new FileOutputStream("/sdcard/screen_test.png");
			//log.i("new FileOutputStream--/sdcard/screen_test.png");
		} catch (FileNotFoundException e) {
			//log.i("异常");
			e.printStackTrace();
		}

		try {
			if (null != out) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
				//log.i("bitmap.compress");
				out.flush();
				out.close();
			}
		} catch (IOException e) {
			//log.i("异常");
		}
		return bitmap;
	}

	private static String TAG = "Listview and ScrollView item 截图:";

	/**
	 * 截图listview
	 * **/
	public static Bitmap getbBitmap(ListView listView) {
		int h = 0;
		Bitmap bitmap = null;
		// 获取listView实际高度
		for (int i = 0; i < listView.getChildCount(); i++) {
			h += listView.getChildAt(i).getHeight();
		}
		//log.i("实际高度:" + h);
		//log.i("list 高度:" + listView.getHeight());
		// 创建对应大小的bitmap
		bitmap = Bitmap.createBitmap(listView.getWidth(), h, Bitmap.Config.ARGB_8888);
		final Canvas canvas = new Canvas(bitmap);
		listView.draw(canvas);
		// 测试输出
		FileOutputStream out = null;
		try {
			out = new FileOutputStream("/sdcard/screen_test.png");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			if (null != out) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
				out.flush();
				out.close();
			}
		} catch (IOException e) {
			// TODO: handle exception
		}
		return bitmap;
	}

	/**
	 * 获取SDCard的目录路径功能
	 */
	private String getSDCardPath() {
		File sdcardDir = null;
		// 判断SDCard是否存在
		boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if (sdcardExist) {
			sdcardDir = Environment.getExternalStorageDirectory();
		}
		return sdcardDir.toString();
	}

}