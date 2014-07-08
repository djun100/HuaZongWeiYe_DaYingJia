package com.hzwydyj.finace.activitys;

import com.google.analytics.tracking.android.EasyTracker;
import com.hzwydyj.finace.R;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 内嵌视频浏览器
 * @author LuoYi
 *
 */
public class PointVideoWebActivity extends Activity implements OnClickListener {

	private WebView 			videoWebView;
	private String 				appaddr,subject;
	private Button 				videoBackButton;

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
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.point_video_web);
		
		Intent intent 		= getIntent();
		subject 			= intent.getStringExtra("subject");
		appaddr 			= intent.getStringExtra("appaddr");

		init();
		webHtml();
	}
	
	public void init() {
		videoBackButton		= (Button) findViewById(R.id.button_video_back);
		videoBackButton.setOnClickListener(this);
		TextView videoTitle = (TextView)findViewById(R.id.video_title);
		videoTitle.setText(subject);
		
		videoWebView 		= (WebView) findViewById(R.id.videoWebView);
		/* 支持网页有js*/
		videoWebView.getSettings().setJavaScriptEnabled(true);

		WebSettings webSettings 	= videoWebView.getSettings();
		/* 设置密度*/
		int screenDensity 			= getResources().getDisplayMetrics().densityDpi;
		WebSettings.ZoomDensity zoomDensity = WebSettings.ZoomDensity.MEDIUM;
		switch (screenDensity) {
		case DisplayMetrics.DENSITY_LOW:
			zoomDensity = WebSettings.ZoomDensity.CLOSE;
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			zoomDensity = WebSettings.ZoomDensity.MEDIUM;
			break;
		case DisplayMetrics.DENSITY_HIGH:
			zoomDensity = WebSettings.ZoomDensity.FAR;
			break;
		}
		webSettings.setDefaultZoom(zoomDensity);

		/* 点击继续停留在当前的browser中响应，而不是新开一个browser*/
		videoWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_video_back:
			PointVideoWebActivity.this.finish();
			break;

		default:
			break;
		}
	}

	private void webHtml() {
		try {
			videoWebView.loadUrl(appaddr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* 系统的back回退键重写，不会退处Activity*/
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && videoWebView.canGoBack()) {
			videoWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
