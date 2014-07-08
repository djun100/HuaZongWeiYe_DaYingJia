package com.hzwydyj.finace.fragments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.activitys.HZWY_BaseActivity;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.KData;
import com.hzwydyj.finace.data.PriceData;
import com.hzwydyj.finace.utils.MSG_WHAT;
import com.hzwydyj.finace.utils.MyLogger;
import com.hzwydyj.finace.utils.UDPtools;
import com.hzwydyj.finace.utils.Util;
import com.hzwydyj.finace.view.TimeDataDraw_SurfaceView;

public class Line_time_Fragment extends Fragment {

	OnUDPListener mCallback;

	/**
	 * 回调接口
	 * 
	 * @author yaoid
	 * 
	 */
	public interface OnUDPListener {
		public void onUDP_push(String timeNow);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mCallback = (OnUDPListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnUDPListener");
		}

	}

	private TimeDataDraw_SurfaceView timeDataDraw_SurfaceView;

	private MyLogger log = MyLogger.yLog();

	private String code;
	private String ex;
	private String name;
	private String decimal;
	private LinearLayout drawLayout;
	private LayoutInflater mInflater;
	// private ListView abListView;
	// private SlidingDrawer mDialerDrawer;
	private ImageButton slidingButton;

	// 本地配置保存对象
	private SharedPreferences preferences;
	// 分时历史数据list
	private List<KData> listData;
	// 分时当前数据
	private PriceData timeNow;
	// 五档数据
	private List<Map<String, Object>> mData;

	private float lastClose = 0;// 最后收盘价

	// 数据取得等待对话框组件
	private ProgressDialog progressDialog;

	// 自动更新线程
	private AutoUpdateThread autoUpdateThread;

	// 自动发送active
	private SendActiveThread sendActiveThread;

	// 自动更新线程启动标志
	private boolean running = false;

	private Util util = new Util();

	public void onPause() {
		super.onPause();
		// log.i("onPause");
		closeUDP();
	}

	public void onResume() {
		super.onResume();
		// log.i("onResume");
		UDPtools.initUDP();
		startUDP();
	}

	private void startUDP() {
		running = true;
		new Thread("onResume") {
			public void run() {
				try {
					UDPtools.startUDP(code);

					startAutoUpdateT();

					startsendActiveT();

				} catch (Exception e) {
				}
			}
		}.start();
	}

	private void closeUDP() {
		running = false;

		UDPtools.closeClient();

		if (autoUpdateThread != null) {
			autoUpdateThread.interrupt();
			autoUpdateThread = null;
		}

		if (sendActiveThread != null) {
			sendActiveThread.interrupt();
			sendActiveThread = null;
		}

	}

	private void startAutoUpdateT() {
		autoUpdateThread = new AutoUpdateThread();
		autoUpdateThread.start();
	}

	private void startsendActiveT() {
		sendActiveThread = new SendActiveThread();
		sendActiveThread.start();
	}

	private synchronized void updateData(String code) {
		String listDataURL = Const.URL_TIMEDATA;
		listDataURL = listDataURL.replaceFirst(Const.URL_EX, ex);
		listDataURL = listDataURL.replaceFirst(Const.URL_CODE, code);
		listData = util.getTimeData(listDataURL);
		if (listData != null) {
			// log.i("listData.size=" + listData.size());
		}
		String timeNowURL = Const.URL_NOW;
		timeNowURL = timeNowURL.replaceFirst(Const.URL_EX, ex);
		timeNowURL = timeNowURL.replaceFirst(Const.URL_CODE, code);
		timeNowURL = timeNowURL.replaceFirst(Const.URL_DATE, "1333605270");
		timeNowURL = timeNowURL.replaceFirst(Const.URL_COUNT, "3");
		timeNow = util.getTimeNow(timeNowURL, ex);
		lastClose = util.getFloat(timeNow.getPrice_lastclose());
		timeDataDraw_SurfaceView.setLastClose(lastClose);
	}

	public void showNew() {
		showNew(timeNow);
	}

	KData data;

