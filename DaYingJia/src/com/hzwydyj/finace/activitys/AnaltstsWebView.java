package com.hzwydyj.finace.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;

import com.hzwydyj.finace.R;

/**
 * 
 * @author LuoYi
 *
 */
public class AnaltstsWebView extends Activity implements OnClickListener {
	private WebView 			awebview;
	private String 				webViewURL = "http://down.fx678.com/hzwy/0402.pdf";

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.analysts);

		awebview 				= (WebView) findViewById(R.id.analystsWebView);
		awebview.getSettings().setJavaScriptEnabled(true);// 支持网页有js

		Button yingjiabackbtn 	= (Button) this.findViewById(R.id.yingjiabackbtn);
		yingjiabackbtn.setOnClickListener(this);
		webHtml();
	}

	private void webHtml() {
		try {
			awebview.loadUrl("http://docs.google.com/gview?embedded=true&url=" + webViewURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.yingjiabackbtn:
			AnaltstsWebView.this.finish();
			break;

		default:
			break;
		}
	}

	// 系统的back回退键重写，不会退处Activity
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && awebview.canGoBack()) {
			awebview.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
