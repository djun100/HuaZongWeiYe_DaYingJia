package com.hzwydyj.finace.activitys;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.PointBean;
import com.hzwydyj.finace.present.view.ShiPanListView;
import com.hzwydyj.finace.present.view.ShiPanListView.ShiPanListViewListener;
import com.hzwydyj.finace.utils.CommonUtil;
import com.hzwydyj.finace.utils.Util;

public class PointTeacherLecture extends Activity implements OnClickListener, ShiPanListViewListener {

	private Button teacherBack;
	private TextView teacherName;
	private ShiPanListView teacherList;
	private Util teacherUtil = new Util();
	private ArrayList<PointBean> teacherTems = new ArrayList<PointBean>();
	private PointPartitionAdapte teacherAdapter;
	private AlertDialog pointDialog;
	private String teacherOne, teacherDName, teacherPUid, teacherPUserName;
	/* 刷新两者之前的时间判断 */
	private String refreshtime; // 这个String是调用onRefresh()方法的当前时间
	private String lastime; // 第一次启动Activity时通过SharedPreferences获取保存的时间
	private String lastimes; // 是软件启动之后把上一次的刷新时间设置给lastimes
	private String nowtime; // 启动Activity时的系统当前时间
	/* 判断是否是刚启动 */
	private int time01 = 0;
	private int videoStart = 0; 
	private boolean time02 = true;

