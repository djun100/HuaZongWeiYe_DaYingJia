package com.hzwydyj.finace.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hzwydyj.finace.R;
import com.hzwydyj.finace.activitys.NewsDetailView_ViewPager;
import com.hzwydyj.finace.data.InforPojo;
import com.hzwydyj.finace.utils.DensityUtil;
import com.hzwydyj.finace.utils.MSG_WHAT;
import com.hzwydyj.finace.utils.MyLogger;
import com.hzwydyj.finace.utils.Util;

public class News_Fragment extends Fragment {

	public News_Fragment() {
	}

	private String URL_NEWS = null;

	/** 下拉刷新条件2 一次touch只能提交一次 */
	private boolean hasOne = false;
	/** 下拉刷新条件1 */
	private boolean nowAtTop = true;
	/** 下拉刷新计数器 */
	private static int canpulltoreflesh_count = 0;
	/** 下拉刷新距离记录 */
	private float touch_move;

	/** 下拉位置记录 */
	private float mDownY;
	/** 下拉移动距离 */
	private float mMoveY;
	/** 列表下拉刷新进度条 */
	private ProgressBar pbl;
	private ProgressBar pbr;
	private ProgressBar pb;

	private MyLogger log = MyLogger.yLog();
	private ListView newslistview;
	/** 数据取得工具类 */
	private Util util;
	/** 新闻数据list */
	private List<InforPojo> newsData;
	/** 新闻数据Map */
	List<Map<String, Object>> list;
	/** list数据列名 */
	private final String DATA_NEWSTYPE = "newsType";
	/** 新闻类型 */
	private final String DATA_NEWSHEAD = "newsTypeHead";
	/** 新闻类型时间 */
	private final String DATA_NEWSTIME = "newsTypeTime";
	/** 无图适配器 */
	private Myadapter adapter;

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case 1:
				// 提示数据加载错误
				errorDialog("异常", what);
				break;
			case MSG_WHAT.LIST_REFRESH:
				// 提示数据加载错误
				// errorDialog("异常", what);
				if (nowAtTop == true && hasOne == false) {
					// canpulltoreflesh_count++;
					doTask(URL_NEWS);
					hasOne = true;
					// doDetailContentTask(news_url);
				}
				nowAtTop = false;

				break;

