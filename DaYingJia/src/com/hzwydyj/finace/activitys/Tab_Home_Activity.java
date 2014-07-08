package com.hzwydyj.finace.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.adapter.Tab_Home_FragmentPagerAdapter;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.WinnerInside;
import com.hzwydyj.finace.utils.HQ;
import com.hzwydyj.finace.utils.MoveBg;
import com.hzwydyj.finace.utils.Util;

/**
 * 
 * @author LuoYi
 * 
 */
public class Tab_Home_Activity extends FragmentActivity {

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

	private Animation fadein;
	private ViewPager viewPager;
	/** 指示点的图片宽度 */
	private int bmpW;
	private LinearLayout bottom_layout, bottom_layout_b1, bottom_layout_b2, bottom_layout_b3;
	private ImageView img, img_b1, img_b2, img_b3;
	private DisplayMetrics dm;
	private Util homeUtil = new Util();
	private AlertDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_home);

		initViewPager();
		// initIndicatiors();
	}

	private void initIndicatiors() {
		bottom_layout = (LinearLayout) findViewById(R.id.indicatiors);
		bottom_layout_b1 = (LinearLayout) findViewById(R.id.indicatiors_b1);
		bottom_layout_b2 = (LinearLayout) findViewById(R.id.indicatiors_b2);
		bottom_layout_b3 = (LinearLayout) findViewById(R.id.indicatiors_b3);
		img = new ImageView(this);
		img_b1 = new ImageView(this);
		img_b2 = new ImageView(this);
		img_b3 = new ImageView(this);

		img.setImageResource(R.drawable.btn_radio_on_holo_dark);
		img_b1.setImageResource(R.drawable.btn_radio_on_disabled_holo_dark);
		img_b2.setImageResource(R.drawable.btn_radio_on_disabled_holo_dark);
		img_b3.setImageResource(R.drawable.btn_radio_on_disabled_holo_dark);

		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.btn_radio_on_holo_dark).getWidth();
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		startLeft = MoveBg.moveFrontBg(img, -bmpW / 2 + dm.widthPixels * (4) / 10, -bmpW / 2 + dm.widthPixels * (4) / 10, 0, 0);
		MoveBg.moveFrontBg(img_b1, -bmpW / 2 + dm.widthPixels * (4) / 10, -bmpW / 2 + dm.widthPixels * (4) / 10, 0, 0);
		MoveBg.moveFrontBg(img_b2, -bmpW / 2 + dm.widthPixels * (5) / 10, -bmpW / 2 + dm.widthPixels * (5) / 10, 0, 0);
		MoveBg.moveFrontBg(img_b3, -bmpW / 2 + dm.widthPixels * (6) / 10, -bmpW / 2 + dm.widthPixels * (6) / 10, 0, 0);

		bottom_layout_b1.addView(img_b1);
		// bottom_layout_b2.addView(img_b2);
		bottom_layout_b3.addView(img_b3);
		bottom_layout.addView(img);
	}

	private Tab_Home_FragmentPagerAdapter adapter;

	private void initViewPager() {
		viewPager = (ViewPager) findViewById(R.id.vPager);
		adapter = new Tab_Home_FragmentPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	int startLeft;

	public class MyOnPageChangeListener implements OnPageChangeListener {

		public void onPageScrollStateChanged(int arg0) {
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		public void onPageSelected(int arg0) {
			startLeft = MoveBg.moveFrontBg(img, startLeft, -bmpW / 2 + dm.widthPixels * (4 + arg0 * 2) / 10, 0, 0);
		}

	}

	/** 民泰广告使用 */
	SharedPreferences sharedPreferences;

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_01:// 贵金属行情
			openHQ(HQ.BS_EX);
			break;

		case R.id.btn_02:// 全球市场
			openHQ(HQ.WH_EX);
			break;

		case R.id.btn_03:// 新闻资讯
			openNews(Const.URL_NEWS_GDBB);
			break;

		case R.id.btn_04:// ETF
			startActivity(new Intent(this, ETFView.class));
			break;

		case R.id.btn_05:// 名师讲堂
			startActivity(new Intent(this, PointPartitionActivity.class));// 名师讲堂
			break;

		case R.id.btn_06:// 实盘直播
			// startActivity(new Intent(Tab_Home_Activity.this,
			// ShiPanLoginActivity.class));// 实盘直播

			SharedPreferences preferences = getSharedPreferences(Const.PREFERNCES_SHIPAN, Activity.MODE_PRIVATE);
			String shiGroupid = preferences.getString("groupid", "");
			String shiGroupTitle = preferences.getString("groupTitle", "");
			String shiGrade = preferences.getString("grade", "");
			String shiRoomlevel = preferences.getString("roomlevel", "");
			String shiCookieid = preferences.getString("cookieid", "");
			String shiUid = preferences.getString("uid", "");

			if (!"".equals(shiUid) || !"".equals(shiCookieid)) {
				Intent intent = new Intent(Tab_Home_Activity.this, ShiPanSelectActivity.class);
				intent.putExtra("groupid", shiGroupid);
				intent.putExtra("groupTitle", shiGroupTitle);
				intent.putExtra("grade", shiGrade);
				intent.putExtra("roomlevel", shiRoomlevel);
				intent.putExtra("cookieid", shiCookieid);
				intent.putExtra("uid", shiUid);
				startActivity(intent);// 实盘直播
			} else {
				startActivity(new Intent(Tab_Home_Activity.this, ShiPanLoginActivity.class));// 实盘直播
			}
			break;

		case R.id.btn_07:// 赢家内参
			openFile();
			break;

		case R.id.btn_08:// 开户指南
			// 金牌分析师
			openWebView_base(Const.URL_KHZN, getResources().getString(R.string.home_btn_08));
			// Toast.makeText(getApplicationContext(), "该模块正在开发中，敬请期待。thank's",
			// Toast.LENGTH_LONG).show();
			break;

		case R.id.btn_10:// 汇通论坛
			// 汇通论坛
			openWebView_base(Const.URL_HTLT, getResources().getString(R.string.home_btn_10));
			break;

		case R.id.btn_11:// 客户服务
			// 客户服务
			startActivity(new Intent(this, HelpView.class));
			break;

		// case R.id.btn_12:// 给我评分
		// // 给我评分
		// giveMeStar();
		// break;

		default:
			break;
		}
	}

	private void openFile() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.shipan_share_dialog, null);
		TextView outInTitle = (TextView) textEntryView.findViewById(R.id.share_title_text);
		TextView outInContent = (TextView) textEntryView.findViewById(R.id.share_content_text);
		Button outInQueRen = (Button) textEntryView.findViewById(R.id.share_queren_button);
		Button outInQuXiao = (Button) textEntryView.findViewById(R.id.share_quxiao_button);

		outInTitle.setText(R.string.tishi_warm_prompt);
		outInContent.setText(R.string.tishi_download);
		outInQueRen.setText(R.string.tishi_yes);
		outInQuXiao.setText(R.string.tishi_no);
		outInQuXiao.setVisibility(View.VISIBLE);

		dialog = new AlertDialog.Builder(this).setView(textEntryView).create();
		dialog.show();

		outInQueRen.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				WinnerInsides();
			}
		});

		outInQuXiao.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
	}

	public void WinnerInsides() {
		new Thread(new Runnable() {

			public void run() {
				String url = Const.HZWY_URL + "type=referlatest";
				Log.i("temp", "URl-->>" + url);
				try {
					Message msg = homeHandler.obtainMessage();
					msg.obj = homeUtil.getWinnerInside_post(url);
					homeHandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	Handler homeHandler = new Handler() {

		public void handleMessage(Message msg) {
			WinnerInside winnerInside = (WinnerInside) msg.obj;
			String doc_url = winnerInside.getDoc_url();
			Uri uri = Uri.parse(doc_url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
			dialog.cancel();
		};
	};

	/**
	 * 跳转指定行情
	 * 
	 * @param HQ_EX
	 */
	private void openHQ(String HQ_EX) {
		Tab_PriceList_Activity.newintent_ex = Integer.parseInt(HQ_EX);
		MainActivity.setHost2();
	}

	/**
	 * 滚动播报
	 */
	private void openNews(String news_URL) {
		Tab_News_Activity.newintent_news = news_URL;
		MainActivity.setHost3();
	}

	/**
	 * 实时解盘
	 */
	private void openWebView_SSJP() {
		sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
		Intent intent_cpmm = new Intent(this, WebView_base.class);
		intent_cpmm.putExtra(Const.WEBVIEW_URL, sharedPreferences.getString("linkurl", "http://www.pm166.com/web/pages/wapbs/M_BsReg.aspx?kind=cpmmAPP"));
		intent_cpmm.putExtra(Const.WEBVIEW_TITLE, sharedPreferences.getString("title", "实时解盘"));
		startActivity(intent_cpmm);
	}

	private void openWebView_base(String url, String title) {
		Intent intent_base = new Intent(this, WebView_base.class);
		intent_base.putExtra(Const.WEBVIEW_URL, url);
		intent_base.putExtra(Const.WEBVIEW_TITLE, title);
		startActivity(intent_base);
	}

	private void giveMeStar() {
		String mAddress = "market://details?id=" + Const.APP_PACKETNAME;
		Intent marketIntent = new Intent("android.intent.action.VIEW");
		marketIntent.setData(Uri.parse(mAddress));
		try {
			startActivity(marketIntent);
		} catch (Exception e) {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	// //////////////////////////////
	// // 系统事件
	// //////////////////////////////

	/**
	 * 将事件传递给TabActivity,统一管理后退事件
	 */
	@Override
	public void onBackPressed() {
		this.getParent().onBackPressed();
	}
}
