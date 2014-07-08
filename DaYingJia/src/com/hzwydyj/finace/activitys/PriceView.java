package com.hzwydyj.finace.activitys;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.PriceData;
import com.hzwydyj.finace.fragments.Line_k_Fragment;
import com.hzwydyj.finace.fragments.Line_tick_Fragment;
import com.hzwydyj.finace.fragments.Line_time_Fragment;
import com.hzwydyj.finace.present.view.TimeDataDraw;
import com.hzwydyj.finace.utils.MSG_WHAT;
import com.hzwydyj.finace.utils.MyLogger;
import com.hzwydyj.finace.utils.Util;

/**
 * 行情详细页面(分时 K线 分笔)
 * 
 * @author LuoYi
 * 
 */
public class PriceView extends FragmentActivity implements com.hzwydyj.finace.fragments.Line_time_Fragment.OnUDPListener,
		com.hzwydyj.finace.fragments.Line_tick_Fragment.OnUDPListener, com.hzwydyj.finace.fragments.Line_k_Fragment.OnUDPListener {

	/**
	 * 颜色设置 0 无色 1红色 2绿色
	 */
	private int 						bgcolor = 0;

	private DecimalFormat 				df_base;
	private MyLogger 					log = MyLogger.yLog();

	public static int 					move;
	public static long 					DOWN1;

	private int 						nowColor = Color.WHITE;
	private Button 						addBtn;
	private String 						code;
	private String 						ex;
	private String 						name;
	private String 						decimal;
//	private LinearLayout 				drawLayout;
	private LayoutInflater 				mInflater;
//	private ListView 					abListView;
//	private SlidingDrawer 				mDialerDrawer;
//	private ImageButton 				slidingButton;

	private TextView 					nowvalue;
	private TextView 					updownpecent;
	private TextView 					updownvalue;
//	private TextView 					evalue;
	private TextView 					closevalue;
	private TextView 					openvalue;
	private TextView 					highvalue;
	private TextView 					lowvalue;
	private TextView 					timenow;
	// ttj
	private TextView 					buylabel;
	private TextView 					ttjbuy;
	private TextView 					selllabel;
	private TextView 					ttjsell;
	
	private SharedPreferences 			preferences;// 本地配置保存对象
//	private List<KData> 				listData;// 分时历史数据list
	private PriceData 					timeNow;// 分时当前数据
	private List<Map<String, Object>> 	mData;// 五档数据
	private float 						lastClose = 0;// 最后收盘价
	private ProgressDialog 				progressDialog;// 数据取得等待对话框组件
	private TimeDataDraw 				timeDataDraw;
	private boolean 					running = false;// 自动更新线程启动标志
	private Util 						util = new Util();
	Button 								backBtn;
	private String 						errorMsg;
	RadioGroup 							radioGroup_pricelistbar;

	protected void onPause() {
		super.onPause();
		running = false;
	}

	protected void onResume() {
		super.onResume();
		running = true;
	}

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

	/**
	 * 初始化标题栏
	 */
	private void init_TitleBar() {
		((TextView) findViewById(R.id.title)).setText(name);

		// addBtn = (Button) findViewById(R.id.addbtn);
		// addBtn.setOnClickListener(new OnClickListener() {
		// public void onClick(View v) {
		// addDialog();
		// }
		// });

		backBtn = (Button) findViewById(R.id.backbtn);
		backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				PriceView.this.finish();
			}
		});

	}

	/**
	 * 初始化顶部价格控件
	 */
	private void init_TopBarView() {
		nowvalue 		= (TextView) findViewById(R.id.nowvalue);
		updownpecent 	= (TextView) findViewById(R.id.updownpecent);
		updownvalue 	= (TextView) findViewById(R.id.updownvalue);
		openvalue 		= (TextView) findViewById(R.id.openvalue);
		highvalue 		= (TextView) findViewById(R.id.highvalue);
		lowvalue 		= (TextView) findViewById(R.id.lowvalue);
		closevalue 		= (TextView) findViewById(R.id.closevalue);
		timenow 		= (TextView) findViewById(R.id.timenow);
		ttjbuy 			= (TextView) findViewById(R.id.ttjbuy);
		ttjsell 		= (TextView) findViewById(R.id.ttjsell);
	}

	private Button 				openAD;
	private SharedPreferences 	sharedPreferences2;
	private String 				open_url;

	private void openWebView_base(String url, String title) {
		Intent intent_base 		= new Intent(this, WebView_base.class);
		intent_base.putExtra(Const.WEBVIEW_URL, url);
		intent_base.putExtra(Const.WEBVIEW_TITLE, title);
		startActivity(intent_base);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.openAD:
			openWebView_base(open_url, "咨询");
			break;
		case R.id.addbtn:
			addDialog();
			break;

		default:
			break;
		}
	}

	private String ht_ad2_num = "0";
	private String selected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.priceview_a);
		
		hzwy_BaseActivity = new HZWY_BaseActivity(MyApplication.CONTEXT);

		Bundle bundle 		= getIntent().getExtras();
		code 				= bundle.getString("code");
		ex 					= bundle.getString("ex");
		name 				= bundle.getString("name");
		selected 			= bundle.getString("selected");
		decimal 			= bundle.getString("decimal");

		//Log.i("temp", "code->" + code);
		//Log.i("temp", "ex->" + ex);
		//Log.i("temp", "name->" + name);
		//Log.i("temp", "selected->" + selected);
		//Log.i("temp", "decimal->" + decimal);

		preferences 		= getSharedPreferences(Const.PREFERENCES_NAME, Activity.MODE_PRIVATE);
		mInflater 			= (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		openAD 				= (Button) findViewById(R.id.openAD);
		sharedPreferences2 	= getSharedPreferences("config", MODE_PRIVATE);
		ht_ad2_num 			= sharedPreferences2.getString(Const.HT_AD2_NUM, "0");

		// 判断广告是否需要显示 已经显示什么样子的广告
		openAD.setVisibility(View.GONE);

		for (int i = 0; i < Integer.parseInt(ht_ad2_num); i++) {

			//Log.i("temp", "pri" + sharedPreferences2.getString(Const.HT_AD2_KEY_SP + i, ""));
			if (selected.equals(sharedPreferences2.getString(Const.HT_AD2_KEY_SP + i, ""))) {
				openAD.setVisibility(View.VISIBLE);
				open_url 	= sharedPreferences2.getString(Const.HT_AD2_URL_SP + i, "");
				// Log.i("temp", "显示呗");
			}

		}

		if (decimal != null) {
			if ("0".equals(decimal)) {
				df_base = Const.df0;
			} else if ("1".equals(decimal)) {
				df_base = Const.df1;
			} else if ("2".equals(decimal)) {
				df_base = Const.df2;
			} else if ("3".equals(decimal)) {
				df_base = Const.df3;
			} else if ("4".equals(decimal)) {
				df_base = Const.df4;
			}
		}

		init_TitleBar();
		init_BottomTabHost();
		init_TopBarView();
		initProgressD();

		getTCPDataTask(code);

	}

	private void getTCPDataTask(String selected) {
		GetTCPDataTask mytask = new GetTCPDataTask();
		mytask.execute(selected, null, null);
	}

	class GetTCPDataTask extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressD_state(true);
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				// log.i("GetTCPDataTask -> " + params[0]);
				updateData(params[0]);
			} catch (Exception e) {
				progressD_state(false);
				e.printStackTrace();
				sendMessagewhat(MSG_WHAT.ASYNCTASK_FAIL);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressD_state(false);
			sendMessagewhat(MSG_WHAT.SHOW_TOPPRICE);
		}
	}

	private void sendMessagewhat(int what) {
		handler.sendEmptyMessage(what);
	}

	private void initProgressD() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("取得数据...");
		progressDialog.setCancelable(true);
	}

	/**
	 * 进度条显示或隐藏
	 * 
	 * @param isshow
	 */
	private void progressD_state(boolean isshow) {
		if (progressDialog != null) {
			if (isshow) {
				if (!progressDialog.isShowing()) {
					progressDialog.show();
				}
			} else {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}
		}
	}

	private synchronized void updateData(String code) {

		String timeNowURL 	= Const.URL_NOW;
		timeNowURL 			= timeNowURL.replaceFirst(Const.URL_EX, ex);
		timeNowURL 			= timeNowURL.replaceFirst(Const.URL_CODE, code);
		timeNowURL 			= timeNowURL.replaceFirst(Const.URL_DATE, "1333605270");
		timeNowURL 			= timeNowURL.replaceFirst(Const.URL_COUNT, "3");
		// log.i("timeNowURL->" + timeNowURL);
		timeNow 			= util.getTimeNow(timeNowURL, ex);

	}

	public void showNew() {
		showNew(timeNow);
	}

	public void showNew(PriceData new_timeNow) {

		timeNow 			= new_timeNow;
		progressD_state(false);
		lastClose 			= util.getFloat(timeNow.getPrice_lastclose());

		float updown 		= util.getFloat(timeNow.getPrice_last()) - lastClose;
		float updownrate = 0;
		if (lastClose > 0) {
			updownrate 		= updown / lastClose * 100;
		}

		timeNow.setPrice_updown(String.valueOf(df_base.format(updown)));
		timeNow.setPrice_updownrate(String.valueOf(Const.df2.format(updownrate)));
		timeNow.setPrice_lastclose(String.valueOf(df_base.format(lastClose)));

		String ttjbuy_str 	= timeNow.getPriceTTJbuy();
		if (ttjbuy_str != null) {
			if (!"".equals(ttjbuy_str)) {
				ttjbuy.setText(String.valueOf(df_base.format(Float.parseFloat(ttjbuy_str))));
			}
		}

		String ttjsell_str = timeNow.getPriceTTJsell();
		if (ttjsell_str != null) {
			if (!"".equals(ttjsell_str)) {
				ttjsell.setText(String.valueOf(df_base.format(Float.parseFloat(ttjsell_str))));
			}
		}

		float timeNow_last 		= util.getFloat(timeNow.getPrice_last());
		float timeNow_lastclose = util.getFloat(timeNow.getPrice_lastclose());

		// 详细数据更新
		if (timeNow_last - timeNow_lastclose > 0) {
			nowvalue.setTextColor(Color.WHITE);
			nowvalue.setBackgroundColor(Color.RED);
			nowColor 			= Color.RED;
			bgcolor 			= 1;
			updownpecent.setTextColor(Color.RED);
			updownvalue.setTextColor(Color.RED);
		} else if (timeNow_last - timeNow_lastclose < 0) {
			nowvalue.setBackgroundColor(Color.GREEN);
			nowvalue.setTextColor(Color.WHITE);
			nowColor 			= Color.GREEN;
			bgcolor 			= 2;
			updownpecent.setTextColor(Color.GREEN);
			updownvalue.setTextColor(Color.GREEN);
		} else {
			nowvalue.setTextColor(Color.WHITE);
			nowColor 			= Color.WHITE;
			updownpecent.setTextColor(Color.WHITE);
			updownvalue.setTextColor(Color.WHITE);
		}

		nowvalue.setText(String.valueOf(df_base.format(timeNow_last)));
		updownpecent.setText(timeNow.getPrice_updownrate() + "%");
		updownvalue.setText(timeNow.getPrice_updown());

		float timeNow_open 		= util.getFloat(timeNow.getPrice_open());
		if (timeNow_open - timeNow_lastclose > 0) {
			openvalue.setTextColor(Color.RED);
		} else if (timeNow_open - timeNow_lastclose < 0) {
			openvalue.setTextColor(Color.GREEN);
		} else {
			openvalue.setTextColor(Color.WHITE);
		}
		openvalue.setText(String.valueOf(df_base.format(timeNow_open)));
		closevalue.setText(String.valueOf(df_base.format(lastClose)));

		float timeNow_high 		= util.getFloat(timeNow.getPrice_high());
		if (timeNow_high - timeNow_lastclose > 0) {
			highvalue.setTextColor(Color.RED);
		} else if (timeNow_high - timeNow_lastclose < 0) {
			highvalue.setTextColor(Color.GREEN);
		} else {
			highvalue.setTextColor(Color.WHITE);
		}
		highvalue.setText(String.valueOf(df_base.format(timeNow_high)));

		float timeNow_low 		= util.getFloat(timeNow.getPrice_low());
		if (timeNow_low - timeNow_lastclose > 0) {
			lowvalue.setTextColor(Color.RED);
		} else if (timeNow_low - timeNow_lastclose < 0) {
			lowvalue.setTextColor(Color.GREEN);
		} else {
			lowvalue.setTextColor(Color.WHITE);
		}
		lowvalue.setText(String.valueOf(df_base.format(timeNow_low)));
		timenow.setText(util.formatTimeHms(timeNow.getPrice_quotetime()));

		handler.sendEmptyMessageDelayed(MSG_WHAT.TOPPRICE_BGGONE, 300);
	}

	/**
	 * UDP刷新的价格背景还原
	 */
	private void bg_resume() {
		if (bgcolor == 1) {// 红色背景
			nowvalue.setTextColor(Color.RED);
			nowvalue.setBackgroundColor(Color.BLACK);
		} else if (bgcolor == 2) {// 绿色背景
			nowvalue.setTextColor(Color.GREEN);
			nowvalue.setBackgroundColor(Color.BLACK);
		}
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_WHAT.SHOW_TOPPRICE:
				showNew();
				break;
			case MSG_WHAT.SHOW_TOPPRICE_REFRESH:
				showNew((PriceData) msg.obj);
				break;
			case MSG_WHAT.TOPPRICE_BGGONE:
				bg_resume();
				break;
			case MSG_WHAT.WATCHLIST_ADD:
				hzwy_BaseActivity.HZWY_Toast1("已添加到我的自选中。");
				break;
			case MSG_WHAT.WATCHLIST_EXIST:
				hzwy_BaseActivity.HZWY_Toast1("我的自选中已有此项。");
				break;
			default:
				break;
			}
		}
	};

	protected void addDialog() {
		LayoutInflater factory 					= LayoutInflater.from(this);
		final View textEntryView 				= factory.inflate(R.layout.shipan_share_dialog, null);
		
		
		RelativeLayout Title = (RelativeLayout) textEntryView.findViewById(R.id.title_layout);
		ProgressBar Progess = (ProgressBar) textEntryView.findViewById(R.id.progress_bar);
		TextView Content = (TextView) textEntryView.findViewById(R.id.share_content_text);
		TextView Message = (TextView) textEntryView.findViewById(R.id.share_message_text);
		Button queren = (Button) textEntryView.findViewById(R.id.share_queren_button);
		Button quxiao = (Button) textEntryView.findViewById(R.id.share_quxiao_button);
		
		
		Title.setVisibility(View.GONE);
		Progess.setVisibility(View.GONE);
		Content.setVisibility(View.VISIBLE);
		Content.setText("确认要添加到<我的自选>吗？");
		Message.setVisibility(View.GONE);
		quxiao.setVisibility(View.VISIBLE);
		
		final AlertDialog dialog = new AlertDialog.Builder(this).setView(textEntryView).create();
		dialog.show();
		
		queren.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread() {
					public void run() {

						SharedPreferences.Editor editor = preferences.edit();
						String opTemp 					= preferences.getString(Const.PREF_OPTIONAL, "");
						String opNameTemp 				= preferences.getString(Const.PREF_OPTIONAL_NAME, "");
						String exTemp 					= preferences.getString(Const.PREF_EX, "");
						String selectedTemp 			= preferences.getString(Const.PREF_SELECTED, "");
						if ("".equals(opTemp)) {
							opTemp 			= code;
							opNameTemp 		= name;
							exTemp 			= ex;
							selectedTemp 	= selected;
						} else {
							String[] tmp = opTemp.split(",");
							for (int i = 0; i < tmp.length; i++) {
								if (code.equals(tmp[i])) {
									sendMessagewhat(MSG_WHAT.WATCHLIST_EXIST);
									return;
								}
							}
							opTemp 			= opTemp + "," + code;
							exTemp 			= exTemp + "," + ex;
							opNameTemp 		= opNameTemp + "," + name;
							selectedTemp 	= selectedTemp + "," + selected;
						}
						editor.putString(Const.PREF_OPTIONAL, opTemp);
						editor.putString(Const.PREF_EX, exTemp);
						editor.putString(Const.PREF_OPTIONAL_NAME, opNameTemp);
						editor.putString(Const.PREF_SELECTED, selectedTemp);
						editor.commit();
						sendMessagewhat(MSG_WHAT.WATCHLIST_ADD);
					}
				}.start();
				dialog.cancel();
			}
		});
		
		quxiao.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
	}

	private FragmentTabHost 		mTabHost;
	private LayoutInflater 			layoutInflater;
	/** Fragment界面列表 */
	private Class fragmentArray[] 	= { Line_time_Fragment.class, Line_k_Fragment.class, Line_tick_Fragment.class };
	/** tabhost 按钮列表 */
