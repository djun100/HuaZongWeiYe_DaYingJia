package com.hzwydyj.finace.activitys;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.xmlpull.v1.XmlPullParser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.ShiPanDriect;
import com.hzwydyj.finace.data.ShiPanLogin;
import com.hzwydyj.finace.data.ShiPanSeedingBean;
import com.hzwydyj.finace.db.DBConst;
import com.hzwydyj.finace.db.DatabaseHelper;
import com.hzwydyj.finace.db.DriectDBhelper;
import com.hzwydyj.finace.image.CacheImageAsyncTask;
import com.hzwydyj.finace.present.view.DirectListener;
import com.hzwydyj.finace.present.view.PlayDirect;
import com.hzwydyj.finace.utils.CommonUtil;
import com.hzwydyj.finace.utils.MSG_WHAT;
import com.hzwydyj.finace.utils.Util;
import com.hzwydyj.finace.view.DriectListView;
import com.hzwydyj.finace.view.DriectListView.IXListViewListener;
import com.hzwydyj.finace.view.NLPullRefreshView;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.umeng.analytics.MobclickAgent;

/**
 * 直播
 * 
 * @author LuoYi
 */
public class ShiPanBirectSeedingActivity extends Activity implements OnClickListener, IXListViewListener {

	private Util 									seedingUtil = new Util();
	private ArrayList<ShiPanSeedingBean> 			seedingTems = new ArrayList<ShiPanSeedingBean>();
	private List<ShiPanSeedingBean> 				seedingList = new ArrayList<ShiPanSeedingBean>();
	private ArrayList<ShiPanSeedingBean> 			seedingBeans;
	Timer 											chatTimer = new Timer(); // 时长
	private ShiPanSeedingAdapter 					seedingAdapter;
	private DriectListView 							seedingListView;
	private String 									seedingRid;
	private String 									seedingRoomlevel;
	private String 									seedingCookieid;
	private String 									seedingUid;
	private static final String 					ACTION_NAME = "com.hzwydyj.finace.driect";
	private int 									current_position;
	private Button 									seedingRefresh;
	public LinearLayout 							seedingTiShi;
	private HZWY_BaseActivity 						hzwy_BaseActivity;
	private String 									tabName;
	PlayDirect 										playBubble;
	PlayDirect 										nowPlayBubble;
	private MyApplication 							application;

	/* 申请开户 */
	private EditText 								applyUserName;
	private EditText 								applyDianHua;
	private EditText 								applyVerification;
	private Button 									applyVerificationButton;
	private Button 									applyImmediately;
	private AlertDialog 							dialog;
	private MyCount 								mc;
	private String 									dianHua;
	private String 									userName;
	private String 									verification;

	/* 保存本地数据库 */
	private Dao<ShiPanSeedingBean, Integer> 		seedingDao;
	private DatabaseHelper 							dataHelper = null;

	@SuppressWarnings("unused")
	private DatabaseHelper getHelper() {
		if (dataHelper == null) {
			dataHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		}
		return dataHelper;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		init();
	}

