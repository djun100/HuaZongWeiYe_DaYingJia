package com.hzwydyj.finace.activitys;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlPullParser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.ShiPanDriect;
import com.hzwydyj.finace.data.ShiPanLogin;
import com.hzwydyj.finace.data.ShiPanSeedingBean;
import com.hzwydyj.finace.image.CacheImageAsyncTask;
import com.hzwydyj.finace.present.view.ZhiBoTextView;
import com.hzwydyj.finace.utils.CommonUtil;
import com.hzwydyj.finace.utils.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * 互动
 * 
 * @author LuoYi
 * 
 */
public class ShiPanInteractionActivity extends Activity implements OnClickListener, OnItemClickListener, OnLongClickListener {

	private static final String 					TAG = "ShiPanInteractionActivity";
	private RelativeLayout 							interactionLayout;
	private Util 									seedingUtil = new Util();
	private ArrayList<ShiPanSeedingBean> 			seedingTems = new ArrayList<ShiPanSeedingBean>();
	private List<ShiPanSeedingBean> 				MessageList = new ArrayList<ShiPanSeedingBean>();
	private ListView 								interactionListView;
	private Animation 								gradualChangeAnimation, AnimationHide;
	private FrameLayout 							interactionFrame;
	private ShiPanSeedingAdapter 					seedingAdapter;
	Timer 											chatTimer = new Timer(); // 时长
	private EditText 								interactionInput;
	private Button 									interactionSend;
	private Button 									interactionRefresh;
	private String 									seedingRid;
	private String 									seedingCookieid;
	private String 									seedingroomlevel;
	private String 									interactionMessage;
	private String 									seedingUid;
	private String 									seedingClose;
	private String 									lid;
	private String 									cid;
	private int 									current_position;
	private File 									chatCache;
	private LinearLayout 							interactionTiShi;

	/**
	 * 开始
	 */
	@Override
	protected void onStart() {
		super.onStart();
		// 谷歌分析统计代码
		EasyTracker.getInstance().activityStart(this);
	}

	/**
	 * 停止
	 */
	@Override
	protected void onStop() {
		super.onStop();
		// 谷歌分析统计代码
		EasyTracker.getInstance().activityStop(this);
	}

	/**
	 * 创建页面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		init();
	}

	/**
	 * 实体控件
	 */
	public void init() {
		setContentView(R.layout.shipan_interaction);

		hzwy_BaseActivity 					= new HZWY_BaseActivity(MyApplication.CONTEXT);
		/* 动画显示and隐藏 */
		gradualChangeAnimation 				= AnimationUtils.loadAnimation(this, R.anim.ug_gradual_change_show);
		AnimationHide = AnimationUtils.loadAnimation(this, R.anim.ug_gradual_change_hide);
		/* 登录接口传过来的值 */
		Intent intent = getIntent();
		seedingRid = intent.getStringExtra("rid");
		seedingroomlevel = intent.getStringExtra("roomlevel");
		seedingCookieid = intent.getStringExtra("cookieid");
		seedingClose = intent.getStringExtra("close");
		seedingUid = intent.getStringExtra("uid");
		/* 控件初始化 */
		interactionFrame = (FrameLayout) this.findViewById(R.id.frame_layout);
		interactionLayout = (RelativeLayout) this.findViewById(R.id.interaction_layout);
		interactionLayout.setVisibility(View.VISIBLE);
		interactionLayout.setOnClickListener(this);
		interactionInput = (EditText) this.findViewById(R.id.interaction_input);
		if ("1".equals(seedingroomlevel)) {
			interactionInput.setHint(R.string.tishi_input_no_comment);
			interactionInput.setCursorVisible(false);/* 隐藏光标*/
			interactionInput.setFocusableInTouchMode(false);/* 禁止弹出软键盘*/
			interactionInput.setFilters(new InputFilter[] { new InputFilter() {/* editext不可编辑*/
				public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
					return source.length() < 1 ? dest.subSequence(dstart, dend) : "";
				}
			} });
		} else {
			interactionInput.setHint(R.string.tishi_input_comment_message);
		}
		interactionSend = (Button) this.findViewById(R.id.interaction_conversation_send);
		interactionSend.setOnClickListener(this);
		interactionRefresh = (Button) this.findViewById(R.id.refresh_searchbtn);
		interactionRefresh.setOnClickListener(this);
		interactionRefresh.setOnLongClickListener(this);
		interactionListView = (ListView) findViewById(R.id.InteractionListView);
		interactionTiShi = (LinearLayout) findViewById(R.id.interaction_tishi_layout);

