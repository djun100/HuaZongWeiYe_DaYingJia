package com.hzwydyj.finace.activitys;

import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
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
import com.hzwydyj.finace.present.view.TransView;
import com.hzwydyj.finace.utils.ManifestData;
import com.hzwydyj.finace.utils.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * 客户服务
 * 
 * @author LuoYi
 * 
 */
public class HelpView extends Activity {

	TextView 			app_version;
	private int 		secret_count;
	private AlertDialog dialog;

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

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	Random 				random;
	TextView 			directions2, 
						directions3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainview2);
		
		hzwy_BaseActivity = new HZWY_BaseActivity(MyApplication.CONTEXT);
		
		ImageView secret = (ImageView) findViewById(R.id.secret);
		secret.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("static-access")
			public void onClick(View arg0) {
				secret_count++;
				if (secret_count == 10) {
					ManifestData mData 		= new ManifestData();
					String UMENG_APPKEY 	= mData.getString(getApplicationContext(), "UMENG_APPKEY");
					int versionCode 		= mData.getVersionCode();
					String packagename 		= mData.getPackageName();
					String versionName 		= mData.getVersionName();
	
					hzwy_BaseActivity.HZWY_Toast1("包名：" + packagename + "\n" + "友盟：" + UMENG_APPKEY + "\n" + "版本名称：" + versionName + "\n" + "版本代码："
									+ versionCode + "\n" + "升级地址：" + Const.APP_VersionCheckURL);
				}
			}
		});

		app_version = (TextView) findViewById(R.id.app_version);
		app_version.setText("版本号：" + Const.APP_Version);
		directions2 = (TextView) findViewById(R.id.directions2);
		directions3 = (TextView) findViewById(R.id.directions3);

		directions2.setText(R.string.service_company_website);
		directions3.setText(R.string.service_dayingjia_website);

		random = new Random();
		changeColor();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.about_01:
			startActivity(new Intent(HelpView.this, TransView.class));
			break;
		case R.id.about_02:
			break;
		case R.id.about_03:
			support();
			break;
		case R.id.about_04:
			email();
			break;
		case R.id.about_05:
			// 异步版本检查
			if (isNetworkConnected(this)) {
				checkVersion();
			} else {
				showNoConn();
			}
			break;

		case R.id.backbtn:
			HelpView.this.finish();
			break;
		case R.id.app_version:

			changeColor();

			break;
		case R.id.directions2:
			Uri uri = Uri.parse("http://www.hzwychina.com/");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
			break;
		case R.id.directions3:
			Uri uri2 = Uri.parse("http://www.91baiyin.com.cn/index.html");
			Intent intent2 = new Intent(Intent.ACTION_VIEW, uri2);
			startActivity(intent2);
			break;
		}
	}

	private String suppoerPhone = "4000-720-760";

	private void support() {
		
		LayoutInflater factory 					= LayoutInflater.from(this);
		final View textEntryView 				= factory.inflate(R.layout.shipan_share_dialog, null);
		
		RelativeLayout title 					= (RelativeLayout) textEntryView.findViewById(R.id.title_layout);
		TextView Content 						= (TextView) textEntryView.findViewById(R.id.share_content_text);
		Button queren 							= (Button) textEntryView.findViewById(R.id.share_queren_button);
		Button quxiao 							= (Button) textEntryView.findViewById(R.id.share_quxiao_button);
		
		title.setVisibility(View.GONE);
		Content.setText(R.string.service_dayingjia_dial);
		queren.setText(R.string.tishi_dial);
		quxiao.setText(R.string.regiter_caler);
		quxiao.setVisibility(View.VISIBLE);
		
		final AlertDialog dialog = new AlertDialog.Builder(this).setView(textEntryView).create();
		dialog.show();
		
		queren.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_DIAL);
				i.setData(Uri.parse("tel:" + suppoerPhone));
				startActivity(i);
				dialog.cancel();
			}
		});
		
		quxiao.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				dialog.cancel();
			}
		});
	}

	/**
	 * 意见反馈
	 */
	private void email() {
		try {

			// SENDTO方式
			Uri uri 		= Uri.parse("mailto:service@hzwychina.com");
			String[] email 	= { "service@hzwychina.com" };
			Intent intent 	= new Intent(Intent.ACTION_SENDTO, uri);
			intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
			intent.putExtra(Intent.EXTRA_SUBJECT, R.string.service_dayingjia_feedback); // 主题
			intent.putExtra(Intent.EXTRA_TEXT, R.string.service_dayingjia_opinion); // 正文
			startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
		} catch (Exception e) {
			LayoutInflater factory 				= LayoutInflater.from(this);
			final View textEntryView 			= factory.inflate(R.layout.shipan_share_dialog, null);
			TextView title 						= (TextView) textEntryView.findViewById(R.id.share_title_text);
			TextView content 					= (TextView) textEntryView.findViewById(R.id.share_content_text);
			Button queren 						= (Button) textEntryView.findViewById(R.id.share_queren_button);
			/* 提示*/
			title.setText(R.string.tishi);
			content.setText(R.string.tishi_mail_account);
			dialog = new AlertDialog.Builder(this).setView(textEntryView).create();
			dialog.show();
			queren.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					dialog.cancel();
				}
			});
		}
	}

	/**
	 * 版本号随机变色
	 */
	private void changeColor() {
		app_version.setTextColor(Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255)));
	}

	private void checkVersion() {

		MyTask mytask = new MyTask();
		mytask.execute(null, null, null);
	}

	private String version 	= "";
	private Util util 		= new Util();
	private HZWY_BaseActivity hzwy_BaseActivity;

	class MyTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			version 		= util.checkVersion(Const.APP_VersionCheckURL);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			String v = version.substring(0, 1);
			if ("1".equals(v)) {
				LayoutInflater factory 				= LayoutInflater.from(HelpView.this);
				final View textEntryView 			= factory.inflate(R.layout.shipan_share_dialog, null);
				TextView title 						= (TextView) textEntryView.findViewById(R.id.share_title_text);
				TextView content 					= (TextView) textEntryView.findViewById(R.id.share_content_text);
				Button queren 						= (Button) textEntryView.findViewById(R.id.share_queren_button);
				Button quxiao 						= (Button) textEntryView.findViewById(R.id.share_quxiao_button);
				
				/* 提示*/
				title.setText(R.string.tishi);
				content.setText(R.string.service_dayingjia_discover);
				queren.setText(R.string.service_dayingjia_gengxin);
				quxiao.setText(R.string.service_dayingjia_bugengxin);
				quxiao.setVisibility(View.VISIBLE);
				dialog = new AlertDialog.Builder(HelpView.this).setView(textEntryView).create();
				dialog.show();
				queren.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						Intent intent1 = new Intent(Intent.ACTION_VIEW);
						intent1.setData(Uri.parse(version.substring(2, version.length())));
						startActivity(intent1);
					}
				});
				quxiao.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						dialog.cancel();
					}
				});
				
			} else if ("2".equals(v)) {
				LayoutInflater factory 				= LayoutInflater.from(HelpView.this);
				final View textEntryView 			= factory.inflate(R.layout.shipan_share_dialog, null);
				TextView title 						= (TextView) textEntryView.findViewById(R.id.share_title_text);
				TextView content 					= (TextView) textEntryView.findViewById(R.id.share_content_text);
				Button queren 						= (Button) textEntryView.findViewById(R.id.share_queren_button);
				/* 提示*/
				title.setText(R.string.tishi);
				content.setText(R.string.service_dayingjia_zuixinbanben);
				queren.setText(R.string.service_dayingjia_gengxin);
				dialog = new AlertDialog.Builder(HelpView.this).setView(textEntryView).create();
				dialog.show();
				queren.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						Intent intent1 = new Intent(Intent.ACTION_VIEW);
						intent1.setData(Uri.parse(version.substring(2, version.length())));
						startActivity(intent1);
					}
				});
				
			} else if ("0".equals(v)) {
				hzwy_BaseActivity.HZWY_Toast1("已是最新版本");
			}
		}
	}

	/**
	 * 判断是否有网络连接
	 * 
	 * @param context
	 * @return
	 */
	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager 	= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo 					= mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 无网络连接提示
	 */
	private void showNoConn() {

		new AlertDialog.Builder(HelpView.this).setTitle("注意：").setCancelable(false).setMessage("无法检查更新,请检查网络连接")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//
					}
				}).create().show();
	}
}
