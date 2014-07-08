package com.hzwydyj.finace.fragments;

import java.io.File;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
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
import com.hzwydyj.finace.db.DBConst;
import com.hzwydyj.finace.db.DBManager;
import com.hzwydyj.finace.utils.MSG_WHAT;
import com.hzwydyj.finace.utils.MyLogger;
import com.hzwydyj.finace.utils.ScreenShot;

public class News_keeped_Detail_Fragment extends Fragment {

	public News_keeped_Detail_Fragment() {
	}

	private MyLogger log = MyLogger.yLog();

	private TextView autherText;
	// 数据取得用dialog
	private ProgressDialog progressDialog;
	// 详细内容地址
	// private String newsid;
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

	/** 进度条 */
	private ProgressBar pb;
	SharedPreferences sharedPreferences;
	private ScrollView newsdetial_scrollview;

	public void onResume() {
		super.onResume();
		// log.i("onResume");
		checkNewFunction();
	}

	private void checkNewFunction() {
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
	}

	public void onPause() {
		super.onPause();
		dbManager.closeDB();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (dbManager != null) {
			dbManager.closeDB();
		}
	}

	Animation hyperspaceJumpAnimation;

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	private String news_id = "";
	String SavePath;
	private boolean idinKeepDB = false;

	// private ListView testlist;
	private ImageView im_keep;
	private TextView tv_keep;
	private LinearLayout screenshot;
	private LinearLayout keep;
	private DBManager dbManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// log.i("onCreateView");
		View view = inflater.inflate(R.layout.infordetailview_viewpager, container, false);
		
		hzwy_BaseActivity = new HZWY_BaseActivity(MyApplication.CONTEXT);
		// 几个参数
		news_id = getArguments().getString("newsid");
		// log.i("需要传入数据库的newsid=" + news_id);

		title = (TextView) view.findViewById(R.id.newsDetailTitle);
		time = (TextView) view.findViewById(R.id.newsDetailTime);
		detail = (TextView) view.findViewById(R.id.newsDetail);
		registerForContextMenu(detail);
		hyperspaceJumpAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.addkeep);

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
				shareScreenShot();
			}
		});

		keep = (LinearLayout) view.findViewById(R.id.keep);
		keep.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// 保存到数据库里面
				// dbManager.closeDB();
				// dbManager = new DBManager(getActivity());
				dbManager.openDB();
				keep.setClickable(false);
				// 查一下是否已经有这个ID 有的话 可以跳过存入
				if (idinKeepDB) {// 在数据库

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

					dbManager.add(keep);
					dbManager.closeDB();

					tv_keep.setText("已收藏");
					im_keep.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_action_important_keeped));
					im_keep.startAnimation(hyperspaceJumpAnimation);
					hzwy_BaseActivity.HZWY_Toast1("添加至《我的收藏》!");
				}

				idinKeepDB = idinKeepDB == true ? false : true;
				keep.setClickable(true);
			}
		});

		getNewsDetail();
		return view;

	}

	/**
	 * 分享新闻图片
	 */
	private void shareScreenShot() {
		SavePath = getSDCardPath() + "/Pictures/screen_" + news_id + ".png";
		MakePNGandShare mytask = new MakePNGandShare();
		mytask.execute(null, null, null);
	}

	boolean shootOK = false;

	class MakePNGandShare extends AsyncTask<String, Void, Void> {

		Bitmap bitmap = null;
		ScreenShot screenshot;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			screenshot = new ScreenShot();
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
		// log.i("getDetail()");

		// 从数据库里面拿数据
		// dbManager = new DBManager(getActivity());
		dbManager.openDB();
		Cursor c = dbManager.queryCursorBynewsID(news_id);
		while (c.moveToNext()) {

			NewsKeep keepitem = new NewsKeep();
			// keepitem._id = arg1.getInt(arg1.getColumnIndex("_id"));
			// keepitem.setNews_id(arg1.getString(arg1.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWS_ID_COLUMN)));
			// log.i("setNews_id->" + arg1.getString(arg1.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWS_ID_COLUMN)));

			keepitem.setNewstime(c.getString(c.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWS_TIME_COLUMN)));
			// log.i("setNewstime->" + arg1.getString(arg1.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWSTIME_COLUMN)));

			keepitem.setNewstitle(c.getString(c.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWS_TITLE_COLUMN)));
			// log.i("setNewstitle->" + arg1.getString(arg1.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWSTITLE_COLUMN)));

			keepitem.setNewscontent(c.getString(c.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWS_CONTENT_COLUMN)));
			// log.i("setNewscontent->" + arg1.getString(arg1.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWSCONTENT_COLUMN)));

			keepitem.setAuthor(c.getString(c.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWSAUTHOR_COLUMN)));
			// log.i("setAuthor->" + arg1.getString(arg1.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWSAUTHOR_COLUMN)));

			keepitem.setType(c.getString(c.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWSTYPE_COLUMN)));
			// log.i("setType->" + arg1.getString(arg1.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWSTYPE_COLUMN)));

			// newsData.add(keepitem);
			timeValue = keepitem.getNewstime();
			titleValue = keepitem.getNewstitle();
			author = keepitem.getAuthor();
			detailValue = keepitem.getNewscontent();
			detailValue = detailValue.trim();

		}
		dbManager.closeDB();

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
		// log.i("初始化progerssDialog");
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
				}
			} else {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}
		}
	}

	private void showNewFunction() {
		new AlertDialog.Builder(getActivity()).setTitle("新功能").setCancelable(true).setMessage("左右滑动可以查看前后新闻条目,\n点击分享可以分享新闻长图。")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//
					}
				}).create().show();
	}
}
