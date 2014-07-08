package com.hzwydyj.finace.activitys;

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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.PriceData;
import com.hzwydyj.finace.utils.DensityUtil;
import com.hzwydyj.finace.utils.MSG_WHAT;
import com.hzwydyj.finace.utils.MyLogger;
import com.hzwydyj.finace.utils.Util;
import com.hzwydyj.finace.view.SyncHorizontalScrollView;

/**
 * 我的自选
 * 
 * @author LuoYi
 * 
 */
public class OptionalView extends Activity {

	private MyLogger log = MyLogger.yLog();
	private static int DEVICE_NORMAL = 0;
	private static int DEVICE_LARGE = 1;
	private int current_device;
	private int lastItem = 0;

	private String errorMsg;
	private String selected;
	// private String selected_ad;
	private String selectedEX;
	private String type;
	private String[] ops;
	private String[] opsname;
	private String[] exs;
	// private String[] opselecteds;
	private String[] KEYS_ARRAY = { "shgold", "hjxh", "stockindex", "wh", "nymex", "comex", "ipe", "ttj", "shqh", "ygy", "zhj", "24k", "hxce", "hainan", "qdce", "hf", "bo", "nybot", "bs", "dyyt",
			"tks", "lme", "xhdz" };
	private boolean[] opssel;
	private boolean running = false;// 自动更新线程启动标志

	private SharedPreferences preferences;// 本地配置保存对象
	private Button editBtn;
	private LayoutInflater mInflater;
	private ListView priceListView;
	private ListView priceNameListView;
	private SyncHorizontalScrollView headScroll;
	private SyncHorizontalScrollView listScroll;
	private GestureDetector mGesture;
	private PriceData timeNow;// 更新数据
	private TextView title;// 当前分类
	private List<Map<String, Object>> mData;// 行情数据map
	private List<PriceData> listData;// 行情数据list
	private List<Map<String, Object>> listNameData;// 名字行情数据list
	private ProgressDialog progressDialog;// 数据取得等待对话框组件
	private AutoUpdateThread autoUpdateThread;// 自动更新线程
	private Util util = new Util();
	MyAdapter adapter;

	protected void onPause() {
		super.onPause();
		closeAutoUpdateT();
	}

