package com.hzwydyj.finace.activitys;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.RMBprice;
import com.hzwydyj.finace.utils.MSG_WHAT;
import com.hzwydyj.finace.utils.MyLogger;
import com.hzwydyj.finace.utils.Util;
import com.hzwydyj.finace.view.CustomDialog;
import com.umeng.analytics.MobclickAgent;

/**
 * 汇率换算
 * 
 * @author Watson Yao
 */
public class CurrencyConverterView extends Activity {

	private MyLogger log = MyLogger.yLog();
	List<RMBprice> gprices;
	private LayoutInflater mInflater;
	private ListView listview;
	private TextView title;
	String sel;
	Util util;
	String ex;
	MyReceiver receiver;
	IntentFilter filter;

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
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		receiver = null;
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private int img_ids[] = { R.drawable.usa, R.drawable.gbp, R.drawable.eur, R.drawable.hkd, R.drawable.rub, R.drawable.jpy, R.drawable.mop, R.drawable.thb, R.drawable.krw, R.drawable.myr,
			R.drawable.twd, R.drawable.chf, R.drawable.sgd, R.drawable.sek, R.drawable.dkk, R.drawable.nok, R.drawable.cad, R.drawable.nzd, R.drawable.inr, R.drawable.aud, R.drawable.php };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rmbprice_main);

		Bundle bundle = getIntent().getExtras();
		// String titleShow = bundle.getString("selectedname");
		title = (TextView) findViewById(R.id.title);
		title.setText("汇率换算");

		listview = (ListView) findViewById(R.id.listview);
		mInflater = getLayoutInflater();

		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.rmbpricechange");
		registerReceiver(receiver, filter);

		initProgressD();

		Button imageButton = (Button) findViewById(R.id.backbtn);
		imageButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CurrencyConverterView.this.finish();
			}
		});

		util = new Util();
		doTask("");

	}

	// 点击后的查询任务
	private void doTask(String url) {
		// TODO Auto-generated method stub
		MyTask mytask = new MyTask();
		mytask.execute(url, null, null);
	}

	CustomDialog dialog;

	class MyTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			try {
				gprices = util.getRMBprice(Const.RMB_EXCHANGE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressD_state(false);
			adapter = new MygpAdapter();
			listview.setAdapter(adapter);
			listview.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					dialog = new CustomDialog(CurrencyConverterView.this, R.style.customDialog, R.layout.customdialog, gprices.get(arg2).getName(), gprices.get(arg2).getRate(), gprices.get(arg2)
							.getRmb());
					dialog.show();
				}
			});
			// 显示数据
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// 显示进度条
			progressD_state(true);
		}
	}

	MygpAdapter adapter;
	// 数据取得等待对话框组件
	private ProgressDialog progressDialog;

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

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_WHAT.CURRENCY_CONVERTER_FEFRESH:
				Bundle bundle = msg.getData();

				// log.i("exchange1_num->" + bundle.getString("exchange1_num"));
				// log.i("exchange_bol->" + bundle.getBoolean("exchange_bol"));

				break;

			default:
				break;
			}
		}

	};

	private static class ViewHolder {
		public ImageView img;
		public TextView name;
		public TextView code;
		public TextView money;
		public TextView rmb;
		public TextView rate;
	}

	class MygpAdapter extends BaseAdapter {

		ViewHolder holder = null;

		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		public int getCount() {
			if (gprices == null) {
				return 0;
			} else {
				return gprices.size();
			}
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.rmbpriceitem, null);
				holder = new ViewHolder();
				holder.name = (TextView) convertView.findViewById(R.id.product);
				holder.code = (TextView) convertView.findViewById(R.id.price);
				holder.money = (TextView) convertView.findViewById(R.id.unit);
				holder.rmb = (TextView) convertView.findViewById(R.id.degree);
				holder.rate = (TextView) convertView.findViewById(R.id.updatetime);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.name.setText(gprices.get(position).getName().toString());
			holder.code.setText(gprices.get(position).getCode().toString());
			holder.money.setText(gprices.get(position).getMoney().toString());
			holder.rmb.setText(gprices.get(position).getRmb().toString());
			holder.rate.setText(Const.df2.format(gprices.get(position).getRate()));
			holder.img.setImageResource(img_ids[position]);

			return convertView;
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.exchange:
			// 还原为最初状态
			doTask("");
			break;
		default:
			break;
		}
	}

	public class MyReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {

			String temp = intent.getExtras().getString("exchange1_num");
			if (temp != null && !"".equals(temp)) {
				if (intent.getExtras().getBoolean("exchange_bol")) {
					// true
					Double newrmb = Double.parseDouble(temp);
					for (int i = 0; i < gprices.size(); i++) {
						gprices.get(i).setMoney(Const.df2.format(100 * newrmb / Double.parseDouble(gprices.get(i).getRmb())));
						gprices.get(i).setRmb(temp);
					}

				} else {
					// false
					Double new100 = Double.parseDouble(temp);
					double rate100 = new100 / 100.0f;
					for (int i = 0; i < gprices.size(); i++) {
						gprices.get(i).setMoney(temp);
						gprices.get(i).setRmb(Const.df2.format(rate100 * Double.parseDouble(gprices.get(i).getRmb())));
					}
				}

				adapter.notifyDataSetChanged();
			}

		}

		public MyReceiver() {
		}
	}

	private void changelist(String string, boolean boolean1) {
	}
}