	public void showNew(PriceData timenow) {
		timeNow = timenow;
		// progressD_state(false);

		if (lastClose != 0) {
			float updown = util.getFloat(timeNow.getPrice_last()) - lastClose;
			float updownrate = updown / lastClose * 100;
			timeNow.setPrice_updown(String.valueOf(Const.df4.format(updown)));
			timeNow.setPrice_updownrate(String.valueOf(Const.df2.format(updownrate)));
			timeNow.setPrice_lastclose(String.valueOf(lastClose));
		}

		if (listData != null && listData.size() > 0) {
			// log.i("updateData->listData->" + listData.size());
			timeDataDraw_SurfaceView.setInitFlag(true);
			timeDataDraw_SurfaceView.updateData(listData);

			data.setK_average(timeNow.getPrice_average());
			data.setK_close(timeNow.getPrice_last());
			data.setK_date(util.formatTimeSec(timeNow.getPrice_quotetime()));
			data.setK_timeLong(timeNow.getPrice_quotetime());
			data.setK_high(timeNow.getPrice_high());
			data.setK_low(timeNow.getPrice_low());
			data.setK_open(timeNow.getPrice_open());
			data.setK_volume(timeNow.getPrice_volume());
			timeDataDraw_SurfaceView.updateNew(data);
		}

	}

