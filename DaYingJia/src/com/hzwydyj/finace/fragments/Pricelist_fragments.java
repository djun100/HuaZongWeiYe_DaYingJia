package com.hzwydyj.finace.fragments;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.activitys.HZWY_BaseActivity;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.PriceData;
import com.hzwydyj.finace.utils.DensityUtil;
import com.hzwydyj.finace.utils.MSG_WHAT;
import com.hzwydyj.finace.utils.MyLogger;
import com.hzwydyj.finace.utils.UDPtools;
import com.hzwydyj.finace.utils.Util;
import com.hzwydyj.finace.view.SyncHorizontalScrollView;

public class Pricelist_fragments extends Fragment {

	private static int DEVICE_NORMAL = 0;
	private static int DEVICE_LARGE = 1;
	private int current_device;

	OnItemSelectedListener mCallback;

	MyAdapter adapter;
	private MyLogger log = MyLogger.yLog();

	private int[] colors = new int[] { 0xe1151515, 0xe10f0f0f };
	private int COLOR_GREEN;

	private boolean refreshData = false;

	private LayoutInflater mInflater;

	private ListView priceListView;
	private ListView priceNameListView;
	private SyncHorizontalScrollView headScroll;
	private SyncHorizontalScrollView listScroll;

	// 更新数据
	private PriceData timeNow;

	// 行情数据map
	private List<Map<String, Object>> mData;

	// 行情数据list
	private List<PriceData> listData;

	// 名字行情数据list
	private List<Map<String, Object>> listNameData;

	// 数据取得等待对话框组件
	// private ProgressDialog progressDialog;

	// 自动更新线程
	private AutoUpdateThread autoUpdateThread;

	// 自动发送active
	private SendActiveThread sendActiveThread;

	// 自动更新线程启动标志
	private boolean running = false;

	private String selected;

	private String type;

	private Util util = new Util();

	private String errorMsg;
	RadioGroup radioGroup_pricelistbar;

	private int ex = 0;

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
					UDPtools.startUDP(selected);

					startAutoUpdateT();

