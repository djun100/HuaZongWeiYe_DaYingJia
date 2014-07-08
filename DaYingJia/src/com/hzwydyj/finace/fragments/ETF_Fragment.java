package com.hzwydyj.finace.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.activitys.ETFView;
import com.hzwydyj.finace.activitys.HZWY_BaseActivity;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.ETF;
import com.hzwydyj.finace.utils.DensityUtil;
import com.hzwydyj.finace.utils.MSG_WHAT;
import com.hzwydyj.finace.utils.MyLogger;
import com.hzwydyj.finace.utils.Util;

public class ETF_Fragment extends Fragment {

	private MyLogger log = MyLogger.yLog();

	public ETF_Fragment() {
	}

	private String uRL_ETFs;

	private ListView newslistview;
	private Util util;
	/** etf数据list */
	private List<ETF> etfData;
	List<Map<String, Object>> list;

	private final String DATA_ETF_TIME = "time";
	private final String DATA_ETF_VAL = "val";
	private final String DATA_ETF_PREVAL = "preval";
	private final String DATA_ETF_AMPLITUDE = "amplitude";
	private Myadapter adapter;

	@Override
	public void onResume() {
		super.onResume();
		clearProgress();
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case MSG_WHAT.ASYNCTASK_FAIL:
				hzwy_BaseActivity.HZWY_Toast1("数据加载失败，检查网络连接");
				break;
			case MSG_WHAT.LIST_REFRESH:
				if (nowAtTop == true && hasOne == false) {
					doTask(uRL_ETFs);
					hasOne = true;
				}
				nowAtTop = false;
				break;

			default:
				break;
			}

		}
	};

	private String[] url_etfs = { Const.URL_ETF_AU, Const.URL_ETF_AG };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// super.onCreate(savedInstanceState);
		//log.i("onCreate");
		View view = inflater.inflate(R.layout.etf_f, container, false);
		util = new Util();
		hzwy_BaseActivity = new HZWY_BaseActivity(MyApplication.CONTEXT);
		

		initURL();
		initListView(view);
		doTask(uRL_ETFs);
		addPulltoReflesh();

		return view;
	}

	/**
	 * 查询地址初始化
	 */
	private void initURL() {
		int position = getArguments().getInt("position");
		switch (position) {
		case 0:
		case 1:
			uRL_ETFs = url_etfs[position];
			break;

		default:
			break;
		}
	}

	private ProgressBar pb;

	private void initListView(View view) {
		newslistview = (ListView) view.findViewById(R.id.newslistview);
		pb = (ProgressBar) view.findViewById(R.id.pb);
		pbr = (ProgressBar) view.findViewById(R.id.pbr);
		pbl = (ProgressBar) view.findViewById(R.id.pbl);
	}

	private void doTask(String url) {
		MyTask mytask = new MyTask();
		mytask.execute(url, null, null);
	}

	class MyTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			try {
				log.i(params[0]);
				etfData = util.getETF_ByXML(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
				handler.sendEmptyMessage(MSG_WHAT.ASYNCTASK_FAIL);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pb.setVisibility(View.GONE);
			newslistview.setVisibility(View.VISIBLE);
			showList();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
				pb.setVisibility(View.GONE);
			} else {
				pb.setVisibility(View.VISIBLE);
			}
			newslistview.setVisibility(View.GONE);
		}
	}

	private class Myadapter extends BaseAdapter {

		private ETFView main;
		private List<? extends Map<String, ?>> list;

		public Myadapter(Context context, List<? extends Map<String, ?>> data) {
			super();
			main = (ETFView) context;
			list = data;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			if (position < 0 || etfData.size() <= 0)
				return null;

			if (convertView == null)
				convertView = LayoutInflater.from(main).inflate(R.layout.etf_item, null);

			TextView time = (TextView) convertView.findViewById(R.id.time);
			TextView val = (TextView) convertView.findViewById(R.id.val);
			TextView preval = (TextView) convertView.findViewById(R.id.preval);
			TextView amplitude = (TextView) convertView.findViewById(R.id.amplitude);
			Map<String, ?> map = list.get(position);
			time.setText((String) map.get(DATA_ETF_TIME));
			val.setText((String) map.get(DATA_ETF_VAL));
			preval.setText((String) map.get(DATA_ETF_PREVAL));
			amplitude.setText((String) map.get(DATA_ETF_AMPLITUDE));

			return convertView;
		}

		public int getCount() {
			if (etfData != null) {
				return etfData.size();
			} else {
				return 0;
			}
		}

		public Object getItem(int position) {
			return etfData.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

	}

	public void showList() {
		if (etfData != null) {
			list = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < etfData.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				ETF inforPojo = etfData.get(i);
				map.put(DATA_ETF_TIME, inforPojo.getTime());
				map.put(DATA_ETF_VAL, inforPojo.getVal());
				map.put(DATA_ETF_PREVAL, inforPojo.getPreVal());
				map.put(DATA_ETF_AMPLITUDE, inforPojo.getAmplitude());

				list.add(map);
			}
			adapter = new Myadapter(getActivity(), list);
			newslistview.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}
	}

	// ///////////////////////////////
	// // 下拉刷新
	// ///////////////////////////////
	/** 下拉刷新条件2 一次touch只能提交一次 */
	private boolean hasOne = false;
	/** 下拉刷新条件1 */
	private boolean nowAtTop = true;
	/** 下拉刷新距离记录 */
	private float touch_move;
	/** 下拉位置记录 */
	private float mDownY;
	/** 下拉移动距离 */
	private float mMoveY;
	/** 列表下拉刷新进度条 */
	private ProgressBar pbl;
	private ProgressBar pbr;

	private HZWY_BaseActivity hzwy_BaseActivity;

	/**
	 * 添加下拉刷新功能
	 */
	private void addPulltoReflesh() {
		newslistview.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem == 0) {
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
