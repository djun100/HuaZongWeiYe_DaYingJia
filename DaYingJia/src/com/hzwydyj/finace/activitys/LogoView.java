package com.hzwydyj.finace.activitys;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.code.microlog4android.config.PropertyConfigurator;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.utils.MSG_WHAT;
import com.hzwydyj.finace.utils.MyLogger;
import com.hzwydyj.finace.utils.Nettools;
import com.hzwydyj.finace.utils.TCPtools;
import com.hzwydyj.finace.utils.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @author LuoYi
 *
 */
public class LogoView extends Activity {

	private MyLogger 		log = MyLogger.yLog();

	@Override
	protected void onStart() {
		super.onStart();
		// 谷歌分析统计代码
		EasyTracker.getInstance().activityStart(this);
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// 谷歌分析统计代码
		EasyTracker.getInstance().activityStop(this);
	}

	// 主画面handler
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			// 先查一下show的开关
			// 再查一下文件 有就跳转
			// 都满足就跳转
			/* File file = new File("/data/data/com.fx678.finace/files/ht_ad.png");
			 if (file.exists() && "0".equals(sharedPreferences.getString(Const.HT_AD_ANDROID_SHOW_SP, "1"))) {
				 startActivity(new Intent(LogoView.this, HTADView.class));
			 } else {
				 startActivity(new Intent(LogoView.this, MainActivity.class));
			 }*/
			if (msg.what == MSG_WHAT.LOGO2MAIN) {
				Intent intent = new Intent(LogoView.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		}
	};

//	SharedPreferences 			sharedPreferences;
	private TCPtools 			tcPtools;
	private Util 				util;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		PropertyConfigurator.getConfigurator(this).configure();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logoview);

		tcPtools 				= new TCPtools();
		util 					= new Util();

		// 拿民泰的广告 OEM版本都应该注释掉               
		/*if (new Nettools().isNetworkConnected(this)) {
			// getAD(Const.AD_MINTA);
		}*/

		// 启动图片暂停2秒后跳转九宫格
		handler.sendEmptyMessageDelayed(MSG_WHAT.LOGO2MAIN, 1600);

	}

	/**
	 * 异步拿民泰广告
	 */
	private void getAD(String url) {
		MyTask mytask = new MyTask();
		mytask.execute(url, null, null);
	}

	class MyTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
			util.getADfromInputStream(tcPtools.ReadHttpResponse(params[0]), sharedPreferences);
			String linkurl 						= sharedPreferences.getString("linkurl", "http://www.pm166.com/web/pages/wapbs/M_BsReg.aspx?kind=cpmmAPP");
			String imgurl_android 				= sharedPreferences.getString("imgurl_android", "http://m.fx678.com/img/mintai/mtht_android.png");
			try {
				// 还没有封装到单独的类里面
				// mBitmap = loadImageFromUrl(imgurl_android);
				// saveMyBitmap("ad");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}
}