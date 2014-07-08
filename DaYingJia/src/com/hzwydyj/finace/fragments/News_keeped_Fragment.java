package com.hzwydyj.finace.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hzwydyj.finace.R;
import com.hzwydyj.finace.activitys.KeepDetailView_ViewPager;
import com.hzwydyj.finace.activitys.Tab_News_Activity;
import com.hzwydyj.finace.data.NewsKeep;
import com.hzwydyj.finace.db.DBConst;
import com.hzwydyj.finace.db.DBManager;
import com.hzwydyj.finace.utils.MyLogger;

public class News_keeped_Fragment extends Fragment {

	public News_keeped_Fragment() {
	}

	private MyLogger log = MyLogger.yLog();
	private ListView newslistview;
	/** 新闻数据list */
	private List<NewsKeep> newsData;
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

	private DBManager dbManager;

	public void onResume() {
		super.onResume();
		// log.i("onResume");
		newsData.clear();
		dbManager = new DBManager(getActivity());
		changeList(dbManager.queryAllCursor());
		dbManager.closeDB();
	};

	@Override
	public void onPause() {
		super.onPause();
		dbManager.closeDB();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// super.onCreate(savedInstanceState);

		// log.i("onCreate");
		newsData = new ArrayList<NewsKeep>();
		View view = inflater.inflate(R.layout.tab_news_f, container, false);
		initListView(view);

		return view;
	}

	ProgressBar pb;

	private void initListView(View view) {
		newslistview = (ListView) view.findViewById(R.id.newslistview);
		pb = (ProgressBar) view.findViewById(R.id.pb);
	}

	private class Myadapter extends BaseAdapter {

		private Tab_News_Activity main;
		private List<? extends Map<String, ?>> list;

		public Myadapter(Context context, List<? extends Map<String, ?>> data) {
			super();
			main = (Tab_News_Activity) context;
			list = data;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (position < 0 || newsData.size() <= 0)
				return null;

			if (convertView == null)
				convertView = LayoutInflater.from(main).inflate(R.layout.newslistitems, null);

			TextView title = (TextView) convertView.findViewById(R.id.newsHead);
			TextView time = (TextView) convertView.findViewById(R.id.newsTime);
			Map<String, ?> map = list.get(position);
			title.setText((String) map.get(DATA_NEWSHEAD));
			time.setText((String) map.get(DATA_NEWSTIME));

			return convertView;
		}

		public int getCount() {
			return newsData.size();
		}

		public Object getItem(int position) {
			return newsData.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

	}

	public void showList() {

		if (newsData != null) {
			pb.setVisibility(View.GONE);

			list = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < newsData.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				NewsKeep keepitem = newsData.get(i);
				map.put(DATA_NEWSHEAD, keepitem.getNewstitle());
				map.put(DATA_NEWSTIME, keepitem.getNewstime());
				list.add(map);
			}

			adapter = new Myadapter(getActivity(), list);

			newslistview.setAdapter(adapter);
			newslistview.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (newsData.size() >= position && position >= 0) {

						int i = 0;
						int size = newsData.size();
						ArrayList<String> newsData_link = new ArrayList<String>();
						while (i < size) {
							newsData_link.add(newsData.get(i).getNews_id());
							i++;
						}
						Intent in = new Intent(getActivity(), KeepDetailView_ViewPager.class);
						Bundle bundle = new Bundle();
						bundle.putStringArrayList("link", newsData_link);
						bundle.putString("position", position + "");
						in.putExtras(bundle);
						startActivity(in);
					}
				}
			});
		}
		adapter.notifyDataSetChanged();
	}

	private void changeList(Cursor arg1) {

		while (arg1.moveToNext()) {

			NewsKeep keepitem = new NewsKeep();
			// keepitem._id = arg1.getInt(arg1.getColumnIndex("_id"));
			keepitem.setNews_id(arg1.getString(arg1.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWS_ID_COLUMN)));
			// log.i("从数据库里面拿到的 id-" + arg1.getString(arg1.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWS_ID_COLUMN)));

			keepitem.setNewstime(arg1.getString(arg1.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWS_TIME_COLUMN)));
			// log.i("setNewstime->" + arg1.getString(arg1.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWSTIME_COLUMN)));

			keepitem.setNewstitle(arg1.getString(arg1.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWS_TITLE_COLUMN)));
			// log.i("setNewstitle->" + arg1.getString(arg1.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWSTITLE_COLUMN)));

			// keepitem.setNewscontent(arg1.getString(arg1.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWSCONTENT_COLUMN)));
			// log.i("setNewscontent->" + arg1.getString(arg1.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWSCONTENT_COLUMN)));

			// keepitem.setAuthor(arg1.getString(arg1.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWSAUTHOR_COLUMN)));
			// log.i("setAuthor->" + arg1.getString(arg1.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWSAUTHOR_COLUMN)));

			// keepitem.setType(arg1.getString(arg1.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWSTYPE_COLUMN)));
			// log.i("setType->" + arg1.getString(arg1.getColumnIndex(DBConst.KEY_NEWSKEEP_NEWSTYPE_COLUMN)));

			newsData.add(keepitem);
			// return true;
		}

		// log.i("onResume newsData.size->" + newsData.size());
		showList();
	}

}
