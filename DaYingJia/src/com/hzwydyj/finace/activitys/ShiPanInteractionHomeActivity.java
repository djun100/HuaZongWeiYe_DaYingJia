package com.hzwydyj.finace.activitys;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.service.HuaZongWeiYeService;
import com.hzwydyj.finace.utils.CommonUtil;
import com.hzwydyj.finace.view.KeyBoardView;

/**
 * tabHost主页
 * 
 * @author LuoYi
 * 
 */
@SuppressWarnings("deprecation")
public class ShiPanInteractionHomeActivity extends TabActivity implements OnClickListener {

	private IntentFilter homeIF;
	private HZWY_TabHostClose homeTabhostclose;
	private KeyBoardView homeLayout;
	private InputHandler mHandler = new InputHandler();
	private LinearLayout homeLayoutT;
	TabWidget tabWidget;
	Timer chatTimer = new Timer(); // 时长
	private String homeName;
	private String homeRid;
	private String homeRoomlevel;
	private String homeCookieid;
	private String homeUid;

	private int[] drawable_id = { R.drawable.tab_pc_1, R.drawable.tab_pc_2, R.drawable.tab_pc_3, R.drawable.tab_pc_4 };
	private String[] tabSpecName = { "直播", "互动", "精华", "喊单" };

	private static final int BIGGER = 1;
	private static final int SMALLER = 2;
	private static final int MSG_RESIZE = 1;
	private static final int HEIGHT_THREADHOLD = 30;

	private final static int HEARTBEAT = 0x1617;

	ShiPanBirectSeedingActivity birectSeedingActivity;
	ShiPanInteractionActivity interactionActivity;
	ShiPanEssenceActivity essenceActivity;
	ShiPanShoutSingleActivity shoutSingleActivity;

	private Intent intent_heartbeat;
	private HZWY_BaseActivity hzwy_BaseActivity;

	private Timer timer;
	private TimerTask timerTask;

