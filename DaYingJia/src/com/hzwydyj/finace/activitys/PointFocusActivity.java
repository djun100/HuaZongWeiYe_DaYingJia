package com.hzwydyj.finace.activitys;

import java.util.List;
import java.util.Timer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.PointBean;
import com.hzwydyj.finace.utils.CommonUtil;
import com.hzwydyj.finace.utils.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * 名师讲堂选择列表
 * 
 * @author LuoYi
 * 
 */
public class PointFocusActivity extends Activity implements OnClickListener, OnItemClickListener {

	private Button 						pointUnbind, pointTechnology, pointBackbtn, pointSearchbtn;
	PointBean 							pointBean;
	private ListView 					pointList;
	private List<PointBean> 			pointBeanList;
	private Util 						util = new Util();
	Timer 								chatTimer = new Timer(); // 时长
	private TextView 					pointTeacher;
	private String 						pointName;
	private String 						pointUid;
	
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
	 * 创建页面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		init();
	}

	/**
	 * 初始化、实例化
	 */
	public void init() {
		setContentView(R.layout.point_focus);
		
		hzwy_BaseActivity = new HZWY_BaseActivity(MyApplication.CONTEXT);
		/* 获取上个页面的值*/
		Intent intent = getIntent();
		pointUid = intent.getStringExtra("pUid");
		pointName = intent.getStringExtra("pUserName");
		/* 初始化控件*/
		pointBackbtn 		= (Button) this.findViewById(R.id.button_backbtn);
		pointList 			= (ListView) this.findViewById(R.id.list_point);
		pointSearchbtn 		= (Button) this.findViewById(R.id.searchbtn);
		pointList.setOnItemClickListener(this);
		pointBackbtn.setOnClickListener(this);
		pointSearchbtn.setOnClickListener(this);
		pointTeacher = (TextView) this.findViewById(R.id.text_point_teacher);
		/* 获取视频密码*/
		pointSearchbtn.setVisibility(View.GONE);
		pointSearchbtn.setText(R.string.point_listview_acquire_video_password);
		pointSearchbtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线

		hzwy_BaseActivity.SingleDialog(R.string.tishi_mingshi_list);
		if (CommonUtil.isConnectingToInternet(PointFocusActivity.this)) {
			initData();
		} else {
			hzwy_BaseActivity.dialogCancel();
			/* 网络连接失败,请稍后重试...*/
			hzwy_BaseActivity.HZWY_Toast(R.string.network_connection_failure);
			PointFocusActivity.this.finish();
		}
		showTitle();
	}
	
	public void showTitle(){
		pointTeacher.setText(R.string.point_listview_jiangtang);
	}

	/**
	 * 控件按钮
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_backbtn:// 返回
			PointFocusActivity.this.finish();
			break;

		case R.id.searchbtn:// 获取视频密码
			callPassword();
			break;

		default:
			break;
		}
	}

	/**
	 * 网络请求
	 */
	private void initData() {
		new Thread(new Runnable() {

			@SuppressWarnings("static-access")
			public void run() {
				String url = Const.HZWY_URL + "type=videotype";
				try {
					Message msg 	= handler.obtainMessage();
					msg.obj 		= util.getListPerson(url);
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			pointBeanList = (List<PointBean>) msg.obj;
			pointList.setAdapter(new PointFocusAdapter(PointFocusActivity.this, pointBeanList));
			hzwy_BaseActivity.dialogCancel();
		};
	};
	private HZWY_BaseActivity hzwy_BaseActivity;

	/**
	 * listView适配器
	 * @author LuoYi
	 *
	 */
	class PointFocusAdapter extends BaseAdapter {

		private Context 			context;
		private LayoutInflater 		inflater;
		private List<PointBean> 	pBean;

		public PointFocusAdapter(Context context, List<PointBean> pointBeanList) {
			this.context 			= context;
			this.pBean 				= pointBeanList;
			inflater 				= LayoutInflater.from(context);
		}

		public int getCount() {
			return pBean.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.point_focus_item, parent, false);
			} 
			LinearLayout unbindLayout 		= CommonUtil.get(convertView, R.id.unbind_layout);
			Button unbind 					= CommonUtil.get(convertView, R.id.button_unbind);
			Button unbind1 					= CommonUtil.get(convertView, R.id.button_unbind1);

			if (position % 2 == 0) {
				unbind.setText(pBean.get(position).getVtname());
				unbind.setVisibility(View.VISIBLE);
				unbind1.setVisibility(View.GONE);
				unbindLayout.setBackgroundResource(R.drawable.shipan_img_1);
			} else {
				unbind1.setText(pBean.get(position).getVtname());
				unbind.setVisibility(View.GONE);
				unbind1.setVisibility(View.VISIBLE);
				unbindLayout.setBackgroundResource(R.drawable.shipan_img_2);
			}
			unbindLayout.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					Intent intent 	= new Intent(PointFocusActivity.this, PointTeacherLecture.class);
					intent.putExtra("one", pBean.get(position).getVtid());
					intent.putExtra("dName", pBean.get(position).getVtname());
					intent.putExtra("pUid", pointUid);
					intent.putExtra("pUserName", pointName);
					Log.i("LOG", "点击第  " + pBean.get(position).getVtid() + " 个");
					startActivity(intent);
				}
			});
			return convertView;
		}
	}

	/**
	 * 重写item点击事件
	 */
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	}

	public void callPassword() {
		final String suppoerPhone 				= "4000-720-760";
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
