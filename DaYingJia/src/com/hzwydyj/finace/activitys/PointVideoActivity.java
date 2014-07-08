package com.hzwydyj.finace.activitys;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.PointBean;
import com.hzwydyj.finace.present.view.PointListView;
import com.hzwydyj.finace.present.view.PointListView.PointListViewListener;
import com.hzwydyj.finace.utils.CommonUtil;
import com.hzwydyj.finace.utils.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * 视频列表页面
 * @author LuoYi
 *
 */
public class PointVideoActivity extends Activity implements OnClickListener, PointListViewListener {

	private PointListView 				videoListView;
	private PointPartitionAdapte 		videoAdapter;
	private ArrayList<PointBean> 		videoTems = new ArrayList<PointBean>();
	private Util 						videoUtil = new Util();
	private static int 					videoRefreshCnt = 0;
	private String 						videoTeacherUid;
	private String 						teacherUserName;
	private String 						numberOne;
	private Button 						videoBackButton, videoButton;
	/* 刷新两者之前的时间判断*/
	private String 						refreshtime; // 这个String是调用onRefresh()方法的当前时间
	private String 						lastime; // 第一次启动Activity时通过SharedPreferences获取保存的时间
	private String 						lastimes; // 是软件启动之后把上一次的刷新时间设置给lastimes
	private String 						nowtime; // 启动Activity时的系统当前时间
	
	public SharedPreferences 			videoSetting;
	/* 判断是否是刚启动*/
	private int 						time01 = 0;
	private int 						videoStart = 0;
	private boolean 					time02 = true;
	private AlertDialog					pointDialog;

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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		init();
		pathUrl();

