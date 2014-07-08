package com.hzwydyj.finace.activitys;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.google.analytics.tracking.android.EasyTracker;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.utils.MyLogger;

/**
 * 启动页面之后的广告页面
 * 
 * @author LuoYi
 * 
 */
public class HTADView extends Activity {

	private MyLogger 			log = MyLogger.yLog();

	@Override
	protected void onStart() {
		super.onStart();
		// 谷歌分析统计代码
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// 谷歌分析统计代码
		EasyTracker.getInstance().activityStop(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// log.i("onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		// log.i("onPause");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// log.i("onDestroy");
	}

	Bitmap 								mBitmap;
	private boolean 					onclick_flag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.htadview);

		ImageView htad 					= (ImageView) findViewById(R.id.htad);

		File file 						= new File("/data/data/com.fx678.finace/files/ht_ad.png");
		if (file.exists()) { // 判断文件是否存在
			// log.i("文件存在");
			mBitmap 					= BitmapFactory.decodeFile("/data/data/com.fx678.finace/files/ht_ad.png");// 通过BitmapFactory将图片文件转成Bitmap
		} else {
			// log.i("文件不存在");
		}

		if (mBitmap != null) {
			htad.setBackgroundDrawable(new BitmapDrawable(getResources(), mBitmap));
			// log.i("mBitmap != null");
		} else {
			// htad.setBackgroundDrawable(getResources().getDrawable(R.drawable.iconsethelp_2));
			// log.i("mBitmap == null");
		}

		handler.sendEmptyMessageDelayed(112, 5000);//

	}

	// 主画面handler
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (onclick_flag != true) {
				startOtherA();
			}
		}
	};

	private void startOtherA() {
		startActivity(new Intent(HTADView.this, MainActivity.class));
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.htad:
			onclick_flag = true;
			startOtherA();
			break;

		default:
			break;
		}
	}
}
