package com.hzwydyj.finace.activitys;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
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
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.fragments.News_Fragment;
import com.hzwydyj.finace.fragments.News_keeped_Fragment;
import com.hzwydyj.finace.utils.MSG_WHAT;
import com.hzwydyj.finace.utils.MyLogger;

/**
 * 
 * @author LuoYi
 * 
 */
public class Tab_News_Activity extends FragmentActivity implements ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener {

	/** 识别其他tab跳转过来的界面更新 */
	public static String newintent_news = "";
	private DrawerLayout mDrawerLayout;
	private ExpandableListView mDrawerList;
	private TextView title;
	private SharedPreferences sharedPreferences;
	private int[] title_icons;
	private MyLogger log = MyLogger.yLog();
	private boolean mykeepedstate;
	private Button btn_keeped;
	private boolean onCreateAddFragment;
	private ArrayList<String> groupList;
	private ArrayList<List<String>> childList;

	@Override
	protected void onStart() {
		super.onStart();
		// 谷歌分析统计代码
		EasyTracker.getInstance().activityStart(this);
		// Log.i("temp", "A onStart");
	}

	@Override
	protected void onStop() {
		super.onStop();
		// 谷歌分析统计代码
		EasyTracker.getInstance().activityStop(this);
		// Log.i("temp", "A onStop");
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Log.i("temp", "A onPause");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_news);
		// Log.i("temp", "A onCreate");

		title = (TextView) findViewById(R.id.title);
		title_icons = new int[] { R.drawable.g1, R.drawable.g2, R.drawable.g3, R.drawable.g4 };

		InitData();
		initDrawerLayout();
		initKeep();

