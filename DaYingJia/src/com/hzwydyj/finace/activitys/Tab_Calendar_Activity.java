package com.hzwydyj.finace.activitys;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.data.CalData;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.utils.DensityUtil;
import com.hzwydyj.finace.utils.MSG_WHAT;
import com.hzwydyj.finace.utils.MyLogger;
import com.hzwydyj.finace.utils.Util;

/**
 * 
 * @author LuoYi
 * 
 */
public class Tab_Calendar_Activity extends FragmentActivity {

	/** 日历列表 */
	private ListView callistview;
	/** 进度条 */
	private ProgressDialog progressDialog;
	/** 查询按钮 */
	private Button searchbtn, backbtn;

	// 时间参数
	private int year, month, day;
	private Calendar c;
	private String date = "";

	/** 日历初始数据 */
	private List<CalData> listData;

	/** 老版本的工具类 */
	private Util util = new Util();

	/** 布局加载器 */
	private LayoutInflater mInflater;

	/** 行情数据map */
	private List<Map<String, Object>> mData;

	private MyLogger log = MyLogger.yLog();

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
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_calendar);
		
		hzwy_BaseActivity = new HZWY_BaseActivity(MyApplication.CONTEXT);

		callistview = (ListView) findViewById(R.id.callistview);
		searchbtn = (Button) findViewById(R.id.searchbtn);
		backbtn = (Button) findViewById(R.id.backbtn);
		backbtn.setVisibility(View.VISIBLE);
		mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		pbr = (ProgressBar) findViewById(R.id.pbr);
		pbl = (ProgressBar) findViewById(R.id.pbl);

		initListView();
		initProgressDialog();
		initDate();

		if (savedInstanceState != null) {
			date = savedInstanceState.getString("date");
			// Log.i("temp", "savedInstanceState->date->" + date);
			searchbtn.setText(date);
		}
		doTask(date);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("date", date);
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case MSG_WHAT.LIST_REFRESH:// 更新列表数据
				if (nowAtTop == true && hasOne == false) {
					doTask(date);
					hasOne = true;
				}
				nowAtTop = false;
				break;
			case MSG_WHAT.ASYNCTASK_FAIL:
				hzwy_BaseActivity.HZWY_Toast1("数据加载失败，请稍候重试");
				break;
			case MSG_WHAT.LIST_SIZE_IS0:
				hzwy_BaseActivity.HZWY_Toast1("今日无重要数据公布");
				break;
			case MSG_WHAT.SHOW_LIST:
				showList();
				if (listData != null && listData.size() == 0) {
					hzwy_BaseActivity.HZWY_Toast1("今日无重要数据公布");
				}
				// 添加下列刷新功能
				addPulltoReflesh();
			default:
				break;
			}
		}
	};

	/**
	 * 显示日期picker
	 */
	private void showDatePickerDialog() {
		new DatePickerDialog(this, new OnDateSetListener() {

			public void onDateSet(DatePicker view, int yearr, int monthOfYear, int dayOfMonth) {
				// TODO Auto-generated method stub
				year = yearr;
				month = monthOfYear;
				day = dayOfMonth;

				if (!makeDateString(year, month, day).equals(date)) {
					date = makeDateString(year, month, day);
					doTask(date);
					searchbtn.setText(date);
				}
			}
		}, year, month, day).show();
	}

	/**
	 * 格式化年月日
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	private String makeDateString(int year, int month, int day) {

		String date;
		if (month < 9) {
			date = year + "-0" + (month + 1);
		} else {
			date = year + "-" + (month + 1);
		}
		if (day < 10) {
			date = date + "-0" + day;
		} else {
			date = date + "-" + day;
		}

		return date;
	}

	private void initListView() {
		MyAdapter adapter = new MyAdapter();
		callistview.setAdapter(adapter);
	}

	public void initProgressDialog() {
		progressDialog = new ProgressDialog(Tab_Calendar_Activity.this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("数据刷新中...");
		progressDialog.setCancelable(true);
	}

	public void initDate() {
		c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		date = makeDateString(year, month, day);

		searchbtn.setText(date);
	}

	private void doTask(String date) {
		MyTask mytask = new MyTask();
		mytask.execute(date, null, null);
	}

	class MyTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			try {
				listData = util.getCalData(Const.URL_CALENDAR + params[0]);
			} catch (Exception e) {
				sendMessagewhat(MSG_WHAT.ASYNCTASK_FAIL);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressD_state(false);

			if (pbl.getProgress() == 100) {
				pbl.setProgress(0);
				pbr.setProgress(0);
			}
			// 显示列表
			sendMessagewhat(MSG_WHAT.SHOW_LIST);

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressD_state(true);
		}
	}

	/**
	 * 进度条显示或隐藏
	 * 
	 * @param isshow
	 */
	private void progressD_state(boolean isshow) {
		try {
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
		} catch (Exception e) {
			// 横屏的时候容易溢出
		}
	}

	/**
	 * 添加下拉刷新功能
	 */
	private void addPulltoReflesh() {
		callistview.setOnScrollListener(new OnScrollListener() {

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

		callistview.setOnTouchListener(new OnTouchListener() {

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

		move = DensityUtil.px2dip(this, move);
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
				sendMessagewhat(MSG_WHAT.LIST_REFRESH);
			}
		}
	}

	private void sendMessagewhat(int what) {
		handler.sendEmptyMessage(what);
	}

	private class ViewHolder {
		public TextView time;
		public TextView country;
		public TextView item;
		public TextView importance;
		public TextView lastValue;
		public TextView prediction;
		public TextView actual;
	}

	int nextBlack = Color.argb(255, 116, 116, 116);
	private HZWY_BaseActivity hzwy_BaseActivity;

	private class MyAdapter extends BaseAdapter {

		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			ViewHolder vholder = null;
			if (paramView == null) {
				vholder = new ViewHolder();
				paramView = mInflater.inflate(R.layout.calendaritem, null);

				vholder.country = (TextView) paramView.findViewById(R.id.calcountry);
				vholder.item = (TextView) paramView.findViewById(R.id.calitem);
				vholder.importance = (TextView) paramView.findViewById(R.id.calimportance);
				vholder.lastValue = (TextView) paramView.findViewById(R.id.callastvalue);
				vholder.prediction = (TextView) paramView.findViewById(R.id.calprediction);
				vholder.actual = (TextView) paramView.findViewById(R.id.calactual);
				vholder.time = (TextView) paramView.findViewById(R.id.time);

				paramView.setTag(vholder);
			} else {
				vholder = (ViewHolder) paramView.getTag();
			}

			// vholder.item.setTextColor(nextBlack);
			vholder.item.setTextColor(Color.BLACK);
			vholder.importance.setTextColor(nextBlack);
			String imp = (String) mData.get(paramInt).get(Const.CAL_IMPORTANCE);
			if ("高".equals(imp)) {
				vholder.item.setTextColor(Color.RED);
				vholder.importance.setTextColor(Color.RED);
			}

			vholder.country.setText((String) mData.get(paramInt).get(Const.CAL_COUNTRY));
			vholder.item.setText((String) mData.get(paramInt).get(Const.CAL_ITEM));
			vholder.importance.setText((String) mData.get(paramInt).get(Const.CAL_IMPORTANCE));
			vholder.lastValue.setText((String) mData.get(paramInt).get(Const.CAL_LASTVALUE));
			vholder.prediction.setText((String) mData.get(paramInt).get(Const.CAL_PREDICTION));
			vholder.actual.setText((String) mData.get(paramInt).get(Const.CAL_ACTUAL));
			vholder.time.setText((String) mData.get(paramInt).get(Const.CAL_TIME));
			return paramView;
		}

		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		public int getCount() {
			if (mData != null) {
				return mData.size();
			} else {
				return 0;
			}
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}
	}

	public void showList() {
		if (listData != null) {
			mData = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < listData.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				CalData calData = listData.get(i);
				map.put(Const.CAL_COUNTRY, calData.getCalCountry());
				map.put(Const.CAL_ITEM, calData.getCalItem());
				map.put(Const.CAL_IMPORTANCE, calData.getCalImportance());
				map.put(Const.CAL_LASTVALUE, calData.getCalLastValue());
				map.put(Const.CAL_PREDICTION, calData.getCalPrediction());
				map.put(Const.CAL_ACTUAL, calData.getCalActual());
				map.put(Const.CAL_TIME, calData.getCalTime());
				mData.add(map);
			}
		} else {
			sendMessagewhat(MSG_WHAT.LIST_SIZE_IS0);
		}

		((MyAdapter) callistview.getAdapter()).notifyDataSetChanged();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.searchbtn:
			showDatePickerDialog();
			break;
		case R.id.backbtn:
			initListView();
			initProgressDialog();
			initDate();

			searchbtn.setText(date);
			doTask(date);
			break;

		default:
			break;
		}
	}

	// //////////////////////////////
	// // 系统事件
	// //////////////////////////////

	/**
	 * 将事件传递给TabActivity,统一管理后退事件
	 */
	@Override
	public void onBackPressed() {
		this.getParent().onBackPressed();
	}
	
}
