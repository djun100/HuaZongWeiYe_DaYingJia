package com.hzwydyj.finace.activitys;

import java.net.SocketException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.ExceptionBean;
import com.hzwydyj.finace.data.ShiPanTanGuBean;
import com.hzwydyj.finace.db.DBConst;
import com.hzwydyj.finace.db.DriectDBhelper;
import com.hzwydyj.finace.service.HuaZongWeiYeService;
import com.hzwydyj.finace.service.SysApplication;
import com.hzwydyj.finace.utils.CommonUtil;
import com.hzwydyj.finace.utils.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * 实盘直播
 * 
 * @author LuoYi
 * 
 */
public class ShiPanSelectActivity extends Activity implements OnClickListener, OnItemClickListener {

	private final static String 			TAG = "ShiPanSelectActivity";
	private Button 							selectBack;
	private Button 							selectOutLogin;
	private Util 							selectUtil = new Util();
	private List<ShiPanTanGuBean> 			selectBeanList;
	private ListView 						selectList;
	ShiPanTanGuBean 						selectBean;
	private String 							selectGroupid;
	private String 							selectGroupTitle;
	private String 							selectGrade;
	private String 							selectRoomlevel;
	private String 							selectCookieid;
	private String 							selectUid;
	private AlertDialog 					selectDialog;
	public int[] 							selectImage = new int[] { R.drawable.pic_1, R.drawable.pic_2, R.drawable.pic_3, R.drawable.pic_4 };

