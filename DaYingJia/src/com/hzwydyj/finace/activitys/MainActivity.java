package com.hzwydyj.finace.activitys;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.HT_AD;
import com.hzwydyj.finace.data.HT_AD2;
import com.hzwydyj.finace.utils.MyLogger;
import com.hzwydyj.finace.utils.Nettools;
import com.hzwydyj.finace.utils.Util;
import com.hzwydyj.finace.xg.NotificationService;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.common.Constants;
import com.tencent.android.tpush.service.cache.CacheManager;

/**
 * 
 * @author LuoYi
 * 
 */
@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class MainActivity extends TabActivity implements OnItemClickListener, OnQueryTextListener, OnScrollListener {

	private MyLogger log = MyLogger.yLog();
	/** 底部导航个数 */
	private static final int MAIN_TAB_NUMBER = 5;

	private HZWY_BaseActivity hzwy_BaseActivity;

	private MsgReceiver updateListViewReceiver;
	private int allRecorders = 0;// 全部记录数
	private NotificationService notificationService;// 获取通知数据服务
	Message m = null;
	private int firstItem;// 第一条显示出来的数据的游标
	private int lastItem;// 最后显示出来数据的游标
	private boolean isLast = false;// 是否最后一条

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
		if (new Nettools().isNetworkConnected(this)) {
			getHTAD(urls);
		}
		XGPushClickedResult click = XGPushManager.onActivityStarted(this);
		Log.d("TPush", "onResumeXGPushClickedResult:" + click);
	}

	@Override
	protected void onPause() {
		super.onPause();
		XGPushManager.onActivityStoped(this);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(updateListViewReceiver);  
		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	private String[] urls = { Const.AD_HT, Const.AD_HT2 };

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainactivity);

		XGPushConfig.enableDebug(this, true);
		// getSupportActionBar().setHomeButtonEnabled(true);
		XGPushManager.registerPush(getApplicationContext());
		notificationService = NotificationService.getInstance(this);
		// 0.注册数据更新监听器
		updateListViewReceiver = new MsgReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.hzwydyj.finace.UPDATE_LISTVIEW");
		registerReceiver(updateListViewReceiver, intentFilter);
		// 1.获取设备Token
		Handler handler = new HandlerExtension(MainActivity.this);
		m = handler.obtainMessage();
		// 注册接口
		XGPushManager.registerPush(getApplicationContext(), new XGIOperateCallback() {
			public void onSuccess(Object data, int flag) {
				Log.w(Constants.LogTag, "+++ register push sucess. token:" + data);
				m.obj = "+++ register push sucess. token:" + data;
				m.sendToTarget();
				CacheManager.getRegisterInfo();
			}

			public void onFail(Object data, int errCode, String msg) {
				Log.w(Constants.LogTag, "+++ register push fail. token:" + data + ", errCode:" + errCode + ",msg:" + msg);

				m.obj = "+++ register push fail. token:" + data + ", errCode:" + errCode + ",msg:" + msg;
				m.sendToTarget();
			}
		});

		hzwy_BaseActivity = new HZWY_BaseActivity(MyApplication.CONTEXT);

		if (new Nettools().isNetworkConnected(this)) {
			checkVersion();
		}
		initTabHost();

		String content = getIntent().getStringExtra("content");
		if (content != null) {
			hzwy_BaseActivity.HZWY_Toast1(content);
		}

	}

	private static TabHost tabHost;
	private Class[] tabclasses = { Tab_Home_Activity.class, Tab_PriceList_Activity.class, Tab_News_Activity.class, Tab_Calendar_Activity.class, OptionalView.class };
	private int[] drawable_id = { R.color.bottombar_01, R.color.bottombar_02, R.color.bottombar_03, R.color.bottombar_04, R.color.bottombar_05 };
	private String[] tabSpec = { "tab1", "tab2", "tab3", "tab4", "tab5" };
	private String[] tabSpecName = { "首页", "行情中心", "新闻资讯", "财经日历", "我的自选" };
	private View currentView;
	private View currentView_old;

	@SuppressWarnings("deprecation")
	private void initTabHost() {
		tabHost = getTabHost();
		Intent intent;
		TabSpec spec;

		for (int i = 0; i < MAIN_TAB_NUMBER; i++) {
			intent = new Intent(this, tabclasses[i]);
			spec = tabHost.newTabSpec(tabSpec[i]).setIndicator(createTabView(tabSpecName[i], i)).setContent(intent);
			tabHost.addTab(spec);
		}

		tabHost.setCurrentTab(0);

		getTabHost().setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				currentView = getTabHost().getCurrentView();
				currentView.setAnimation(inFromRightAnimation());
				if (currentView_old != null) {
					currentView_old.setAnimation(outToRightAnimation());
				}
				currentView_old = currentView;
			}
		});
	}

	private View createTabView(String text, int position) {
		View view = LayoutInflater.from(this).inflate(R.layout.mainactivity_tabindicator, null);
		TextView tv = (TextView) view.findViewById(R.id.titletab);
		ImageView img = (ImageView) view.findViewById(R.id.img);
		img.setImageDrawable((getResources().getDrawable(drawable_id[position])));
		tv.setText(text);
		return view;
	}

	public static void setHost2() {
		tabHost.setCurrentTab(1);
	}

	public static void setHost3() {
		tabHost.setCurrentTab(2);
	}

	public static void setHost4() {
		tabHost.setCurrentTab(3);
	}

	public static void setHost5() {
		tabHost.setCurrentTab(4);
	}

	/**
	 * 淡入动画
	 * 
	 * @return
	 */
	public Animation inFromRightAnimation() {
		Animation inFromRight = new AlphaAnimation(0.5f, 1);
		inFromRight.setDuration(200);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}

	/**
	 * 淡出动画
	 * 
	 * @return
	 */
	public Animation outToRightAnimation() {
		Animation outtoLeft = new AlphaAnimation(1, 0.5f);
		outtoLeft.setDuration(200);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}

	private String version = "";
	private Util util;

	/**
	 * 检查版本信息
	 */
	private void checkVersion() {
		MyTask mytask = new MyTask();
		mytask.execute(null, null, null);
	}

	class MyTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			util = new Util();
		}

		@Override
		protected Void doInBackground(Void... params) {
			version = util.checkVersion(Const.APP_VersionCheckURL);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			String v = version.substring(0, 1);
			// v = "1";
			if ("1".equals(v)) {
				AlertDialog.Builder builder = new Builder(MainActivity.this);
				builder.setMessage("发现新版本，需要下载最新版吗？");
				builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent1 = new Intent(Intent.ACTION_VIEW);
						intent1.setData(Uri.parse(version.substring(2, version.length())));
						startActivity(intent1);
					}
				});
				builder.setNegativeButton("不更新", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			} else if ("2".equals(v)) {
				AlertDialog.Builder builder = new Builder(MainActivity.this);
				builder.setMessage("请更新到最新版使用！");
				builder.setTitle("提示");
				builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent1 = new Intent(Intent.ACTION_VIEW);
						intent1.setData(Uri.parse(version.substring(2, version.length())));
						startActivity(intent1);
					}
				});
				builder.create().show();
			}
		}
	}

	/**
	 * 异步拿民泰广告
	 */
	private void getHTAD(String[] url) {
		HTAD_Task mytask = new HTAD_Task();
		mytask.execute(url, null, null);
	}

	SharedPreferences sharedPreferences;

	class HTAD_Task extends AsyncTask<String[], Void, Void> {

		private Util mUtil;
		private Editor editor;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mUtil = new Util();
			sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
			editor = sharedPreferences.edit();
		}

		@Override
		protected Void doInBackground(String[]... params) {

			try {
				List<HT_AD2> ht_AD2 = mUtil.getHT_AD2(params[0][1]);
				HT_AD ht_AD = mUtil.getHT_AD(params[0][0]);

				// 检查版本号 如果有更新 需要重新下载图片保存
				// 查看本地版本
				String net_ht_ad_version = ht_AD.getVersion();
				String net_ht_ad_androidurl = ht_AD.getAndroid_url();
				String local_ht_ad_version = sharedPreferences.getString(Const.HT_AD_VERSION_SP, "0");

				// 如果本地版本和 查询版本不一致（具体是新的大于旧的 则下载新图，覆盖原图）
				// 如果没有文件 也会去下载的
				/*
				 * File file = new
				 * File("/data/data/com.fx678.finace/files/ht_ad.png");
				 * 
				 * if (!file.exists() || (Integer.parseInt(net_ht_ad_version) >
				 * Integer.parseInt(local_ht_ad_version))) { log.i("下载图片");
				 * download_save(net_ht_ad_androidurl); }
				 */

				editor.putString(Const.HT_AD_VERSION_SP, ht_AD.getVersion());
				editor.putString(Const.HT_AD_ANDROID_SHOW_SP, ht_AD.getAndroid_show());
				editor.putString(Const.HT_AD_ANDROID_URL_SP, ht_AD.getAndroid_url());
				editor.putString(Const.HT_AD_ANDROIDPAD_SHOW, ht_AD.getAndroidpad_show());
				editor.putString(Const.HT_AD_ANDROIDPAD_URL_SP, ht_AD.getAndroidpad_url());
				editor.putString(Const.HT_AD_DESCRIBE_MODIFY_DATE_SP, ht_AD.getDescribe_modify_date());
				editor.putString(Const.HT_AD_DESCRIBE_EXPIRE_DATE_SP, ht_AD.getDescribe_expire_date());
				// 第二类广告添加
				editor.putString(Const.HT_AD2_NUM, ht_AD2.size() + "");
				// Log.i("temp", "get size->" + ht_AD2.size());
				for (int i = 0; i < ht_AD2.size(); i++) {
					editor.putString(Const.HT_AD2_KEY_SP + i, ht_AD2.get(i).getKey());
					// Log.i("temp", "putsp->key->" + i + "->" +
					// ht_AD2.get(i).getKey());
					editor.putString(Const.HT_AD2_URL_SP + i, ht_AD2.get(i).getUrl());
					// Log.i("temp", "putsp->url->" + i + "->" +
					// ht_AD2.get(i).getUrl());
				}

				editor.commit();
			} catch (Exception e) {
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}

	}

	private void download_save(String url) {
		try {
			mBitmap = loadImageFromUrl(url);
			saveMyBitmap("ht_ad");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * url 下载图片
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public Bitmap loadImageFromUrl(String url) throws Exception {
		final DefaultHttpClient client = new DefaultHttpClient();
		final HttpGet getRequest = new HttpGet(url);

		HttpResponse response = client.execute(getRequest);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			// Log.e("PicShow", "Request URL failed, error code =" +
			// statusCode);
		}

		HttpEntity entity = response.getEntity();
		if (entity == null) {
			// Log.e("PicShow", "HttpEntity is null");
		}
		InputStream is = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			is = entity.getContent();
			byte[] buf = new byte[1024];
			int readBytes = -1;
			while ((readBytes = is.read(buf)) != -1) {
				baos.write(buf, 0, readBytes);
			}
		} finally {
			if (baos != null) {
				baos.close();
			}
			if (is != null) {
				is.close();
			}
		}
		byte[] imageArray = baos.toByteArray();
		return BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length);
	}

	Bitmap mBitmap;

	/**
	 * 保存图片
	 * 
	 * @param bitName
	 * @throws IOException
	 */
	public void saveMyBitmap(String bitName) throws IOException {
		File f = new File("/data/data/com.fx678.finace/files/" + bitName + ".png");
		f.createNewFile();
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 2秒内后退两次即可退出应用 */
	private static final int EXIT_APP_TIME = 2000;

	/** 后退键计时 */
	private long time;

	public void onBackPressed() {
		if (time != 0 && System.currentTimeMillis() - time < EXIT_APP_TIME) {
			this.finish();
		} else {
			time = System.currentTimeMillis();
			hzwy_BaseActivity.HZWY_Toast1("再按一次退出《大赢家》");
			return;
		}
		super.onBackPressed();
	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		firstItem = firstVisibleItem;
		lastItem = totalItemCount - 1;
		if (firstVisibleItem + visibleItemCount == totalItemCount) {
			isLast = true;
		} else {
			isLast = false;
		}
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		newText = newText.isEmpty() ? "" : newText;
		return false;
	}

	public boolean onQueryTextSubmit(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public class MsgReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			allRecorders = notificationService.getCount();
		}
	}

	private static class HandlerExtension extends Handler {
		WeakReference<MainActivity> mActivity;

		HandlerExtension(MainActivity activity) {
			mActivity = new WeakReference<MainActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			MainActivity theActivity = mActivity.get();
			if (msg != null) {
				Log.w(Constants.LogTag, msg.obj.toString());
			}
		}
	}
}
