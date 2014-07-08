package com.hzwydyj.finace.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.data.Const;

/**
 * 
 * @author LuoYi
 *
 */
public class WebView_base extends Activity {
	private WebView 		webview;
	private Button 			button;
	private TextView 		title;
	String 					url;

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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_08);

		url 				= getIntent().getExtras().getString(Const.WEBVIEW_URL);
		String title_text 	= getIntent().getExtras().getString(Const.WEBVIEW_TITLE);

		title 				= (TextView) findViewById(R.id.title);
		title.setText(title_text);

		webview 			= (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith("tel:")) {
					startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
					return true;
				} else if (url.startsWith("mailto:")) {
					url 		= url.replaceFirst("mailto:", "");
					url 		= url.trim();
					Intent i 	= new Intent(Intent.ACTION_SEND);
					i.setType("plain/text").putExtra(Intent.EXTRA_EMAIL, new String[] { url });
					startActivity(i);
					return true;
				} else if (url.startsWith("geo:")) {
					Intent searchAddress = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(searchAddress);
					return true;
				} else {
					view.loadUrl(url);
					return true;
				}
			}
		});

		webview.setWebChromeClient(new MyWebChromeClient());
		webview.getSettings().setSupportZoom(true);
		webview.getSettings().setBuiltInZoomControls(true);
		webview.getSettings().setUseWideViewPort(true);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.setDownloadListener(new MyWebViewDownLoadListener());

		button = (Button) findViewById(R.id.Btn_fanhui);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		try {
			webview.loadUrl(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 系统的back回退键重写，不会退处Activity
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
			webview.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	final Context myApp = this;

	final class MyWebChromeClient extends WebChromeClient {
		public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
			new AlertDialog.Builder(myApp).setTitle("提示：").setMessage(message)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							result.confirm();
						}
					}).create().show();
			return true;
		};

		public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
			new AlertDialog.Builder(myApp).setTitle("提示：").setMessage(message)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							result.confirm();
						}
					}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							result.cancel();
						}
					}).create().show();

			return true;
		}
	}

	private class MyWebViewDownLoadListener implements DownloadListener {

		public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
			//log.i("tag", "url=" + url);
			//log.i("tag", "userAgent=" + userAgent);
			//log.i("tag", "contentDisposition=" + contentDisposition);
			//log.i("tag", "mimetype=" + mimetype);
			//log.i("tag", "contentLength=" + contentLength);
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
	}
}
