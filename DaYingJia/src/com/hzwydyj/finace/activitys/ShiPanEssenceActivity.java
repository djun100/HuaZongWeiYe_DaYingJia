package com.hzwydyj.finace.activitys;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.activitys.ShiPanShoutSingleActivity.MyCount;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.ShiPanDriect;
import com.hzwydyj.finace.data.ShiPanLogin;
import com.hzwydyj.finace.data.ShiPanSeedingBean;
import com.hzwydyj.finace.image.CacheImageAsyncTask;
import com.hzwydyj.finace.present.view.DirectListener;
import com.hzwydyj.finace.present.view.PlayDirect;
import com.hzwydyj.finace.service.SysApplication;
import com.hzwydyj.finace.utils.CommonUtil;
import com.hzwydyj.finace.utils.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * 精华
 * 
 * @author LuoYi
 * 
 */
public class ShiPanEssenceActivity extends Activity implements OnClickListener{

	private Util 								essenceUtil = new Util();
	private ArrayList<ShiPanSeedingBean> 		essenceTems = new ArrayList<ShiPanSeedingBean>();
	private String 								essenceRid;
	private String 								essenceCookieid;
	private String 								essenceRoomlevel;
	private String 								essenceUid;
	private ShiPanEssenceAdapter 				essenceAdapter;
	private ListView 							essenceListView;
	private static final String 				mimeType = "text/html";
	private static final String 				encoding = "utf-8";
	private Button 								essenceRefresh;
	/* 申请开户*/
	private EditText 							applyUserName;
	private EditText 							applyDianHua;
	private EditText 							applyVerification;
	private Button 								applyVerificationButton;
	private Button 								applyImmediately;
	private AlertDialog 						dialog;
	private MyCount 							mc;
	private String 								dianHua;
	private String 								userName;
	private String 								verification;
	private LinearLayout 						singleTiShi;
	

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
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		init();
	}

	public void init() {
		setContentView(R.layout.shipan_essence);
		
		hzwy_BaseActivity = new HZWY_BaseActivity(MyApplication.CONTEXT);
		
		hzwy_BaseActivity.SingleDialog(R.string.tishi_jinghua_list);
		
		Intent intent 		= getIntent();
		essenceRid 			= intent.getStringExtra("rid");
		essenceCookieid 	= intent.getStringExtra("cookieid");
		essenceRoomlevel 	= intent.getStringExtra("roomlevel");
		essenceUid 			= intent.getStringExtra("uid");
		/* 控件初始化*/
		singleTiShi 		= (LinearLayout)findViewById(R.id.tishi_layout);
		essenceRefresh 		= (Button) findViewById(R.id.refresh_searchbtn);
		essenceRefresh.setOnClickListener(this);
		essenceListView 	= (ListView) findViewById(R.id.BirectEssenceListView);
		if (essenceListView.getCount() >= 3) {
			essenceListView.setStackFromBottom(true);
		}
		
		if (CommonUtil.isConnectingToInternet(ShiPanEssenceActivity.this)) {
			birectSeeding();
		} else {
			hzwy_BaseActivity.dialogCancel();
			hzwy_BaseActivity.HZWY_Toast(R.string.network_connection_failure);
		}
		mc = new MyCount(60000, 1000);
	}

	/* 控件响应事件*/
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.refresh_searchbtn:
			if (essenceTems != null) {
				essenceTems.clear();
				birectSeeding();
			}
			break;

		default:
			break;
		}
	}
	
	/* 请求服务器*/
	public void birectSeeding() {
		new Thread(new Runnable() {

			public void run() {
				String url = Const.HZWY_URL + "type=live&rid=" + essenceRid + "&is_wonderful=1" + "&cookieid=" + essenceCookieid;
				try {
					Message msg 	= handler.obtainMessage();
					msg.obj 		= essenceUtil.getZhiBo_post(url);
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/* 返回数据列表并显示*/
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			ArrayList<ShiPanSeedingBean> temp = (ArrayList<ShiPanSeedingBean>) msg.obj;
			int index = 0;
			for (int i = 0; i < temp.size(); i++) {
				if ("1".equals(temp.get(i).getIs_wonderful())) {
					essenceTems.add(temp.get(i));
				} else {
					index ++;
				}
			}
			if (index == temp.size()) {
				singleTiShi.setVisibility(View.VISIBLE);
				essenceListView.setVisibility(View.GONE);
			} else {
				essenceAdapter = new ShiPanEssenceAdapter(ShiPanEssenceActivity.this, essenceTems);
				essenceListView.setAdapter(essenceAdapter);
				essenceAdapter.notifyDataSetChanged();
			}
			hzwy_BaseActivity.dialogCancel();
		};
	};

	/**
	 * 显示控件
	 * @author Administrator
	 */
	class ShiPanEssenceAdapter extends BaseAdapter {

		private Context 					essenceContext;
		private LayoutInflater 				essenceInflater;
		private List<ShiPanSeedingBean> 	essenceBean;
		private ShiPanDriect 				shiPanDriect;
		private String 						link;
		private String 						mp3url;
		private String 						imgthumburl;
		private String 						imgurl;
		private String 						replaydate;
		private String 						replaytext;
		private String 						msgtext;
		private String 						havelink;
		private String 						havereplay;
		private String 						havemp3;
		private String 						haveimg;
		private PlayDirect 					nowPlayBubble;
		private PlayDirect 					playBubble;
		
		public ShiPanEssenceAdapter(Context context, List<ShiPanSeedingBean> essenceBeanList) {
			this.essenceContext 		= context;
			this.essenceBean			= essenceBeanList;
			essenceInflater 			= LayoutInflater.from(context);
		}

		public int getCount() {
			return essenceBean.size();
		}

		public Object getItem(int position) {
			return essenceBean.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			/* 获取列表行数 */
			ShiPanSeedingBean shipanEssenceBean = essenceBean.get(position);
			String dataLine 					= shipanEssenceBean.getDateline();
			String message 						= shipanEssenceBean.getMessage();
			try {
				StringBuffer stringBuffer 		= new StringBuffer();
				stringBuffer.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
				stringBuffer.append("<note>");
				stringBuffer.append(message);
				stringBuffer.append("</note>");
				String driectMessage 			= stringBuffer.toString();
				InputStream inputStream 		= new ByteArrayInputStream(driectMessage.getBytes());
				XmlPullParser parser 			= Xml.newPullParser();
				parser.setInput(inputStream, "UTF-8");
				int event 						= parser.getEventType();
				while (event != XmlPullParser.END_DOCUMENT) {
					switch (event) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						if ("note".equals(parser.getName())) {
							shiPanDriect 		= new ShiPanDriect();
						}
						if (shiPanDriect != null) {
							if ("haveimg".equals(parser.getName())) {
								haveimg 		= parser.nextText();
							} else if ("havemp3".equals(parser.getName())) {
								havemp3 		= parser.nextText();
							} else if ("havereplay".equals(parser.getName())) {
								havereplay 		= parser.nextText();
							} else if ("havelink".equals(parser.getName())) {
								havelink 		= parser.nextText();
							} else if ("msgtext".equals(parser.getName())) {
								msgtext 		= parser.nextText();
							} else if ("replaytext".equals(parser.getName())) {
								replaytext 		= parser.nextText();
							} else if ("replaydate".equals(parser.getName())) {
								replaydate 		= parser.nextText();
							} else if ("imgurl".equals(parser.getName())) {
								imgurl 			= parser.nextText();
							} else if ("imgthumburl".equals(parser.getName())) {
								imgthumburl 	= parser.nextText();
							} else if ("mp3url".equals(parser.getName())) {
								mp3url 			= parser.nextText();
							} else if ("link".equals(parser.getName())) {
								link 			= parser.nextText();
							}
						}
						break;
					case XmlPullParser.END_TAG:
						Log.i("TAG", "精华列表:" + "haveimg:" + haveimg + "\n" + 
								"havemp3:" + havemp3 + "\n" + 
								"havereplay:" + havereplay + "\n" + 
								"havelink:" + havelink + "\n" + 
								"msgtext:" + msgtext + "\n" + 
								"replaytext:" + replaytext + "\n" + 
								"replaydate:" + replaydate + "\n" + 
								"imgurl:" + imgurl + "\n" + 
								"imgthumburl:" + imgthumburl + "\n" + 
								"mp3url:" + mp3url + "\n" + 
								"link:" + link);
						break;
					}
					event = parser.next();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			String wonderful 					= shipanEssenceBean.getIs_wonderful();
			String call 						= shipanEssenceBean.getIs_call();
			String showname 					= shipanEssenceBean.getShowname();
			String c_showname 					= shipanEssenceBean.getC_showname();
			String authority 					= shipanEssenceBean.getAuthority();
			String is_system 					= shipanEssenceBean.getIs_system();
			String avatar_small 				= shipanEssenceBean.getAvatar_small();
			
			/* 控件存放view中 */
			View essenceView = null;
			if (essenceView == null) {
				essenceView = LayoutInflater.from(essenceContext).inflate(R.layout.shipan_direct_seeding_item, parent, false);
			}
			
			FrameLayout essenceTeacherLayout 			= CommonUtil.get(essenceView, R.id.interaction_teacher_layout);
			LinearLayout systemLayout 					= CommonUtil.get(essenceView, R.id.system_layout);
			LinearLayout layout 						= CommonUtil.get(essenceView, R.id.layout);
			LinearLayout essenceLayout 					= CommonUtil.get(essenceView, R.id.seeding_layout);
			LinearLayout essenceMessageLayout 			= CommonUtil.get(essenceView, R.id.seeding_message_layout);
			LinearLayout essenceMessageHuifuLayout 		= CommonUtil.get(essenceView, R.id.seeding_message_huifu_layout);
			LinearLayout essenceMessageMp3 				= CommonUtil.get(essenceView, R.id.seeding_message_mp3_layout);
			LinearLayout essenceMessageFinish			= CommonUtil.get(essenceView, R.id.seeding_message_finish_layout);
			TextView essenceName 						= CommonUtil.get(essenceView, R.id.seeding_name_item);
			TextView essenceTime 						= CommonUtil.get(essenceView, R.id.seeding_time_item);
			TextView essenceMessageText 				= CommonUtil.get(essenceView, R.id.seeding_message_text);
			TextView essenceMessageNameText 			= CommonUtil.get(essenceView, R.id.seeding_message_name_text);
			TextView essenceMessageDateText 			= CommonUtil.get(essenceView, R.id.seeding_message_date_text);
			TextView essenceMessageContentText 			= CommonUtil.get(essenceView, R.id.seeding_message_content_text);
//			TextView essenceMessageFinishText 			= CommonUtil.get(essenceView, R.id.seeding_message_finish_text);
			TextView essenceNameText 					= CommonUtil.get(essenceView, R.id.seeding_name_text);
			TextView essenceSystemMessageText 			= CommonUtil.get(essenceView, R.id.system_message);
			ImageView essenceEssenceImage 				= CommonUtil.get(essenceView, R.id.seeding_essence_image);
			ImageView essenceIscallImage 				= CommonUtil.get(essenceView, R.id.seeding_iscall_image);
			ImageView essenceMessageImage 				= CommonUtil.get(essenceView, R.id.seeding_message_image);
			ImageView essenceMessageTeacherImage 		= CommonUtil.get(essenceView, R.id.seeding_message_teacher_image);
			ImageView essenceMessageImageAmplification 	= CommonUtil.get(essenceView, R.id.seeding_meesage_image_amplification);
			Button essenceMessageAuthorityButton 		= CommonUtil.get(essenceView, R.id.seeding_message_authority_button);
			Button essenceMessageButton 				= CommonUtil.get(essenceView, R.id.seeding_message_button);
			
			if (!"".equals(imgurl)) {
				essenceTeacherLayout.setOnClickListener(new OnClickListener() {
					
					String imageUrl = imgurl;
					
					public void onClick(View v) {
						hzwy_BaseActivity.showPicture(imageUrl);
					}
				});
			}
			
			essenceMessageAuthorityButton.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					hzwy_BaseActivity.permissionHint(R.string.tishi_authority_message);
				}
			});			
			
			essenceMessageButton.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					applyAccountDialog();
				}
			});
			layout.setBackgroundResource(R.color.layout_beijing);
			/* 控件显示 */
			
			
			if ("1".equals(wonderful)) {
				essenceName.setText(showname);
				essenceTime.setText(dataLine);
				if ("1".equals(essenceRoomlevel) || "0".equals(essenceRoomlevel)) {
					if (!"1".equals(authority)) {
						if ("1".equals(call)) {
							essenceMessageButton.setVisibility(View.VISIBLE);
							essenceMessageLayout.setVisibility(View.GONE);
							essenceMessageAuthorityButton.setVisibility(View.GONE);
						} else if("0".equals(call)) {
							essenceMessageLayout.setVisibility(View.GONE);
							essenceMessageAuthorityButton.setVisibility(View.GONE);
							essenceMessageButton.setVisibility(View.VISIBLE);
						}
					} else {
						essenceMessageLayout.setVisibility(View.VISIBLE);
						essenceMessageAuthorityButton.setVisibility(View.GONE);
						essenceMessageButton.setVisibility(View.GONE);
					}
				} else {
					essenceMessageButton.setVisibility(View.GONE);
					essenceMessageLayout.setVisibility(View.VISIBLE);
					essenceMessageAuthorityButton.setVisibility(View.GONE);
				}
				essenceEssenceImage.setVisibility(View.VISIBLE);
				if ("1".equals(call)) {
					essenceIscallImage.setVisibility(View.VISIBLE);
				}
			}
			
			if ("".equals(c_showname) || "null".equals(c_showname)) {
				essenceMessageText.setText("     " + msgtext);//如果没有回复信息就直接显示内容
				essenceLayout.setVisibility(View.GONE);
			} else {
				essenceLayout.setVisibility(View.VISIBLE);
				essenceMessageText.setText("     " + msgtext);
				essenceNameText.setText(c_showname);
//				essenceMessageText.setText("回复" + c_showname + "的评论：" + msgtext);// 用户回复内容
			}

			if (!"".equals(replaytext)) {
				essenceMessageHuifuLayout.setVisibility(View.VISIBLE);
				essenceMessageNameText.setText(c_showname);// 回复人名称和时间
				essenceMessageDateText.setText(replaydate);
				essenceMessageContentText.setText(replaytext);// 回复人内容
			}

			loadImag(avatar_small, essenceMessageTeacherImage);// 老师头像
			if (!"".equals(imgthumburl)) {
				essenceTeacherLayout.setVisibility(View.VISIBLE);
				if (!"".equals(imgurl)) {
					essenceMessageImageAmplification.setVisibility(View.VISIBLE);
				} else {
					essenceMessageImageAmplification.setVisibility(View.GONE);
				}
				loadImag(imgthumburl, essenceMessageImage);// message图片
			}
			
			if ("1".equals(is_system)) {// 系统提示
				systemLayout.setVisibility(View.GONE);
				essenceMessageFinish.setVisibility(View.VISIBLE);
				essenceSystemMessageText.setText(msgtext);
			}
			
			playBubble = new PlayDirect(ShiPanEssenceActivity.this);
			nowPlayBubble = new PlayDirect(ShiPanEssenceActivity.this);
			if (!"".equals(mp3url)) {
				essenceMessageMp3.setVisibility(View.VISIBLE);
				essenceMessageMp3.addView(playBubble);
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
			return essenceView;
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
	
	/*********************************************申请开户******开始*************************************************/
	private void applyAccountDialog() {
		LayoutInflater factory 				= LayoutInflater.from(this);
		final View textEntryView 			= factory.inflate(R.layout.shipan_apply_account_dialog, null);
		Button applyImageButton 			= (Button) textEntryView.findViewById(R.id.apply_image_button);
		applyUserName 						= (EditText) textEntryView.findViewById(R.id.edit_apply_user_name);
		applyDianHua 						= (EditText) textEntryView.findViewById(R.id.edit_apply_dianhua);
		applyVerification 					= (EditText) textEntryView.findViewById(R.id.edit_apply_verification_code);
		applyVerificationButton 			= (Button) textEntryView.findViewById(R.id.button_apply_verification_code);
		applyImmediately 					= (Button) textEntryView.findViewById(R.id.button_apply_immediately_account);
		dialog 								= new AlertDialog.Builder(this).setView(textEntryView).create();
		dialog.show();
		applyImageButton.setOnClickListener(new OnClickListener() {/* 取消*/

					public void onClick(View v) {
						mc.onFinish();
						dialog.cancel();
					}
				});

		applyVerificationButton.setOnClickListener(new OnClickListener() {/* 获取验证码*/

					public void onClick(View v) {
						dianHua = applyDianHua.getText().toString().trim();
						if (!"".equals(dianHua)) {
							if (dianHua.length() == 11) {
								if (CommonUtil.isMobileNum(dianHua)) {
									if (CommonUtil.isConnectingToInternet(ShiPanEssenceActivity.this)) {
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

		applyImmediately.setOnClickListener(new OnClickListener() {/* 立即开户*/

					public void onClick(View v) {
						dianHua 			= applyDianHua.getText().toString().trim();
						userName 			= applyUserName.getText().toString().trim();
						verification 		= applyVerification.getText().toString().trim();
						if (!"".equals(dianHua)) {
							if (dianHua.length() == 11) {
								if (!"".equals(userName)) {
									if (verification.length() == 4) {
										if (CommonUtil.isConnectingToInternet(ShiPanEssenceActivity.this)) {
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
							hzwy_BaseActivity.HZWY_Toast(R.string.judge_phone_number_null);
						}
					}
				});
	}
	
	private void applyAccount() {
		new Thread(new Runnable() {

			public void run() {
				String url = Const.HZWY_URL + "type=kaihu&";
				try {
					Message msg 	= essenceHandler.obtainMessage();
					msg.obj 		= essenceUtil.getApplyAccount_post(url, dianHua, verification, userName, essenceUid, essenceCookieid);
					essenceHandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	Handler essenceHandler = new Handler() {
		public void handleMessage(Message msg) {
			ShiPanLogin shipanLogin 	= (ShiPanLogin) msg.obj;
			String Error 				= shipanLogin.getError();
			String Message 				= shipanLogin.getMessage();
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
	/***********************************************申请开户******结束**********************************************/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			ShiPanEssenceActivity.this.finish();
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
					String url 			= Const.HZWY_URL + "type=sendmobilecode&";
					Log.i("temp", "获取验证码--->>>" + url);
					try {
						Message msg 	= verificationHandler.obtainMessage();
						msg.obj 		= essenceUtil.getVerificationCodePost(url, dianHua);
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
				ShiPanLogin panLogin 	= (ShiPanLogin) msg.obj;
				String Error 			= panLogin.getError();
				String Message 			= panLogin.getMessage();
				Log.i("LOG", "验证码返回：" + Error);
				if ("0".equals(Error)) {
					mc.start();
				} else {
					hzwy_BaseActivity.HZWY_Toast1(Message);
				}
			};
		};
		private HZWY_BaseActivity hzwy_BaseActivity;
	
		/**
		 * 获取验证码倒计时
		 * @author Administrator
		 */
		class MyCount extends CountDownTimer {

			public MyCount(long millisInFuture, long countDownInterval) {
				super(millisInFuture, countDownInterval);
			}

			/* 倒计时停止 */
			@Override
			public void onFinish() {
				Log.i("temp", "onFinish");
				applyVerificationButton.setText(R.string.regiter_gain_verification_code);
				applyVerificationButton.setBackgroundResource(R.color.login_button);
				applyVerificationButton.setEnabled(true);/* 启动button*/
				mc.cancel();
				applyDianHua.setCursorVisible(true);/* 显示光标*/
				applyDianHua.setFocusableInTouchMode(true);/* 弹出软键盘*/
				applyDianHua.setFilters(new InputFilter[] { new InputFilter() {/* editext可编辑*/
					public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
						return null;
					}
				} });
			}

			/* 倒计时开始 */
			@Override
			public void onTick(long millisUntilFinished) {

				Log.i("temp", "onTick");
				applyVerificationButton.setText(millisUntilFinished / 1000 + "秒后重新获取");
				applyVerificationButton.setBackgroundResource(R.drawable.hzwy_verification_code);
				long a = millisUntilFinished / 1000;
				applyVerificationButton.setEnabled(false);/* 禁止button*/
				applyVerificationButton.setTextSize(12);

				applyDianHua.setCursorVisible(false);/* 隐藏光标*/
				applyDianHua.setFocusableInTouchMode(false);/* 禁止弹出软键盘*/
				applyDianHua.setFilters(new InputFilter[] { new InputFilter() {/* editext不可编辑*/
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
			super.onDestroy();
			System.gc();
		}
}