	public void init() {
		setContentView(R.layout.shipan_direct_seeding);
		new DriectDBhelper(MyApplication.CONTEXT);

		hzwy_BaseActivity 		= new HZWY_BaseActivity(MyApplication.CONTEXT);

		Intent intent 			= getIntent();
		seedingRid 				= intent.getStringExtra("rid");
		seedingCookieid 		= intent.getStringExtra("cookieid");
		seedingRoomlevel 		= intent.getStringExtra("roomlevel");
		seedingUid 				= intent.getStringExtra("uid");

		tabName = "";
		if ("1".equals(seedingRid)) {
			tabName = DBConst.DB_TABLE_DRIECT1;
		} else if ("2".equals(seedingRid)) {
			tabName = DBConst.DB_TABLE_DRIECT2;
		} else if ("3".equals(seedingRid)) {
			tabName = DBConst.DB_TABLE_DRIECT3;
		} else if ("4".equals(seedingRid)) {
			tabName = DBConst.DB_TABLE_DRIECT4;
		}

		seedingTiShi 			= (LinearLayout) this.findViewById(R.id.seeding_tishi_driect_layout);
		seedingRefresh 			= (Button) findViewById(R.id.refresh_searchbtn);
		seedingRefresh.setOnClickListener(this);
		seedingListView 		= (DriectListView) findViewById(R.id.BirectSeedingListView);
		seedingListView.setXListViewListener(this);
		seedingListView.setPullLoadEnable(true);

		hzwy_BaseActivity.SingleDialog(R.string.tishi_zhibo_list);
		chatTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (CommonUtil.isConnectingToInternet(ShiPanBirectSeedingActivity.this)) {
					SharedPreferences preferences 	= getApplication().getSharedPreferences("zhibo", 0);
					Boolean user_first 				= preferences.getBoolean("FIRST", true);
					if (user_first) {
						preferences.edit().putBoolean("FIRST", false).commit();
						birectSeeding("one");
						Log.i("LOG", "true");
					} else {
						birectSeeding("repetition");
						Log.i("LOG", "false");
					}
				}
			}
		}, 0, 8000);
		mc = new MyCount(60000, 1000);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.refresh_searchbtn:
			if (CommonUtil.isConnectingToInternet(ShiPanBirectSeedingActivity.this)) {
				hzwy_BaseActivity.SingleDialog(R.string.tishi_zhibo_list);
				birectSeeding("refresh");
			} else {
				hzwy_BaseActivity.HZWY_Toast(R.string.network_connection_failure);
			}
			break;

		default:
			break;
		}
	}

	public void birectSeeding(String driectNumber) {

		if ("one".equals(driectNumber)) {
			new Thread(new Runnable() {

				public void run() {

					String url = Const.HZWY_URL + "type=live&rid=" + seedingRid + "&cookieid=" + seedingCookieid;
					Log.i("temp", "第一次进入直播室：URl-->>" + url);
					try {
						List<ShiPanSeedingBean> zhiBo_post = seedingUtil.getZhiBo_post(url);
						Log.i("temp", "第一次进入直播室 + one + zhiBo_post:>>>" + zhiBo_post);
						if (zhiBo_post.size() > 0) {
							application 		= (MyApplication) getApplication();
							for (int i = zhiBo_post.size() - 1; i < zhiBo_post.size(); i++) {
								String recordid = zhiBo_post.get(i).getRecordid();
								application.setLastId(recordid);
							}
							String recordid2 	= zhiBo_post.get(0).getRecordid();
							application.setFirstId(recordid2);
							
							DriectDBhelper db 		= new DriectDBhelper(MyApplication.CONTEXT);
							List<ShiPanSeedingBean> spbean = db.queryAllData(tabName);
							for (ShiPanSeedingBean shiPanBean : zhiBo_post) {
								if (spbean.size() == 0) {
									db.insert(shiPanBean, tabName);
								}
							}
							Message msg = handler.obtainMessage();
							msg.what = MSG_WHAT.SEEDING_ONE;
							handler.sendMessage(msg);
						} else {
							hzwy_BaseActivity.dialogCancel();
							seedingTiShi.setVisibility(View.VISIBLE);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		} else if ("refresh".equals(driectNumber)) {
			new Thread(new Runnable() {

				private String recordid;

				public void run() {
					application = (MyApplication) getApplication();
					String url = Const.HZWY_URL + "type=live&rid=" + seedingRid + "&lastid=" + application.getLastId() + "&cookieid=" + seedingCookieid;
					Log.i("temp", "手动刷新直播室：URl-->>" + url);
					try {
						List<ShiPanSeedingBean> zhiBo_post = seedingUtil.getZhiBo_post(url);
						Log.i("temp", "手动刷新直播室 + refresh + zhiBo_post:>>>" + zhiBo_post);
						DriectDBhelper db = new DriectDBhelper(MyApplication.CONTEXT);
						for (ShiPanSeedingBean shiPanBean : zhiBo_post) {
							db.insert(shiPanBean, tabName);
						}
						if (zhiBo_post.size() > 0) {
							for (int i = zhiBo_post.size() - 1; i < zhiBo_post.size(); i++) {
								recordid = zhiBo_post.get(i).getRecordid();
								application.setLastId(recordid);
							}
							Message msg = handler.obtainMessage();
							msg.what = MSG_WHAT.SEEDING_REFRESH;
							handler.sendMessage(msg);
						} else {
							hzwy_BaseActivity.dialogCancel();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		} else if ("history".equals(driectNumber)) {
			new Thread(new Runnable() {

				private String firstId;
				private String recordid2;

				public void run() {
					application = (MyApplication) getApplication();
					String url = Const.HZWY_URL + "type=live&rid=" + seedingRid + "&firstid=" + application.getFirstId() + "&cookieid=" + seedingCookieid;
					Log.i("temp", "手动加载直播室历史：URl-->>" + url);
					try {
						List<ShiPanSeedingBean> zhiBo_post = seedingUtil.getZhiBo_post(url);
						Log.i("temp", "手动加载直播室历史 + history + zhiBo_post:>>>" + zhiBo_post);
						if (zhiBo_post.size() > 0) {
							recordid2 		= zhiBo_post.get(0).getRecordid();
							application.setFirstId(recordid2);
						}
						DriectDBhelper db 	= new DriectDBhelper(MyApplication.CONTEXT);
						for (ShiPanSeedingBean shiPanBean : zhiBo_post) {
							db.insert(shiPanBean, tabName);
						}
						Message msg 		= handler.obtainMessage();
						msg.what 			= MSG_WHAT.SEEDING_HISTORY;
						handler.sendMessage(msg);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		} else if ("repetition".equals(driectNumber)) {
			new Thread(new Runnable() {

				private String recordid;

				public void run() {
					application 	= (MyApplication) getApplication();
					String url	 	= Const.HZWY_URL + "type=live&rid=" + seedingRid + "&lastid=" + application.getLastId() + "&cookieid=" + seedingCookieid;
					Log.i("temp", "轮询刷新直播室：URl-->>" + url);
					try {
						List<ShiPanSeedingBean> zhiBo_post = seedingUtil.getZhiBo_post(url);
						Log.i("temp", "轮询刷新直播室 + repetition + zhiBo_post:>>>" + zhiBo_post);
						DriectDBhelper db = new DriectDBhelper(MyApplication.CONTEXT);
						if (zhiBo_post.size() > 0) {
							for (int i = zhiBo_post.size() - 1; i < zhiBo_post.size(); i++) {
								recordid = zhiBo_post.get(i).getRecordid();
								application.setLastId(recordid);
							}
							for (ShiPanSeedingBean shiPanBean : zhiBo_post) {
								db.insert(shiPanBean, tabName);
							}
							Message msg 	= handler.obtainMessage();
							msg.what 		= MSG_WHAT.SEEDING_REPETITION;
							handler.sendMessage(msg);
						} 
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_WHAT.SEEDING_ONE:
				DriectDBhelper db = new DriectDBhelper(MyApplication.CONTEXT);
				List<ShiPanSeedingBean> listData = db.queryAllData(tabName);
				Log.i("LOG", "listData-->>" + listData);
				seedingAdapter = new ShiPanSeedingAdapter(MyApplication.CONTEXT, listData);
				seedingListView.setAdapter(seedingAdapter);
				seedingListView.setSelection(seedingAdapter.getCount() - 1);
				seedingAdapter.notifyDataSetChanged();
				hzwy_BaseActivity.dialogCancel();
				break;

			case MSG_WHAT.SEEDING_REFRESH:
				DriectDBhelper db1 = new DriectDBhelper(MyApplication.CONTEXT);
				List<ShiPanSeedingBean> listData1 = db1.queryAllData(tabName);
				Log.i("LOG", "listData-->>" + listData1);
				seedingAdapter = new ShiPanSeedingAdapter(MyApplication.CONTEXT, listData1);
				seedingAdapter.notifyDataSetChanged();
				seedingListView.setAdapter(seedingAdapter);
				seedingListView.setSelection(seedingAdapter.getCount() - 1);
				hzwy_BaseActivity.dialogCancel();
				break;

			case MSG_WHAT.SEEDING_HISTORY:
				DriectDBhelper db2 = new DriectDBhelper(MyApplication.CONTEXT);
				List<ShiPanSeedingBean> listData2 = db2.queryAllData(tabName);
				seedingAdapter = new ShiPanSeedingAdapter(MyApplication.CONTEXT, listData2);
				seedingListView.setAdapter(seedingAdapter);
				seedingAdapter.notifyDataSetChanged();
				break;

			case MSG_WHAT.SEEDING_REPETITION:
				DriectDBhelper db3 = new DriectDBhelper(MyApplication.CONTEXT);
				List<ShiPanSeedingBean> listData3 = db3.queryAllData(tabName);
				seedingAdapter = new ShiPanSeedingAdapter(MyApplication.CONTEXT, listData3);
				seedingListView.setAdapter(seedingAdapter);
				/*seedingListView.setOnScrollListener(new OnScrollListener() {

					public void onScrollStateChanged(AbsListView view, int scrollState) {
						 不滚动时保存当前滚动到的位置 
						if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
							if (seedingList != null) {
								current_position = seedingListView.getFirstVisiblePosition();
							}
						}
					}

					public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
					}
				});*/
				seedingAdapter.notifyDataSetChanged();
				seedingListView.setSelection(seedingAdapter.getCount() - 1);
				try {
					hzwy_BaseActivity.dialogCancel();
				} catch (Exception e) {
				}
				break;
			default:
				break;
			}
		};
	};

	public void onRefresh() {// 加载
		seedingHandler.postDelayed(new Runnable() {
			public void run() {
				if (CommonUtil.isConnectingToInternet(ShiPanBirectSeedingActivity.this)) {
					birectSeeding("history");
					onLoad();
					geneItems();
				} else {
					hzwy_BaseActivity.HZWY_Toast(R.string.network_connection_failure);
				}
			}
		}, 1000);
	}

	public void onLoadMore() {
	}

	private void onLoad() {
		seedingListView.stopRefresh();
		seedingListView.setRefreshTime("刚刚");
	}

	private void geneItems() {
		seedingAdapter.notifyDataSetChanged();
		DriectDBhelper db = new DriectDBhelper(MyApplication.CONTEXT);
		List<ShiPanSeedingBean> listData = db.queryAllData(tabName);
		seedingAdapter = new ShiPanSeedingAdapter(ShiPanBirectSeedingActivity.this, listData);
		seedingListView.setAdapter(seedingAdapter);
	}

	class ShiPanSeedingAdapter extends BaseAdapter {

		private Context seedingContext;
		private LayoutInflater seedingInflater;
		private List<ShiPanSeedingBean> seedingBean;
		private ShiPanDriect shiPanDriect;
		private String haveimg;
		private String havemp3;
		private String havereplay;
		private String havelink;
		private String msgtext;
		private String replaytext;
		private String replaydate;
		private String imgurl;
		private String imgthumburl;
		private String mp3url;
		private String link;

		public ShiPanSeedingAdapter(Context context, List<ShiPanSeedingBean> seedingBeanList) {
			this.seedingContext = context;
			this.seedingBean = seedingBeanList;
			this.seedingInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return seedingBean.size();
		}

		public Object getItem(int position) {
			return seedingBean.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			ShiPanSeedingBean shipanSeedingBean 	= seedingBean.get(position);
			String dataLine 						= shipanSeedingBean.getDateline();
			String is_system 						= shipanSeedingBean.getIs_system();
			String avatar_small 					= shipanSeedingBean.getAvatar_small();
			String authority 						= shipanSeedingBean.getAuthority();
			String message 							= shipanSeedingBean.getMessage();
			try {
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
				stringBuffer.append("<note>");
				stringBuffer.append(message);
				stringBuffer.append("</note>");
				String driectMessage = stringBuffer.toString();
				InputStream inputStream = new ByteArrayInputStream(driectMessage.getBytes());
				XmlPullParser parser = Xml.newPullParser();
				parser.setInput(inputStream, "UTF-8");
				int event = parser.getEventType();
				while (event != XmlPullParser.END_DOCUMENT) {
					switch (event) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						if ("note".equals(parser.getName())) {
							shiPanDriect = new ShiPanDriect();
						}
						if (shiPanDriect != null) {
							if ("haveimg".equals(parser.getName())) {
								haveimg = parser.nextText();
							} else if ("havemp3".equals(parser.getName())) {
								havemp3 = parser.nextText();
							} else if ("havereplay".equals(parser.getName())) {
								havereplay = parser.nextText();
							} else if ("havelink".equals(parser.getName())) {
								havelink = parser.nextText();
							} else if ("msgtext".equals(parser.getName())) {
								msgtext = parser.nextText();
							} else if ("replaytext".equals(parser.getName())) {
								replaytext = parser.nextText();
							} else if ("replaydate".equals(parser.getName())) {
								replaydate = parser.nextText();
							} else if ("imgurl".equals(parser.getName())) {
								imgurl = parser.nextText();
							} else if ("imgthumburl".equals(parser.getName())) {
								imgthumburl = parser.nextText();
							} else if ("mp3url".equals(parser.getName())) {
								mp3url = parser.nextText();
							} else if ("link".equals(parser.getName())) {
								link = parser.nextText();
							}
						}
						break;
					case XmlPullParser.END_TAG:
						Log.i("TAG", "直播列表：" + "haveimg:" + haveimg + "\n" + "havemp3:" + havemp3 + "\n" + "havereplay:" + havereplay + "\n" + "havelink:" + havelink + "\n" + "msgtext:" + msgtext
								+ "\n" + "replaytext:" + replaytext + "\n" + "replaydate:" + replaydate + "\n" + "imgurl:" + imgurl + "\n" + "imgthumburl:" + imgthumburl + "\n" + "mp3url:" + mp3url
								+ "\n" + "link:" + link);
						break;
					}
					event = parser.next();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			String wonderful 								= shipanSeedingBean.getIs_wonderful();
			String call 									= shipanSeedingBean.getIs_call();
			String showname 								= shipanSeedingBean.getShowname();
			String c_showname 								= shipanSeedingBean.getC_showname();

			View seedingView = null;
			if (seedingView == null) {
				seedingView 								= LayoutInflater.from(seedingContext).inflate(R.layout.shipan_direct_seeding_item, parent, false);
			}

			FrameLayout seedingTeacherLayout 				= CommonUtil.get(seedingView, R.id.interaction_teacher_layout);
			LinearLayout systemLayout 						= CommonUtil.get(seedingView, R.id.system_layout);
			LinearLayout layout 							= CommonUtil.get(seedingView, R.id.layout);
			LinearLayout seedingLayout 						= CommonUtil.get(seedingView, R.id.seeding_layout);
			LinearLayout seedingMessageLayout 				= CommonUtil.get(seedingView, R.id.seeding_message_layout);
			LinearLayout seedingMessageHuifuLayout 			= CommonUtil.get(seedingView, R.id.seeding_message_huifu_layout);
			LinearLayout seedingMessageMp3 					= CommonUtil.get(seedingView, R.id.seeding_message_mp3_layout);
			LinearLayout seedingMessageFinish 				= CommonUtil.get(seedingView, R.id.seeding_message_finish_layout);
			TextView seedingName 							= CommonUtil.get(seedingView, R.id.seeding_name_item);
			TextView seedingTime 							= CommonUtil.get(seedingView, R.id.seeding_time_item);
			TextView seedingMessageText 					= CommonUtil.get(seedingView, R.id.seeding_message_text);
			TextView seedingMessageNameText 				= CommonUtil.get(seedingView, R.id.seeding_message_name_text);
			TextView seedingMessageDateText 				= CommonUtil.get(seedingView, R.id.seeding_message_date_text);
			TextView seedingMessageContentText 				= CommonUtil.get(seedingView, R.id.seeding_message_content_text);
			// TextView seedingMessageFinishText 			= CommonUtil.get(seedingView, R.id.seeding_message_finish_text);
			TextView seedingSystemMessageText 				= CommonUtil.get(seedingView, R.id.system_message);
			TextView seedingNameText 						= CommonUtil.get(seedingView, R.id.seeding_name_text);
			ImageView seedingEssenceImage 					= CommonUtil.get(seedingView, R.id.seeding_essence_image);
			ImageView seedingIscallImage 					= CommonUtil.get(seedingView, R.id.seeding_iscall_image);
			ImageView seedingMessageImage 					= CommonUtil.get(seedingView, R.id.seeding_message_image);
			ImageView seedingMessageTeacherImage 			= CommonUtil.get(seedingView, R.id.seeding_message_teacher_image);
			ImageView seedingMessageImageAmplification 		= CommonUtil.get(seedingView, R.id.seeding_meesage_image_amplification);
			Button seedingMessageAuthorityButton 			= CommonUtil.get(seedingView, R.id.seeding_message_authority_button);
			Button seedingMessageButton 					= CommonUtil.get(seedingView, R.id.seeding_message_button);
			if (!"".equals(imgurl)) {
				seedingTeacherLayout.setOnClickListener(new OnClickListener() {
	
					String imageUrl = imgurl;
	
					public void onClick(View v) {
						hzwy_BaseActivity.showPicture(imageUrl);
					}
				});
			}
			
			seedingMessageAuthorityButton.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					hzwy_BaseActivity.permissionHint(R.string.tishi_authority_message);
				}
			});

			seedingMessageButton.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					applyAccountDialog();
				}
			});
			/* 控件显示 */
			seedingName.setText(showname);// 姓名
			seedingTime.setText(dataLine);// 时间
			if ("1".equals(seedingRoomlevel) || "0".equals(seedingRoomlevel)) {
				if (!"1".equals(authority)) {
					if ("1".equals(call)) {
						seedingMessageLayout.setVisibility(View.GONE);
						seedingMessageAuthorityButton.setVisibility(View.GONE);
						seedingMessageButton.setVisibility(View.VISIBLE);
					} else if ("0".equals(call)) {
						seedingMessageLayout.setVisibility(View.GONE);
						seedingMessageAuthorityButton.setVisibility(View.GONE);
						seedingMessageButton.setVisibility(View.VISIBLE);
					}
				} else {
					seedingMessageLayout.setVisibility(View.VISIBLE);
					seedingMessageAuthorityButton.setVisibility(View.GONE);
					seedingMessageButton.setVisibility(View.GONE);
				}
			} else {
				seedingMessageLayout.setVisibility(View.VISIBLE);
				seedingMessageAuthorityButton.setVisibility(View.GONE);
				seedingMessageButton.setVisibility(View.GONE);
			}

			if ("".equals(c_showname) || "null".equals(c_showname)) {
				seedingMessageText.setText("     " + msgtext);// 如果没有回复信息就直接显示内容
				seedingLayout.setVisibility(View.GONE);
			} else {
				seedingNameText.setText(c_showname);
				seedingMessageText.setText("     " + msgtext);
				seedingLayout.setVisibility(View.VISIBLE);
				// seedingMessageText.setText("回复" + c_showname + "的评论：" + msgtext);// 用户回复内容
			}

			if (!"".equals(replaytext)) {
				seedingMessageHuifuLayout.setVisibility(View.VISIBLE);
				seedingMessageNameText.setText(c_showname);// 回复人名称
				seedingMessageDateText.setText(replaydate);// 回复人时间
				seedingMessageContentText.setText(replaytext);// 回复人内容
			}

			loadImag(avatar_small, seedingMessageTeacherImage);// 老师头像
			if (!"".equals(imgthumburl)) {
				seedingTeacherLayout.setVisibility(View.VISIBLE);
				if (!"".equals(imgurl)) {
					seedingMessageImageAmplification.setVisibility(View.VISIBLE);
				} else {
					seedingMessageImageAmplification.setVisibility(View.GONE);
				}
				loadImag(imgthumburl, seedingMessageImage);// message图片
			}
			if ("1".equals(wonderful)) {// 精华
				seedingEssenceImage.setVisibility(View.VISIBLE);
				layout.setBackgroundResource(R.color.layout_beijing);
			}
			if ("1".equals(call)) {// 喊单
				seedingIscallImage.setVisibility(View.VISIBLE);
				layout.setBackgroundResource(R.color.layout_beijing);
			}
			if ("1".equals(is_system)) {// 系统提示
				systemLayout.setVisibility(View.GONE);
				seedingMessageFinish.setVisibility(View.VISIBLE);
				seedingSystemMessageText.setText(msgtext);
			}

			playBubble = new PlayDirect(ShiPanBirectSeedingActivity.this);
			nowPlayBubble = new PlayDirect(ShiPanBirectSeedingActivity.this);
			if ("".equals(mp3url) || mp3url == null) {
			} else {
				seedingMessageMp3.setVisibility(View.VISIBLE);
				seedingMessageMp3.addView(playBubble);
				Log.i("ShiPan", "mp3url:" + mp3url);
				playBubble.setAudioUrl(mp3url);// MP3
			}
			playBubble.setBubbleListener(new DirectListener() {

				public void playFail(PlayDirect playBubble) {
					Log.e("", "playFail id=" + playBubble.getId() + "  playFail url=" + playBubble.getUrl());
				}

				public void playStoped(PlayDirect playBubble) {
				}

				public void playStart(PlayDirect playBubble) {
					// 判断之前是否有正在播放的，有则暂停
					if (nowPlayBubble != null && nowPlayBubble.getId() != playBubble.getId()) {
						nowPlayBubble.stopPlay();
					}
					nowPlayBubble = playBubble;
				}

				public void playCompletion(PlayDirect playBubble) {
				}

			});
			return seedingView;
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		/**
		 * 设置图片路径
		 * 
		 * @param path
		 * @param imag
		 * @return
		 */
		public Bitmap loadImag(String path, ImageView imag) {
			new CacheImageAsyncTask(imag, MyApplication.CONTEXT).execute(path);
			return null;
		}
	}

	/************************************************ 申请开户*******开始 **********************************************/
	private void applyAccountDialog() {
		LayoutInflater factory 					= LayoutInflater.from(this);
		final View textEntryView 				= factory.inflate(R.layout.shipan_apply_account_dialog, null);
		Button applyImageButton 				= (Button) textEntryView.findViewById(R.id.apply_image_button);
		applyUserName 							= (EditText) textEntryView.findViewById(R.id.edit_apply_user_name);
		applyDianHua 							= (EditText) textEntryView.findViewById(R.id.edit_apply_dianhua);
		applyVerification 						= (EditText) textEntryView.findViewById(R.id.edit_apply_verification_code);
		applyVerificationButton 				= (Button) textEntryView.findViewById(R.id.button_apply_verification_code);
		applyImmediately 						= (Button) textEntryView.findViewById(R.id.button_apply_immediately_account);
		dialog 									= new AlertDialog.Builder(this).setView(textEntryView).create();
		dialog.show();
		applyImageButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mc.onFinish();
				dialog.cancel();
			}
		});

		applyVerificationButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dianHua = applyDianHua.getText().toString().trim();
				if (!"".equals(dianHua)) {
					if (dianHua.length() == 11) {
						if (CommonUtil.isMobileNum(dianHua)) {
							if (CommonUtil.isConnectingToInternet(ShiPanBirectSeedingActivity.this)) {
								new Thread() {
									public void run() {
										verificationCode();
									};
								}.start();
							} else {
								hzwy_BaseActivity.HZWY_Toast(R.string.network_connection_failure);
							}
						} else {
							hzwy_BaseActivity.HZWY_Toast(R.string.judge_phone_number_format);
						}
					} else {
						hzwy_BaseActivity.HZWY_Toast(R.string.judge_phone_number_format);
					}
				} else {
					hzwy_BaseActivity.HZWY_Toast(R.string.judge_phone_number_null);
				}
			}
		});

		applyImmediately.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dianHua 			= applyDianHua.getText().toString().trim();
				userName 			= applyUserName.getText().toString().trim();
				verification 		= applyVerification.getText().toString().trim();
				if (!"".equals(dianHua)) {
					if (dianHua.length() == 11) {
						if (!"".equals(userName)) {
							if (verification.length() == 4) {
								if (CommonUtil.isConnectingToInternet(ShiPanBirectSeedingActivity.this)) {
									applyAccount();
									mc.onFinish();
								} else {
									hzwy_BaseActivity.HZWY_Toast(R.string.network_connection_failure);
								}
							} else {
								hzwy_BaseActivity.HZWY_Toast(R.string.judge_code_input_number);
							}
						} else {
							hzwy_BaseActivity.HZWY_Toast(R.string.judge_user_name);
						}
					} else {
						hzwy_BaseActivity.HZWY_Toast(R.string.judge_phone_number_format);
					}
				} else {
					hzwy_BaseActivity.HZWY_Toast(R.string.judge_please_input_phone);
				}
			}
		});
	}

	private void applyAccount() {
		new Thread(new Runnable() {

			public void run() {
				String url = Const.HZWY_URL + "type=kaihu&";
				try {
					Message msg = seedingHandler.obtainMessage();
					msg.obj = seedingUtil.getApplyAccount_post(url, dianHua, verification, userName, seedingUid, seedingCookieid);
					seedingHandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	Handler seedingHandler = new Handler() {
		public void handleMessage(Message msg) {
			ShiPanLogin shipanLogin = (ShiPanLogin) msg.obj;
			String Error = shipanLogin.getError();
			String Message = shipanLogin.getMessage();
			if ("0".equals(Error)) {
				dialog.dismiss();
				hzwy_BaseActivity.HZWY_Toast1(Message);
			} else {
				hzwy_BaseActivity.HZWY_Toast1(Message);
				applyUserName.setText("");
				applyDianHua.setText("");
				applyVerification.setText("");
			}
		};
	};

	/************************************************ 申请开户*******结束 ************************************************/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ShiPanBirectSeedingActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	// /////////////////////////////////////////////获取验证码//////开始///////////////////////////////////////////////////
	/**
	 * 获取验证码
	 */
	public void verificationCode() {
		new Thread(new Runnable() {

			public void run() {
				String url = Const.HZWY_URL + "type=sendmobilecode&";
				try {
					Message msg = verificationHandler.obtainMessage();
					msg.obj = seedingUtil.getVerificationCodePost(url, dianHua);
					verificationHandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	Handler verificationHandler = new Handler() {
		public void handleMessage(Message msg) {
			ShiPanLogin panLogin = (ShiPanLogin) msg.obj;
			String Error = panLogin.getError();
			String Message = panLogin.getMessage();
			if ("0".equals(Error)) {
				mc.start();
			} else {
				hzwy_BaseActivity.HZWY_Toast1(Message);
			}
		};
	};

	/**
	 * 获取验证码倒计时
	 * 
	 * @author Administrator
	 */
	class MyCount extends CountDownTimer {

		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		/* 倒计时停止 */
		@Override
		public void onFinish() {
			applyVerificationButton.setText(R.string.regiter_gain_verification_code);
			applyVerificationButton.setBackgroundResource(R.color.login_button);
			applyVerificationButton.setEnabled(true);/* 启动button */
			mc.cancel();
			applyDianHua.setCursorVisible(true);/* 显示光标 */
			applyDianHua.setFocusableInTouchMode(true);/* 弹出软键盘 */
			applyDianHua.setFilters(new InputFilter[] { new InputFilter() {/* editext可编辑 */
				public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
					return null;
				}
			} });
		}

		/* 倒计时开始 */
		@Override
		public void onTick(long millisUntilFinished) {
			applyVerificationButton.setText(millisUntilFinished / 1000 + "秒后重新获取");
			applyVerificationButton.setBackgroundResource(R.drawable.hzwy_verification_code);
			@SuppressWarnings("unused")
			long a = millisUntilFinished / 1000;
			applyVerificationButton.setEnabled(false);/* 禁止button */
			applyVerificationButton.setTextSize(12);

			applyDianHua.setCursorVisible(false);/* 隐藏光标 */
			applyDianHua.setFocusableInTouchMode(false);/* 禁止弹出软键盘 */
			applyDianHua.setFilters(new InputFilter[] { new InputFilter() {/* editext不可编辑 */
				public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
					return source.length() < 1 ? dest.subSequence(dstart, dend) : "";
				}
			} });
		}
	}

	// ///////////////////////////////////////////////获取验证码/////////结束/////////////////////////////////////////////

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.gc();

		SharedPreferences preferences = getApplication().getSharedPreferences("zhibo", 0);
		preferences.edit().clear().commit();

		chatTimer.cancel();

		if (dataHelper != null) {
			OpenHelperManager.releaseHelper();
			dataHelper = null;
		}
		// 退出暂停播放
		if (nowPlayBubble != null) {
			nowPlayBubble.stopPlay();
		}

		DriectDBhelper driectDBhelper = new DriectDBhelper(MyApplication.CONTEXT);
		driectDBhelper.deleteAllData(tabName);
	}
}
