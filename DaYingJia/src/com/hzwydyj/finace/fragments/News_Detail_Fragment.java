package com.hzwydyj.finace.fragments;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.activitys.HZWY_BaseActivity;
import com.hzwydyj.finace.data.NewsKeep;
import com.hzwydyj.finace.db.DBManager;
import com.hzwydyj.finace.utils.MSG_WHAT;
import com.hzwydyj.finace.utils.MyLogger;
import com.hzwydyj.finace.utils.ScreenShot;

public class News_Detail_Fragment extends Fragment {

	public News_Detail_Fragment() {
	}

	private DBManager dbManager;

	private MyLogger log = MyLogger.yLog();

	private TextView autherText;
	// 数据取得用dialog
	private ProgressDialog progressDialog;
	// 详细内容地址
	private String url;
	// 详细内容标题组件
	private TextView title;
	// 详细内容发表时间组件
	private TextView time;
	// 详细内容组件
	private TextView detail;
	// 详细内容标题
	private String titleValue;
	// 详细内容发表时间
	private String timeValue;
	// 详细内容
	private String detailValue;
	// 详细内容
	private String author;
	// 异常语句
	private String errorMsg = "";
	// 论坛监听按钮
	private Button btn_i;

	/** 进度条 */
	private ProgressBar pb;
	SharedPreferences sharedPreferences;
	private ScrollView newsdetial_scrollview;

	public void onResume() {
		super.onResume();
		// log.i("onResume");

		sharedPreferences = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
		int i = sharedPreferences.getInt("news_detail_introduction", 0);
		if (i == 0) {
			showNewFunction();
		}
		i++;
		if (i == Integer.MAX_VALUE - 1) {
			i = 0;
		}
		Editor editor = sharedPreferences.edit();
		editor.putInt("news_detail_introduction", i);
		editor.commit();

		// MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		dbManager.closeDB();
		// MobclickAgent.onPause(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (dbManager != null) {
			dbManager.closeDB();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	private String news_id = "";
	String SavePath;
	private boolean idinKeepDB = false;
	private ImageView im_keep;
	private TextView tv_keep;
	LinearLayout screenshot;
	LinearLayout keep;

	Animation hyperspaceJumpAnimation;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// log.i("onCreateView");
		View view = inflater.inflate(R.layout.infordetailview_viewpager, container, false);
		// 几个参数
		url = getArguments().getString("link");
		// log.i("link->" + url);
		// 寻找id后面的
		int id_index = url.indexOf("id=");
		news_id = url.substring(id_index + 3, url.length());
		hyperspaceJumpAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.addkeep);

		title = (TextView) view.findViewById(R.id.newsDetailTitle);
		time = (TextView) view.findViewById(R.id.newsDetailTime);
		detail = (TextView) view.findViewById(R.id.newsDetail);
		registerForContextMenu(detail);

		autherText = (TextView) view.findViewById(R.id.author);
		pb = (ProgressBar) view.findViewById(R.id.pb);
		tv_keep = (TextView) view.findViewById(R.id.tv_keep);
		im_keep = (ImageView) view.findViewById(R.id.im_keep);