//	private int mImageViewArray[] 	= { R.drawable._nav_bg, R.drawable._nav_bg, R.drawable._nav_bg };
	/** tabhost 文字列表 */
	private String mTextviewArray[] = { "分时", "K线", "分笔" };

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

	View currentView;
	View currentView_old;

	private HZWY_BaseActivity hzwy_BaseActivity;

	/**
	 * 初始化底部tabhost组件
	 */
	private void init_BottomTabHost() {
		layoutInflater 	= LayoutInflater.from(this);
		mTabHost 		= (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setBackgroundColor(Color.BLACK);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.fragment_container);

		int count = fragmentArray.length;
		for (int i = 0; i < count; i++) {
			TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));

			Bundle args = new Bundle();
			args.putString("code", code);
			args.putString("ex", ex);
			args.putString("name", name);
			args.putString("decimal", decimal);
			mTabHost.addTab(tabSpec, fragmentArray[i], args);

			// 设置Tab按钮的背景
			// mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.action_search);
		}

		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				currentView = mTabHost.getCurrentView();
				currentView.setAnimation(inFromRightAnimation());
				if (currentView_old != null) {
					currentView_old.setAnimation(outToRightAnimation());
				}
				currentView_old = currentView;
			}
		});

	}

	/**
	 * 给Tab按钮设置图标和文字
	 */
	private View getTabItemView(int index) {
		View view = layoutInflater.inflate(R.layout.tab_item_view, null);

//		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
//		imageView.setImageResource(mImageViewArray[index]);

		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setText(mTextviewArray[index]);

		return view;
	}

	public void onUDP_push(String timeNow) {
		// TODO Auto-generated method stub
		String str 		= timeNow;
		PriceData temp 	= null;
		if (str.indexOf("{") >= 0 && str.indexOf("}") >= 0) {
			temp 		= util.getTimeNowUDPString(str, null);
//			mCallback.onUDP_Update(str);
//			log.i("mCallback->str");
		}
		if (temp != null && running == true) {
//			log.i("temp", "UDP->" + str.trim());
			Message message = Message.obtain();
			message.what = MSG_WHAT.SHOW_TOPPRICE_REFRESH;
			message.obj = temp;
			handler.sendMessage(message);
		}
	}
}