		refreshtime 	= new SimpleDateFormat("MM-dd HH:mm").format(new Date());
		nowtime 		= refreshtime;
	}

	private void init() {
		setContentView(R.layout.point_video_list);
		
		hzwy_BaseActivity = new HZWY_BaseActivity(MyApplication.CONTEXT);

		Intent intent 				= getIntent();
		videoTeacherUid 			= intent.getStringExtra("pUid");
		teacherUserName 			= intent.getStringExtra("pUserName");
		numberOne 					= intent.getStringExtra("numberOne");

		TextView videoTitle 		= (TextView) this.findViewById(R.id.video_title);
		videoBackButton 			= (Button) this.findViewById(R.id.button_video_back);
		videoButton 				= (Button) this.findViewById(R.id.button_video);
		videoTitle.setText(teacherUserName);
		/* 获取视频密码*/
		videoButton.setText(R.string.point_listview_acquire_video_password);
		videoButton.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
		videoBackButton.setOnClickListener(this);
		videoButton.setOnClickListener(this);

		PointDialog();
		if (CommonUtil.isConnectingToInternet(PointVideoActivity.this)) {
			geneItems();
		} else {
			pointDialog.cancel();
			hzwy_BaseActivity.HZWY_Toast(R.string.network_connection_failure);
		}
		videoListView 				= (PointListView) findViewById(R.id.pointVideoList);
		videoListView.setPullLoadEnable(true);
		videoAdapter 				= new PointPartitionAdapte(PointVideoActivity.this, videoTems);
		videoListView.setAdapter(videoAdapter);
		videoListView.setPointListViewListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_video_back:
			PointVideoActivity.this.finish();
			break;

		case R.id.button_video:
			callPassword();
			break;

		default:
			break;
		}
	}

	public void pathUrl() {
		new Thread() {
			public void run() {
				String urlString 	= null;
				urlString 			= Const.HZWY_URL + "type=video&tuid=" + videoTeacherUid + "&vtid=" + numberOne;
				try {
					Message msg 	= handler.obtainMessage();
					msg.obj 		= videoUtil.getVideoResult_post(urlString);
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
			videoTems 		= (ArrayList<PointBean>) msg.obj;
			videoAdapter 	= new PointPartitionAdapte(PointVideoActivity.this, videoTems);
			videoListView.setAdapter(videoAdapter);
			videoAdapter.notifyDataSetChanged();
			pointDialog.cancel();
		};
	};
	private HZWY_BaseActivity hzwy_BaseActivity;
	

	private void geneItems() {
		if (CommonUtil.isConnectingToInternet(PointVideoActivity.this)) {
			pathUrl();
		} else {
			hzwy_BaseActivity.HZWY_Toast(R.string.network_connection_failure);
		}
	}

	private void onLoad() {
		videoListView.stopRefresh();
		videoListView.stopLoadMore();
		compareRefreshTime();
	}

	public void onLoadMore() {
		handler.postDelayed(new Runnable() {
			public void run() {
				geneItems();
				videoAdapter.notifyDataSetChanged();
				onLoad();
			}
		}, 2000);
	}

	public void onRefresh() {
		handler.postDelayed(new Runnable() {

			public void run() {
				videoStart 		= ++videoRefreshCnt;
				videoTems.clear();
				geneItems();
				videoAdapter 	= new PointPartitionAdapte(PointVideoActivity.this, videoTems);
				videoListView.setAdapter(videoAdapter);
				onLoad();

				refreshtime 	= new SimpleDateFormat("MM-dd HH:mm").format(new Date());
				lastimes 		= refreshtime;

				videoSetting 	= getSharedPreferences("LasTime", Activity.MODE_PRIVATE);
				Editor editor 	= videoSetting.edit();
				editor.putString("lastime", refreshtime);
				editor.commit();

				time02 			= false;
			}
		}, 2000);
	}

	/**
	 * 刷新列表
	 * 
	 * @author Administrator
	 * 
	 */
	class PointPartitionAdapte extends BaseAdapter {

		private Context 				videoContext;
		private List<PointBean> 		videoBeans;
		private LayoutInflater 			videoInflater;

		public PointPartitionAdapte(Context context, List<PointBean> pointBeans) {
			this.videoContext 		= context;
			this.videoBeans 		= pointBeans;
			videoInflater 			= LayoutInflater.from(context);
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
				convertView = LayoutInflater.from(videoContext).inflate(R.layout.point_list_item, parent, false);
			}
			TextView videoDsignation 		= CommonUtil.get(convertView, R.id.text_designation);
			TextView videoListName 			= CommonUtil.get(convertView, R.id.list_item_name);
			TextView videoListItem 			= CommonUtil.get(convertView, R.id.list_item_textview);
			Button videoViewDetails 		= CommonUtil.get(convertView, R.id.button_view_details);
			Button videoViewDetail 			= CommonUtil.get(convertView, R.id.button_view_detail);
			ImageView videoImage 			= CommonUtil.get(convertView, R.id.image_video);
			
			videoImage.setVisibility(View.GONE);
			videoDsignation.setText(videoBeans.get(position).getSubject());
			videoListName.setVisibility(View.GONE);
			videoListItem.setText(videoBeans.get(position).getDateline());
			videoViewDetails.setVisibility(View.GONE);
			videoViewDetail.setVisibility(View.VISIBLE);
			videoViewDetail.setText(R.string.point_listview_watch_video);
			videoViewDetail.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					/* 第三方浏览器播放视频*/
					try {
						Uri uri 			= Uri.parse(videoBeans.get(position).getAppaddr());
						Intent intent 		= new Intent(Intent.ACTION_VIEW, uri);
						startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
						hzwy_BaseActivity.HZWY_Toast(R.string.point_listview_video_tishi);
						Toast.makeText(PointVideoActivity.this, R.string.point_listview_video_tishi, Toast.LENGTH_LONG).show();;
					}
					/* 内嵌浏览器播放视频*/
					/*try {
						Intent intent = new Intent(PointVideoActivity.this, PointVideoWebActivity.class);
						intent.putExtra("subject", videoBeans.get(position).getSubject());
						intent.putExtra("appaddr", videoBeans.get(position).getAppaddr());
						startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
						HZWY_Toast(R.string.point_listview_video_tishi);
					}*/
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
		SharedPreferences preferences 	= getSharedPreferences("LasTime", Context.MODE_PRIVATE);
		lastime 						= preferences.getString("lastime", "");

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
			videoListView.setRefreshTime("刚刚");
		} else if ((refresh - last) / 60000 < 10) {
			videoListView.setRefreshTime((refresh - last) / 60000 + "分钟之前");
		} else if ((refresh - last) / 3600000 < 10) {
			videoListView.setRefreshTime((refresh - last) / 3600000 + "小时之前");
		} else {
			videoListView.setRefreshTime(lastime);
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
	
	/*********************************************获取老师列表弹窗********开始************************************************/
	public void PointDialog(){
		LayoutInflater factory 					= LayoutInflater.from(this);
		final View textEntryView 				= factory.inflate(R.layout.shipan_share_dialog, null);
		
		TextView loginTitle 					= (TextView) textEntryView.findViewById(R.id.share_title_text);
		ProgressBar loginProgess 				= (ProgressBar) textEntryView.findViewById(R.id.progress_bar);
		TextView loginContent 					= (TextView) textEntryView.findViewById(R.id.share_content_text);
		TextView loginMessage 					= (TextView) textEntryView.findViewById(R.id.share_message_text);
		LinearLayout loginLayout 				= (LinearLayout) textEntryView.findViewById(R.id.share_layout);
		
		loginTitle.setText(R.string.later_on_user);
		loginProgess.setVisibility(View.VISIBLE);
		loginContent.setVisibility(View.GONE);
		loginMessage.setText("正在获取" + teacherUserName + "列表");
		loginMessage.setVisibility(View.VISIBLE);
		loginLayout.setVisibility(View.GONE);
		
		pointDialog 							= new AlertDialog.Builder(this).setView(textEntryView).create();
		pointDialog.show();
	}
	/**********************************************获取老师表弹窗********结束***********************************************/

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
	}
}