	public SharedPreferences videoSetting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		inint();
		pathUrl();
		showTitle();
	}

	public void inint() {
		setContentView(R.layout.point_teacher_lecture);
		
		hzwy_BaseActivity = new HZWY_BaseActivity(MyApplication.CONTEXT);

		Intent intent = getIntent();
		teacherOne = intent.getStringExtra("one");
		teacherDName = intent.getStringExtra("dName");
		teacherPUid = intent.getStringExtra("pUid");
		teacherPUserName = intent.getStringExtra("pUserName");

		teacherBack = (Button) this.findViewById(R.id.button_video_back);
		teacherBack.setOnClickListener(this);
		teacherName = (TextView) this.findViewById(R.id.text_teacher_name);
		teacherList = (ShiPanListView) this.findViewById(R.id.pointTeacherList);
		teacherAdapter = new PointPartitionAdapte(PointTeacherLecture.this, teacherTems);
		teacherList.setAdapter(teacherAdapter);
		teacherList.setPullRefreshEnable(true);
		teacherList.setXListViewListener(this);

		hzwy_BaseActivity.SingleDialog(R.string.tishi_mingshi_list);
		if (CommonUtil.isConnectingToInternet(PointTeacherLecture.this)) {
			geneItems();
		} else {
			hzwy_BaseActivity.dialogCancel();
			hzwy_BaseActivity.HZWY_Toast(R.string.network_connection_failure);
			finish();
		}

		refreshtime = new SimpleDateFormat("MM-dd HH:mm").format(new Date());
		nowtime = refreshtime;
	}

	public void showTitle() {
		teacherName.setText(R.string.point_listview_jiangtang);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_video_back:
			finish();
			break;

		default:
			break;
		}
	}

	private void onLoad() {
		teacherList.stopRefresh();
		compareRefreshTime();
	}

	public void onRefresh() {
		// TODO Auto-generated method stub
		handler.postDelayed(new Runnable() {

			public void run() {
				teacherTems.clear();
				geneItems();
				teacherAdapter = new PointPartitionAdapte(PointTeacherLecture.this, teacherTems);
				teacherList.setAdapter(teacherAdapter);
				onLoad();

				refreshtime = new SimpleDateFormat("MM-dd HH:mm").format(new Date());
				lastimes = refreshtime;

				videoSetting = getSharedPreferences("LasTime", Activity.MODE_PRIVATE);
				Editor editor = videoSetting.edit();
				editor.putString("lastime", refreshtime);
				editor.commit();
				time02 = false;
			}
		}, 2000);
	}

	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	private void geneItems() {
		if (CommonUtil.isConnectingToInternet(PointTeacherLecture.this)) {
			pathUrl();
		} else {
			hzwy_BaseActivity.HZWY_Toast(R.string.network_connection_failure);
		}
	}

	public void pathUrl() {
		new Thread() {
			public void run() {
				String urlString = null;
				urlString = Const.HZWY_URL + "type=videolist&tuid=" + teacherPUid + "&ttype=" + teacherOne;
				try {
					Message msg = handler.obtainMessage();
					msg.obj = teacherUtil.getVideoResult_post(urlString);
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			teacherTems = (ArrayList<PointBean>) msg.obj;
			teacherAdapter = new PointPartitionAdapte(PointTeacherLecture.this, teacherTems);
			teacherList.setAdapter(teacherAdapter);
			teacherAdapter.notifyDataSetChanged();
			hzwy_BaseActivity.dialogCancel();
		};
	};
	private HZWY_BaseActivity hzwy_BaseActivity;

	/**
	 * 刷新列表
	 * 
	 * @author Administrator
	 * 
	 */
	class PointPartitionAdapte extends BaseAdapter {

		private Context videoContext;
		private List<PointBean> videoBeans;
		private LayoutInflater videoInflater;

		public PointPartitionAdapte(Context context, List<PointBean> pointBeans) {
			this.videoContext = context;
			this.videoBeans = pointBeans;
			videoInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return videoBeans.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(videoContext).inflate(R.layout.point_partition_teacher_item, parent, false);
			}
			LinearLayout pointTeacherLayout 		= CommonUtil.get(convertView, R.id.layout_teacher_video);
			TextView pointTeacherVideo 				= CommonUtil.get(convertView, R.id.text_teacher_video);
			TextView pointTeacherType 				= CommonUtil.get(convertView, R.id.text_teacher_type);
			TextView pointTeacherAnchor 			= CommonUtil.get(convertView, R.id.text_teacher_anchor_name);
			TextView pointTeacherDate 				= CommonUtil.get(convertView, R.id.text_teacher_date);
			Button pointTeacherPassword 			= CommonUtil.get(convertView, R.id.button_teacher_password);

			pointTeacherType.setText(teacherDName);
			pointTeacherAnchor.setText(teacherPUserName);
			pointTeacherDate.setText(videoBeans.get(position).getDateline());
			pointTeacherVideo.setText(videoBeans.get(position).getSubject());
			pointTeacherLayout.setOnClickListener(new OnClickListener() {

				private EditText				 	pointVerification;
				private Button 						pointConfirm;
				private Button 						pointCancel;
				private Button 						pointCancelImage;

				public void onClick(View v) {
					LayoutInflater factory 			= LayoutInflater.from(videoContext);

					final View textEntryView 		= factory.inflate(R.layout.video_dialog, null);
					
					pointVerification 				= (EditText) textEntryView.findViewById(R.id.edit_video_pass_word);
					pointConfirm 					= (Button) textEntryView.findViewById(R.id.button_video_confirm);
					pointCancel 					= (Button) textEntryView.findViewById(R.id.button_video_cancel);
					pointCancelImage	 			= (Button) textEntryView.findViewById(R.id.cancel_video_image);
					
					pointDialog 							= new AlertDialog.Builder(videoContext).setView(textEntryView).create();
					pointDialog.show();
					
					pointConfirm.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									String pointPassWord 	= pointVerification.getText().toString().trim();
									String pointPasswd 		= videoBeans.get(position).getPasswd();
									String pointAppaddr 	= videoBeans.get(position).getAppaddr();
									if (pointPassWord.equals(pointPasswd)) {
										/* 第三方浏览器播放视频 */
										try {
											Uri uri = Uri.parse(pointAppaddr);
											Intent intent = new Intent(Intent.ACTION_VIEW, uri);
											startActivity(intent);
											pointDialog.cancel();
										} catch (Exception e) {
											e.printStackTrace();
											hzwy_BaseActivity.HZWY_Toast(R.string.point_listview_video_tishi);
										}
									} else {
										hzwy_BaseActivity.HZWY_Toast(R.string.point_listview_error_password);
									}
								}
							});
					pointCancelImage.setOnClickListener(new OnClickListener() {

								public void onClick(View v) {
									pointDialog.dismiss();
								}
							});
					
					pointCancel.setOnClickListener(new OnClickListener() {
						
						public void onClick(View v) {
							pointDialog.dismiss();
						}
					});
				}
			});

			pointTeacherPassword.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
			pointTeacherPassword.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					callPassword();
				}
			});
			return convertView;
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}
	}

	/* 判断刷新之间的时间 */
	public void compareRefreshTime() {
		SharedPreferences preferences = getSharedPreferences("LasTime", Context.MODE_PRIVATE);
		lastime = preferences.getString("lastime", "");

		System.out.println("lastimes上次刷新执行的时间-->" + lastimes);
		System.out.println("refreshtime上次执行的时间--->" + refreshtime);
		System.out.println("lastime保存上次刷新的时间-->" + lastime);
		System.out.println("nowtime进入本页面的时间-->" + nowtime);

		long last = 0;
		long refresh = 0;
		try {
			if (time01 < 2) {
				last = new SimpleDateFormat("MM-dd HH:mm").parse(lastime).getTime();
				Log.i("temp", "刷新后的时间1：" + last);
				time01++;
			} else {
				last = new SimpleDateFormat("MM-dd HH:mm").parse(lastimes).getTime();
				Log.i("temp", "上次刷新后的时间2：" + last);
			}
			if (time02) {
				refresh = new SimpleDateFormat("MM-dd HH:mm").parse(nowtime).getTime();
				time02 = false;
				Log.i("temp", "进入本页的时间3：" + refresh);
			} else {
				refresh = new SimpleDateFormat("MM-dd HH:mm").parse(refreshtime).getTime();
				Log.i("temp", "本次刷新后的时间4：" + refresh);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ((refresh - last) / 60000 < 1) {
			teacherList.setRefreshTime("刚刚");
		} else if ((refresh - last) / 60000 < 10) {
			teacherList.setRefreshTime((refresh - last) / 60000 + "分钟之前");
		} else if ((refresh - last) / 3600000 < 10) {
			teacherList.setRefreshTime((refresh - last) / 3600000 + "小时之前");
		} else {
			teacherList.setRefreshTime(lastime);
		}
	}

	public void callPassword() {
		final String suppoerPhone = "4000-720-760";

		LayoutInflater factory 					= LayoutInflater.from(this);
		final View textEntryView 				= factory.inflate(R.layout.shipan_share_dialog, null);
		
		RelativeLayout callTitle 				= (RelativeLayout) textEntryView.findViewById(R.id.title_layout);
		TextView callContent 					= (TextView) textEntryView.findViewById(R.id.share_content_text);
		Button callQueRen 						= (Button) textEntryView.findViewById(R.id.share_queren_button);
		Button callQuXiao 						= (Button) textEntryView.findViewById(R.id.share_quxiao_button);
		
		callTitle.setVisibility(View.GONE);
		callContent.setText(R.string.point_listview_dial_video);
		callQueRen.setText(R.string.tishi_dial);
		callQuXiao.setVisibility(View.VISIBLE);
		
		final AlertDialog dialog = new AlertDialog.Builder(this).setView(textEntryView).create();
		dialog.show();
		
		callQueRen.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setData(Uri.parse("tel://" + suppoerPhone));
				startActivity(intent);
				dialog.cancel();
			}
		});
		
		callQuXiao.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				dialog.cancel();
			}
		});
	}
}