		// 横屏切换保存
		if (savedInstanceState == null) {
			addFragment(Const.URL_NEWS_GDBB, "滚动播报");
			onCreateAddFragment = true;
		} else {

			onCreateAddFragment = savedInstanceState.getBoolean("onCreateAddFragment");
			if (savedInstanceState.getBoolean("mykeepedstate")) {
				addkeepedFragment();
			} else {
				int groupPosition = savedInstanceState.getInt("current_groupPosition");
				int childPosition = savedInstanceState.getInt("current_childPosition");
				// Log.i("temp", "groupPosition->" + groupPosition +
				// ",childPosition->" + childPosition);

				if (groupPosition == 0) {
					addFragment(group0_URL[childPosition], group0_title[childPosition]);
				} else if (groupPosition == 1) {
					addFragment(group1_URL[childPosition], group1_title[childPosition]);
				} else if (groupPosition == 2) {
					addFragment(group2_URL[childPosition], group2_title[childPosition]);
				} else if (groupPosition == 3) {
					addFragment(group3_URL[childPosition], group3_title[childPosition]);
				}
				current_groupPosition = groupPosition;
				current_childPosition = childPosition;
			}
		}
	}

	private void initKeep() {
		btn_keeped = (Button) findViewById(R.id.btn_keeped);
		btn_keeped.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				addkeepedFragment();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Log.i("temp", "A onResume");

		// 如果是从九宫格跳过来的 就需要刷新到“滚动播报”列表
		if (Const.URL_NEWS_GDBB.equals(newintent_news)) {
			if (onCreateAddFragment == false) {
				// Log.i("temp", "onResume-addFragment");
				current_groupPosition = 0;
				current_childPosition = 0;
				addFragment(Const.URL_NEWS_GDBB, "滚动播报");
				newintent_news = "";
			}
		}
		newFunctionIntroduction();
	}

	/**
	 * 添加我的自选
	 * 
	 * @param exstr
	 * @param type
	 * @param ex
	 */
	private void addkeepedFragment() {

		News_keeped_Fragment fragment_price = new News_keeped_Fragment();

		FragmentTransaction transaction_priceItem = getSupportFragmentManager().beginTransaction();
		transaction_priceItem.replace(R.id.fragment_container, fragment_price);
		transaction_priceItem.addToBackStack(null);
		transaction_priceItem.commitAllowingStateLoss();

		mykeepedstate = true;
		title.setText("我的收藏");
		btn_keeped.setVisibility(View.GONE);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	/**
	 * 
	 * 
	 * @param exstr
	 * @param type
	 * @param ex
	 */
	private void addFragment(String URL_NEWS, String titleName) {
		title.setText(titleName);
		News_Fragment fragment_price = new News_Fragment();
		Bundle args = new Bundle();
		args.putString("URL_NEWS", URL_NEWS);
		fragment_price.setArguments(args);

		FragmentTransaction transaction_priceItem = getSupportFragmentManager().beginTransaction();
		transaction_priceItem.replace(R.id.fragment_container, fragment_price);
		// transaction_priceItem.addToBackStack(null);
		// transaction_priceItem.commit();
		transaction_priceItem.commitAllowingStateLoss();

		handler.sendEmptyMessageDelayed(MSG_WHAT.DRAWER_CLOSE, 400);
		mykeepedstate = false;
		btn_keeped.setVisibility(View.VISIBLE);
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

	/**
	 * 新功能 新界面 首次打开介绍
	 */
	private void newFunctionIntroduction() {
		sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
		int i = sharedPreferences.getInt("Tab_News_Activity", 0);
		if (i == 0) {
			showNewFunction();
		}
		i++;
		if (i == Integer.MAX_VALUE - 1) {
			i = 0;
		}
		Editor editor = sharedPreferences.edit();
		editor.putInt("Tab_News_Activity", i);
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
		Content.setText("所有新闻类别移到侧边栏\n可以点击左上角《选择》\n也可以从屏幕左侧向右滑动呼出");
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
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ExpandableListView) findViewById(R.id.left_drawer);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerList.setAdapter(new MyexpandableListAdapter(this));
		mDrawerList.setOnChildClickListener((OnChildClickListener) this);
		mDrawerList.setOnGroupClickListener((OnGroupClickListener) this);
		mDrawerLayout.setDrawerListener(new MyDrawerLayoutListener());

		for (int i = 0; i < groupList.size(); i++) {
			mDrawerList.expandGroup(i);
		}

		Button btn_selected = (Button) findViewById(R.id.btn_selected);
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

	/***
	 * 初始化 侧边栏 信息
	 */
	void InitData() {
		groupList = new ArrayList<String>();
		for (int k = 0; k < groupItems.length; k++) {
			groupList.add(groupItems[k]);

		}
		childList = new ArrayList<List<String>>();
		for (int i = 0; i < groupList.size(); i++) {
			ArrayList<String> childTemp = new ArrayList<String>();
			if (i == 0) {
				for (int j = 0; j < group0_title.length; j++) {
					childTemp.add(group0_title[j]);
				}
			} else if (i == 1) {
				for (int j = 0; j < group1_title.length; j++) {
					childTemp.add(group1_title[j]);
				}
			} else if (i == 2) {
				for (int j = 0; j < group2_title.length; j++) {
					childTemp.add(group2_title[j]);
				}
			} else if (i == 3) {
				for (int j = 0; j < group3_title.length; j++) {
					childTemp.add(group3_title[j]);
				}
			}
			childList.add(childTemp);
		}

	}

	class MyexpandableListAdapter extends BaseExpandableListAdapter {
		private LayoutInflater inflater;

		public MyexpandableListAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public int getGroupCount() {
			return groupList.size();
		}

		public int getChildrenCount(int groupPosition) {
			return childList.get(groupPosition).size();
		}

		public Object getGroup(int groupPosition) {
			return groupList.get(groupPosition);
		}

		public Object getChild(int groupPosition, int childPosition) {
			return childList.get(groupPosition).get(childPosition);
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public boolean hasStableIds() {
			return true;
		}

		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			GroupHolder groupHolder = null;
			if (convertView == null) {
				groupHolder = new GroupHolder();
				convertView = inflater.inflate(R.layout.group, null);
				groupHolder.textView = (TextView) convertView.findViewById(R.id.group);
				groupHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
				groupHolder.title_icon = (ImageView) convertView.findViewById(R.id.title_icon);
				convertView.setTag(groupHolder);
			} else {
				groupHolder = (GroupHolder) convertView.getTag();
			}

			groupHolder.textView.setText(getGroup(groupPosition).toString());
			groupHolder.title_icon.setImageResource(title_icons[groupPosition]);
			if (isExpanded) {
				groupHolder.imageView.setImageResource(R.drawable.expanded);
			} else {
				groupHolder.imageView.setImageResource(R.drawable.collapse);
			}
			return convertView;
		}

		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item, null);
			}
			TextView textView = (TextView) convertView.findViewById(R.id.item);
			textView.setText(getChild(groupPosition, childPosition).toString());
			return convertView;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	public boolean onGroupClick(final ExpandableListView parent, final View v, int groupPosition, final long id) {
		return false;
	}

	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

		if (groupPosition == 0) {
			addFragment(group0_URL[childPosition], group0_title[childPosition]);
		} else if (groupPosition == 1) {
			addFragment(group1_URL[childPosition], group1_title[childPosition]);
		} else if (groupPosition == 2) {
			addFragment(group2_URL[childPosition], group2_title[childPosition]);
		} else if (groupPosition == 3) {
			addFragment(group3_URL[childPosition], group3_title[childPosition]);
		}

		current_groupPosition = groupPosition;
		current_childPosition = childPosition;
		btn_keeped.setVisibility(View.VISIBLE);
		return false;
	}

	private int current_groupPosition = 0;
	private int current_childPosition = 0;

	// ///////////////////////////////
	// // 分类
	// ///////////////////////////////
	private String[] groupItems = { "头条", "市场", "策略", "评论" };

	// ///////////////////////////////
	// // 第0分组
	// ///////////////////////////////
	private String[] group0_URL = { Const.URL_NEWS_GDBB, Const.URL_NEWS_CJYW, Const.URL_NEWS_QQGS, Const.URL_NEWS_MTTT, Const.URL_NEWS_GGYH, Const.URL_NEWS_JJZB };
	private String[] group0_title = { "滚动播报", "财经要闻", "全球股市", "媒体头条", "各国央行", "经济指标" };

	// ///////////////////////////////
	// // 第1分组
	// ///////////////////////////////
	private String[] group1_URL = { Const.URL_NEWS_WHSC, Const.URL_NEWS_HJSC, Const.URL_NEWS_YYSC };
	private String[] group1_title = { "外汇市场", "黄金市场", "原油市场" };

	// ///////////////////////////////
	// // 第2分组
	// ///////////////////////////////
	private String[] group2_URL = { Const.URL_NEWS_LCTZ, Const.URL_NEWS_SDBD, Const.URL_NEWS_JJCL };
	private String[] group2_title = { "理财投资", "深度报道", "交易策略" };

	// ///////////////////////////////
	// // 第3分组
	// ///////////////////////////////
	private String[] group3_URL = { Const.URL_NEWS_YHPL, Const.URL_NEWS_ZJPL, Const.URL_NEWS_JGPL };
	private String[] group3_title = { "银行评论", "专家评论", "机构评论" };

	class GroupHolder {
		ImageView title_icon;
		TextView textView;
		ImageView imageView;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// super.onSaveInstanceState(outState);
		outState.putInt("current_groupPosition", current_groupPosition);
		outState.putInt("current_childPosition", current_childPosition);
		outState.putBoolean("mykeepedstate", mykeepedstate);
		outState.putBoolean("onCreateAddFragment", onCreateAddFragment);
	}

	private boolean drawerlayout_flag = false;

	class MyDrawerLayoutListener implements DrawerLayout.DrawerListener {

		public void onDrawerClosed(View arg0) {
			drawerlayout_flag = false;
			// Log.i("temp", "D onDrawerClosed->" + drawerlayout_flag);
		}

		public void onDrawerOpened(View arg0) {
			drawerlayout_flag = true;
			// Log.i("temp", "D onDrawerOpened->" + drawerlayout_flag);
		}

		public void onDrawerSlide(View arg0, float arg1) {
			// Log.i("temp", "D onDrawerSlide");
		}

		public void onDrawerStateChanged(int arg0) {
			// Log.i("temp", "D onDrawerStateChanged->" + arg0);
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
