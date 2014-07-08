package com.hzwydyj.finace.service;

import java.lang.reflect.Field;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.IntentService;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.activitys.MainActivity;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.ShiPanTanGuBean;
import com.hzwydyj.finace.db.DBConst;
import com.hzwydyj.finace.db.DriectDBhelper;
import com.hzwydyj.finace.utils.Util;

public class HuaZongWeiYeService extends IntentService {

	public HuaZongWeiYeService() {
		super("HuaZongWeiYeService");
	}

	public HuaZongWeiYeService(String name) {
		super(name);

	}

	private Util homeUtil;
	private String serviceRid;
	private String serviceName;
	private String url;

	@SuppressWarnings("static-access")
	@Override
	protected void onHandleIntent(final Intent intent) {
		serviceRid = intent.getStringExtra("rid");
		serviceName = intent.getStringExtra("name");
		Log.i("LOG", "serviceRid:" + serviceRid + "   serviceName:" + serviceName);
		homeUtil = new Util();
		try {
			url = homeUtil.getStatus_post(Const.HZWY_URL + "type=livestatus&rid=" + serviceRid);
			ShiPanTanGuBean shiPanTanGuBean = new ShiPanTanGuBean();
			JSONArray jsonArray = new JSONArray(url);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String status = jsonObject.getString("status");
				Log.i("LOG", "status:" + status);
				shiPanTanGuBean.setStatus(status);
			}
			
			if ("stop".equals(shiPanTanGuBean.getStatus())) {
				DriectDBhelper driectDBhelper = new DriectDBhelper(MyApplication.CONTEXT);
				driectDBhelper.deleteAllData(DBConst.DB_TABLE_DRIECT1);
				driectDBhelper.deleteAllData(DBConst.DB_TABLE_DRIECT2);
				driectDBhelper.deleteAllData(DBConst.DB_TABLE_DRIECT3);
				driectDBhelper.deleteAllData(DBConst.DB_TABLE_DRIECT4);
			}
			
			if ("stop".equals(shiPanTanGuBean.getStatus()) || "pause".equals(shiPanTanGuBean.getStatus()) || "leave".equals(shiPanTanGuBean.getStatus())) {

				Intent intent3 = new Intent();
				intent3.setAction("com.huazongweiye.finance.activity");
				sendBroadcast(intent3);

				Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
				intent2.putExtra("content", serviceName + "直播室现在已经停止直播!");
				intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent2);
				
				/*SharedPreferences preferences = getApplication().getSharedPreferences("CitiGame.ini", 0);
				Boolean user_first = preferences.getBoolean("FIRST", true);
				Log.i("LOG", "user_first:" + user_first);
				 if (user_first) {
				//if (true) {
					preferences.edit().putBoolean("FIRST", false).commit();

					LayoutInflater factory = LayoutInflater.from(getApplicationContext());
					final View textEntryView = factory.inflate(R.layout.shipan_share_dialog, null);

					TextView homeTitle = (TextView) textEntryView.findViewById(R.id.share_title_text);
					TextView homeContent = (TextView) textEntryView.findViewById(R.id.share_content_text);
					Button homeQueren = (Button) textEntryView.findViewById(R.id.share_queren_button);
					// 提示
					homeTitle.setText(R.string.tishi_warm_prompt);
					homeContent.setText(serviceName + "直播室现在已经停止直播!");

					AlertDialog homeDialog = new AlertDialog.Builder(getApplication()).setView(textEntryView).create();
					homeDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
					homeDialog.setCanceledOnTouchOutside(false);

					homeQueren.setOnClickListener(new OnClickListener() {

						@SuppressWarnings("static-access")
						public void onClick(View v) {
							Log.i("LOG", "---------");
							SharedPreferences preferences = getApplication().getSharedPreferences("CitiGame.ini", 0);
							preferences.edit().clear().commit();
							// homeDialog.dismiss();
							Log.i("LOG", "---------");
							Intent intent3 = new Intent();
							intent3.setAction("com.huazongweiye.finance.activity");
							sendBroadcast(intent3);

							Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
							intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(intent2);
						}
					});
				}*/
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