	@SuppressLint("HandlerLeak")
	class InputHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_RESIZE: {
				if (msg.arg1 == BIGGER) {
					homeLayoutT.setVisibility(View.VISIBLE);
				} else {
					homeLayoutT.setVisibility(View.GONE);
				}
			}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.shipan_interaction_homepage);
		/* 初始化布局元素 */
		initViews();
		tabHost();

	}

	/**
	 * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
	 */
	private void initViews() {

		hzwy_BaseActivity = new HZWY_BaseActivity(MyApplication.CONTEXT);
		
		homeTabhostclose = new HZWY_TabHostClose();
		homeIF = new IntentFilter();
		homeIF.addAction("com.huazongweiye.finance.activity");
		registerReceiver(homeTabhostclose, homeIF);
		
		if (CommonUtil.isConnectingToInternet(ShiPanInteractionHomeActivity.this)) {
			timer = new Timer();
			timerTask = new MyTimerTask();
			timer.schedule(timerTask, 0, 5000);
		} else {
			hzwy_BaseActivity.HZWY_Toast(R.string.network_connection_failure);
			ShiPanInteractionHomeActivity.this.finish();// 结束本页
		}
		
		/* 获取前个页面的值 */
		Intent intent = getIntent();
		homeName = intent.getStringExtra("name");
		homeRid = intent.getStringExtra("rid");
		homeRoomlevel = intent.getStringExtra("roomlevel");
		homeCookieid = intent.getStringExtra("cookieid");
		homeUid = intent.getStringExtra("uid");

		birectSeedingActivity = new ShiPanBirectSeedingActivity();
		/* 标题 */
		TextView homeTitle = (TextView) findViewById(R.id.title_text);
		homeTitle.setText(homeName);
		/* 刷新 */
		Button homeRefresh = (Button) findViewById(R.id.refresh_searchbtn);
		homeRefresh.setOnClickListener(this);
		/* 返回 */
		Button homeBack = (Button) findViewById(R.id.button_home_backbtn);
		homeBack.setOnClickListener(this);

		homeLayoutT = (LinearLayout) findViewById(R.id.layout_t);
		homeLayoutT.setVisibility(View.VISIBLE);
		homeLayout = (KeyBoardView) findViewById(R.id.tab_layout);
		homeLayout.setOnResizeListener(new KeyBoardView.OnResizeListener() {

			public void OnResize(int w, int h, int oldw, int oldh) {
				int change = BIGGER;
				if (h < oldh) {
					change = SMALLER;
				}

				Message msg = new Message();
				msg.what = 1;
				msg.arg1 = change;
				mHandler.sendMessage(msg);
			}
		});
	}

	private void tabHost() {
		Resources res = getResources();
		final TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		/**
		 * 直播, 创建一个意图发起的活动选项卡(重用)
		 * */
		intent = new Intent(ShiPanInteractionHomeActivity.this, ShiPanBirectSeedingActivity.class);
		intent.putExtra("rid", homeRid);
		intent.putExtra("roomlevel", homeRoomlevel);
		intent.putExtra("cookieid", homeCookieid);
		intent.putExtra("uid", homeUid);
		// 初始化每个选项卡并将其添加到TabHost TabSpec
		spec = tabHost.newTabSpec("zhibo").setIndicator(createTabView(tabSpecName[0], 0)).setContent(intent);
		tabHost.addTab(spec);

		/* 互动 */
		intent = new Intent(ShiPanInteractionHomeActivity.this, ShiPanInteractionActivity.class);
		intent.putExtra("rid", homeRid);
		intent.putExtra("roomlevel", homeRoomlevel);
		intent.putExtra("cookieid", homeCookieid);
		intent.putExtra("uid", homeUid);
		spec = tabHost.newTabSpec("hudong").setIndicator(createTabView(tabSpecName[1], 1)).setContent(intent);
		tabHost.addTab(spec);

		/* 精华 */
		// Do the same for the other tabs 相同的其他标签吗
		intent = new Intent(ShiPanInteractionHomeActivity.this, ShiPanEssenceActivity.class);
		intent.putExtra("rid", homeRid);
		intent.putExtra("roomlevel", homeRoomlevel);
		intent.putExtra("cookieid", homeCookieid);
		intent.putExtra("uid", homeUid);
		spec = tabHost.newTabSpec("jinghua").setIndicator(createTabView(tabSpecName[2], 2)).setContent(intent);
		tabHost.addTab(spec);

		/* 喊单 */
		intent = new Intent(ShiPanInteractionHomeActivity.this, ShiPanShoutSingleActivity.class);
		intent.putExtra("rid", homeRid);
		intent.putExtra("roomlevel", homeRoomlevel);
		intent.putExtra("cookieid", homeCookieid);
		intent.putExtra("uid", homeUid);
		spec = tabHost.newTabSpec("handan").setIndicator(createTabView(tabSpecName[3], 3)).setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
		View v;
		tabWidget = tabHost.getTabWidget();

		for (int i = 0; i < tabWidget.getChildCount(); i++) {
			v = tabWidget.getChildAt(i);
			v.setBackgroundResource(R.color.tab_beijing);
			TextView textView = (TextView) v.findViewById(R.id.titletab);
			textView.setTextColor(Color.WHITE);
			if (tabHost.getCurrentTab() == i) {
				v.setBackgroundResource(R.drawable.winer_bg_hover);
			}
		}

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
					View v = tabHost.getTabWidget().getChildAt(i);
					v.setBackgroundResource(R.color.tab_beijing);
					if (tabHost.getCurrentTab() == i) {
						v.setBackgroundResource(R.drawable.winer_bg_hover);
					}
				}
			}
		});
	}

	class MyTimerTask extends TimerTask {
		@Override
		public void run() {
			if (CommonUtil.isConnectingToInternet(ShiPanInteractionHomeActivity.this)) {
				handler.sendEmptyMessage(HEARTBEAT);
			} else {
				timerTask.cancel();
			}
		}
	}

	@SuppressLint("HandlerLeak")
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case HEARTBEAT:
				intent_heartbeat = new Intent(ShiPanInteractionHomeActivity.this, HuaZongWeiYeService.class);
				intent_heartbeat.putExtra("rid", homeRid);
				intent_heartbeat.putExtra("name", homeName);
				startService(intent_heartbeat);
				break;
			default:
				break;
			}

		}
	};

	public class HZWY_TabHostClose extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			ShiPanInteractionHomeActivity.this.finish();
		}

		public HZWY_TabHostClose() {
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	private View createTabView(String text, int position) {
		View view 			= LayoutInflater.from(this).inflate(R.layout.mainactivity_tabindicator, null);
		TextView tv 		= (TextView) view.findViewById(R.id.titletab);
		ImageView img 		= (ImageView) view.findViewById(R.id.img);
		img.setImageDrawable((getResources().getDrawable(drawable_id[position])));
		tv.setText(text);
		return view;
	}

	/* 点击事件 */
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.button_home_backbtn:
			timerTask.cancel();
			ShiPanInteractionHomeActivity.this.finish();// 结束本页
			break;

		case R.id.refresh_searchbtn:
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(homeTabhostclose);  
		try {
			timerTask.cancel();
		} catch (Exception e) {
		}
	}
}
