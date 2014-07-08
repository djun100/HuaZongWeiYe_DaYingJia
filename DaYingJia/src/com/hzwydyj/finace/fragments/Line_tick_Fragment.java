package com.hzwydyj.finace.fragments;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.activitys.HZWY_BaseActivity;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.PriceData;
import com.hzwydyj.finace.utils.MSG_WHAT;
import com.hzwydyj.finace.utils.MyLogger;
import com.hzwydyj.finace.utils.UDPtools;
import com.hzwydyj.finace.utils.Util;

public class Line_tick_Fragment extends Fragment {

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

	private MyLogger log = MyLogger.yLog();
	private String code;
	private String ex;
	private String name;
	private String decimal;
	private LayoutInflater mInflater;
	private ListView fenbiListView;

	// 本地配置保存对象
	private SharedPreferences preferences;
	// 分时历史数据list
	private List<PriceData> listData;
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

	private String errorMsg;

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
		String timeNowURL = Const.URL_NOW;
		timeNowURL = timeNowURL.replaceFirst(Const.URL_EX, ex);
		timeNowURL = timeNowURL.replaceFirst(Const.URL_CODE, code);
		timeNowURL = timeNowURL.replaceFirst(Const.URL_DATE, "1333605270");
		timeNowURL = timeNowURL.replaceFirst(Const.URL_COUNT, "40");
		listData = util.getFenbi(timeNowURL, ex);
	}

	public void showNew(PriceData timenow) {
		progressD_state(false);
		timeNow = timenow;

		DecimalFormat df_base = Const.df4;
		if (decimal != null) {
			if ("0".equals(decimal)) {
				df_base = Const.df0;
			} else if ("1".equals(decimal)) {
				df_base = Const.df1;
			} else if ("2".equals(decimal)) {
				df_base = Const.df2;
			} else if ("3".equals(decimal)) {
				df_base = Const.df3;
			} else if ("4".equals(decimal)) {
				df_base = Const.df4;
			}
		}

		if (listData != null) {
			if (lastClose != 0) {
				float updown = util.getFloat(timeNow.getPrice_last()) - lastClose;
				float updownrate = updown / lastClose * 100;
				timeNow.setPrice_updown(String.valueOf(Const.df4.format(updown)));
				timeNow.setPrice_updownrate(String.valueOf(Const.df2.format(updownrate)));
				timeNow.setPrice_lastclose(String.valueOf(lastClose));
			}

			if (listData != null && timeNow != null && listData.size() > 0
					&& !listData.get(0).getPrice_quotetime().equals(timeNow.getPrice_quotetime())) {
				listData.add(listData.get(listData.size() - 1));
				for (int i = listData.size() - 1; i > 0; i--) {
					listData.set(i, listData.get(i - 1));
				}
				listData.set(0, timeNow);
			}

			mData = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < listData.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				PriceData pData = listData.get(i);
				map.put(Const.FENBI_PRICE, df_base.format(Double.parseDouble(pData.getPrice_last())));
				map.put(Const.FENBI_TIME, util.formatTimeHms(pData.getPrice_quotetime()));
				map.put(Const.FENBI_VOLUME, pData.getPrice_volume());
				map.put(Const.PRICE_UPDOWN, pData.getPrice_updown());
				mData.add(map);
			}

			adapter.notifyDataSetChanged();
		}

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
						sendMessagewhat(MSG_WHAT.THREAD_SENDACTIVE_FAIL);
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
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				try {

					if (UDPtools.client != null && !UDPtools.client.isClosed()) {
						UDPtools.client.receive(UDPtools.recpacket);

						String str = new String(UDPtools.recpacket.getData(), 0, 1000);
						PriceData temp = null;
						if (str.indexOf("{") >= 0 && str.indexOf("}") >= 0) {
							temp = util.getTimeNowUDPString(str, null);
						}
						if (temp != null && running == true) {
							// Message message = Message.obtain();
							// message.what = MSG_WHAT.LIST_REFRESH;
							// message.obj = temp;
							// handler.sendMessage(message);
							timeNow = util.getTimeNowUDPString(str, timeNow);

							if (timeNow != null) {
								mCallback.onUDP_push(str);
								Message msg = handler.obtainMessage();
								msg.what = MSG_WHAT.LIST_REFRESH;
								msg.obj = timeNow;
								handler.sendMessage(msg);
							}
						}
					}

				} catch (IOException e) {
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
		public TextView fenbitime;
		public TextView fenbiprice;
		public TextView fenbivolume;
	}

	// 行情数据列表组件用数据adapter
	private class MyAdapter extends BaseAdapter {

		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			ViewHolder vholder = null;
			if (paramView == null) {
				vholder = new ViewHolder();
				paramView = mInflater.inflate(R.layout.fenbiitem, null);
				vholder.fenbitime = (TextView) paramView.findViewById(R.id.fenbi_time);
				vholder.fenbiprice = (TextView) paramView.findViewById(R.id.fenbi_price);
				vholder.fenbivolume = (TextView) paramView.findViewById(R.id.fenbi_volume);
				paramView.setTag(vholder);
			} else {
				vholder = (ViewHolder) paramView.getTag();
			}

			vholder.fenbiprice.setTextColor(Color.WHITE);
			String updown = (String) mData.get(paramInt).get(Const.PRICE_UPDOWN);
			if (util.getFloat(updown) != 0) {
				if (util.getFloat(updown) < 0) {
					vholder.fenbiprice.setTextColor(Color.GREEN);
				} else {
					vholder.fenbiprice.setTextColor(Color.RED);
				}
			}

			vholder.fenbitime.setText((String) mData.get(paramInt).get(Const.FENBI_TIME));
			vholder.fenbiprice.setText((String) mData.get(paramInt).get(Const.FENBI_PRICE));
			vholder.fenbivolume.setText((String) mData.get(paramInt).get(Const.FENBI_VOLUME));

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
				Thread t = new Thread() {
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
							if (opTemp.indexOf(code) >= 0) {
								sendMessagewhat(MSG_WHAT.WATCHLIST_EXIST);
								return;
							} else {
								opTemp = opTemp + "," + code;
								exTemp = exTemp + "," + ex;
								opNameTemp = opNameTemp + "," + name;
								editor.putString(Const.PREF_OPTIONAL, opTemp);
								editor.putString(Const.PREF_EX, exTemp);
								editor.putString(Const.PREF_OPTIONAL_NAME, opNameTemp);
							}
						}
						editor.commit();
						sendMessagewhat(MSG_WHAT.WATCHLIST_ADD);
					}
				};
				t.start();
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

	public Line_tick_Fragment() {
		// TODO Auto-generated constructor stub
	}

	/** 缓存view */
	private View view;
	MyAdapter adapter;
	private HZWY_BaseActivity hzwy_BaseActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (view == null) {
			view = inflater.inflate(R.layout.f_line_tick, container, false);
			code = getArguments().getString("code");
			ex = getArguments().getString("ex");
			name = getArguments().getString("name");
			decimal = getArguments().getString("decimal");
			// log.i("code - " + code + ",ex - " + ex + ",name - " + name + ",decimal - " + decimal);

			mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			fenbiListView = (ListView) view.findViewById(R.id.fenbiListView);
			adapter = new MyAdapter();
			fenbiListView.setAdapter(adapter);
			initProgressD();

			getTCPDataTask(code);
		}

		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		
		hzwy_BaseActivity = new  HZWY_BaseActivity(MyApplication.CONTEXT);

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