		dbManager = new DBManager(getActivity());
		idinKeepDB = dbManager.query(news_id);
		dbManager.closeDB();
		if (idinKeepDB) {
			im_keep.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_action_important_keeped));
			tv_keep.setText("已收藏");
		} else {
			im_keep.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_action_important));
			tv_keep.setText("收藏");
		}

		initProgressD();
		// progressD_state(true);

		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
			pb.setVisibility(View.GONE);
		}

		// ///////////////////////////////
		// // 异步查询ID是否在keep数据库中
		// ///////////////////////////////

		newsdetial_scrollview = (ScrollView) view.findViewById(R.id.newsdetial_scrollview);

		screenshot = (LinearLayout) view.findViewById(R.id.screenshot);
		screenshot.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// log.i("点击screenshot");
				SavePath = getSDCardPath() + "/Pictures/screen_" + news_id + ".png";
				getTCPDataTask();

			}
		});

		keep = (LinearLayout) view.findViewById(R.id.keep);
		keep.setOnClickListener(new OnClickListener() {
			public synchronized void onClick(View arg0) {
				// 保存到数据库里面
				keep.setClickable(false);
				// 如果数据库里面有
				if (idinKeepDB) {// 在数据库
					// dbManager = new DBManager(getActivity());
					dbManager.openDB();
					hzwy_BaseActivity.HZWY_Toast1("取消收藏！");
					dbManager.deleteItem(news_id);
					dbManager.closeDB();
					tv_keep.setText("收藏");
					im_keep.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_action_important));

				} else {
					NewsKeep keep = new NewsKeep();
					keep.setAuthor(author);
					// log.i("author->" + author);

					keep.setNews_id(news_id);
					// log.i("news_id->" + news_id);

					keep.setNewscontent(detailValue);
					// log.i("detailValue->" + detailValue);

					keep.setNewstime(timeValue);
					// log.i("timeValue->" + timeValue);

					keep.setNewstitle(titleValue);
					// log.i("titleValue->" + titleValue);

					keep.setType("新闻");// 传入的新闻类型

					dbManager.openDB();
					// dbManager = new DBManager(getActivity());
					dbManager.add(keep);
					dbManager.closeDB();
					tv_keep.setText("已收藏");
					// main.xml中的ImageView
					// 加载动画

					// 使用ImageView显示动画
					im_keep.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_action_important_keeped));
					im_keep.startAnimation(hyperspaceJumpAnimation);
					hzwy_BaseActivity.HZWY_Toast1("添加至《我的收藏》!");
				}

				idinKeepDB = idinKeepDB == true ? false : true;
				keep.setClickable(true);
			}
		});

		getNewsDetail();
		
		hzwy_BaseActivity = new HZWY_BaseActivity(MyApplication.CONTEXT);
		return view;

	}

	private void getTCPDataTask() {
		GetTCPDataTask mytask = new GetTCPDataTask();
		mytask.execute(null, null, null);
	}

	boolean shootOK = false;

	class GetTCPDataTask extends AsyncTask<String, Void, Void> {

		Bitmap bitmap = null;
		ScreenShot screenshot;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			screenshot = new ScreenShot();
			// progressD_state(true);
			hzwy_BaseActivity.HZWY_Toast1("长图生成中，请稍候");
			bitmap = screenshot.getBitmapByView(newsdetial_scrollview);
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				// 判断SDcard是否存在并且可读写
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					screenshot.savePic(bitmap, SavePath);
					shootOK = true;
				} else {
					hzwy_BaseActivity.HZWY_Toast1("长图生成失败，请检查SD卡");
					// log.i("打开SD卡失败");
					shootOK = false;
				}

			} catch (Exception e) {
				progressD_state(false);
				e.printStackTrace();
				// sendMessagewhat(MSG_WHAT.ASYNCTASK_FAIL);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressD_state(false);
			// sendMessagewhat(MSG_WHAT.SHOW_TOPPRICE);
			if (shootOK) {
				Intent shareIntent = new Intent(Intent.ACTION_SEND);
				File file = new File(SavePath);
				shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
				shareIntent.setType("image/jpeg");
				startActivity(Intent.createChooser(shareIntent, "选择分享应用"));
			}
		}
	}

	private void sendMessagewhat(int what) {
		handler.sendEmptyMessage(what);
	}

	// 取得详细数据
	public void getDetail() throws Exception {
		Document doc = null;
		try {
			URL inforUrl = new URL(url);
			URLConnection ucon = inforUrl.openConnection();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			doc = builder.parse(ucon.getInputStream());
		} catch (Exception e) {
			throw e;
		}
		titleValue = doc.getElementsByTagName("NewsTitle").item(0).getFirstChild().getNodeValue();
		timeValue = doc.getElementsByTagName("NewsTime").item(0).getFirstChild().getNodeValue();
		author = doc.getElementsByTagName("author").item(0).getFirstChild().getNodeValue();
		detailValue = doc.getElementsByTagName("NewsContent").item(0).getFirstChild().getNodeValue();
		detailValue = detailValue.trim();
		detailValue = detailValue.replace("(本文结束 来自:汇通网 fx678.com)", "");

	}

	private void getNewsDetail() {
		MyListTask mytask = new MyListTask();
		mytask.execute(null, null, null);
	}

	class MyListTask extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				getDetail();
			} catch (Exception e) {
				e.printStackTrace();
				sendMessagewhat(MSG_WHAT.ASYNCTASK_FAIL);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			sendMessagewhat(MSG_WHAT.SHOW_LIST);
		}
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MSG_WHAT.SHOW_LIST:
				pb.setVisibility(View.GONE);

				title.setText(titleValue);
				time.setText(timeValue);
//				autherText.setText("大赢家");
				detail.setText(Html.fromHtml(detailValue));
				detail.setMovementMethod(ScrollingMovementMethod.getInstance());

				screenshot.setVisibility(View.VISIBLE);
				keep.setVisibility(View.VISIBLE);

				break;

			default:
				break;
			}

		}
	};

	private HZWY_BaseActivity hzwy_BaseActivity;

	/**
	 * Dialog提示
	 */
	private void showNewFunction() {
		LayoutInflater factory 					= LayoutInflater.from(getActivity());
		final View textEntryView 				= factory.inflate(R.layout.shipan_share_dialog, null);
		
		TextView Title = (TextView) textEntryView.findViewById(R.id.share_title_text);
		ProgressBar Progess = (ProgressBar) textEntryView.findViewById(R.id.progress_bar);
		TextView Content = (TextView) textEntryView.findViewById(R.id.share_content_text);
		TextView Message = (TextView) textEntryView.findViewById(R.id.share_message_text);
		Button queren = (Button) textEntryView.findViewById(R.id.share_queren_button);
		
		Title.setText("新功能");
		Progess.setVisibility(View.GONE);
		Content.setVisibility(View.VISIBLE);
		Content.setText("左右滑动可以查看前后新闻条目,\n点击分享可以分享新闻长图。\n点击收藏可以保存到《我的收藏》并离线查看。");
		Message.setVisibility(View.GONE);
		
		final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(textEntryView).create();
		dialog.show();
		
		queren.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
	}

	/**
	 * 获取SDCard的目录路径功能
	 */
	private String getSDCardPath() {
		File sdcardDir = null;
		// 判断SDCard是否存在
		boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if (sdcardExist) {
			sdcardDir = Environment.getExternalStorageDirectory();
		}
		if (sdcardDir == null) {
			hzwy_BaseActivity.HZWY_Toast1("没有查找到SD卡");
			return "";
		} else {
			return sdcardDir.toString();
		}
	}

	private void initProgressD() {
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("长图生成中,请稍候...");
		progressDialog.setCancelable(true);
	}

	/**
	 * 进度条显示或隐藏
	 * 
	 * @param isshow
	 */
	private void progressD_state(boolean isshow) {
		if (progressDialog != null) {
			if (isshow) {
				if (!progressDialog.isShowing()) {
					progressDialog.show();
					// log.i("Dialog.show()");
				}
			} else {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
					// log.i("Dialog.dismiss()");
				}
			}
		}
	}
}