			default:
				break;
			}

		}
	};

	@Override
	public void onPause() {
		super.onPause();
		//Log.i("temp", "F onPause");
		pb.setVisibility(View.GONE);
	}

	@Override
	public void onStop() {
		super.onStop();
		//Log.i("temp", "F onStop");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		//Log.i("temp", "F onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//Log.i("temp", "F onDestroy");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		//Log.i("temp", "F onDetach");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Log.i("temp", "F onCreateView");
		URL_NEWS = getArguments().getString("URL_NEWS");
		View view = inflater.inflate(R.layout.tab_news_f, container, false);
		// log.i("onCreate");

		// 初始化控件
		// initRadioG(view);
		initListView(view);
		// initProgressDialog();
		// 执行第一个后台任务 初始化数据
		// doTask(URL_NEWS);
		// doTask("http://boce.hgold.cn/boce/newslist.asp?page=1&count=20&type=syhg");

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		util = new Util();
		doTask(URL_NEWS);
		addPulltoReflesh();
	}

	private void initListView(View view) {
		newslistview = (ListView) view.findViewById(R.id.newslistview);
		pb = (ProgressBar) view.findViewById(R.id.pb);
		pbr = (ProgressBar) view.findViewById(R.id.pbr);
		pbl = (ProgressBar) view.findViewById(R.id.pbl);
	}

	// 点击后的查询任务
	private void doTask(String url) {
		// log.i("doTask");
		MyTask mytask = new MyTask();
		mytask.execute(url, null, null);
	}

	class MyTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			try {
				log.i(params[0]);
				newsData = util.getInforByXML(params[0]);
				if (newsData != null && newsData.size() > 25) {
					newsData = newsData.subList(0, 25);
				}
			} catch (Exception e) {
				e.printStackTrace();
				sendM(handler, 1);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// 显示数据
			showList();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			newslistview.setVisibility(View.INVISIBLE);
			pb.setVisibility(View.VISIBLE);
			// progressD_state(true);
			// progressD_show();
			// 显示进度条
		}
	}

	private class Myadapter extends BaseAdapter {

		public Myadapter(Context context) {
			super();
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (position < 0 || newsData.size() <= 0)
				return null;

			if (convertView == null)
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.newslistitems, null);

			TextView title = (TextView) convertView.findViewById(R.id.newsHead);
			TextView time = (TextView) convertView.findViewById(R.id.newsTime);
			Map<String, ?> map = list.get(position);
			title.setText((String) map.get(DATA_NEWSHEAD));
			time.setText((String) map.get(DATA_NEWSTIME));

			return convertView;
		}

		public int getCount() {
			return list.size();
		}

		public Object getItem(int position) {
			return list.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

	}

	public void showList() {
		pb.setVisibility(View.GONE);
		newslistview.setVisibility(View.VISIBLE);

		if (newsData != null) {
			list = new ArrayList<Map<String, Object>>();
			Map<String, Object> map;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (int i = 0; i < newsData.size(); i++) {
				map = new HashMap<String, Object>();
				InforPojo inforPojo = newsData.get(i);
				Date inforPojoTime = new Date(inforPojo.getTime_());
				String DateInforPojoTime = format.format(inforPojoTime);
				map.put(DATA_NEWSHEAD, inforPojo.getTitle_());
				map.put(DATA_NEWSTIME, DateInforPojoTime);
				list.add(map);
			}

			adapter = new Myadapter(getActivity());
			newslistview.setAdapter(adapter);

			newslistview.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (newsData.size() >= position && position >= 0) {

						int i = 0;
						int size = newsData.size();
						ArrayList<String> newsData_link = new ArrayList<String>();
						while (i < size) {
							newsData_link.add(newsData.get(i).getLink_());
							i++;
						}
						Intent in = new Intent(getActivity(), NewsDetailView_ViewPager.class);
						Bundle bundle = new Bundle();
						bundle.putStringArrayList("link", newsData_link);
						bundle.putString("position", position + "");
						in.putExtras(bundle);
						startActivity(in);

					}
				}

			});

		}

	}

	private void sendM(Handler mHandler, int what) {
		Message msg = new Message();
		msg.what = what;
		mHandler.sendMessage(msg);
	}

	protected void errorDialog(String title, int what) {

		AlertDialog.Builder builder = new Builder(getActivity());
		switch (what) {
		case 1:
			builder.setMessage("数据加载失败，请稍候重试");
			break;

		default:
			break;
		}

		builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	/**
	 * 添加下拉刷新功能
	 */
	private void addPulltoReflesh() {
		newslistview.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem == 0) {
					// Log.i("log", "滑到顶部");
					nowAtTop = true;
				} else {
					nowAtTop = false;
				}
			}
		});

		newslistview.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// 记下按下位置
					mDownY = event.getY();
					touch_move = 0;
					hasOne = false;
					break;
				case MotionEvent.ACTION_MOVE:
					// 移动时手指的位置
					mMoveY = event.getY();
					if (mMoveY > touch_move && hasOne == false) {
						sendMove(mMoveY - mDownY);
					} else {
						// 上移
						clearProgress();
					}
					touch_move = mMoveY;
					break;
				case MotionEvent.ACTION_UP:
					// 手指抬起，取消更新进度显示
					if (pbl.getProgress() <= 100) {
						clearProgress();
					}
					// log.i("canpulltoreflesh_count = 0;");
					break;
				}
				return false;
			}
		});
	}

	private void clearProgress() {
		pbl.setProgress(0);
		pbr.setProgress(0);
	}

	private void maxProgress() {
		pbr.setProgress(100);
		pbl.setProgress(100);
	}

	/**
	 * 处理移动距离
	 * 
	 * @param move
	 */
	private void sendMove(float move) {
		// 小于一个值得时候我就 显示进度条

		move = DensityUtil.px2dip(getActivity(), move);
		move = (float) (move * 1.5);
		if (nowAtTop == true) {
			if (move >= 200) {
				maxProgress();
			} else {
				pbr.setProgress((int) move / 2);
				pbl.setProgress((int) move / 2);
			}

			if (pbr.getProgress() == 100) {
				maxProgress();
				handler.sendEmptyMessage(MSG_WHAT.LIST_REFRESH);
			}
		}
	}
}
