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
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.PointBean;
import com.hzwydyj.finace.image.CacheImageAsyncTask;
import com.hzwydyj.finace.present.view.PointListView.PointListViewListener;
import com.hzwydyj.finace.utils.CommonUtil;
import com.hzwydyj.finace.utils.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * 老师列表页面
 * @author LuoYi
 *
 */
public class PointPartitionActivity extends Activity implements OnClickListener, PointListViewListener {

	private Util 						pointUtil = new Util();
	private ListView 					pointListView;
	private PointPartitionAdapte 		pointAdapter;
	private ArrayList<PointBean>		pointTems = new ArrayList<PointBean>();
	private Button 						pointBack, pointSearch;
	private TextView 					pointTitle;
	private String 						numberOne, dName;
	/* 刷新两者之前的时间判断*/
	private String 						refreshtime; // 这个String是调用onRefresh()方法的当前时间
	private String 						lastime; // 第一次启动Activity时通过SharedPreferences获取保存的时间
	private String 						lastimes; // 是软件启动之后把上一次的刷新时间设置给lastimes
	private String 						nowtime; // 启动Activity时的系统当前时间
	public SharedPreferences 			settings;

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

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.point_partition);
		hzwy_BaseActivity = new HZWY_BaseActivity(MyApplication.CONTEXT);
		init();
		hzwy_BaseActivity.SingleDialog(R.string.tishi_mingshi_list);
		if (CommonUtil.isConnectingToInternet(PointPartitionActivity.this)) {
			pathUrl();
		} else {
			hzwy_BaseActivity.dialogCancel();
			hzwy_BaseActivity.HZWY_Toast(R.string.network_connection_failure);
			PointPartitionActivity.this.finish();
		}
		refreshtime 	= new SimpleDateFormat("MM-dd HH:mm").format(new Date());
		nowtime 		= refreshtime;
	}

	public void init() {
		Intent intent 	= getIntent();
		numberOne 		= intent.getStringExtra("one");
		dName			= intent.getStringExtra("dName");
		pointBack 		= (Button) this.findViewById(R.id.button_backbtn);
		pointSearch 	= (Button) this.findViewById(R.id.button_searchbtn);
		/* 获取视频密码*/
		pointSearch.setVisibility(View.GONE);
		pointSearch.setText(R.string.point_listview_acquire_video_password);
		pointSearch.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
		pointBack.setOnClickListener(this);
		pointSearch.setOnClickListener(this);
		
		pointTitle 		= (TextView) this.findViewById(R.id.paging_title);
		pointListView 	= (ListView) this.findViewById(R.id.pointListView);
		showTitle();
	}
	
	public void showTitle(){
		pointTitle.setText(R.string.point_listview_jiangtang);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_backbtn:
			PointPartitionActivity.this.finish();
			break;

		case R.id.button_searchbtn:
			callPassword();
			break;

		default:
			break;
		}
	}

	public void pathUrl() {
		new Thread() {
			public void run() {
				String urlString = Const.HZWY_URL + "type=teacherlist&size=big";
				Log.i("ShiPan", "urlString:" + urlString);
				try {
					Message msg = handler.obtainMessage();
					msg.obj 	= pointUtil.getTeacherResult_post(urlString);
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
			pointTems = (ArrayList<PointBean>) msg.obj;
			pointAdapter = new PointPartitionAdapte(PointPartitionActivity.this, pointTems);
			pointListView.setAdapter(pointAdapter);
			pointAdapter.notifyDataSetChanged();
			hzwy_BaseActivity.dialogCancel();
		};
	};
	private HZWY_BaseActivity hzwy_BaseActivity;

	public void onRefresh() {
	}


	private void geneItems() {
		pathUrl();
	}

	public void onLoadMore() {
	}

	/**
	 * 刷新列表
	 * @author Administrator
	 */
	class PointPartitionAdapte extends BaseAdapter {

		private Context 			pointContext;
		private List<PointBean> 	pointBeans;
		private LayoutInflater 		pointInflater;

		public PointPartitionAdapte(Context context, List<PointBean> pointBeans) {
			this.pointContext 		= context;
			this.pointBeans 		= pointBeans;
			pointInflater 			= LayoutInflater.from(context);
		}

		public int getCount() {
			return pointBeans.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			/* 将控件保存在容器里*/
			if (convertView == null) {
				convertView = LayoutInflater.from(pointContext).inflate(R.layout.point_partition_teacher, parent, false);
			} 
			ImageView pointImageHead 		= CommonUtil.get(convertView, R.id.image_teacher_head);
			TextView pointTextName 			= CommonUtil.get(convertView, R.id.text_teacher_name);
			TextView pointTextContent		= CommonUtil.get(convertView, R.id.text_teacher_content);
			Button pointTeacherList 		= CommonUtil.get(convertView, R.id.image_teacher_list);
			/* 显示List*/
			String urlpath = pointBeans.get(position).getAvatar();
			
			loadImag(urlpath, pointImageHead);// 显示图片
			pointTextName.setText(pointBeans.get(position).getName());
			pointTextContent.setText(pointBeans.get(position).getIntro());
			pointTeacherList.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					Intent intent = new Intent(PointPartitionActivity.this, PointFocusActivity.class);
					intent.putExtra("pUid", pointBeans.get(position).getUid());
					intent.putExtra("pUserName", pointBeans.get(position).getName());
					intent.putExtra("numberOne", numberOne);
					startActivity(intent);
				}
			});
			return convertView;
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}
		
		/**
		 * 设置图片路径
		 * @param path
		 * @param imag
		 * @return
		 */
		public Bitmap loadImag(String path, ImageView imag) {
			new CacheImageAsyncTask(imag, MyApplication.CONTEXT).execute(path);
			return null;
		}
	}

	/**
	 * 获取视频密码
	 */
	public void callPassword() {
		final String suppoerPhone = "4000-720-760";

		LayoutInflater factory 					= LayoutInflater.from(this);
		final View textEntryView 				= factory.inflate(R.layout.shipan_share_dialog, null);
		
		RelativeLayout callTitle 				= (RelativeLayout) textEntryView.findViewById(R.id.title_layout);
		TextView callContent 					= (TextView) textEntryView.findViewById(R.id.share_content_text);
		Button callQueRen 						= (Button) textEntryView.findViewById(R.id.share_queren_button);
		Button callQuXiao 						= (Button) textEntryView.findViewById(R.id.share_quxiao_button);
		
		/* 提示*/
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
