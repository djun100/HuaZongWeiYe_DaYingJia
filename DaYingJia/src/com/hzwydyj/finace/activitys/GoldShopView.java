package com.hzwydyj.finace.activitys;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.GPrice;
import com.hzwydyj.finace.utils.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * 金店报价详细
 * 
 * @author LuoYi
 * 
 */
public class GoldShopView extends Activity {

	private List<GPrice> 			gprices;
	private LayoutInflater 			mInflater;
	private ListView 				listview;
	private TextView 				title;
	private Util 					util;
	private String 					gs_id;

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);

	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

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

	String titleName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goldshop_a);

		initData();
		initTitle();
		initProgressD();
		initBackBtn();

		doTask("");

	}

	private void initData() {
		Bundle bundle 		= getIntent().getExtras();
		gs_id 				= bundle.getString("selectedid");
		titleName 			= bundle.getString("selectedname");

		listview 			= (ListView) findViewById(R.id.listview);
		mInflater 			= getLayoutInflater();
		util 				= new Util();
	}

	private void initBackBtn() {
		Button backBtn 		= (Button) findViewById(R.id.backbtn);
		backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				GoldShopView.this.finish();
			}
		});
	}

	private void initTitle() {

		title = (TextView) findViewById(R.id.title);
		title.setText(titleName);
	}

	private void doTask(String url) {
		MyTask mytask = new MyTask();
		mytask.execute(url, null, null);
	}

	class MyTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			try {
				gprices = util.getJDDetialByXML(Const.URL_GoldPriceDetail + gs_id);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressD_state(false);
			listview.setAdapter(new MygpAdapter());
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressD_state(true);
		}
	}

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

	private static class ViewHolder {
		// public TextView name;
		public TextView 			name;
		public TextView 			product;
		public TextView 			price;
		public TextView 			degree;
		public TextView 			updatetime;
		public TextView 			createtime;
		public TextView 			change;
		public TextView 			unit;
	}

	class MygpAdapter extends BaseAdapter {

		ViewHolder holder = null;

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
				// 创建新的view
				convertView 		= mInflater.inflate(R.layout.gpriceitem, null);
				holder 				= new ViewHolder();
//				holder.name 		= (TextView) convertView.findViewById(R.id.name);
				holder.product 		= (TextView) convertView.findViewById(R.id.product);
				holder.price 		= (TextView) convertView.findViewById(R.id.price);
				holder.unit 		= (TextView) convertView.findViewById(R.id.unit);
				holder.degree 		= (TextView) convertView.findViewById(R.id.degree);
				holder.updatetime 	= (TextView) convertView.findViewById(R.id.updatetime);
				convertView.setTag(holder);
			} else {
				// 使用缓存的view
				holder 				= (ViewHolder) convertView.getTag();
			}

			holder.product.setText(gprices.get(position).getProduct().toString());
			if (gprices.get(position).getChange().toString().equals("涨")) {
				holder.price.setText(gprices.get(position).getPrice().toString());
				holder.price.setTextColor(Color.RED);
			} else if (gprices.get(position).getChange().toString().equals("跌")) {
				holder.price.setText(gprices.get(position).getPrice().toString());
				holder.price.setTextColor(Color.GREEN);
			} else {
				holder.price.setText(gprices.get(position).getPrice().toString());
			}
			holder.price.setText(gprices.get(position).getPrice().toString());
			holder.unit.setText("元/克");
			holder.degree.setText(gprices.get(position).getDegree().toString().equals("0") ? "-" : gprices.get(position).getDegree().toString());
			holder.updatetime.setText(gprices.get(position).getUpdatetime().toString().substring(5));
			return convertView;
		}
	}
}