					startsendActiveT();

				} catch (Exception e) {
				}
			}
		}.start();
	}

	/**
	 * 滚动条绑定
	 * 
	 * @param view
	 */
	private void initScroll(View view) {
		headScroll = (SyncHorizontalScrollView) view.findViewById(R.id.scrollListHead);
		listScroll = (SyncHorizontalScrollView) view.findViewById(R.id.scrollList);
		if (headScroll == null || listScroll == null) {
			current_device = DEVICE_LARGE;
		}

		if (current_device == DEVICE_NORMAL) {
			headScroll.setSmoothScrollingEnabled(true);
			listScroll.setSmoothScrollingEnabled(true);
			headScroll.setScrollView(listScroll);
			listScroll.setScrollView(headScroll);
		}

	}

	private LinearLayout listHigh;
	private ProgressBar pb;

	/**
	 * 初始化列表
	 * 
	 * @param view
	 */
	private void initListView(View view) {
		pb = (ProgressBar) view.findViewById(R.id.pb);
		listHigh = (LinearLayout) view.findViewById(R.id.listHigh);
		priceNameListView = (ListView) view.findViewById(R.id.priceNameListView);
		priceListView = (ListView) view.findViewById(R.id.priceListView);

		adapter = new MyAdapter();
		priceListView.setAdapter(adapter);

		priceListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (listData.size() > position) {
					PriceData pData = listData.get(position);
					mCallback.onpriceitemListsSelected(pData.getPrice_code(), pData.getPrice_name(), selected, type,
							pData.getPrice_Decimal());
				}
			}
		});

		priceNameListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (listData.size() > position) {
					PriceData pData = listData.get(position);
					mCallback.onpriceitemListsSelected(pData.getPrice_code(), pData.getPrice_name(), selected, type,
							pData.getPrice_Decimal());
				}
			}
		});
	}

	// private void initProgressD() {
	// progressDialog = new ProgressDialog(getActivity());
	// progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	// progressDialog.setMessage("取得数据...");
	// progressDialog.setCancelable(true);
	// }

	/**
	 * 进度条显示或隐藏
	 * 
	 * @param isshow
	 */
	// private void progressD_state(boolean isshow) {
	// try {
	// if (progressDialog != null) {
	// if (isshow) {
	// if (!progressDialog.isShowing()) {
	// progressDialog.show();
	// }
	// } else {
	// if (progressDialog.isShowing()) {
	// progressDialog.dismiss();
	// }
	// }
	// }
	// } catch (Exception e) {
	// // 横屏的时候容易溢出
	// }
	// }

	/**
	 * 数据转存
	 * 
	 * @param sel
	 * @param type
	 * @param ex
	 */
	private void initselect(String sel, String type, int ex) {
		this.selected = sel;
		this.type = type;
		this.ex = ex;
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

	private void updateData(String type) {
		listData = util.getPriceDataTTJ("http://mhth.fx678.com/quotelist.aspx?key=" + type, type);
	}

	public void showNew(PriceData new_priceData) {

		PriceData timeNow = new_priceData;
		if (mData != null) {
			for (int i = 0; i < mData.size(); i++) {
				Map<String, Object> map = mData.get(i);
				String code = (String) map.get(Const.PRICE_CODE);

				if (code.equals(timeNow.getPrice_code())) {

					String decimal = (String) map.get(Const.PRICE_DECIMAL);
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

					float lastClose = util.getFloat((String) map.get(Const.PRICE_LASTCLOSE));
					if ("".equals(timeNow.getPrice_lastclose())) {

						if (Const.AUT_D.equals(code) || Const.AGT_D.equals(code)) {
							if (map.get(Const.PRICE_SETTLE) != null && !"".equals(map.get(Const.PRICE_SETTLE))) {
								lastClose = util.getFloat((String) map.get(Const.PRICE_SETTLE));
							}
						}
					}

					float updown = util.getFloat(timeNow.getPrice_last()) - lastClose;
					timeNow.setPrice_lastclose(String.valueOf(lastClose));
					float updownrate = 0;
					// log.i("code->" + timeNow.getPrice_code() + ",lastClose->" + lastClose);
					if (lastClose > 0) {
						updownrate = updown / lastClose * 100;
					}
					timeNow.setPrice_updown(String.valueOf(df_base.format(updown)));
					timeNow.setPrice_updownrate(String.valueOf(Const.df2.format(updownrate)));

					map.put(Const.PRICE_CODE, timeNow.getPrice_code());
					map.put(Const.PRICE_QUOTETIME, timeNow.getPrice_quotetime());
					map.put(Const.PRICE_OPEN, String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_open()))));
					map.put(Const.PRICE_HIGH, String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_high()))));
					map.put(Const.PRICE_LAST, String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_last()))));
					map.put(Const.PRICE_LOW, String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_low()))));
					map.put(Const.PRICE_UPDOWN, timeNow.getPrice_updown());
					map.put(Const.PRICE_UPDOWNRATE, timeNow.getPrice_updownrate());
				}

				this.timeNow = timeNow;

				adapter.notifyDataSetChanged();
			}
		}
	}

	public void showData() {
		// progressD_state(false);
		if (getActivity() != null) {
			listHigh.setVisibility(View.VISIBLE);
			pb.setVisibility(View.GONE);

			listNameData = new ArrayList<Map<String, Object>>();
			mData = new ArrayList<Map<String, Object>>();

			Map<String, Object> map;
			Map<String, Object> mapName;
			PriceData pData;
			String decimal;

			for (int i = 0; i < listData.size(); i++) {

				map = new HashMap<String, Object>();
				mapName = new HashMap<String, Object>();
				pData = listData.get(i);
				decimal = pData.getPrice_Decimal();
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

				map.put(Const.PRICE_NAME, pData.getPrice_name());
				map.put(Const.PRICE_CODE, pData.getPrice_code());
				map.put(Const.PRICE_QUOTETIME, pData.getPrice_quotetime());
				map.put(Const.PRICE_DECIMAL, decimal);

				map.put(Const.PRICE_OPEN, String.valueOf(df_base.format(Float.parseFloat(pData.getPrice_open()))));
				map.put(Const.PRICE_HIGH, String.valueOf(df_base.format(Float.parseFloat(pData.getPrice_high()))));
				map.put(Const.PRICE_LAST, String.valueOf(df_base.format(Float.parseFloat(pData.getPrice_last()))));
				map.put(Const.PRICE_LOW, String.valueOf(df_base.format(Float.parseFloat(pData.getPrice_low()))));
				map.put(Const.PRICE_LASTCLOSE, String.valueOf(df_base.format(Float.parseFloat(pData.getPrice_lastclose()))));
				map.put(Const.PRICE_UPDOWN, String.valueOf(df_base.format(Float.parseFloat(pData.getPrice_updown()))));
				map.put(Const.PRICE_UPDOWNRATE, pData.getPrice_updownrate());

				// 目前没有显示下面这些内容的需求
				// if (pData.getPriceTTJbuy() != null) {
				// map.put(Const.PRICE_TTJBUY,
				// String.valueOf(df_base.format(Float.parseFloat(pData.getPriceTTJbuy()))));
				// }
				// if (pData.getPriceTTJsell() != null) {
				// map.put(Const.PRICE_TTJSELL,
				// String.valueOf(df_base.format(Float.parseFloat(pData.getPriceTTJsell()))));
				// }
				// map.put(Const.PRICE_TTJAMLITUDE,
				// pData.getPriceTTJAmplitude());
				mData.add(map);

				mapName.put(Const.PRICE_NAME, pData.getPrice_name());
				listNameData.add(mapName);
			}

			ViewGroup.LayoutParams linearParams1 = priceListView.getLayoutParams();
			linearParams1.height = DensityUtil.dip2px(getActivity(), 35) * listData.size();
			priceListView.setLayoutParams(linearParams1);

			ViewGroup.LayoutParams linearParams2 = priceNameListView.getLayoutParams();
			linearParams2.height = DensityUtil.dip2px(getActivity(), 35) * listData.size();
			priceNameListView.setLayoutParams(linearParams2);

			CustomAdapter adapterS = new CustomAdapter(getActivity(), listNameData, R.layout.pricenameitem,
					new String[] { Const.PRICE_NAME }, new int[] { R.id.pricename });

			priceNameListView.setAdapter(adapterS);

			adapter.notifyDataSetChanged();

		}
	}

	@Deprecated
	private class ProgressThread extends Thread {
		int mState;

		public void setState(int state) {
			mState = state;
		}

		public void run() {
			try {
				updateData(selected);

				if (1 == mState) {
					// sendM(handler, 1, "");
				} else if (2 == mState) {
					// sendM(handler, 4, "");
				}

				return;
			} catch (Exception e) {
				// progressD_state(false);
				// sendM(handler, 2, "无法取得数据！请稍后再试。");
				return;
			}
		}
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MSG_WHAT.SHOW_LIST:
				showData();
				break;
			case MSG_WHAT.LIST_REFRESH:
				PriceData new_priceData = (PriceData) msg.obj;
				showNew(new_priceData);
				break;
			case MSG_WHAT.THREAD_AUTOUPDATE_FAIL:
				hzwy_BaseActivity.HZWY_Toast1("THREAD_AUTOUPDATE_FAIL");
				break;
			case MSG_WHAT.THREAD_SENDACTIVE_FAIL:
				hzwy_BaseActivity.HZWY_Toast1("THREAD_SENDACTIVE_FAIL");
				break;
			case MSG_WHAT.DENSITY_UTIL_EXCEPTION:
				break;
			default:
				break;
			}

		}
	};
	private HZWY_BaseActivity hzwy_BaseActivity;

	private class ViewHolder {
		// public TextView name;
		public TextView last;
		public TextView low;
		public TextView close;
		public TextView updown;
		public TextView updownrate;
		public TextView high;
		public TextView open;
	}

	private class ViewHolder_TTJ {
		// public TextView name;
		public TextView last;
		public TextView low;
		public TextView close;
		public TextView updown;
		public TextView updownrate;
		public TextView high;
		public TextView open;
		public TextView buy;
		public TextView sell;
		public TextView amplitude;
	}

	private class ViewHolder_sh {
		// public TextView name;
		public TextView last;
		public TextView low;
		public TextView close;
		public TextView settle;
		public TextView updown;
		public TextView updownrate;
		public TextView high;
		public TextView open;
		public TextView volume;
		public TextView average;
		public TextView turnover;
	}

	// 行情数据列表组件用数据adapter
	private class MyAdapter extends BaseAdapter {

		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			ViewHolder vholder = null;
			// log.i("getview->" + paramInt);
			if (paramView == null) {

				vholder = new ViewHolder();
				paramView = mInflater.inflate(R.layout.priceitemgg, null);
				vholder.last = (TextView) paramView.findViewById(R.id.last);
				vholder.low = (TextView) paramView.findViewById(R.id.low);
				vholder.close = (TextView) paramView.findViewById(R.id.close);
				vholder.updown = (TextView) paramView.findViewById(R.id.updown);
				vholder.updownrate = (TextView) paramView.findViewById(R.id.updownrate);
				vholder.high = (TextView) paramView.findViewById(R.id.high);
				vholder.open = (TextView) paramView.findViewById(R.id.open);

				paramView.setTag(vholder);
			} else {
				vholder = (ViewHolder) paramView.getTag();
			}

			// 设置最新的颜色
			vholder.last.setTextColor(Color.WHITE);
			if (!refreshData) {
				vholder.last.setBackgroundColor(Color.TRANSPARENT);
			}
			vholder.low.setTextColor(Color.WHITE);
			vholder.close.setTextColor(Color.WHITE);

			vholder.updown.setTextColor(Color.WHITE);
			vholder.updownrate.setTextColor(Color.WHITE);
			vholder.high.setTextColor(Color.WHITE);
			vholder.open.setTextColor(Color.WHITE);

			String updown = (String) mData.get(paramInt).get(Const.PRICE_UPDOWN);
			String newCode = (String) mData.get(paramInt).get(Const.PRICE_CODE);
			if (util.getFloat(updown) != 0) {
				if (util.getFloat(updown) < 0) {

					vholder.low.setTextColor(COLOR_GREEN);
					vholder.updown.setTextColor(COLOR_GREEN);
					vholder.updownrate.setTextColor(COLOR_GREEN);
					vholder.high.setTextColor(COLOR_GREEN);
					vholder.open.setTextColor(COLOR_GREEN);
					if (timeNow != null && newCode.equals(timeNow.getPrice_code())) {
						vholder.last.setBackgroundColor(COLOR_GREEN);
						vholder.last.setTextColor(Color.WHITE);
					} else {
						vholder.last.setBackgroundColor(Color.TRANSPARENT);
						vholder.last.setTextColor(COLOR_GREEN);
					}
				} else {
					vholder.last.setTextColor(Color.RED);
					vholder.low.setTextColor(Color.RED);
					vholder.updown.setTextColor(Color.RED);
					vholder.updownrate.setTextColor(Color.RED);
					vholder.high.setTextColor(Color.RED);
					vholder.open.setTextColor(Color.RED);
					if (timeNow != null && newCode.equals(timeNow.getPrice_code())) {
						vholder.last.setBackgroundColor(Color.RED);
						vholder.last.setTextColor(Color.WHITE);
					} else {
						vholder.last.setBackgroundColor(Color.TRANSPARENT);
						vholder.last.setTextColor(Color.RED);
					}
				}
			}
			float lastclose = util.getFloat((String) mData.get(paramInt).get(Const.PRICE_LASTCLOSE));
			float price_open = util.getFloat((String) mData.get(paramInt).get(Const.PRICE_OPEN));
			if (price_open != 0 && (price_open - lastclose) > 0) {
				vholder.open.setTextColor(Color.RED);
			} else {
				vholder.open.setTextColor(COLOR_GREEN);
			}

			float price_high = util.getFloat((String) mData.get(paramInt).get(Const.PRICE_HIGH));
			if (price_high != 0 && (price_high - lastclose) > 0) {
				vholder.high.setTextColor(Color.RED);
			} else {
				vholder.high.setTextColor(COLOR_GREEN);
			}

			float price_low = util.getFloat((String) mData.get(paramInt).get(Const.PRICE_LOW));
			if (price_low != 0 && (price_low - lastclose) > 0) {
				vholder.low.setTextColor(Color.RED);
			} else {
				vholder.low.setTextColor(COLOR_GREEN);
			}

			String price_last = (String) mData.get(paramInt).get(Const.PRICE_LAST);
			vholder.last.setText(price_last);
			vholder.open.setText((String) mData.get(paramInt).get(Const.PRICE_OPEN));
			vholder.high.setText((String) mData.get(paramInt).get(Const.PRICE_HIGH));
			vholder.low.setText((String) mData.get(paramInt).get(Const.PRICE_LOW));
			vholder.close.setText((String) mData.get(paramInt).get(Const.PRICE_LASTCLOSE));
			vholder.updown.setText((String) mData.get(paramInt).get(Const.PRICE_UPDOWN));
			vholder.updownrate.setText((String) mData.get(paramInt).get(Const.PRICE_UPDOWNRATE) + "%");

			if (util.getFloat(price_last) == 0) {
				vholder.last.setTextColor(Color.WHITE);
				vholder.updown.setTextColor(Color.WHITE);
				vholder.updownrate.setTextColor(Color.WHITE);
				vholder.low.setTextColor(Color.WHITE);
				vholder.close.setTextColor(Color.WHITE);
				vholder.high.setTextColor(Color.WHITE);
				vholder.open.setTextColor(Color.WHITE);
				vholder.updown.setText("0.00");
				vholder.updownrate.setText("0.00" + "%");
			}
			int colorPos = paramInt % colors.length;
			paramView.setBackgroundColor(colors[colorPos]);
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

	// 自动更新线程
	private class AutoUpdateThread extends Thread {

		AutoUpdateThread() {
		}

		public void run() {
			while (running) {

				// switch (ex) {
				// case 4:
				try {
					AutoUpdateThread.sleep(100);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// break;
				//
				// default:
				// break;
				// }
				try {
					if (UDPtools.client != null && !UDPtools.client.isClosed()) {
						UDPtools.client.receive(UDPtools.recpacket);

						String str = new String(UDPtools.recpacket.getData(), 0, 1000);
						PriceData temp = null;
						if (str.indexOf("{") >= 0 && str.indexOf("}") >= 0) {
							temp = util.getTimeNowUDPString(str, null);
						}
						if (temp != null && running == true) {
							Message message = Message.obtain();
							message.what = MSG_WHAT.LIST_REFRESH;
							message.obj = temp;
							handler.sendMessage(message);
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

	// 自动发送active
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
				refreshData = false;

			}
		}
	}

	/**
	 * 列表左侧固定Name 适配器
	 * 
	 * @author Administrator
	 * 
	 */
	private class CustomAdapter extends SimpleAdapter {

		public CustomAdapter(Context context, List<Map<String, Object>> items, int resource, String[] from, int[] to) {
			super(context, items, resource, from, to);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			int colorPos = position % colors.length;
			view.setBackgroundColor(colors[colorPos]);
			return view;
		}
	}

	/**
	 * 构造
	 */
	public Pricelist_fragments() {
	}

	/**
	 * 回调接口
	 * 
	 * @author yaoid
	 * 
	 */
	public interface OnItemSelectedListener {
		public void onpriceitemListsSelected(String code, String name, String selected, String ex, String decimal);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mCallback = (OnItemSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnItemSelectedListener");
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		/** 初始化颜色 */
		COLOR_GREEN = getActivity().getResources().getColor(R.color.new_green);

		mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		selected = getArguments().getString("selected");
		ex = getArguments().getInt("ex");
		type = ex + "";

		View view = inflater.inflate(R.layout.pricelist_fg, container, false);

		initselect(selected, type, ex);
		initScroll(view);
		initListView(view);

		// initProgressD();

		doListTask(selected);
		
		hzwy_BaseActivity = new HZWY_BaseActivity(MyApplication.CONTEXT);
		return view;
	}

	private void doListTask(String selected) {
		MyListTask mytask = new MyListTask();
		mytask.execute(selected, null, null);
	}

	class MyListTask extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			listHigh.setVisibility(View.GONE);
			pb.setVisibility(View.VISIBLE);
			// progressD_state(true);
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				// log.i("MyListTask -> " + params[0]);
				updateData(params[0]);
			} catch (Exception e) {
				// progressD_state(false);
				e.printStackTrace();
				sendMessagewhat(MSG_WHAT.ASYNCTASK_FAIL);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// progressD_state(false);
			sendMessagewhat(MSG_WHAT.SHOW_LIST);
		}
	}

	private void sendMessagewhat(int what) {
		handler.sendEmptyMessage(what);
	}
}