	private HZWY_BaseActivity hzwy_BaseActivity;
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
		SelectDialog();
		init();
	}

	public void init() {
		setContentView(R.layout.shipanselect);
		
		hzwy_BaseActivity = new HZWY_BaseActivity(MyApplication.CONTEXT);
		
		/* 获取上个页面的值 */
		Intent intent 				= getIntent();
		selectGroupid 				= intent.getStringExtra("groupid");
		selectGroupTitle 			= intent.getStringExtra("groupTitle");
		selectGrade 				= intent.getStringExtra("grade");
		selectRoomlevel 			= intent.getStringExtra("roomlevel");
		selectCookieid 				= intent.getStringExtra("cookieid");
		selectUid 					= intent.getStringExtra("uid");
		/* 初始化控件 */
		selectList 					= (ListView) findViewById(R.id.list_select);
		selectBack 					= (Button) findViewById(R.id.button_backbtn);
		selectOutLogin 				= (Button) findViewById(R.id.button_out_login);
		selectOutLogin.setText(R.string.tishi_login_out);
		selectList.setOnItemClickListener(this);
		selectBack.setOnClickListener(this);
		selectOutLogin.setOnClickListener(this);
		if (CommonUtil.isConnectingToInternet(ShiPanSelectActivity.this)) {
			select();
		} else {
			hzwy_BaseActivity.HZWY_Toast(R.string.network_connection_failure);
			selectDialog.cancel();
			ShiPanSelectActivity.this.finish();
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_backbtn:// 返回
			ShiPanSelectActivity.this.finish();
			break;

		case R.id.button_out_login:// 退出登录
			clearData();
			break;

		default:
			break;
		}
	}

	public void clearData() { // 便于测试，需求没有要求退出登录
		LayoutInflater factory 					= LayoutInflater.from(this);
		final View textEntryView 				= factory.inflate(R.layout.shipan_share_dialog, null);
		TextView outInTitle 					= (TextView) textEntryView.findViewById(R.id.share_title_text);
		TextView outInContent 					= (TextView) textEntryView.findViewById(R.id.share_content_text);
		Button outInQueRen 						= (Button) textEntryView.findViewById(R.id.share_queren_button);
		Button outInQuXiao 						= (Button) textEntryView.findViewById(R.id.share_quxiao_button);
		/* 提示*/
		outInTitle.setText(R.string.tishi_warm_prompt);
		outInContent.setText(R.string.tishi_logout);
		outInQueRen.setText(R.string.tishi_que_dings);
		outInQuXiao.setVisibility(View.VISIBLE);
		
		final AlertDialog dialog 				= new AlertDialog.Builder(this).setView(textEntryView).create();
		dialog.show();
		
		outInQueRen.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				DriectDBhelper driectDBhelper = new DriectDBhelper(MyApplication.CONTEXT);
				driectDBhelper.deleteAllData(DBConst.DB_TABLE_DRIECT1);
				driectDBhelper.deleteAllData(DBConst.DB_TABLE_DRIECT2);
				driectDBhelper.deleteAllData(DBConst.DB_TABLE_DRIECT3);
				driectDBhelper.deleteAllData(DBConst.DB_TABLE_DRIECT4);
				
				SharedPreferences preferences = getSharedPreferences("ShiPanLogin", Context.MODE_PRIVATE);
				preferences.edit().clear().commit();
				ShiPanSelectActivity.this.finish();
				dialog.cancel();
			}
		});
		
		outInQuXiao.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				dialog.cancel();
			}
		});
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	}

	/**
	 * 直播室信息接口 网络请求
	 */
	public void select() {
		new Thread(new Runnable() {

			@SuppressLint("ShowToast")
			public void run() {
				String url = Const.HZWY_URL + "type=roomstatus&cookieid=" + selectCookieid;
				Log.i("temp", "直播室信息接口 网络请求-->>" + url);
				try {
					Message msg 	= selectHandler.obtainMessage();
					msg.obj 		= selectUtil.getTanGu_post(url);
					selectHandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	Handler selectHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			selectBeanList = (List<ShiPanTanGuBean>) msg.obj;
			selectList.setAdapter(new ShiPanSelectAdapter(ShiPanSelectActivity.this, selectBeanList));
			selectDialog.cancel();
		};
	};

	class ShiPanSelectAdapter extends BaseAdapter {

		private Context 						context;
		private LayoutInflater 					inflater;
		private List<ShiPanTanGuBean> 			selectBean;

		public ShiPanSelectAdapter(Context context, List<ShiPanTanGuBean> pointBeanList) {
			this.context 				= context;
			this.selectBean 			= pointBeanList;
			inflater	 				= LayoutInflater.from(context);
		}

		public int getCount() {
			return selectBean.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		@SuppressLint("ResourceAsColor")
		@SuppressWarnings("deprecation")
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.shipan_select_item, parent, false);
			}
			LinearLayout layout_button_dayingjia = CommonUtil.get(convertView, R.id.layout_button_dayingjia);
			LinearLayout layout_dayingjia 		= CommonUtil.get(convertView, R.id.layout_dayingjia);
			TextView button_dayingjia 			= CommonUtil.get(convertView, R.id.button_dayingjia);
			TextView button_dayingjia1			= CommonUtil.get(convertView, R.id.button_dayingjia1);
			TextView theme_dayingjia 			= CommonUtil.get(convertView, R.id.text_theme);
			TextView viewPoint_dayingjia		= CommonUtil.get(convertView, R.id.text_viewpoint);
			TextView analyst_dayingjia			= CommonUtil.get(convertView, R.id.text_analyst);
			String listName 					= selectBean.get(position).getName();
			if (position % 2 == 0) {
				button_dayingjia.setText(listName);
				button_dayingjia.setVisibility(View.VISIBLE);
				button_dayingjia1.setVisibility(View.GONE);
			} else {
				button_dayingjia1.setText(listName);
				button_dayingjia.setVisibility(View.GONE);
				button_dayingjia1.setVisibility(View.VISIBLE);
			}
			theme_dayingjia.setText("主    题：" + selectBean.get(position).getSubject());
			viewPoint_dayingjia.setText("观    点：" + selectBean.get(position).getPoint());
			analyst_dayingjia.setText("分析师：" + selectBean.get(position).getUsername());
			for (int i = 0; i < 4; i++) {
				layout_dayingjia.setBackgroundDrawable(getResources().getDrawable(selectImage[position]));
			}
			layout_button_dayingjia.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					if ("open".equals(selectBean.get(position).getStatus())) {
						Intent intent = new Intent(ShiPanSelectActivity.this, ShiPanInteractionHomeActivity.class);
						intent.putExtra("name", selectBean.get(position).getName());
						intent.putExtra("username", selectBean.get(position).getUsername());
						intent.putExtra("subject", selectBean.get(position).getSubject());
						intent.putExtra("point", selectBean.get(position).getPoint());
						intent.putExtra("public_notice", selectBean.get(position).getPublic_notice());
						intent.putExtra("rid", selectBean.get(position).getRid());
						/* 登录界面的值 */
						intent.putExtra("groupid", selectGroupid);
						intent.putExtra("groupTitle", selectGroupTitle);
						intent.putExtra("grade", selectGrade);
						intent.putExtra("roomlevel", selectRoomlevel);
						intent.putExtra("cookieid", selectCookieid);
						intent.putExtra("uid", selectUid);
						if ("1".equals(selectBean.get(position).getAuthority())) {
							startActivity(intent);
						} else {
							if ("-1".equals(selectBean.get(position).getAuthority())) {
								hzwy_BaseActivity.permissionHint(R.string.tishi_zhibojian_kaifang1);
							} else if("-2".equals(selectBean.get(position).getAuthority())) {
								hzwy_BaseActivity.permissionHint(R.string.tishi_zhibojian_kaifang2);
							} else if("-3".equals(selectBean.get(position).getAuthority())) {
								hzwy_BaseActivity.permissionHint(R.string.tishi_zhibojian_kaifang3);
							} else if("-4".equals(selectBean.get(position).getAuthority()) || "-5".equals(selectBean.get(position).getAuthority())){
								hzwy_BaseActivity.permissionHint(R.string.tishi_zhibojian_kaifang4);
							}
						}
					} else {
						hzwy_BaseActivity.HZWY_Toast1(selectBean.get(position).getName() + "直播室现已关闭");
					}
				}
			});
			return convertView;
		}
	}
	
	
	
	/*********************************************获取实盘直播列表弹窗********开始************************************************/
	public void SelectDialog(){
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
		loginMessage.setText(R.string.tishi_shipan_list);
		loginMessage.setVisibility(View.VISIBLE);
		loginLayout.setVisibility(View.GONE);
		selectDialog 							= new AlertDialog.Builder(this).setView(textEntryView).create();
		selectDialog.show();
	}
	/**********************************************获取实盘直播列表弹窗********结束***********************************************/

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