		interactionListView.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Log.i("LOG", "停止");
				// 不滚动时保存当前滚动到的位置
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (MessageList != null) {
						current_position = interactionListView.getFirstVisiblePosition();
					}
				}
			}

			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				Log.i("LOG", "滑动");
			}
		});

		/*
		 * interactionListView.setOnTouchListener(new OnTouchListener() { //
		 * 对ListView注册触屏事件 public boolean onTouch(View v, MotionEvent event) {
		 * Log.i("temp", "onTouch"); if (interactionLayout.getVisibility() ==
		 * View.VISIBLE) { Log.i("temp", "GONE");
		 * interactionLayout.startAnimation(AnimationHide);
		 * interactionLayout.setVisibility(View.GONE); } return true; } });
		 */

		/* 网络请求 */
		/* 获取列表提示 */
		hzwy_BaseActivity.SingleDialog(R.string.tishi_hudong_list);
		/* 重复更新 */
		chatTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (CommonUtil.isConnectingToInternet(ShiPanInteractionActivity.this)) {
					birectSeeding();
				}
			}
		}, 0, 60 * 1000);
		/* 创建缓存目录，系统一运行就得创建缓存目录的 */
		chatCache = new File(Environment.getExternalStorageDirectory(), "cache");

		if (!chatCache.exists()) {
			chatCache.mkdirs();
		}
	}

	/**
	 * ListView监听
	 */
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Log.i("temp", "setOnItemClickListener");
		/*
		 * if (interactionLayout.getVisibility() == View.VISIBLE) {
		 * Log.i("temp", "GONE");
		 * interactionLayout.startAnimation(AnimationHide);
		 * interactionLayout.setVisibility(View.GONE); }
		 */
	}

	/**
	 * 监听button按钮
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.InteractionListView:
			break;

		case R.id.interaction_conversation_send:
			if (!"1".equals(seedingroomlevel)) {
				send();
			} else {
				hzwy_BaseActivity.pinglun(R.string.tishi_warm_prompt, R.string.tishi_jurisdiction_comment);
			}
			break;

		case R.id.interaction_layout:

			break;

		case R.id.refresh_searchbtn:
			birectSeeding();
			break;

		default:
			break;
		}
	}

	/* 长按事件 */
	public boolean onLongClick(View v) {
		/* 实现接口中的方法,当按下的是按钮时 */
		/*
		 * if(v == interactionRefresh){ if (interactionLayout.getVisibility() ==
		 * View.GONE) {
		 * interactionLayout.startAnimation(gradualChangeAnimation);
		 * interactionLayout.setVisibility(View.VISIBLE);
		 * CommonUtil.ShowSoftFast(this);
		 * interactionInput.setHint(R.string.tishi_input_comment_message); } }
		 */
		return false;
	}

	/**
	 * 发送
	 */
	private void send() {
		interactionMessage = interactionInput.getText().toString().trim();
		if (interactionMessage.length() > 0 || !"".equals(interactionMessage)) {
			if (interactionLayout.getVisibility() == View.VISIBLE) {
				// interactionLayout.startAnimation(AnimationHide);
				// interactionLayout.setVisibility(View.GONE);
				CommonUtil.hiddenSoft(this);
				if (CommonUtil.isConnectingToInternet(ShiPanInteractionActivity.this)) {
					interactionSend();
				} else {
					hzwy_BaseActivity.HZWY_Toast(R.string.network_connection_failure);
				}
				interactionInput.setText("");
				interactionInput.setHint(R.string.tishi_input_comment_message);
			}
		} else {
			hzwy_BaseActivity.HZWY_Toast(R.string.tishi_send_null_messge);
		}
	}

	/**
	 * 回复
	 */
	private void interactionReturn(int position) {
		cid = seedingTems.get(position).getCid();
		String returnNickname = seedingTems.get(position).getShowname();
		String returnName = seedingTems.get(position).getC_showname();
		String seedingName = returnNickname;

		// 显示
		if (interactionLayout.getVisibility() == View.GONE) {
			// interactionLayout.startAnimation(gradualChangeAnimation);
			// interactionLayout.setVisibility(View.VISIBLE);
			CommonUtil.ShowSoftFast(this);
			interactionInput.setHint("回复：" + seedingName + "的评论");
			interactionInput.setFocusable(true);
			interactionInput.setFocusableInTouchMode(true);
			interactionInput.requestFocus();
		} else {
			interactionInput.setHint("回复：" + seedingName + "的评论");
			interactionInput.setFocusable(true);
			interactionInput.setFocusableInTouchMode(true);
			interactionInput.requestFocus();
		}
	}

	/**
	 * 提交评论
	 */
	public void interactionSend() {
		new Thread(new Runnable() {
			/* 发送请求 */
			public void run() {
				String url = Const.HZWY_URL + 
						"type=postcomment&uid=" + seedingUid + 
						"&rid=" + seedingRid + 
						"&comment=" + interactionMessage + 
						"&replycid=" + cid + 
						"&cookieid=" + seedingCookieid;
				Log.i("LOG", "互动发送的消息：" + url);
				try {
					Message msg = sendHandler.obtainMessage();
					msg.obj = seedingUtil.getLiveComment_post(url);
					sendHandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")        
	Handler sendHandler = new Handler() {
		public void handleMessage(Message msg) {
			/* 返回结果 */
			cid = "";
			ShiPanLogin panLogin = (ShiPanLogin) msg.obj;
			String error = panLogin.getError();
			Log.i("LOG", "error-----------:" + error);
			String Message = panLogin.getMessage();
			if ("0".equals(error)) {
				hzwy_BaseActivity.HZWY_Toast(R.string.tishi_send_succeed);
			} else {
				hzwy_BaseActivity.HZWY_Toast1(Message);
			}
		};
	};

	/**
	 * 获取互动列表数据
	 */
	public void birectSeeding() {
		new Thread(new Runnable() {

			public void run() {
				String url = Const.HZWY_URL + "type=comment&rid=" + seedingRid + "&cookieid=" + seedingCookieid;
				Log.i("LOG", "互动列表返回数据：" + url);
				try {
					Message msg = handler.obtainMessage();
					msg.obj = seedingUtil.getSeeding_post(url);
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 处理数据
	 */
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			try {
				seedingTems = (ArrayList<ShiPanSeedingBean>) msg.obj;
				String userId = "";
				for (int i = 0; i < seedingTems.size(); i++) {
					userId = seedingTems.get(i).getUid();
				}
				if ("".equals(userId)) {
					interactionTiShi.setVisibility(View.VISIBLE);
					interactionListView.setVisibility(View.GONE);
					hzwy_BaseActivity.dialogCancel();
				} else {
					seedingAdapter = new ShiPanSeedingAdapter(ShiPanInteractionActivity.this, seedingTems);
					interactionListView.setAdapter(seedingAdapter);
					seedingAdapter.notifyDataSetChanged();
					interactionListView.setSelectionFromTop(current_position, 0);
					interactionListView.setSelection(seedingAdapter.getCount() - 1);
					hzwy_BaseActivity.dialogCancel();
				}
				
			} catch (Exception e) {
			}
		};
	};
	private HZWY_BaseActivity hzwy_BaseActivity;

	/**
	 * 自定义适配器
	 * 
	 * @author Administrator
	 */
	class ShiPanSeedingAdapter extends BaseAdapter {

		private Context interactionContext;
		private LayoutInflater interactionInflater;
		private List<ShiPanSeedingBean> interactionBean;
		private ShiPanDriect shiPanDriect;
		private String havereplay;
		private String msgtext;
		private String replaytext;
		private String replaydate;

		public ShiPanSeedingAdapter(Context context, List<ShiPanSeedingBean> interactionBeanList) {
			this.interactionContext = context;
			this.interactionBean = interactionBeanList;
			interactionInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return interactionBean.size();
		}

		public Object getItem(int position) {
			return interactionBean.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		@SuppressWarnings({ "unused" })
		public View getView(final int position, View convertView, ViewGroup parent) {
			/* 获取列表行数 */
			ShiPanSeedingBean shipanInteractionBean = interactionBean.get(position);
			String showname = shipanInteractionBean.getShowname();
			String dataLine = shipanInteractionBean.getDateline();
			String message = shipanInteractionBean.getMessage();
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
							if ("havereplay".equals(parser.getName())) {
								havereplay = parser.nextText();
							} else if ("msgtext".equals(parser.getName())) {
								msgtext = parser.nextText();
							} else if ("replaytext".equals(parser.getName())) {
								replaytext = parser.nextText();
							} else if ("replaydate".equals(parser.getName())) {
								replaydate = parser.nextText();
							}
							Log.i("TAG", "havereplay:" + havereplay + "\n" + "msgtext:" + msgtext + "\n" + "replaytext:" + replaytext + "\n" + "replaydate:" + replaydate);
						}
						break;
					case XmlPullParser.END_TAG:
						break;
					}
					event = parser.next();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			String wonderful = shipanInteractionBean.getIs_wonderful();
			String call = shipanInteractionBean.getIs_call();
			String avatar_small = shipanInteractionBean.getAvatar_small();
			String c_showname = shipanInteractionBean.getC_showname();

			/* 获取返回的参数 */
			lid = shipanInteractionBean.getLid();
			/* 控件存放view中 */
			View interactionView = null;
			if (interactionView == null) {
				interactionView = LayoutInflater.from(interactionContext).inflate(R.layout.shipan_interaction_item, parent, false);
			}
			LinearLayout interactionLayout = CommonUtil.get(interactionView, R.id.seeding_layout);
			LinearLayout interactionMessagehuifuLayout = CommonUtil.get(interactionView, R.id.interaction_message_huifu_layout);
			ImageView interactionTeacherImage = CommonUtil.get(interactionView, R.id.interaction_teacher_image);
			TextView interactionName = CommonUtil.get(interactionView, R.id.interaction_name_item);
			TextView interactionTime = CommonUtil.get(interactionView, R.id.interaction_time_item);
			TextView interactionMessageNameText = CommonUtil.get(interactionView, R.id.interaction_message_name_text);
			TextView interactionMessageDateText = CommonUtil.get(interactionView, R.id.interaction_message_date_text);
			TextView interactionMessageContentText = CommonUtil.get(interactionView, R.id.interaction_message_content_text);
			TextView interactionNameText = CommonUtil.get(interactionView, R.id.seeding_name_text);
			Button interactionReturn = CommonUtil.get(interactionView, R.id.interaction_return_button);
			ZhiBoTextView interactionMessageText = CommonUtil.get(interactionView, R.id.interaction_message_text);
			/* 控件显示 */
			loadImag(avatar_small, interactionTeacherImage);
			interactionName.setText(showname);
			interactionTime.setText(dataLine);
			if ("".equals(replaytext)) {
				interactionMessageText.insertGif("     " + msgtext);
				interactionLayout.setVisibility(View.GONE);
			} else {
				interactionNameText.setText(c_showname);
				interactionMessageText.insertGif("     " + msgtext);
				interactionLayout.setVisibility(View.VISIBLE);
				interactionMessagehuifuLayout.setVisibility(View.VISIBLE);
//				interactionMessageText.insertGif("回复" + c_showname + "的评论：" + msgtext);
				interactionMessageNameText.setText(c_showname);
				interactionMessageDateText.setText(replaydate);
				interactionMessageContentText.setText(replaytext);
			}

			interactionReturn.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					if (!"1".equals(seedingroomlevel)) {
						interactionReturn(position);
					} else {
						hzwy_BaseActivity.pinglun(R.string.tishi_warm_prompt, R.string.tishi_jurisdiction_huifu_comment);
					}
				}
			});
			return interactionView;
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
		super.onDestroy();
		/* 清空缓存 */
		/*
		 * File[] files = chatCache.listFiles(); for(File file :files){
		 * file.delete(); } chatCache.delete(); System.gc();
		 */
	}
}