	protected void onResume() {
		super.onResume();
		// log.i("temp", "onResume");
		checkSharedPers();
		if (autoUpdateThread == null) {
			startAutoUpdateT();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.optionalview);
		
		hzwy_BaseActivity = new HZWY_BaseActivity(MyApplication.CONTEXT);

		preferences = getSharedPreferences(Const.PREFERENCES_NAME, Activity.MODE_PRIVATE);
		mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		editBtn = (Button) findViewById(R.id.editbtn);
		editBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (ops == null || ops.length <= 0) {
					Message msg = handler.obtainMessage();
					Bundle b = new Bundle();
					b.putInt("total", 3);
					msg.setData(b);
					handler.sendMessage(msg);
					sendMessagewhat(MSG_WHAT.WATCHLIST_NULL);
				} else {
					editDialog();
				}
			}
		});
		title = (TextView) findViewById(R.id.title);
		Button backBtn = (Button) findViewById(R.id.backbtn);
		backBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				OptionalView.this.finish();
			}

		});

		initProgressD();

		headScroll = (SyncHorizontalScrollView) findViewById(R.id.scrollListHead);
		listScroll = (SyncHorizontalScrollView) findViewById(R.id.scrollList);
		if (headScroll == null || listScroll == null) {
			current_device = DEVICE_LARGE;
		}

		if (current_device == DEVICE_NORMAL) {
			headScroll.setSmoothScrollingEnabled(true);
			listScroll.setSmoothScrollingEnabled(true);
			headScroll.setScrollView(listScroll);
			listScroll.setScrollView(headScroll);
		}

		priceNameListView = (ListView) findViewById(R.id.priceNameListView);
		priceListView = (ListView) findViewById(R.id.priceListView);
		adapter = new MyAdapter();
		priceListView.setAdapter(adapter);
		priceListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (listData.size() > position) {
					PriceData pData = listData.get(position);

					int sel = 0;
					for (int i = 0; i < ops.length; i++) {
						if (pData.getPrice_code().equals(ops[i])) {
							sel = i;
						}
					}

					Intent intent = new Intent(OptionalView.this, PriceView.class);
					intent.putExtra("code", pData.getPrice_code());
					intent.putExtra("name", pData.getPrice_name());
					intent.putExtra("ex", exs[sel]);
					// Log.i("temp", "op-ex->" + exs[sel]);
					// 查找ex对应的 key
					for (int i = 1; i < KEYS_ARRAY.length + 1; i++) {
						if ((i + "").equals(exs[sel])) {
							intent.putExtra("selected", KEYS_ARRAY[i - 1]);
						}
					}
					intent.putExtra("decimal", pData.getPrice_Decimal());
					startActivity(intent);
				}
			}
		});
	}

	/**
	 * 检查保存的sharedpers是否有数据
	 */
	private void checkSharedPers() {
		String op = preferences.getString(Const.PREF_OPTIONAL, "");
		// log.i("op->" + op);
		String opname = preferences.getString(Const.PREF_OPTIONAL_NAME, "");
		// log.i("opname->" + opname);
		String opex = preferences.getString(Const.PREF_EX, "");
		// log.i("opex->" + opex);
		String opselected = preferences.getString(Const.PREF_SELECTED, "");

		if (!"".equals(op)) {
			if (op.indexOf(",") < 0) {
				ops = new String[] { op };
				exs = new String[] { opex };
				opsname = new String[] { opname };
				// opselecteds = new String[] { opselected };
			} else {
				ops = op.split(",");
				exs = opex.split(",");
				opsname = opname.split(",");
				// opselecteds = opselected.split(",");
			}
			opssel = new boolean[ops.length];
		}

		selected = op;
		selectedEX = opex;
		// selected_ad = opselected;

		if (ops == null || ops.length <= 0) {
			sendMessagewhat(MSG_WHAT.WATCHLIST_VIEWNULL);
		} else {
			getTCPDataTask(selected);
		}
	}

	private void getTCPDataTask(String selected) {
		GetTCPDataTask mytask = new GetTCPDataTask();
		mytask.execute(selected, null, null);
	}

	class GetTCPDataTask extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progressD_state(true);
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				updateData(params[0]);
				// log.i("temp", params[0]);
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

	private synchronized void updateData(String type) {
		listData = util.getPriceDataOP("http://mhth.fx678.com/diy.aspx?code=" + type, type, selectedEX);
	}

	public void showData() {
		progressD_state(false);

		listNameData = new ArrayList<Map<String, Object>>();
		mData = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < listData.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> mapName = new HashMap<String, Object>();
			PriceData pData = listData.get(i);
			String decimal = pData.getPrice_Decimal();
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
			map.put(Const.PRICE_QUOTETIME, pData.getPrice_quotetime());
			map.put(Const.PRICE_NAME, pData.getPrice_name());
			map.put(Const.PRICE_CODE, pData.getPrice_code());

			map.put(Const.PRICE_OPEN, String.valueOf(df_base.format(Float.parseFloat(pData.getPrice_open()))));
			map.put(Const.PRICE_HIGH, String.valueOf(df_base.format(Float.parseFloat(pData.getPrice_high()))));
			map.put(Const.PRICE_LAST, String.valueOf(df_base.format(Float.parseFloat(pData.getPrice_last()))));
			map.put(Const.PRICE_LOW, String.valueOf(df_base.format(Float.parseFloat(pData.getPrice_low()))));
			map.put(Const.PRICE_LASTCLOSE, String.valueOf(df_base.format(Float.parseFloat(pData.getPrice_lastclose()))));
			map.put(Const.PRICE_LASTSETTLE, pData.getPrice_lastsettle());
			map.put(Const.PRICE_UPDOWN, String.valueOf(df_base.format(Float.parseFloat(pData.getPrice_updown()))));
			map.put(Const.PRICE_UPDOWNRATE, pData.getPrice_updownrate());
			map.put(Const.PRICE_VOLUME, pData.getPrice_volume());
			map.put(Const.PRICE_TURNOVER, pData.getPrice_turnover());
			map.put(Const.PRICE_DECIMAL, pData.getPrice_Decimal());
			mapName.put(Const.PRICE_NAME, pData.getPrice_name());

			mData.add(map);
			listNameData.add(mapName);
		}
		ViewGroup.LayoutParams linearParams1 = priceListView.getLayoutParams();
		linearParams1.height = DensityUtil.dip2px(this, 35) * listData.size();
		priceListView.setLayoutParams(linearParams1);
		ViewGroup.LayoutParams linearParams2 = priceNameListView.getLayoutParams();
		linearParams2.height = DensityUtil.dip2px(this, 35) * listData.size();
		priceNameListView.setLayoutParams(linearParams2);
		CustomAdapter adapterS = new CustomAdapter(this, listNameData, R.layout.pricenameitem, new String[] { Const.PRICE_NAME }, new int[] { R.id.pricename });

		priceNameListView.setAdapter(adapterS);
		// MyAdapter adapter = (MyAdapter) priceListView.getAdapter();
		adapter.notifyDataSetChanged();

		// log.i("temp", "showData finish");
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

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MSG_WHAT.WATCHLIST_NULL:
				hzwy_BaseActivity.HZWY_Toast1("您还没有添加过自选行情");
				break;
			case MSG_WHAT.WATCHLIST_VIEWNULL:
				hzwy_BaseActivity.HZWY_Toast1("您还没有添加过自选行情，请到行情详细画面中点击【+自选】添加自选行情数据");
				break;
			case MSG_WHAT.WATCHLIST_FEFRESH:
				getTCPDataTask(selected);
				break;
			case MSG_WHAT.SHOW_LIST:
				showData();
				if (listData != null && listData.size() > 0 && running == true ) {
					hzwy_BaseActivity.HZWY_Toast1("数据已更新!");
				}
				break;

			default:
				break;
			}
		}
	};

	private void startAutoUpdateT() {
		running = true;
		autoUpdateThread = new AutoUpdateThread();
		autoUpdateThread.start();
	}

	private void closeAutoUpdateT() {
		running = false;
		if (autoUpdateThread != null) {
			autoUpdateThread.interrupt();
			autoUpdateThread = null;
		}
	}

	private class ViewHolder {
		// public TextView name;
		public TextView last;
		public TextView low;
		public TextView close;
		// public TextView settle;
		public TextView updown;
		public TextView updownrate;
		public TextView high;
		public TextView open;
	}

	// 行情数据列表组件用数据adapter
	private class MyAdapter extends BaseAdapter {

		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			ViewHolder vholder = null;
			if (paramView == null) {
				vholder = new ViewHolder();
				paramView = mInflater.inflate(R.layout.priceitemgg, null);
				// vholder.name = (TextView)
				// paramView.findViewById(R.id.pricename);
				vholder.last = (TextView) paramView.findViewById(R.id.last);
				vholder.low = (TextView) paramView.findViewById(R.id.low);
				vholder.close = (TextView) paramView.findViewById(R.id.close);
				// vholder.settle = (TextView)
				// paramView.findViewById(R.id.settle);
				vholder.updown = (TextView) paramView.findViewById(R.id.updown);
				vholder.updownrate = (TextView) paramView.findViewById(R.id.updownrate);
				vholder.high = (TextView) paramView.findViewById(R.id.high);
				vholder.open = (TextView) paramView.findViewById(R.id.open);
				paramView.setTag(vholder);
			} else {
				vholder = (ViewHolder) paramView.getTag();
			}

			vholder.last.setTextColor(Color.WHITE);
			vholder.last.setBackgroundColor(Color.TRANSPARENT);
			vholder.low.setTextColor(Color.WHITE);
			vholder.close.setTextColor(Color.WHITE);
			// vholder.settle.setTextColor(Color.WHITE);
			vholder.updown.setTextColor(Color.WHITE);
			vholder.updownrate.setTextColor(Color.WHITE);
			vholder.high.setTextColor(Color.WHITE);
			vholder.open.setTextColor(Color.WHITE);
			String updown = (String) mData.get(paramInt).get(Const.PRICE_UPDOWN);
			String newCode = (String) mData.get(paramInt).get(Const.PRICE_CODE);
			if (util.getFloat(updown) != 0) {
				if (util.getFloat(updown) < 0) {
					vholder.last.setTextColor(Color.GREEN);
					vholder.low.setTextColor(Color.GREEN);
					vholder.updown.setTextColor(Color.GREEN);
					vholder.updownrate.setTextColor(Color.GREEN);
					vholder.high.setTextColor(Color.GREEN);
					vholder.open.setTextColor(Color.GREEN);

				} else {
					vholder.last.setTextColor(Color.RED);
					vholder.low.setTextColor(Color.RED);
					vholder.updown.setTextColor(Color.RED);
					vholder.updownrate.setTextColor(Color.RED);
					vholder.high.setTextColor(Color.RED);
					vholder.open.setTextColor(Color.RED);

				}
			}
			float lastclose = util.getFloat((String) mData.get(paramInt).get(Const.PRICE_LASTCLOSE));
			if (util.getFloat((String) mData.get(paramInt).get(Const.PRICE_OPEN)) != 0 && (util.getFloat((String) mData.get(paramInt).get(Const.PRICE_OPEN)) - lastclose) > 0) {
				vholder.open.setTextColor(Color.RED);
			} else {
				vholder.open.setTextColor(Color.GREEN);
			}
			if (util.getFloat((String) mData.get(paramInt).get(Const.PRICE_HIGH)) != 0 && (util.getFloat((String) mData.get(paramInt).get(Const.PRICE_HIGH)) - lastclose) > 0) {
				vholder.high.setTextColor(Color.RED);
			} else {
				vholder.high.setTextColor(Color.GREEN);
			}
			if (util.getFloat((String) mData.get(paramInt).get(Const.PRICE_LOW)) != 0 && (util.getFloat((String) mData.get(paramInt).get(Const.PRICE_LOW)) - lastclose) > 0) {
				vholder.low.setTextColor(Color.RED);
			} else {
				vholder.low.setTextColor(Color.GREEN);
			}

			vholder.last.setText((String) mData.get(paramInt).get(Const.PRICE_LAST));
			vholder.low.setText((String) mData.get(paramInt).get(Const.PRICE_LOW));
			vholder.close.setText((String) mData.get(paramInt).get(Const.PRICE_LASTCLOSE));
			// vholder.settle.setText((String)
			// mData.get(paramInt).get(Const.PRICE_LASTSETTLE));
			vholder.updown.setText((String) mData.get(paramInt).get(Const.PRICE_UPDOWN));
			vholder.updownrate.setText((String) mData.get(paramInt).get(Const.PRICE_UPDOWNRATE) + "%");
			vholder.high.setText((String) mData.get(paramInt).get(Const.PRICE_HIGH));
			vholder.open.setText((String) mData.get(paramInt).get(Const.PRICE_OPEN));
			String turnovertmp = (String) mData.get(paramInt).get(Const.PRICE_TURNOVER);
			float turnovertmpvalue = util.getFloat(turnovertmp) / 10000;
			turnovertmpvalue = (float) Math.round(turnovertmpvalue * 100) / 100;
			String lastValue = (String) mData.get(paramInt).get(Const.PRICE_LAST);
			if (util.getFloat(lastValue) == 0) {
				vholder.last.setTextColor(Color.WHITE);
				vholder.updown.setTextColor(Color.WHITE);
				vholder.updownrate.setTextColor(Color.WHITE);
				vholder.low.setTextColor(Color.WHITE);
				vholder.close.setTextColor(Color.WHITE);
				// vholder.settle.setTextColor(Color.WHITE);
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

	private int[] colors = new int[] { 0xe1151515, 0xe10f0f0f };
	private HZWY_BaseActivity hzwy_BaseActivity;

	// 自动更新线程
	private class AutoUpdateThread extends Thread {
		public void run() {
			while (running) {
				try {
					sleep(10000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				sendMessagewhat(MSG_WHAT.WATCHLIST_FEFRESH);
			}
		}
	}

	// 菜单
	protected void editDialog() {
		Builder b = new AlertDialog.Builder(this);
		b.setTitle("选择需要删除的自选行情");
		// checkSharedPers();

		for (int i = 0; i < opsname.length; i++) {
			// log.i("删除前=opsname->" + opsname[i]);
		}

		b.setMultiChoiceItems(opsname, opssel, new DialogInterface.OnMultiChoiceClickListener() {
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				opssel[which] = isChecked; // 设置选中标志位
				log.i(which + "is " + isChecked);
			}
		});
		b.setPositiveButton("删除", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				String opssave = "";
				String opnamesave = "";
				String exsave = "";
				String selectedsave = "";

				for (int i = 0; i < ops.length; i++) {
					// log.i("opssel[" + i + "]=" + opssel[i]);

					if (!opssel[i]) {
						// 当前是否在选择状态 不在选择状态
						if ("".equals(opssave)) {
							opssave = ops[i];
							opnamesave = opsname[i];
							exsave = exs[i];
							// selectedsave = opselecteds[i];
						} else {
							opssave = opssave + "," + ops[i];
							opnamesave = opnamesave + "," + opsname[i];
							exsave = exsave + "," + exs[i];
							// selectedsave = selectedsave + "," +
							// opselecteds[i];
						}
					}
				}

				// selected_ad = selectedsave;

				SharedPreferences.Editor editor = preferences.edit();
				// log.i("放入opssave=" + opssave);
				// log.i("放入opnamesave=" + opnamesave);
				// log.i("放入exsave=" + exsave);

				editor.putString(Const.PREF_OPTIONAL, opssave);
				editor.putString(Const.PREF_OPTIONAL_NAME, opnamesave);
				editor.putString(Const.PREF_EX, exsave);
				editor.putString(Const.PREF_SELECTED, selectedsave);

				// log.i("selected->" + selected);
				selected = opssave;
				// log.i("selected--->" + selected);

				if (!"".equals(opssave)) {
					if (opssave.indexOf(",") < 0) {
						ops = new String[] { opssave };
						opsname = new String[] { opnamesave };
						exs = new String[] { exsave };
						// opselecteds = new String[] { selectedsave };
					} else {
						ops = opssave.split(",");
						opsname = opnamesave.split(",");
						exs = exsave.split(",");
						// opselecteds = selectedsave.split(",");
					}
					opssel = new boolean[ops.length];
				} else {
					ops = null;
					opsname = null;
					exs = null;
					opssel = null;
					// opselecteds = null;
				}

				editor.commit();
				dialog.dismiss();

				if (!"".equals(selected)) {
					getTCPDataTask(selected);
				} else {
					listNameData = new ArrayList<Map<String, Object>>();
					SimpleAdapter adapter = new SimpleAdapter(OptionalView.this, listNameData, R.layout.pricenameitem, new String[] { Const.PRICE_NAME }, new int[] { R.id.pricename });
					priceNameListView.setAdapter(adapter);
					mData = new ArrayList<Map<String, Object>>();
					MyAdapter adapters = (MyAdapter) priceListView.getAdapter();
					adapters.notifyDataSetChanged();
				}

			}
		});
		b.setNegativeButton(R.string.regiter_caler, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		b.create().show();
	}

	private void sendMessagewhat(int what) {
		handler.sendEmptyMessage(what);
	}

	private void initProgressD() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("取得数据...");
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
