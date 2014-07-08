package com.hzwydyj.finace.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.service.SysApplication;

/**
 * 分析师介绍
 * 
 * @author LuoYi
 * 
 */
public class ShiPanIntroduceActivity extends Activity implements OnClickListener {

	private TextView introduceTitle;
	private TextView introduceTheme;
	private TextView introduceViewpoint;
	private TextView introduceTextNotice;
	private Button introduceEntrance;
	private Button introduceBack;
	private TextView introduceAnalyst;
	private String introduceRid;
	private String introduceName;
	private String introduceUsername;
	private String introduceSubject;
	private String introducePoint;
	private String introducePublic_notice;
	private String introduceStatus;
	private String introduceGroupid;
	private String introduceGroupTitle;
	private String introduceGrade;
	private String introduceRoomlevel;
	private String introduceCookieid;
	private String introduceUid;

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
		// TODO Auto-generated method stub 自动生成的方法存根
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		init();
		SysApplication.getInstance().addActivity(this);
	}

	public void init() {
		setContentView(R.layout.shipanintroduce);
		Intent intent = getIntent();
		/* 上个页面的 值 */
		introduceName = intent.getStringExtra("name");
		introduceUsername = intent.getStringExtra("username");
		introduceSubject = intent.getStringExtra("subject");
		introducePoint = intent.getStringExtra("point");
		introducePublic_notice = intent.getStringExtra("public_notice");
		introduceRid = intent.getStringExtra("rid");
		/* 登录页面的值 */
		introduceGroupid = intent.getStringExtra("groupid");
		introduceGroupTitle = intent.getStringExtra("groupTitle");
		introduceGrade = intent.getStringExtra("grade");
		introduceRoomlevel = intent.getStringExtra("roomlevel");
		introduceCookieid = intent.getStringExtra("cookieid");
		introduceUid = intent.getStringExtra("uid");
		Log.i("temp", "name-->" + introduceName + "\n" + "username-->" + introduceUsername + "\n" + "subject-->" + introduceSubject + "\n" + "point-->" + introducePoint + "\n" + "public_notice-->"
				+ introducePublic_notice + "\n" + "rid-->" + introduceRid + "\n" + "groupid-->" + introduceGroupid + "\n" + "groupTitle-->" + introduceGroupTitle + "\n" + "grade-->" + introduceGrade
				+ "\n" + "roomlevel-->" + introduceRoomlevel + "\n" + "cookieid-->" + introduceCookieid + "\n" + "uid-->>" + introduceUid);
		introduceTitle = (TextView) this.findViewById(R.id.text_title);
		introduceTheme = (TextView) this.findViewById(R.id.text_theme);
		introduceViewpoint = (TextView) this.findViewById(R.id.text_viewpoint);
		introduceAnalyst = (TextView) this.findViewById(R.id.text_analyst);
		introduceTextNotice = (TextView) this.findViewById(R.id.text_public_notice);
		introduceEntrance = (Button) this.findViewById(R.id.button_entrance);
		introduceBack = (Button) this.findViewById(R.id.button_backbtn);
		introduceEntrance.setOnClickListener(this);
		introduceBack.setOnClickListener(this);
		show();
	}

	public void show() {
		introduceTitle.setText(introduceName);
		introduceTheme.setText(introduceSubject);
		introduceViewpoint.setText(introducePoint);
		introduceAnalyst.setText(introduceUsername);
		introduceTextNotice.setText(introducePublic_notice);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_entrance:// 点击进入
			Intent intent = new Intent(ShiPanIntroduceActivity.this, ShiPanInteractionHomeActivity.class);
			intent.putExtra("name", introduceName);
			intent.putExtra("groupid", introduceGroupid);
			intent.putExtra("rid", introduceRid);
			intent.putExtra("roomlevel", introduceRoomlevel);
			intent.putExtra("cookieid", introduceCookieid);
			intent.putExtra("uid", introduceUid);
			startActivity(intent);
			ShiPanIntroduceActivity.this.finish();
			break;

		case R.id.button_backbtn:// 返回
			ShiPanIntroduceActivity.this.finish();
			break;

		default:
			break;
		}
	}

}
