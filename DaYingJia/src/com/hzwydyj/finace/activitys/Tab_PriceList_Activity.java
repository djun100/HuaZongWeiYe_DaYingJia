package com.hzwydyj.finace.activitys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.fragments.Pricelist_fragments;
import com.hzwydyj.finace.utils.HQ;
import com.hzwydyj.finace.utils.MSG_WHAT;
import com.hzwydyj.finace.utils.MyLogger;

/**
 * 行情列表界面
 * 
 * @author Watson Yao
 * 
 */
public class Tab_PriceList_Activity extends FragmentActivity implements com.hzwydyj.finace.fragments.Pricelist_fragments.OnItemSelectedListener {

	/** 识别其他tab跳转过来的界面更新 */
	public static int newintent_ex = -1;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ArrayList<String> drawerTitles;
	private MyLogger log = MyLogger.yLog();
	private TextView title;
	private List<Map<String, String>> listMaps;
	private SharedPreferences sharedPreferences;
	private SharedPreferences sharedPreferences2;

	private Button btn_ad;
	private String ht_ad2_num = "0";
	private String open_url;

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

	protected void onPause() {
		super.onPause();
		// log.i("onPause");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// log.i("onDestroy");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_pricelist);
		// log.i("onCreate");

		title = (TextView) findViewById(R.id.title);
		btn_ad = (Button) findViewById(R.id.btn_ad);
		btn_ad.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				openWebView_base(open_url, "咨询");
			}
		});

		// 第二类广告处理
		sharedPreferences2 = getSharedPreferences("config", MODE_PRIVATE);
		ht_ad2_num = sharedPreferences2.getString(Const.HT_AD2_NUM, "0");

		// init_listMaps();
		if (HQ.BS_EX.equals(newintent_ex + "")) {
			init_listBSS();
		} else if (HQ.WH_EX.equals(newintent_ex + "")) {
			init_listWHHQ();
		} else {
			init_listWHHQ();
		}
		initDrawerLayout();
		selectItem(0);

		if (savedInstanceState == null) {
			selectItem(0);
		} else {
			int position = savedInstanceState.getInt("current_position");
			selectItem(position);
			current_position = position;
		}
	}

	private void openWebView_base(String url, String title) {
		Intent intent_base = new Intent(this, WebView_base.class);
		intent_base.putExtra(Const.WEBVIEW_URL, url);
		intent_base.putExtra(Const.WEBVIEW_TITLE, title);
		startActivity(intent_base);
	}

	protected void onResume() {
		super.onResume();
		// log.i("onResume");
		if (HQ.BS_EX.equals(newintent_ex + "")) {
			init_listBSS();
			selectItem(0);
			initDrawerLayout();
			newintent_ex = -1;
		} else if (HQ.WH_EX.equals(newintent_ex + "")) {
			init_listWHHQ();
			selectItem(0);
			initDrawerLayout();
			newintent_ex = -1;
		}
		newFunctionIntroduction();
	}

	/**
	 * 新功能 新界面 首次打开介绍
	 */
	private void newFunctionIntroduction() {
		sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
		int i = sharedPreferences.getInt("Tab_PriceList_Activity", 0);
		if (i == 0) {
			showNewFunction();
		}
		i++;
		if (i == Integer.MAX_VALUE - 1) {
			i = 0;
		}
		Editor editor = sharedPreferences.edit();
		editor.putInt("Tab_PriceList_Activity", i);
		editor.commit();
	}

	/**
	 * Dialog提示
	 */
	private void showNewFunction() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.shipan_share_dialog, null);

		TextView Title = (TextView) textEntryView.findViewById(R.id.share_title_text);
		ProgressBar Progess = (ProgressBar) textEntryView.findViewById(R.id.progress_bar);
		TextView Content = (TextView) textEntryView.findViewById(R.id.share_content_text);
		TextView Message = (TextView) textEntryView.findViewById(R.id.share_message_text);
		Button queren = (Button) textEntryView.findViewById(R.id.share_queren_button);

		Title.setText("新界面介绍!");
		Progess.setVisibility(View.GONE);
		Content.setVisibility(View.VISIBLE);
		Content.setText("所有行情类别移到侧边栏\n可以点击左上角《选择》\n也可以从屏幕左侧向右滑动呼出");
		Message.setVisibility(View.GONE);

		final AlertDialog dialog = new AlertDialog.Builder(this).setView(textEntryView).create();
		dialog.show();

		queren.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
	}

	private void initDrawerLayout() {
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, drawerTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerLayout.setDrawerListener(new MyDrawerLayoutListener());

		Button btn_selected = (Button) findViewById(R.id.btn_selected_pricelist);
		btn_selected.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// log.i("选择->" + mDrawerLayout.isDrawerOpen(mDrawerList));
				if (drawerlayout_flag) {// 为真就是打开的，要关闭
					mDrawerLayout.closeDrawer(mDrawerList);
				} else {
					mDrawerLayout.openDrawer(mDrawerList);
				}
				drawerlayout_flag = drawerlayout_flag == true ? false : true;
			}
		});
	}

	private int current_position;

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	// <item>国际黄金</item>
	// <item>上金所</item>
	// <item>津贵所</item>
	// <item>广贵所</item>
	// <item>天矿所</item>
	// <item>渤商所</item>
	// <item>海西所</item>
	// <item>大圆银泰</item>
	// <item>兰溪汇丰</item>
	// <item>青岛国金</item>
	// <item>外汇市场</item>
	// <item>全球股指</item>
	// <item>NYME原油</item>
	// <item>IPE原油</item>
	// <item>COMEX期金</item>
	// <item>上海期货</item>

	private void selectItem(int position) {
		// log.i("selectItem->" + position);
		addFragment(listMaps.get(position).get(HQ.MAPKEY_KEY), Integer.parseInt(listMaps.get(position).get(HQ.MAPKEY_EX)));
		title.setText(listMaps.get(position).get(HQ.MAPKEY_NAME));
		// 设置标题栏的标题
		mDrawerList.setItemChecked(position, true);
		current_position = position;
	}

	private void init_listBSS() {
		listMaps = new ArrayList<Map<String, String>>();
		drawerTitles = new ArrayList<String>();
		HashMap<String, String> itemmap = new HashMap<String, String>();

		// 北商所
		itemmap.put(HQ.MAPKEY_EX, HQ.BS_EX);
		itemmap.put(HQ.MAPKEY_KEY, HQ.BS_KEY);
		itemmap.put(HQ.MAPKEY_NAME, HQ.BS_NAME);
		listMaps.add(itemmap);
		drawerTitles.add(HQ.BS_NAME);

		// 上金所
		itemmap = new HashMap<String, String>();
		itemmap.put(HQ.MAPKEY_EX, HQ.SHGOLD_EX);
		itemmap.put(HQ.MAPKEY_KEY, HQ.SHGOLD_KEY);
		itemmap.put(HQ.MAPKEY_NAME, HQ.SHGOLD_NAME);
		listMaps.add(itemmap);
		drawerTitles.add(HQ.SHGOLD_NAME);

		// 国际黄金
		itemmap = new HashMap<String, String>();
		itemmap.put(HQ.MAPKEY_EX, HQ.HJXH_EX);
		itemmap.put(HQ.MAPKEY_KEY, HQ.HJXH_KEY);
		itemmap.put(HQ.MAPKEY_NAME, HQ.HJXH_NAME);
		listMaps.add(itemmap);
		drawerTitles.add(HQ.HJXH_NAME);
	}

	private void init_listWHHQ() {
		listMaps = new ArrayList<Map<String, String>>();
		drawerTitles = new ArrayList<String>();
		HashMap<String, String> itemmap = new HashMap<String, String>();

		// 外汇市场
		itemmap.put(HQ.MAPKEY_EX, HQ.WH_EX);
		itemmap.put(HQ.MAPKEY_KEY, HQ.WH_KEY);
		itemmap.put(HQ.MAPKEY_NAME, HQ.WH_NAME);
		listMaps.add(itemmap);
		drawerTitles.add(HQ.WH_NAME);

		// 全球股指
		itemmap = new HashMap<String, String>();
		itemmap.put(HQ.MAPKEY_EX, HQ.STOCKINDEX_EX);
		itemmap.put(HQ.MAPKEY_KEY, HQ.STOCKINDEX_KEY);
		itemmap.put(HQ.MAPKEY_NAME, HQ.STOCKINDEX_NAME);
		listMaps.add(itemmap);
		drawerTitles.add(HQ.STOCKINDEX_NAME);

		// NYME原油
		itemmap = new HashMap<String, String>();
		itemmap.put(HQ.MAPKEY_EX, HQ.NYMEX_EX);
		itemmap.put(HQ.MAPKEY_KEY, HQ.NYMEX_KEY);
		itemmap.put(HQ.MAPKEY_NAME, HQ.NYMEX_NAME);
		listMaps.add(itemmap);
		drawerTitles.add(HQ.NYMEX_NAME);

		// IPE原油
		itemmap = new HashMap<String, String>();
		itemmap.put(HQ.MAPKEY_EX, HQ.IPE_EX);
		itemmap.put(HQ.MAPKEY_KEY, HQ.IPE_KEY);
		itemmap.put(HQ.MAPKEY_NAME, HQ.IPE_NAME);
		listMaps.add(itemmap);
		drawerTitles.add(HQ.IPE_NAME);

		// COMEX期金
		itemmap = new HashMap<String, String>();
		itemmap.put(HQ.MAPKEY_EX, HQ.COMEX_EX);
		itemmap.put(HQ.MAPKEY_KEY, HQ.COMEX_KEY);
		itemmap.put(HQ.MAPKEY_NAME, HQ.COMEX_NAME);
		listMaps.add(itemmap);
		drawerTitles.add(HQ.COMEX_NAME);

		// 上海期货
		itemmap = new HashMap<String, String>();
		itemmap.put(HQ.MAPKEY_EX, HQ.SHQH_EX);
		itemmap.put(HQ.MAPKEY_KEY, HQ.SHQH_KEY);
		itemmap.put(HQ.MAPKEY_NAME, HQ.SHQH_NAME);
		listMaps.add(itemmap);
		drawerTitles.add(HQ.SHQH_NAME);

	}

	/**
	 * 添加Fragment
	 * 
	 * @param exstr
	 * @param type
	 * @param ex
	 */
	private void addFragment(String selected, int ex) {

		Pricelist_fragments fragment_price = new Pricelist_fragments();

		Bundle args = new Bundle();
		args.putString("selected", selected);
		args.putInt("ex", ex);
		fragment_price.setArguments(args);

		FragmentTransaction transaction_priceItem = getSupportFragmentManager().beginTransaction();
		transaction_priceItem.replace(R.id.fragment_container, fragment_price);
		transaction_priceItem.commit();

		handler.sendEmptyMessageDelayed(MSG_WHAT.DRAWER_CLOSE, 400);

		// 判断广告是否需要显示 已经显示什么样子的广告
		btn_ad.setVisibility(View.GONE);

		for (int i = 0; i < Integer.parseInt(ht_ad2_num); i++) {

			// Log.i("temp", "add Fragment->" + i + "->" +
			// sharedPreferences2.getString(Const.HT_AD2_KEY_SP + i, ""));
			if (selected.equals(sharedPreferences2.getString(Const.HT_AD2_KEY_SP + i, ""))) {
				btn_ad.setVisibility(View.VISIBLE);
				// Log.i("temp", "显示广告");
				open_url = sharedPreferences2.getString(Const.HT_AD2_URL_SP + i, "");
				// Log.i("temp", "显示呗");
			}
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_WHAT.DRAWER_CLOSE:
				mDrawerLayout.closeDrawer(mDrawerList);
				drawerlayout_flag = false;
				break;

			default:
				break;
			}
		};
	};

	public void onpriceitemListsSelected(String code, String name, String selected, String ex, String decimal) {
		new_A(code, name, selected, ex, decimal);
	}

	/**
	 * 行情条目点击后跳转
	 * 
	 * @param code
	 * @param name
	 * @param selected
	 * @param ex
	 */
	private void new_A(String code, String name, String selected, String ex, String decimal) {
		Intent intent = new Intent(Tab_PriceList_Activity.this, PriceView.class);
		intent.putExtra("code", code);
		intent.putExtra("name", name);
		intent.putExtra("selected", selected);
		intent.putExtra("ex", ex);
		intent.putExtra("decimal", decimal);
		// log.i("code=" + code + ", name=" + name + ", selected=" + selected +
		// ", ex=" + ex + ", decimal=" + decimal);
		startActivity(intent);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// super.onSaveInstanceState(outState);
		outState.putInt("current_position", current_position);
	}

	private boolean drawerlayout_flag = false;

	class MyDrawerLayoutListener implements DrawerLayout.DrawerListener {

		public void onDrawerClosed(View arg0) {
			drawerlayout_flag = false;
		}

		public void onDrawerOpened(View arg0) {
			drawerlayout_flag = true;
		}

		public void onDrawerSlide(View arg0, float arg1) {
		}

		public void onDrawerStateChanged(int arg0) {
		}
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