	private Map<String, Object> getABData(String abname, String ab, String ablot) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Const.ABNAME, abname);
		map.put(Const.AB, ab);
		map.put(Const.ABLOT, ablot);
		return map;
	}

	private class SendActiveThread extends Thread {
		private long waitTime = 10000;

		public void run() {
			while (running) {
				try {
					SendActiveThread.sleep(waitTime);
					if (UDPtools.client != null && !UDPtools.client.isClosed()) {
						UDPtools.sendpacket.setData(UDPtools.sendBuf_active);
						UDPtools.client.send(UDPtools.sendpacket);
						// log.i("UDP active,active");
					}

				} catch (Exception e) {
					e.printStackTrace();
					SendActiveThread.currentThread().interrupt();
					if (running) {
						// sendMessagewhat(MSG_WHAT.THREAD_SENDACTIVE_FAIL);
					}
					return;
				}

			}
		}
	}

	private class AutoUpdateThread extends Thread {

		public void run() {
			while (running) {
				try {

					AutoUpdateThread.sleep(500);

					if (UDPtools.client != null && !UDPtools.client.isClosed()) {
						UDPtools.client.receive(UDPtools.recpacket);

						String str = new String(UDPtools.recpacket.getData(), 0, 1000);
						PriceData temp = null;
						if (str.indexOf("{") >= 0 && str.indexOf("}") >= 0) {
							temp = util.getTimeNowUDPString(str, null);
						}
						if (temp != null && running == true) {

							mCallback.onUDP_push(str);
							Message msg = handler.obtainMessage();
							msg.what = MSG_WHAT.LIST_REFRESH;
							msg.obj = temp;
							handler.sendMessage(msg);

						}
					}

				} catch (Exception e) {
					e.printStackTrace();
					if (running) {
						sendMessagewhat(MSG_WHAT.THREAD_AUTOUPDATE_FAIL);
					}
				}
			}
		}
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MSG_WHAT.SHOW_LIST:
				showNew(timeNow);
				break;
			case MSG_WHAT.LIST_REFRESH:
				showNew((PriceData) msg.obj);
				break;
			case MSG_WHAT.WATCHLIST_ADD:
				hzwy_BaseActivity.HZWY_Toast1("已添加到我的自选中。");
				break;
			case MSG_WHAT.THREAD_SENDACTIVE_FAIL:
				hzwy_BaseActivity.HZWY_Toast(MSG_WHAT.THREAD_SENDACTIVE_FAIL);
				break;
			case MSG_WHAT.THREAD_AUTOUPDATE_FAIL:
				hzwy_BaseActivity.HZWY_Toast(MSG_WHAT.THREAD_AUTOUPDATE_FAIL);
				break;
			case MSG_WHAT.WATCHLIST_EXIST:
				hzwy_BaseActivity.HZWY_Toast1("我的自选中已有此项。");
				break;
			default:
				break;
			}
		}
	};

	private final class ViewHolder {
		public TextView abname;
		public TextView ab;
		public TextView ablot;
	}

	// 行情数据列表组件用数据adapter
	private class MyAdapter extends BaseAdapter {

		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			ViewHolder holder = null;
			if (paramView == null) {
				paramView = mInflater.inflate(R.layout.timeabitem, null);
				holder = new ViewHolder();
				holder.abname = (TextView) paramView.findViewById(R.id.abname);
				holder.ab = (TextView) paramView.findViewById(R.id.ab);
				holder.ablot = (TextView) paramView.findViewById(R.id.ablot);

				paramView.setTag(holder);
			} else {
				holder = (ViewHolder) paramView.getTag();
			}
			String ab = (String) mData.get(paramInt).get(Const.AB);
			holder.ab.setTextColor(Color.WHITE);
			holder.ablot.setTextColor(Color.WHITE);
			if (util.getFloat(ab) - lastClose > 0) {
				holder.ab.setTextColor(Color.RED);
				holder.ablot.setTextColor(Color.RED);
			} else if (util.getFloat(ab) - lastClose < 0) {
				holder.ab.setTextColor(Color.GREEN);
				holder.ablot.setTextColor(Color.GREEN);
			}
			holder.abname.setText((String) mData.get(paramInt).get(Const.ABNAME));
			holder.ab.setText((String) mData.get(paramInt).get(Const.AB));
			holder.ablot.setText((String) mData.get(paramInt).get(Const.ABLOT));
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

	protected void addDialog() {
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setMessage("确认要添加到我的自选吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				new Thread() {
					public void run() {

						SharedPreferences.Editor editor = preferences.edit();
						String opTemp = preferences.getString(Const.PREF_OPTIONAL, "");
						String opNameTemp = preferences.getString(Const.PREF_OPTIONAL_NAME, "");
						String exTemp = preferences.getString(Const.PREF_EX, "");
						if ("".equals(opTemp)) {
							editor.putString(Const.PREF_OPTIONAL, code);
							editor.putString(Const.PREF_OPTIONAL_NAME, name);
							editor.putString(Const.PREF_EX, ex);
						} else {
							String[] tmp = opTemp.split(",");
							for (int i = 0; i < tmp.length; i++) {
								if (code.equals(tmp[i])) {
									sendMessagewhat(MSG_WHAT.WATCHLIST_EXIST);
									return;
								}
							}
							opTemp = opTemp + "," + code;
							exTemp = exTemp + "," + ex;
							opNameTemp = opNameTemp + "," + name;
							editor.putString(Const.PREF_OPTIONAL, opTemp);
							editor.putString(Const.PREF_EX, exTemp);
							editor.putString(Const.PREF_OPTIONAL_NAME, opNameTemp);
						}
						editor.commit();
						sendMessagewhat(MSG_WHAT.WATCHLIST_ADD);
					}
				}.start();
				dialog.dismiss();
			}
		});
		builder.setNegativeButton(R.string.regiter_caler, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	public Line_time_Fragment() {
	}

	/** 缓存view */
	private View view;
	MyAdapter adapter;

	private HZWY_BaseActivity hzwy_BaseActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// log.i("onCreateView");
		if (view == null) {
			// log.i("view 重绘");
			view = inflater.inflate(R.layout.f_line_time, container, false);
			code = getArguments().getString("code");
			ex = getArguments().getString("ex");
			name = getArguments().getString("name");
			decimal = getArguments().getString("decimal");
			// log.i("code - " + code + ",ex - " + ex + ",name - " + name + ",decimal - " + decimal);

			preferences = getActivity().getSharedPreferences(Const.PREFERENCES_NAME, Activity.MODE_PRIVATE);
			mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			drawLayout = (LinearLayout) view.findViewById(R.id.timeviewlayout);
			// abListView = (ListView) view.findViewById(R.id.timeABlist);
			// adapter = new MyAdapter();
			// abListView.setAdapter(adapter);

			data = new KData();
			// DisplayMetrics dm = new DisplayMetrics();
			// getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
			timeDataDraw_SurfaceView = new TimeDataDraw_SurfaceView(getActivity(), ex, decimal);
			drawLayout.addView(timeDataDraw_SurfaceView);
			timeDataDraw_SurfaceView.setCode(code);

			// slidingButton = (ImageButton) view.findViewById(R.id.handle);
			// slidingButton.setImageResource(R.drawable.shows);

			initProgressD();
			getTCPDataTask(code);

		}

		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		
		hzwy_BaseActivity = new HZWY_BaseActivity(MyApplication.CONTEXT);

		return view;
	}

	private void getTCPDataTask(String code) {
		GetTCPDataTask mytask = new GetTCPDataTask();
		mytask.execute(code, null, null);
	}

	class GetTCPDataTask extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressD_state(true);
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				// log.i("GetTCPDataTask -> " + params[0]);
				updateData(params[0]);
			} catch (Exception e) {
				progressD_state(false);
				e.printStackTrace();
				sendMessagewhat(MSG_WHAT.ASYNCTASK_FAIL);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressD_state(false);
			sendMessagewhat(MSG_WHAT.SHOW_LIST);
		}
	}

	private void sendMessagewhat(int what) {
		handler.sendEmptyMessage(what);
	}

	private void initProgressD() {
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("取得数据...");
		progressDialog.setCancelable(false);
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
}
