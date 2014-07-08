package com.hzwydyj.finace.activitys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.Gstore;
import com.hzwydyj.finace.utils.Util;

/**
 * 
 * @author LuoYi
 * 
 */
public class GoldPriceSelectView extends Activity {

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

	private LayoutInflater mInflater;
	private ListView cornerListView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectview);

		initTitle();
		initProgressD();
		initBackBtn();
		initData();

		MyAsynTask myAsynTask = new MyAsynTask(Const.URL_GoldPriceList);
		myAsynTask.execute("", "", "");
	}

	private List<Map<String, String>> listData = null;
	private Util util;

	private void initData() {
		listData = new ArrayList<Map<String, String>>();
		util = new Util();
		cornerListView = (ListView) findViewById(R.id.setting_list);
		mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
	}

	private void initBackBtn() {
		Button backBtn = (Button) findViewById(R.id.backbtn);
		backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				GoldPriceSelectView.this.finish();
			}
		});
	}

	private TextView title = null;

	private void initTitle() {
		title = (TextView) findViewById(R.id.title);
		title.setText("金店报价");
	}

	private ProgressDialog progressDialog;

	private void initProgressD() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("金店信息加载中...");
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

	List<Gstore> itemsGstores = null;

	private class MyAsynTask extends AsyncTask<String, String, String> {

		private String url;

		public MyAsynTask(String url) {
			this.url = url;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressD_state(true);

		}

		@Override
		protected String doInBackground(String... params) {
			try {
				itemsGstores = util.getJDInforByXML(url);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			progressD_state(false);
			if (itemsGstores != null) {
				for (int i = 0; i < itemsGstores.size(); i++) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("gs_name", itemsGstores.get(i).getGs_name());
					map.put("gs_id", itemsGstores.get(i).getGs_id());
					listData.add(map);
				}

				MyAdapter adapter = new MyAdapter();
				cornerListView.setAdapter(adapter);
				cornerListView.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						if (listData.size() > position) {
							Intent in = new Intent(GoldPriceSelectView.this, GoldShopView.class);
							in.putExtra("selectedname", listData.get(position).get("gs_name"));
							in.putExtra("selectedid", listData.get(position).get("gs_id"));
							startActivity(in);
						}
					}
				});
			}
		}
	}

	// 行情数据列表组件用数据adapter
	private class MyAdapter extends BaseAdapter {

		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {

			View review = mInflater.inflate(R.layout.selectitem, null);
			TextView textView = (TextView) review.findViewById(R.id.selectitem);
			textView.setText((String) listData.get(paramInt).get("gs_name"));

			return review;
		}

		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		public int getCount() {
			if (listData != null) {
				return listData.size();
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
}